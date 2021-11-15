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

package net.sf.barefoot.example.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/** demonstration of filter working with spring */
public class BarefootExampleFilter implements Filter {

  @Override
  public void init(FilterConfig fc) throws ServletException {}

  @Override
  public void doFilter(ServletRequest sr, ServletResponse sr1, FilterChain fc)
      throws IOException, ServletException {

    HttpServletResponse resp = (HttpServletResponse) sr1;
    long before = System.currentTimeMillis();
    resp.addHeader("barefoot-example-filter-before", Long.toString(before));

    fc.doFilter(sr, sr1);
    resp.addHeader(
        "barefoot-example-filter-after", Long.toString(System.currentTimeMillis() - before));
  }

  @Override
  public void destroy() {}
}
