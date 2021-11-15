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
import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.ServletSecurityElement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.Set;
import net.sf.barefoot.context.AbstractServletRegistration;

/** Concrete servlet registation */
public final class BarefootServletRegistration extends AbstractServletRegistration
    implements ServletRegistration.Dynamic, ServletConfig, RequestDispatcher {
  final Servlet servlet;
  final ServletContext servletContext;
  MultipartConfigElement multipartConfigElement;

  /** creates a holder for the servlet registration information */
  BarefootServletRegistration(ServletContext ctx, String string, Servlet srvlt) {
    super(string);
    servletContext = ctx;
    servlet = srvlt;
  }

  @Override
  public String getClassName() {
    return servlet.getClass().getCanonicalName();
  }

  @Override
  public ServletContext getServletContext() {
    return servletContext;
  }

  @Override
  public void setMultipartConfig(MultipartConfigElement multipartConfigElement) {
    this.multipartConfigElement = multipartConfigElement;
  }

  @Override
  public Set<String> setServletSecurity(ServletSecurityElement arg0) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  /** calls the servlet's initialisation method */
  void onStartup() throws ServletException {
    if (!isInitialised) {
      servlet.init(this);
      isInitialised = true;
    }
  }

  @Override
  public void forward(ServletRequest sr, ServletResponse sr1) throws ServletException, IOException {
    invoke(DispatcherType.FORWARD, sr, sr1);
  }

  @Override
  public void include(ServletRequest sr, ServletResponse sr1) throws ServletException, IOException {
    invoke(DispatcherType.INCLUDE, sr, sr1);
  }

  void invoke(DispatcherType dispatcherType, ServletRequest sr, ServletResponse sr1)
      throws ServletException, IOException {
    servlet.service(
        new HttpServletRequestWrapper((HttpServletRequest) sr) {
          @Override
          public DispatcherType getDispatcherType() {
            return dispatcherType;
          }
        },
        sr1);
  }
}
