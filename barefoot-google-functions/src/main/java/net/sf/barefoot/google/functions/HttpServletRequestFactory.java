/*
 *
 *  Copyright 2020, Roger Brown
 *
 *  This file is part of Barefoot.
 *
 *  This program is free software: you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as published by the
 *  Free Software Foundation, either version 3 of the License, or (at your
 *  option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT
 *  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 *  more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */

package net.sf.barefoot.google.functions;

import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import net.sf.barefoot.context.AbstractServletContext;
import net.sf.barefoot.context.AbstractServletRequest;
import net.sf.barefoot.context.BarefootServletContextLogger;

/** create HTTP servlet request from Google request */
public class HttpServletRequestFactory {
  static final String COOKIE = "cookie",
      X_FORWARD_PORT = "x-forwarded-port",
      X_FORWARD_PROTO = "x-forwarded-proto",
      X_FORWARD_HOST = "x-forwarded-host",
      X_FORWARD_PREFIX = "x-forwarded-prefix";
  static final String[] EMPTY_LIST = {};

  /**
   * create servlet request based on Azure request
   *
   * @param servletContext current context
   * @param req Google request
   * @param resp Google response
   * @return new request
   * @throws java.io.IOException on error
   */
  public AbstractServletRequest create(
      AbstractServletContext servletContext, HttpRequest req, HttpResponse resp)
      throws IOException {
    final String reqUri = req.getUri();
    final URL reqUrl = new URL(reqUri);
    AbstractServletRequest.Builder builder = servletContext.getServletRequestBuilder();
    final Map<String, List<String>> hdrs = new HashMap<>();
    final Map<String, String[]> params = new HashMap<>();
    final long contentLength = req.getContentLength();
    final String contentType = req.getContentType().orElse(null);
    final String queryString = req.getQuery().orElse(null);
    final String path = req.getPath();
    final String charEncoding = req.getCharacterEncoding().orElse(null);
    String serverName = reqUrl.getHost();
    int serverPort = reqUrl.getPort();
    String contextPath = servletContext.getContextPath();
    String proto = reqUrl.getProtocol();

    builder.method(req.getMethod());
    builder.headers(hdrs);
    builder.isSecure(Boolean.TRUE);

    Map<String, List<String>> reqHead = req.getHeaders();

    if (reqHead != null) {
      for (Map.Entry<String, List<String>> s : reqHead.entrySet()) {
        final String key = s.getKey().toLowerCase();
        final List<String> values = s.getValue();
        boolean addHeader = true;
        switch (key) {
          case COOKIE:
            builder.cookies(values);
            addHeader = false;
            break;
          case X_FORWARD_PREFIX:
            {
              String value = values.get(0);
              switch (value) {
                case "":
                case "/":
                  contextPath = "";
                  break;
                default:
                  contextPath = value;
                  break;
              }
            }
            break;
          case X_FORWARD_HOST:
            serverName = values.get(0);
            break;
          case X_FORWARD_PORT:
            serverPort = Integer.parseInt(values.get(0));
            break;
          case X_FORWARD_PROTO:
            proto = values.get(0);
            break;
        }
        if (addHeader) {
          hdrs.put(key, values);
        }
      }
    }

    final boolean isSecure = "https".equals(proto);

    String uri = contextPath + path;
    String url;

    switch (serverPort) {
      case 80:
        if ("http".equals(proto)) {
          serverPort = -1;
        }
        break;
      case 443:
        if ("https".equals(proto)) {
          serverPort = -1;
        }
        break;
    }

    if (serverName != null) {
      StringBuilder sb = new StringBuilder();
      sb.append(proto);
      sb.append("://");
      sb.append(serverName);
      if (serverPort != -1) {
        sb.append(":");
        sb.append(Integer.toString(serverPort));
      }
      sb.append(uri);
      url = sb.toString();
    } else {
      url = uri;
    }

    Map<String, List<String>> qp = req.getQueryParameters();

    if (qp != null && !qp.isEmpty()) {
      qp.entrySet()
          .forEach(
              (q) -> {
                params.put(q.getKey(), q.getValue().toArray(EMPTY_LIST));
              });
    }

    builder.parameters(params);

    builder.inputStream(
        () -> {
          try {
            return req.getInputStream();
          } catch (IOException ex) {
            throw new RuntimeException(ex);
          }
        });

    builder.reader(
        () -> {
          try {
            return req.getReader();
          } catch (IOException ex) {
            throw new RuntimeException(ex);
          }
        });

    builder.queryString(queryString);
    builder.contentLength(contentLength);
    builder.serverName(serverName);
    builder.serverPort(serverPort);
    builder.protocol("HTTP/1.1");
    builder.isSecure(isSecure);
    builder.logger(new ContextLogger());
    builder.contextPath(contextPath);
    builder.requestUri(uri);
    builder.requestUrl(url);
    builder.contentType(contentType);
    builder.characterEncoding(charEncoding);

    AbstractServletRequest result = builder.build();

    result.setAttribute(AbstractServletRequest.ATTR_ORIGINAL_REQUEST, req);
    result.setAttribute(AbstractServletRequest.ATTR_ORIGINAL_RESPONSE, resp);

    return result;
  }

  private static class ContextLogger implements BarefootServletContextLogger {
    @Override
    public void log(String msg) {
      Logger.getGlobal().info(msg);
    }

    @Override
    public void log(String msg, Throwable thr) {
      Logger.getGlobal().info(msg);
    }
  }
}
