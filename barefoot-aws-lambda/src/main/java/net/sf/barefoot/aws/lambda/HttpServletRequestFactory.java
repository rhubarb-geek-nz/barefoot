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

package net.sf.barefoot.aws.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.barefoot.context.AbstractServletContext;
import net.sf.barefoot.context.AbstractServletRequest;
import net.sf.barefoot.context.BarefootContentType;
import net.sf.barefoot.context.BarefootPreprocessor;
import net.sf.barefoot.context.BarefootServletContextLogger;
import net.sf.barefoot.util.ReaderInputStream;

/** create an HTTP request from the input from the gateway */
public class HttpServletRequestFactory {
  static final String UTF8_NAME = StandardCharsets.UTF_8.name(),
      CONTENT_TYPE = "content-type",
      HOST = "host",
      X_FORWARD_PORT = "x-forwarded-port",
      X_FORWARD_PROTO = "x-forwarded-proto";
  private final BarefootPreprocessor preProcessor = new BarefootPreprocessor();

  /* create request from AWS proxy request */
  public AbstractServletRequest create(
      AbstractServletContext servletContext, Map<String, Object> map, Context ctxt)
      throws IOException {
    AbstractServletRequest.Builder builder = servletContext.getServletRequestBuilder();
    String body = null;
    boolean isBase64Encoded = false;
    String rawPath = null, rawQueryString = null, path = null, resource = null;
    String version = null;
    String domainName = null, requestPath = null;
    String protocol = "HTTP";
    Map<String, List<String>> multiValueQueryStringParameters = null, multiValueHeaders = null;
    Map<String, String> headers = null;
    List<String> cookies = null;
    String contentType = null;
    String serverName;
    String host = null;
    int serverPort = -1;
    String fwdProto = null;
    boolean isSecure = true;
    String contextPath;

    for (Map.Entry<String, Object> e : map.entrySet()) {
      switch (e.getKey()) {
        case "resource":
          resource = toStringWithDefault(e.getValue(), resource);
          break;
        case "path":
          path = toStringWithDefault(e.getValue(), path);
          break;
        case "httpMethod":
          builder.method(e.getValue().toString());
          break;
        case "version":
          version = e.getValue().toString();
          break;
        case "isBase64Encoded":
          isBase64Encoded = toBooleanWithDefault(e.getValue(), isBase64Encoded);
          break;
        case "body":
          body = toStringWithDefault(e.getValue(), body);
          break;
        case "rawQueryString":
          rawQueryString = toStringWithDefault(e.getValue(), rawQueryString);
          break;
        case "rawPath":
          rawPath = toStringWithDefault(e.getValue(), rawPath);
          break;
        case "multiValueQueryStringParameters":
          multiValueQueryStringParameters = (Map<String, List<String>>) e.getValue();
          break;
        case "multiValueHeaders":
          multiValueHeaders = (Map<String, List<String>>) e.getValue();
          break;
        case "headers":
          headers = (Map<String, String>) e.getValue();
          break;
        case "cookies":
          cookies = (List<String>) e.getValue();
          break;
        case "requestContext":
          {
            Map<String, Object> requestContext = (Map<String, Object>) e.getValue();
            for (Map.Entry<String, Object> f : requestContext.entrySet()) {
              switch (f.getKey()) {
                case "http":
                  {
                    Map<String, Object> http = (Map<String, Object>) f.getValue();
                    for (Map.Entry<String, Object> g : http.entrySet()) {
                      switch (g.getKey()) {
                        case "method":
                          builder.method(g.getValue().toString());
                          break;
                        case "protocol":
                          protocol = toStringWithDefault(g.getValue(), protocol);
                          break;
                      }
                    }
                  }
                  break;
                case "domainName":
                  domainName = toStringWithDefault(f.getValue(), domainName);
                  break;
                case "protocol":
                  protocol = toStringWithDefault(f.getValue(), protocol);
                  break;
                case "path":
                  requestPath = toStringWithDefault(f.getValue(), requestPath);
                  break;
              }
            }
          }
          break;
      }
    }

    builder.protocol(protocol);

    String rp = rawPath == null ? path == null ? resource : path : rawPath;

    if (requestPath != null) {
      builder.requestUri(requestPath);
      if (requestPath.length() >= rp.length() && requestPath.endsWith(rp)) {
        int contextPathLength = requestPath.length() - rp.length();
        contextPath = requestPath.substring(0, contextPathLength);
      } else {
        contextPath = "";
      }
    } else {
      contextPath = "";
      builder.requestUri(rp);
    }

    if (rawQueryString != null) {
      builder.queryString(rawQueryString);
      if (multiValueQueryStringParameters == null) {
        multiValueQueryStringParameters = parseQueryString(rawQueryString);
      }
    } else {
      if (multiValueQueryStringParameters != null && !multiValueQueryStringParameters.isEmpty()) {
        builder.queryString(writeQueryString(multiValueQueryStringParameters));
      }
    }

    if (cookies != null && !cookies.isEmpty()) {
      builder.cookies(cookies);
    }

    Map<String, List<String>> lowerCaseHeaders = new HashMap<>();

    if (headers != null) {
      for (Map.Entry<String, String> e : headers.entrySet()) {
        String key = e.getKey().toLowerCase();
        String value = e.getValue();
        List list = new ArrayList();
        list.add(value);
        lowerCaseHeaders.put(key, list);
        switch (key) {
          case HOST:
            host = value;
            break;
          case X_FORWARD_PORT:
            serverPort = Integer.parseInt(value);
            break;
          case X_FORWARD_PROTO:
            fwdProto = value;
            break;
          case CONTENT_TYPE:
            contentType = value;
            break;
        }
      }
    }

    builder.headers(lowerCaseHeaders);
    builder.contentType(contentType);

    if (body != null) {
      if (preProcessor.isApplicationFormUrlEncoded(contentType)) {
        String cs = BarefootContentType.getCharsetFromContentType(contentType);
        Charset charSet = cs == null ? StandardCharsets.UTF_8 : Charset.forName(cs);

        if (multiValueQueryStringParameters == null) {
          multiValueQueryStringParameters = new HashMap<>();
        } else {
          multiValueQueryStringParameters = deepCopy(multiValueQueryStringParameters);
        }

        preProcessor.processApplicationFormUrlEncoded(
            isBase64Encoded
                ? new InputStreamReader(base64InputStream(body))
                : new StringReader(body),
            multiValueQueryStringParameters,
            charSet);
      } else {
        final String finalBody = body;

        if (isBase64Encoded) {
          builder.inputStream(() -> base64InputStream(finalBody));
        } else {
          builder.reader(() -> new StringReader(finalBody));
        }
      }
    }

    if (multiValueQueryStringParameters == null) {
      builder.parameters(new HashMap<>());
    } else {
      builder.parameters(collapse(multiValueQueryStringParameters));
    }

    if (host != null) {
      String[] hp = host.split(":");
      serverName = hp[0];
      if (hp.length > 1) serverPort = Integer.parseInt(hp[1]);
      isSecure = !"localhost".equals(serverName);
    } else {
      serverName = domainName;
    }

    StringBuilder sb = new StringBuilder();
    if (fwdProto == null) {
      fwdProto = isSecure ? "https" : "http";
    }

    sb.append(fwdProto);
    sb.append("://");
    sb.append(serverName);

    if (serverPort != -1) {
      switch (fwdProto) {
        case "http":
          if (serverPort == 80) {
            serverPort = -1;
          }
          break;
        case "https":
          if (serverPort == 443) {
            serverPort = -1;
          }
          break;
      }
      if (serverPort != -1) {
        sb.append(":");
        sb.append(Integer.toString(serverPort));
      }
    }

    if (requestPath != null) {
      sb.append(requestPath);
    } else {
      if (rawPath != null) {
        sb.append(rawPath);
      } else {
        if (path != null) {
          sb.append(path);
        }
      }
    }

    builder.contextPath(contextPath);
    builder.requestUrl(sb.toString());
    builder.serverPort(serverPort);
    builder.serverName(serverName);
    builder.isSecure(isSecure);
    builder.logger(new ContextLogger(ctxt));

    AbstractServletRequest result = builder.build();

    result.setAttribute(AbstractServletRequest.ATTR_ORIGINAL_REQUEST, map);
    result.setAttribute(AbstractServletRequest.ATTR_ORIGINAL_CONTEXT, ctxt);

    return result;
  }

  private boolean toBooleanWithDefault(Object o, boolean b) {
    return o == null ? b : o instanceof Boolean ? (Boolean) o : Boolean.parseBoolean(o.toString());
  }

  private String toStringWithDefault(Object o, String s) {
    return o == null ? s : o.toString();
  }

  Map<String, List<String>> parseQueryString(String s) throws UnsupportedEncodingException {
    Map<String, List<String>> result = new HashMap<>();
    ;

    if (s != null && !s.isEmpty()) {
      for (String nv : s.split("&")) {
        String[] nvp = nv.split("=");
        String n = URLDecoder.decode(nvp[0], UTF8_NAME);
        String v = nvp.length > 1 ? URLDecoder.decode(nvp[1], UTF8_NAME) : null;

        List<String> list = result.get(n);

        if (list == null) {
          list = new ArrayList();
          result.put(n, list);
        }
        list.add(v);
      }
    }

    return result;
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

  private String writeQueryString(Map<String, List<String>> multiValueQueryStringParameters)
      throws UnsupportedEncodingException {
    StringBuilder sb = new StringBuilder();

    for (Map.Entry<String, List<String>> e : multiValueQueryStringParameters.entrySet()) {
      String name = e.getKey();
      for (String value : e.getValue()) {
        if (sb.length() > 0) sb.append('&');
        sb.append(URLEncoder.encode(name, UTF8_NAME));
        if (value != null) {
          sb.append('=');
          if (!value.isEmpty()) {
            sb.append(URLEncoder.encode(value, UTF8_NAME));
          }
        }
      }
    }

    return sb.toString();
  }

  private Map<String, List<String>> deepCopy(Map<String, List<String>> map) {
    Map<String, List<String>> result = new HashMap<>();
    map.entrySet()
        .forEach(
            (e) -> {
              result.put(e.getKey(), new ArrayList<>(e.getValue()));
            });
    return result;
  }

  private static class ContextLogger implements BarefootServletContextLogger {
    final Context context;

    public ContextLogger(Context cntxt) {
      context = cntxt;
    }

    @Override
    public void log(String msg) {
      context.getLogger().log(msg);
    }

    @Override
    public void log(String msg, Throwable thr) {
      context.getLogger().log(msg);
    }
  }

  private static InputStream base64InputStream(String body) {
    return Base64.getDecoder()
        .wrap(new ReaderInputStream(new StringReader(body), StandardCharsets.UTF_8));
  }
}
