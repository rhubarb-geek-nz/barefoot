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

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.barefoot.context.AbstractServletContext;
import net.sf.barefoot.context.AbstractServletRequest;
import net.sf.barefoot.context.AbstractServletResponse;
import net.sf.barefoot.context.BarefootServletException;

/** dispatcher for Google messages */
public class BarefootGoogleFunction implements HttpFunction {
  final AbstractServletContext servletContext;
  final HttpServletRequestFactory requestFactory;
  static final String SET_COOKIE = "SetCookie";

  public BarefootGoogleFunction(AbstractServletContext ctx) {
    servletContext = ctx;
    requestFactory = new HttpServletRequestFactory();
  }

  @Override
  public void service(HttpRequest request, HttpResponse response) {
    try {
      AbstractServletRequest sreq = requestFactory.create(servletContext, request, response);
      AbstractServletResponse resp =
          sreq.getServletResponseBuilder()
              .outputStream(
                  () -> {
                    try {
                      return response.getOutputStream();
                    } catch (IOException ex) {
                      throw new RuntimeException(ex);
                    }
                  })
              .writer(
                  () -> {
                    try {
                      return new PrintWriter(response.getWriter());
                    } catch (IOException ex) {
                      throw new RuntimeException(ex);
                    }
                  })
              .build();

      servletContext.dispatch(sreq, resp);
      response.setStatusCode(resp.getStatus());
      String contentType = resp.getContentType();

      List<String> cookies = resp.getSetCookieHeaders();

      if (cookies != null && !cookies.isEmpty()) {
        cookies.forEach(
            (e) -> {
              response.appendHeader(SET_COOKIE, e);
            });
      }

      Collection<String> headers = resp.getHeaderNames();

      if (headers != null && !headers.isEmpty()) {
        for (String header : headers) {
          if ("Content-Type".equalsIgnoreCase(header)) {
            contentType = resp.getHeader(header);
          } else {
            Collection<String> values = resp.getHeaders(header);
            if (values != null && !values.isEmpty()) {
              for (String value : values) {
                response.appendHeader(header, value);
              }
            }
          }
        }
      }

      response.setContentType(contentType);

    } catch (RuntimeException | IOException | BarefootServletException ex) {
      Logger.getGlobal().log(Level.INFO, ex.getMessage(), ex);
      response.setStatusCode(500);
    }
  }
}
