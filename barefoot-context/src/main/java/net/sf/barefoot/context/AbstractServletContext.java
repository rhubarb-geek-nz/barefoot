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

package net.sf.barefoot.context;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.barefoot.util.IteratorEnumeration;

/** Base class for servlet context */
public abstract class AbstractServletContext {
  static final Level LOG_LEVEL = Level.INFO;
  protected final Map<String, Object> attributes = new HashMap<>();
  protected final Map<String, String> initParameters = new HashMap<>();
  protected final String contextPath;
  protected final ThreadLocal<AbstractServletRequest> currentRequest = new ThreadLocal<>();
  protected final Map<Class<? extends EventListener>, List<? extends EventListener>> listenerMap =
      new HashMap<>();
  protected int state = STATE_NEW;
  protected final List<Runnable> listenerStartupTasks = new ArrayList<>(),
      filterStartupTasks = new ArrayList<>(),
      servletStartupTasks = new ArrayList<>();
  protected final List[] startupList = {
    listenerStartupTasks, filterStartupTasks, servletStartupTasks
  };
  protected int sessionTimeout;
  protected String requestCharacterEncoding, responseCharacterEncoding;
  protected static final int
      /** context is uninitialized */
      STATE_NEW = 0,
      /** context is being initialized */
      STATE_INIT = 1,
      /** context is able to process requests */
      STATE_RUN = 2,
      /** context is shutting down */
      STATE_SHUTDOWN = 3;
  protected final ClassLoader resourceLoader;

  /** Validate state on construction */
  private void commonInit() {
    if (contextPath.equals("/")) {
      throw new java.lang.IllegalArgumentException("use empty string instead as contextPath");
    }
  }

  /**
   * Constructor requires a context and class loader.
   *
   * @param cp servlet context path
   * @param loader used for resource loading
   */
  protected AbstractServletContext(String cp, ClassLoader loader) {
    resourceLoader = loader == null ? getClass().getClassLoader() : loader;
    contextPath = cp;
    commonInit();
  }

  /**
   * Constructor requires a context.
   *
   * @param cp servlet context path
   */
  protected AbstractServletContext(String cp) {
    resourceLoader = getClass().getClassLoader();
    contextPath = cp;
    commonInit();
  }

  /**
   * passes request to first filter in the chain
   *
   * @param req request
   * @param resp response
   * @throws java.io.IOException on io exception
   * @throws net.sf.barefoot.context.BarefootServletException wrapped original servlet exception
   */
  public abstract void dispatch(AbstractServletRequest req, AbstractServletResponse resp)
      throws IOException, BarefootServletException;

  public int getSessionTimeout() {
    return sessionTimeout;
  }

  public void setSessionTimeout(int i) {
    sessionTimeout = i;
  }

  public String getRequestCharacterEncoding() {
    return requestCharacterEncoding;
  }

  public void setRequestCharacterEncoding(String string) {
    requestCharacterEncoding = string;
  }

  public String getResponseCharacterEncoding() {
    return responseCharacterEncoding;
  }

  public void setResponseCharacterEncoding(String string) {
    responseCharacterEncoding = string;
  }

  public ClassLoader getClassLoader() {
    return getClass().getClassLoader();
  }

  public void declareRoles(String... strings) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public String getVirtualServerName() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public String getContextPath() {
    return contextPath;
  }

  public int getMajorVersion() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public int getMinorVersion() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public int getEffectiveMajorVersion() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public int getEffectiveMinorVersion() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public String getMimeType(String string) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public Set<String> getResourcePaths(String path) {
    return null;
  }

  protected String translateResourcePath(String path) {
    if (path.startsWith("/WEB-INF/")) {
      path = path.substring(1);
    } else {
      path = null;
    }
    return path;
  }

  public URL getResource(String path) throws MalformedURLException {
    path = translateResourcePath(path);

    if (path == null) return null;

    return resourceLoader.getResource(path);
  }

  public InputStream getResourceAsStream(String path) {
    path = translateResourcePath(path);

    if (path == null) return null;

    return resourceLoader.getResourceAsStream(path);
  }

  public void log(String string) {
    AbstractServletRequest request = currentRequest.get();
    if (request == null) {
      Logger.getGlobal().log(LOG_LEVEL, string);
    } else {
      request.log(string);
    }
  }

  public void log(Exception excptn, String string) {
    log(string, excptn);
  }

  public void log(String string, Throwable thrwbl) {
    AbstractServletRequest request = currentRequest.get();
    if (request == null) {
      Logger.getGlobal().log(LOG_LEVEL, string, thrwbl);
    } else {
      request.log(string);
    }
  }

  public String getRealPath(String string) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public String getServerInfo() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public String getInitParameter(String string) {
    return initParameters.get(string);
  }

  public Enumeration<String> getInitParameterNames() {
    return new IteratorEnumeration(initParameters.keySet().iterator());
  }

  public boolean setInitParameter(String name, String value) {
    if (initParameters.containsKey(name)) return false;
    initParameters.put(name, value);
    return true;
  }

  public Object getAttribute(String string) {
    return attributes.get(string);
  }

  public Enumeration<String> getAttributeNames() {
    Iterator<String> it = attributes.keySet().iterator();

    return new IteratorEnumeration(it);
  }

  public String getServletContextName() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public <T extends EventListener> void addListener(T t) {
    if (state >= STATE_RUN) throw new IllegalStateException();

    for (Map.Entry<Class<? extends EventListener>, List<? extends EventListener>> e :
        listenerMap.entrySet()) {
      if (e.getKey().isAssignableFrom(t.getClass())) {
        List list = e.getValue();
        list.add(t);
      }
    }
  }

  public void addListener(Class<? extends EventListener> type) {
    try {
      addListener(type.getConstructor().newInstance());
    } catch (NoSuchMethodException
        | InstantiationException
        | IllegalAccessException
        | InvocationTargetException ex) {
      throw new RuntimeException(ex);
    }
  }

  public Object addJspFile(String string, String string1) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  /** Initializes the context. Activates the listeners, filters and servlets. */
  public void onStartup() throws Exception {
    if (state != STATE_NEW) throw new IllegalStateException();

    state = STATE_INIT;

    while (true) {
      Runnable task = null;

      for (List<Runnable> list : startupList) {
        if (!list.isEmpty()) {
          task = list.get(0);
          list.remove(0);
          break;
        }
      }

      if (task == null) {
        break;
      }

      task.run();
    }

    state = STATE_RUN;
  }

  public void destroy() throws Exception {
    state = STATE_SHUTDOWN;
    listenerMap
        .entrySet()
        .forEach(
            (e) -> {
              e.getValue().clear();
            });
    initParameters.clear();
    attributes.clear();
    listenerStartupTasks.clear();
    filterStartupTasks.clear();
    servletStartupTasks.clear();
    requestCharacterEncoding = null;
    responseCharacterEncoding = null;
    sessionTimeout = 0;
    state = STATE_NEW;
  }

  public abstract AbstractServletRequest.Builder getServletRequestBuilder();

  public abstract AbstractCookieCutter getCookieCutter();

  public abstract AbstractSessionCookieConfig getSessionCookieConfig();

  public abstract AbstractServletRegistration addServlet(String string, String type);

  public abstract AbstractFilterRegistration addFilter(String string, String type);

  public void addListener(String string) {
    try {
      Class<?> cls = resourceLoader.loadClass(string);
      EventListener obj = (EventListener) cls.getConstructor().newInstance();
      addListener(obj);
    } catch (ClassNotFoundException
        | NoSuchMethodException
        | InstantiationException
        | IllegalAccessException
        | IllegalArgumentException
        | InvocationTargetException ex) {
      throw new RuntimeException(ex);
    }
  }
}
