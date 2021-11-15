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
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;
import java.util.EnumSet;
import net.sf.barefoot.context.AbstractFilterRegistration;

/** Filter registration object */
public final class BarefootFilterRegistration extends AbstractFilterRegistration
    implements FilterRegistration.Dynamic, FilterConfig, FilterChain {
  final ServletContext servletContext;
  final Filter filter;
  FilterChain chain;

  /** registration for a specific filter for a given servlet */
  BarefootFilterRegistration(ServletContext c, String string, Filter f) {
    super(string);
    servletContext = c;
    filter = f;
  }

  @Override
  public void addMappingForServletNames(
      EnumSet<DispatcherType> es, boolean bln, String... strings) {}

  @Override
  public void addMappingForUrlPatterns(
      EnumSet<DispatcherType> es, boolean bln, String... strings) {}

  @Override
  public String getClassName() {
    return filter.getClass().getCanonicalName();
  }

  void onStartup() throws ServletException {
    if (!isInitialised) {
      filter.init(this);
      isInitialised = true;
    }
  }

  @Override
  public ServletContext getServletContext() {
    return servletContext;
  }

  @Override
  public void doFilter(ServletRequest sr, ServletResponse sr1)
      throws IOException, ServletException {
    filter.doFilter(sr, sr1, chain);
  }
}
