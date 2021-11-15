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

package net.sf.barefoot.context.jakarta;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;

/** Dispatcher for include and forward */
class BarefootRequestDispatcher implements RequestDispatcher {
  final BarefootServletContext context;
  final BarefootServletRegistration servlet;
  final String uri, pathInfo, contextPath;

  BarefootRequestDispatcher(BarefootServletContext ctx, BarefootServletRegistration sr, String p) {
    context = ctx;
    servlet = sr;
    uri = p;
    String cp = context.getContextPath();

    if (!cp.isEmpty()) {
      if (p.startsWith(cp)) {
        p = p.substring(cp.length());
      } else {
        cp = "";
      }
    }

    contextPath = cp;
    pathInfo = p;
  }

  @Override
  public void forward(ServletRequest request, ServletResponse response)
      throws ServletException, IOException {
    invoke(DispatcherType.FORWARD, request, response);
  }

  @Override
  public void include(ServletRequest request, ServletResponse response)
      throws ServletException, IOException {
    invoke(DispatcherType.INCLUDE, request, response);
  }

  void invoke(DispatcherType dispatcherType, ServletRequest request, ServletResponse response)
      throws ServletException, IOException {
    servlet.servlet.service(
        new HttpServletRequestWrapper((HttpServletRequest) request) {
          @Override
          public DispatcherType getDispatcherType() {
            return dispatcherType;
          }

          @Override
          public String getPathInfo() {
            return pathInfo;
          }

          @Override
          public String getRequestURI() {
            return uri;
          }

          @Override
          public String getContextPath() {
            return contextPath;
          }
        },
        response);
  }
}
