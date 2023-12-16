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

package net.sf.barefoot.testtool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * captures state of HTTP request
 *
 * @param <T> cookie concrete type
 */
public class HttpServletRequestState<T> {
  final String method,
      uri,
      query,
      path,
      trans,
      contentType,
      encoding,
      authType,
      content,
      servletPath,
      url,
      serverName,
      contextPath,
      dispatcherType;
  final int contentLength, status, serverPort;
  final long contentLengthLong;
  final Map<String, String> header;
  final Map<String, List<String>> headers;
  final Map<String, String[]> params;
  final List<T> cookies;
  final Boolean isSecure;

  public String getMethod() {
    return method;
  }

  public String getRequestURI() {
    return uri;
  }

  public String getRequestURL() {
    return url;
  }

  public String getQueryString() {
    return query;
  }

  public String getPathInfo() {
    return path;
  }

  public String getPathTranslated() {
    return trans;
  }

  public String getContentType() {
    return contentType;
  }

  public String getContextPath() {
    return contextPath;
  }

  public String getCharacterEncoding() {
    return encoding;
  }

  public String getAuthType() {
    return authType;
  }

  public Map<String, String[]> getParameterMap() {
    return params;
  }

  public Map<String, List<String>> getHeaders() {
    return headers;
  }

  public Map<String, String> getHeader() {
    return header;
  }

  public int getContentLength() {
    return contentLength;
  }

  public long getContentLengthLong() {
    return contentLengthLong;
  }

  public int getStatus() {
    return status;
  }

  public String getContent() {
    return content;
  }

  public String getServletPath() {
    return servletPath;
  }

  public String getServerName() {
    return serverName;
  }

  public int getServerPort() {
    return serverPort;
  }

  public String getDispatcherType() {
    return dispatcherType;
  }

  public Boolean getIsSecure() {
    return isSecure;
  }

  public static class Builder<T> {
    public String method,
        uri,
        url,
        query,
        path,
        trans,
        contentType,
        encoding,
        authType,
        content,
        servletPath,
        serverName,
        contextPath,
        dispatcherType;
    public int contentLength, status, serverPort;
    public long contentLengthLong;
    public Map<String, String> header = new HashMap<>();
    public Map<String, List<String>> headers = new HashMap<>();
    public Map<String, String[]> params = new HashMap<>();
    public List<T> cookies = new ArrayList<>();
    public Boolean isSecure;

    public HttpServletRequestState<T> build() {
      return new HttpServletRequestState<T>(this);
    }
  }

  HttpServletRequestState(Builder obj) {
    method = obj.method;
    uri = obj.uri;
    query = obj.query;
    path = obj.path;
    trans = obj.trans;
    contentType = obj.contentType;
    encoding = obj.encoding;
    authType = obj.authType;
    content = obj.content;
    servletPath = obj.servletPath;
    contentLength = obj.contentLength;
    contentLengthLong = obj.contentLengthLong;
    status = obj.status;
    header = obj.header;
    headers = obj.headers;
    params = obj.params;
    cookies = obj.cookies;
    url = obj.url;
    serverName = obj.serverName;
    serverPort = obj.serverPort;
    contextPath = obj.contextPath;
    dispatcherType = obj.dispatcherType;
    isSecure = obj.isSecure;
  }
}
