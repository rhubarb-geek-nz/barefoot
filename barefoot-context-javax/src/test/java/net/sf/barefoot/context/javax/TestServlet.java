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

package net.sf.barefoot.context.javax;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.sf.barefoot.testtool.HttpServletRequestState;
import net.sf.barefoot.testtool.javax.HttpServletRequestStateFactory;

/** servlet returning request state */
public class TestServlet extends HttpServlet {
  ObjectMapper mapper = new ObjectMapper();

  @Override
  protected void service(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String redirect = request.getHeader("barefoot-redirect");

    if (redirect != null) {
      response.sendRedirect(redirect);
    } else {

      String barefootSession = request.getHeader("barefoot-session");

      if (barefootSession != null) {
        switch (barefootSession) {
          case "get-true":
            {
              HttpSession session = request.getSession(true);
              response.addHeader("barefoot-session", session.toString());
              response.addHeader("barefoot-session-id", session.getId());
              response.addHeader("barefoot-session-isNew", Boolean.toString(session.isNew()));
              response.addHeader(
                  "barefoot-session-creation", Long.toString(session.getCreationTime()));
              response.addHeader(
                  "barefoot-session-access", Long.toString(session.getLastAccessedTime()));
            }
            break;
          case "get-false":
            {
              HttpSession session = request.getSession(false);
              if (session != null) {
                response.addHeader("barefoot-session", session.toString());
              }
            }
            break;
        }
      }

      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      try (PrintWriter out = response.getWriter()) {
        HttpServletRequestState state =
            new HttpServletRequestStateFactory().create(request, response);
        mapper.writeValue(out, state);
      }
    }
  }
}
