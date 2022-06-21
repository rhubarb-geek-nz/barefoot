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

package net.sf.barefoot.testtool.jakarta;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import net.sf.barefoot.testtool.HttpServletRequestState;

/** captures HTTP request state */
public class HttpServletRequestStateFactory {

  public HttpServletRequestState<Cookie> create(
      HttpServletRequest request, HttpServletResponse response) throws IOException {
    HttpServletRequestState.Builder<Cookie> state = new HttpServletRequestState.Builder<>();

    state.status = response.getStatus();
    state.method = request.getMethod();
    state.uri = request.getRequestURI();
    state.query = request.getQueryString();
    state.path = request.getPathInfo();
    //      state.trans=request.getPathTranslated();
    state.contentLength = request.getContentLength();
    state.contentLengthLong = request.getContentLengthLong();
    state.contentType = request.getContentType();
    state.encoding = request.getCharacterEncoding();
    state.authType = request.getAuthType();
    state.servletPath = request.getServletPath();
    state.serverPort = request.getServerPort();
    state.serverName = request.getServerName();
    state.contextPath = request.getContextPath();
    state.isSecure = request.isSecure();

    DispatcherType dispatcherType = request.getDispatcherType();
    if (dispatcherType != null) {
      state.dispatcherType = dispatcherType.name();
    }

    StringBuffer url = request.getRequestURL();
    if (url != null) {
      state.url = url.toString();
    }

    request.getParameterMap().entrySet().stream()
        .forEach(e -> state.params.put(e.getKey(), e.getValue()));

    Enumeration<String> names = request.getHeaderNames();

    // Jetty leaves the case of the items as they were
    // Tomcat converts the case of header names to lower case

    while (names.hasMoreElements()) {
      String name = names.nextElement();
      String nameLower = name.toLowerCase();
      state.header.put(nameLower, request.getHeader(name));
      List<String> list = new ArrayList<>();
      Enumeration<String> headers = request.getHeaders(name);
      while (headers.hasMoreElements()) {
        list.add(headers.nextElement());
      }
      state.headers.put(nameLower, list);
    }

    //
    // request.getTrailerFields().entrySet().stream().forEach(e->state.trailer.put(e.getKey(),e.getValue()));

    Cookie[] cookies = request.getCookies();

    if (cookies != null) {
      for (Cookie c : cookies) {
        state.cookies.add(c);
      }
    }

    if (state.contentLength > 0) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try (ServletInputStream is = request.getInputStream()) {
        byte[] b = new byte[512];
        int l = state.contentLength;

        while (l > 0) {
          int i = is.read(b, 0, (l < b.length) ? l : b.length);
          if (i < 1) break;
          baos.write(b, 0, i);
          l -= i;
        }
      }
      state.content = new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }

    return state.build();
  }
}
