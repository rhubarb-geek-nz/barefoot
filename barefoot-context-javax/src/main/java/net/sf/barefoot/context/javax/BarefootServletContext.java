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

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.ServletResponse;
import javax.servlet.SessionTrackingMode;
import javax.servlet.annotation.HandlesTypes;
import javax.servlet.descriptor.JspConfigDescriptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionListener;
import net.sf.barefoot.context.*;
import net.sf.barefoot.util.IteratorEnumeration;

/**
 * Context for all servlets. This holds the list of servlets allowing them to have requests
 * dispatched to them
 */
public final class BarefootServletContext extends AbstractServletContext implements ServletContext {
  final Map<String, BarefootServletRegistration> servlets = new HashMap<>();
  final Map<String, BarefootServletRegistration> servletPaths = new HashMap<>();
  final Map<String, BarefootFilterRegistration> filters = new HashMap<>();
  final List<ServletContextListener> contextListeners = new ArrayList<>();
  final List<ServletContextAttributeListener> contextAttributeListeners = new ArrayList<>();
  final List<HttpSessionListener> httpSessionListeners = new ArrayList<>();
  final List<HttpSessionAttributeListener> httpSessionAttributeListeners = new ArrayList<>();
  final List<ServletRequestListener> servletRequestListeners = new ArrayList<>();
  final BarefootSessionCookieConfig sessionCookieConfig = new BarefootSessionCookieConfig();

  /** internal, to be called in constructor */
  private void commonInit() {
    listenerMap.put(ServletContextListener.class, contextListeners);
    listenerMap.put(ServletContextAttributeListener.class, contextAttributeListeners);
    listenerMap.put(HttpSessionListener.class, httpSessionListeners);
    listenerMap.put(HttpSessionAttributeListener.class, httpSessionAttributeListeners);
    listenerMap.put(ServletRequestListener.class, servletRequestListeners);
  }

  /**
   * constructor with root path of context and loader
   *
   * @param contextPath context path for context
   * @param loader loader to use for resources
   */
  public BarefootServletContext(String contextPath, ClassLoader loader) {
    super(contextPath, loader);
    commonInit();
  }

  /**
   * constructor with root path of context with default class loader
   *
   * @param contextPath context path for context´
   */
  public BarefootServletContext(String contextPath) {
    super(contextPath);
    commonInit();
  }

  @Override
  public ServletContext getContext(String string) {
    return contextPath.equals(string) ? this : null;
  }

  @Override
  public RequestDispatcher getRequestDispatcher(String string) {
    BarefootServletRegistration dispatcher = servletPaths.get(string);
    if (dispatcher == null) {
      if (!string.isEmpty()) {
        for (Map.Entry<String, BarefootServletRegistration> entry : servletPaths.entrySet()) {
          String key = entry.getKey();
          if (key != null && !key.isEmpty()) {
            if (string.startsWith(key)) {
              dispatcher = entry.getValue();
              break;
            }
          }
        }
      }
      if (dispatcher == null) {
        dispatcher = servletPaths.get(null);
      }
    }
    return new BarefootRequestDispatcher(this, dispatcher, string);
  }

  @Override
  public RequestDispatcher getNamedDispatcher(String string) {
    return servlets.get(string);
  }

  @Override
  public Servlet getServlet(String string) throws ServletException {
    BarefootServletRegistration s = servlets.get(string);
    return s == null ? null : s.servlet;
  }

  @Override
  public Enumeration<Servlet> getServlets() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Enumeration<String> getServletNames() {
    return new IteratorEnumeration<>(servlets.keySet().iterator());
  }

  @Override
  public BarefootServletRegistration addServlet(String string, String type) {
    try {
      return addServlet(string, (Class<Servlet>) resourceLoader.loadClass(type));
    } catch (ClassNotFoundException ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public BarefootServletRegistration addServlet(String name, Servlet srvlt) {
    if (state >= STATE_RUN) throw new IllegalStateException();

    if (servlets.containsKey(name)) {
      throw new IllegalStateException("Already contains servlet " + name);
    }

    BarefootServletRegistration reg = new BarefootServletRegistration(this, name, srvlt);
    servlets.put(name, reg);

    servletStartupTasks.add(
        () -> {
          for (String m : reg.getMappings()) {
            servletPaths.put(m, reg);
            if ("/".equals(m)) {
              servletPaths.put("", reg);
              servletPaths.put(null, reg);
            }
          }
          try {
            reg.onStartup();
          } catch (ServletException ex) {
            throw new RuntimeException(ex);
          }
        });

    return reg;
  }

  @Override
  public BarefootServletRegistration addServlet(String string, Class<? extends Servlet> type) {
    try {
      return addServlet(string, type.getConstructor().newInstance());
    } catch (SecurityException
        | NoSuchMethodException
        | InstantiationException
        | IllegalAccessException ex) {
      throw new RuntimeException(ex);
    } catch (InvocationTargetException ex) {
      throw new RuntimeException(ex.getTargetException());
    }
  }

  @Override
  public <T extends Servlet> T createServlet(Class<T> type) throws ServletException {
    try {
      return type.getConstructor().newInstance();
    } catch (SecurityException
        | NoSuchMethodException
        | InstantiationException
        | IllegalAccessException ex) {
      throw new RuntimeException(ex);
    } catch (InvocationTargetException ex) {
      throw new RuntimeException(ex.getTargetException());
    }
  }

  @Override
  public ServletRegistration getServletRegistration(String string) {
    return servlets.get(string);
  }

  @Override
  public Map<String, ? extends ServletRegistration> getServletRegistrations() {
    return servlets;
  }

  @Override
  public BarefootFilterRegistration addFilter(String string, String type) {
    try {
      return addFilter(string, (Class<Filter>) resourceLoader.loadClass(type));
    } catch (ClassNotFoundException ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public BarefootFilterRegistration addFilter(String name, Filter filter) {
    if (state >= STATE_RUN) throw new IllegalStateException();

    if (filters.containsKey(name)) {
      throw new IllegalStateException("Filter already registered " + name);
    }

    BarefootFilterRegistration fr = new BarefootFilterRegistration(this, name, filter);
    filters.put(name, fr);

    filterStartupTasks.add(
        () -> {
          fr.chain = rootFilter;
          rootFilter = fr;
          try {
            fr.onStartup();
          } catch (ServletException ex) {
            throw new RuntimeException(ex);
          }
        });

    return fr;
  }

  @Override
  public BarefootFilterRegistration addFilter(String string, Class<? extends Filter> type) {
    try {
      return addFilter(string, type.getConstructor().newInstance());
    } catch (SecurityException
        | NoSuchMethodException
        | InstantiationException
        | IllegalAccessException ex) {
      throw new RuntimeException(ex);
    } catch (InvocationTargetException ex) {
      throw new RuntimeException(ex.getTargetException());
    }
  }

  @Override
  public <T extends Filter> T createFilter(Class<T> type) throws ServletException {
    try {
      return type.getConstructor().newInstance();
    } catch (SecurityException
        | NoSuchMethodException
        | InstantiationException
        | IllegalAccessException ex) {
      throw new RuntimeException(ex);
    } catch (InvocationTargetException ex) {
      throw new RuntimeException(ex.getTargetException());
    }
  }

  @Override
  public FilterRegistration getFilterRegistration(String name) {
    return filters.get(name);
  }

  @Override
  public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
    return filters;
  }

  @Override
  public BarefootSessionCookieConfig getSessionCookieConfig() {
    return sessionCookieConfig;
  }

  @Override
  public void setSessionTrackingModes(Set<SessionTrackingMode> set) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public <T extends EventListener> T createListener(Class<T> type) throws ServletException {
    try {
      return type.getConstructor().newInstance();
    } catch (SecurityException
        | NoSuchMethodException
        | InstantiationException
        | IllegalAccessException ex) {
      throw new RuntimeException(ex);
    } catch (InvocationTargetException ex) {
      throw new RuntimeException(ex.getTargetException());
    }
  }

  @Override
  public JspConfigDescriptor getJspConfigDescriptor() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  /**
   * Activates listeners, servlets and filters
   *
   * @throws ServletException may throw exception if startup of registered servlets fails
   */
  @Override
  public void onStartup() throws ServletException {
    try {
      ServiceLoader<ServletContainerInitializer> serviceLoader =
          ServiceLoader.load(ServletContainerInitializer.class, resourceLoader);
      boolean isEmpty = true;

      for (ServletContainerInitializer init : serviceLoader) {
        isEmpty = false;
        Set<Class<?>> setClasses = new HashSet<>();
        Annotation[] annotations = init.getClass().getAnnotations();

        for (Annotation annotation : annotations) {
          if (annotation instanceof HandlesTypes) {
            HandlesTypes handlesTypes = (HandlesTypes) annotation;

            for (Class<?> type : handlesTypes.value()) {
              ServiceLoader<?> classLoader = ServiceLoader.load(type, resourceLoader);
              classLoader.stream().forEach(provider -> setClasses.add(provider.type()));
            }
          }
        }

        log("Barefoot, initializing " + init.getClass().getCanonicalName());

        init.onStartup(setClasses, this);
      }

      if (isEmpty) {
        log("Barefoot, no " + ServletContainerInitializer.class.getCanonicalName() + " found.");
      }

      super.onStartup();
    } catch (RuntimeException ex) {
      Throwable cause = ex.getCause();
      if (cause instanceof ServletException) {
        throw (ServletException) cause;
      }
      throw ex;
    } catch (ServletException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new ServletException(ex);
    }
  }

  /** the end of the chain that calls the servlets */
  protected FilterChain rootFilter =
      (ServletRequest req, ServletResponse resp) -> {
        HttpServletRequest http = (HttpServletRequest) req;
        String servletPath = http.getServletPath();
        BarefootServletRegistration reg = servletPaths.get(servletPath);
        if (reg == null) {
          ((HttpServletResponse) resp).sendError(HttpServletResponse.SC_NOT_FOUND);
        } else {
          reg.servlet.service(req, resp);
        }
      };

  @Override
  public void dispatch(AbstractServletRequest req, AbstractServletResponse resp)
      throws IOException, BarefootServletException {
    try {
      currentRequest.set(req);
      if (!servletRequestListeners.isEmpty()) {
        ServletRequestEvent sce = new ServletRequestEvent(this, (ServletRequest) req);
        for (ServletRequestListener e : servletRequestListeners) {
          e.requestInitialized(sce);
        }
      }
      try {
        rootFilter.doFilter((ServletRequest) req, (ServletResponse) resp);
      } finally {
        if (!servletRequestListeners.isEmpty()) {
          int i = servletRequestListeners.size();
          ServletRequestEvent sce = new ServletRequestEvent(this, (ServletRequest) req);
          while (0 != i--) {
            servletRequestListeners.get(i).requestDestroyed(sce);
          }
        }
      }
    } catch (ServletException ex) {
      throw new BarefootServletException(ex);
    } finally {
      currentRequest.set(null);
    }
  }

  @Override
  public BarefootServletRequest.Builder getServletRequestBuilder() {
    return BarefootServletRequest.builder().servletContext(this);
  }

  @Override
  public BarefootCookieCutter getCookieCutter() {
    return BarefootCookieCutter.SINGLETON_INSTANCE;
  }

  @Override
  public ServletRegistration.Dynamic addJspFile(String string, String string1) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public <T extends EventListener> void addListener(T t) {
    super.addListener(t);

    if (t instanceof ServletContextListener) {
      ServletContextListener listener = (ServletContextListener) t;
      listenerStartupTasks.add(
          () -> {
            ServletContextEvent sce = new ServletContextEvent(this);
            listener.contextInitialized(sce);
          });
    }
  }

  @Override
  public void setAttribute(String name, Object value) {
    if (contextAttributeListeners.isEmpty()) {
      attributes.put(name, value);
    } else {
      boolean replace = attributes.containsKey(name);
      attributes.put(name, value);
      ServletContextAttributeEvent scae = new ServletContextAttributeEvent(this, name, value);
      contextAttributeListeners.forEach(
          (e) -> {
            if (replace) {
              e.attributeReplaced(scae);
            } else {
              e.attributeAdded(scae);
            }
          });
    }
  }

  @Override
  public void removeAttribute(String name) {
    if (contextAttributeListeners.isEmpty()) {
      attributes.remove(name);
    } else {
      if (attributes.containsKey(name)) {
        Object value = attributes.get(name);
        attributes.remove(name);
        ServletContextAttributeEvent scae = new ServletContextAttributeEvent(this, name, value);

        contextAttributeListeners.forEach(
            (e) -> {
              e.attributeRemoved(scae);
            });
      }
    }
  }

  @Override
  public void destroy() throws ServletException {
    servlets
        .entrySet()
        .forEach(
            (e) -> {
              e.getValue().servlet.destroy();
            });

    filters
        .entrySet()
        .forEach(
            (e) -> {
              e.getValue().filter.destroy();
            });

    if (!contextListeners.isEmpty()) {
      ServletContextEvent sce = new ServletContextEvent(this);
      contextListeners.forEach(
          (e) -> {
            e.contextDestroyed(sce);
          });
    }

    try {
      servlets.clear();
      servletPaths.clear();
      filters.clear();
      super.destroy();
    } catch (ServletException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }
}
