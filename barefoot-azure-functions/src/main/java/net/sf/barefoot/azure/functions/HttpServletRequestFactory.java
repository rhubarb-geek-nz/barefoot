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

package net.sf.barefoot.azure.functions;

import com.google.gson.Gson;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.sf.barefoot.context.AbstractServletContext;
import net.sf.barefoot.context.AbstractServletRequest;
import net.sf.barefoot.context.BarefootContentType;
import net.sf.barefoot.context.BarefootPreprocessor;
import net.sf.barefoot.context.BarefootServletContextLogger;
import net.sf.barefoot.util.ByteBufferInputStream;

/** create HTTP servlet request from Azure request */
public class HttpServletRequestFactory {
  final Gson gson;
  final BarefootPreprocessor preProcessor = new BarefootPreprocessor();
  static final String COOKIE = "cookie",
      CONTENT_TYPE = "content-type",
      HOST = "host",
      X_FORWARD_PORT = "x-forwarded-port",
      X_FORWARD_PROTO = "x-forwarded-proto",
      X_FORWARD_HOST = "x-forwarded-host",
      X_FORWARD_PREFIX = "x-forwarded-prefix";

  public HttpServletRequestFactory(Gson g) {
    gson = g;
  }

  /**
   * create servlet request based on Azure request
   *
   * @param servletContext current context
   * @param req Azure request
   * @param execContext Azure execution context
   * @return new request
   * @throws java.io.IOException on error
   */
  public AbstractServletRequest create(
      AbstractServletContext servletContext,
      HttpRequestMessage<?> req,
      ExecutionContext execContext)
      throws IOException {
    AbstractServletRequest.Builder builder = servletContext.getServletRequestBuilder();
    Map<String, List<String>> hdrs = new HashMap<>();
    Map<String, List<String>> params = new HashMap<>();
    String contentType = null;
    Supplier<Reader> reader = null;
    Supplier<InputStream> inputStream = null;
    String serverName = null;
    int serverPort = -1;
    boolean isSecure = false;
    String host = null;
    String contextPath = servletContext.getContextPath();
    String proto = null;

    Object body = req.getBody();

    final String path = req.getUri().getPath();

    Map<String, String> reqHead = req.getHeaders();

    if (reqHead != null) {
      for (Map.Entry<String, String> s : reqHead.entrySet()) {
        final String key = s.getKey().toLowerCase();
        final String value = s.getValue();
        boolean addHeader = true;
        switch (key) {
          case COOKIE:
            builder.cookies(value);
            addHeader = false;
            break;
          case HOST:
            host = value;
            break;
          case CONTENT_TYPE:
            contentType = value;
            break;
          case X_FORWARD_PREFIX:
            switch (value) {
              case "":
              case "/":
                contextPath = "";
                break;
              default:
                contextPath = value;
                break;
            }
            break;
          case X_FORWARD_HOST:
            serverName = value;
            break;
          case X_FORWARD_PORT:
            serverPort = Integer.parseInt(value);
            break;
          case X_FORWARD_PROTO:
            proto = value;
            isSecure = "https".equals(value);
            break;
        }
        if (addHeader) {
          List<String> list = new ArrayList<>();
          list.add(value);
          hdrs.put(key, list);
        }
      }
    }

    String uri = contextPath + path;
    String url;

    if (serverName == null && host != null) {
      String[] parts = host.split(":");
      serverName = parts[0];
      if (parts.length > 1) serverPort = Integer.parseInt(parts[1]);
    }

    if (proto == null && serverName != null) {
      isSecure = !"localhost".equals(serverName);
      proto = isSecure ? "https" : "http";
    }

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

    Map<String, String> qp = req.getQueryParameters();

    if (qp != null && !qp.isEmpty()) {
      StringBuilder sb = new StringBuilder();

      for (Map.Entry<String, String> s : qp.entrySet()) {
        List<String> list = new ArrayList<>();
        list.add(s.getValue());
        params.put(s.getKey(), list);

        if (sb.length() != 0) sb.append("&");
        sb.append(s.getKey());
        if (s.getValue() != null) {
          sb.append("=");
          sb.append(URLEncoder.encode(s.getValue(), StandardCharsets.UTF_8.name()));
        }
      }

      if (sb.length() != 0) {
        builder.queryString(sb.toString());
      }
    }

    while (body != null) {
      if (body instanceof String) {
        final String str = (String) body;
        reader = () -> new StringReader(str);
        break;
      }

      if (body instanceof byte[]) {
        final byte[] bytes = (byte[]) body;
        inputStream = () -> new ByteArrayInputStream(bytes);
        break;
      }

      if (body instanceof ByteBuffer) {
        final ByteBuffer buf = (ByteBuffer) body;
        inputStream = () -> new ByteBufferInputStream(buf);
        break;
      }

      if (body instanceof InputStream) {
        final InputStream str = (InputStream) body;
        inputStream = () -> str;
        break;
      }

      if (body instanceof Reader) {
        final Reader rdr = (Reader) body;
        reader = () -> rdr;
        break;
      }

      if (body instanceof Optional) {
        Optional optional = (Optional) body;
        if (optional.isPresent()) {
          body = optional.get();
        } else {
          break;
        }
      } else {
        if (gson != null && BarefootContentType.APPLICATION_JSON.equals(contentType)) {
          body = gson.toJson(body);
        } else {
          body = body.toString();
        }
      }
    }

    if ((reader != null || inputStream != null)
        && preProcessor.isApplicationFormUrlEncoded(contentType)) {
      String cs = BarefootContentType.getCharsetFromContentType(contentType);
      Charset charSet = cs == null ? StandardCharsets.UTF_8 : Charset.forName(cs);
      preProcessor.processApplicationFormUrlEncoded(
          reader == null ? new InputStreamReader(inputStream.get(), charSet) : reader.get(),
          params,
          charSet);
    } else {
      builder.reader(reader);
      builder.inputStream(inputStream);
    }

    builder.method(req.getHttpMethod().name());
    builder.headers(hdrs);
    builder.parameters(collapse(params));
    builder.isSecure(Boolean.TRUE);
    builder.serverName(serverName);
    builder.serverPort(serverPort);
    builder.protocol("HTTP/1.1");
    builder.isSecure(isSecure);
    builder.logger(new ContextLogger(execContext));
    builder.contextPath(contextPath);
    builder.requestUri(uri);
    builder.requestUrl(url);
    builder.contentType(contentType);

    AbstractServletRequest result = builder.build();

    result.setAttribute(AbstractServletRequest.ATTR_ORIGINAL_REQUEST, req);
    result.setAttribute(AbstractServletRequest.ATTR_ORIGINAL_CONTEXT, execContext);

    return result;
  }

  private static class ContextLogger implements BarefootServletContextLogger {
    final ExecutionContext context;

    public ContextLogger(ExecutionContext c) {
      context = c;
    }

    @Override
    public void log(String msg) {
      context.getLogger().info(msg);
    }

    @Override
    public void log(String msg, Throwable thr) {
      context.getLogger().info(msg);
    }
  }

  private static final String[] EMPTY_LIST = {};

  private Map<String, String[]> collapse(Map<String, List<String>> in) {
    Map<String, String[]> result = new HashMap<>();

    in.entrySet()
        .forEach(
            (e) -> {
              result.put(e.getKey(), e.getValue().toArray(EMPTY_LIST));
            });

    return result;
  }
}
