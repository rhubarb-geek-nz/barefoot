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

package net.sf.barefoot.annotation.javax;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.EventListener;
import java.util.Set;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.Servlet;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.HandlesTypes;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

/** Initializer for annotations. */
@HandlesTypes({Servlet.class, EventListener.class, Filter.class})
public final class BarefootAnnotationInitializer implements ServletContainerInitializer {

  @Override
  public void onStartup(Set<Class<?>> classSet, ServletContext servletContext)
      throws ServletException {

    for (Class<?> cls : classSet) {
      for (Annotation annotation : cls.getAnnotations()) {
        if (annotation instanceof WebServlet) {
          WebServlet ws = (WebServlet) annotation;
          ServletRegistration.Dynamic servlet =
              servletContext.addServlet(ws.name(), (Class<Servlet>) cls);
          servlet.setLoadOnStartup(ws.loadOnStartup());
          WebInitParam[] params = ws.initParams();
          if (params != null) {
            for (WebInitParam param : params) {
              servlet.setInitParameter(param.name(), param.value());
            }
          }
          String[] urls = ws.urlPatterns();
          if (urls != null) {
            servlet.addMapping(urls);
          }
        }

        if (annotation instanceof WebListener) {
          servletContext.addListener((Class<EventListener>) cls);
        }

        if (annotation instanceof WebFilter) {
          WebFilter wf = (WebFilter) annotation;
          FilterRegistration.Dynamic filter =
              servletContext.addFilter(wf.filterName(), (Class<? extends Filter>) cls);
          WebInitParam[] params = wf.initParams();
          if (params != null) {
            for (WebInitParam param : params) {
              filter.setInitParameter(param.name(), param.value());
            }
          }
          String[] urls = wf.urlPatterns();
          if (urls != null) {
            DispatcherType[] dts = wf.dispatcherTypes();
            EnumSet<DispatcherType> es = EnumSet.copyOf(Arrays.asList(dts));
            filter.addMappingForUrlPatterns(es, true, urls);
          }
        }
      }
    }
  }
}
