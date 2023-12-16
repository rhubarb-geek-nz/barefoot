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

package net.sf.barefoot.example.jakarta;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import net.sf.barefoot.testtool.HttpServletRequestState;
import net.sf.barefoot.testtool.jakarta.HttpServletRequestStateFactory;

/** very simple test servlet */
public abstract class AbstractExampleServlet extends HttpServlet {
  HttpServletRequestStateFactory factory = new HttpServletRequestStateFactory();

  private void process(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("application/json");
    HttpServletRequestState<Cookie> state = factory.create(req, resp);
    String val = writeValueAsString(state);
    try (PrintWriter out = resp.getWriter()) {
      out.write(val);
    }
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    process(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    process(req, resp);
  }

  protected abstract String writeValueAsString(Object value) throws IOException;
}
