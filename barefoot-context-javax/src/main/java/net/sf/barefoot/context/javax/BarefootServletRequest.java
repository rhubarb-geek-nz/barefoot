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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;
import net.sf.barefoot.context.AbstractServletRequest;

/** Standard request for servlets */
public final class BarefootServletRequest extends AbstractServletRequest
    implements HttpServletRequest {
  private final BarefootServletContext servletContext;
  private final Cookie[] cookies;

  // thses two are calculated from URI in constructor
  private final String pathInfo, servletPath;

  /**
   * Initialise request state from builder
   *
   * @param builder initial request state
   */
  BarefootServletRequest(Builder builder) {
    super(builder);
    final int contextPathLength = contextPath.length();
    servletContext = builder.servletContext;
    cookies = builder.cookies;

    String sp = null, pi = null;

    String suri = uri;

    if (contextPathLength > 1 && suri.startsWith(contextPath)) {
      if (suri.length() == contextPathLength) {
        suri = "/";
      } else {
        suri = suri.substring(contextPathLength);
      }
    }

    for (Map.Entry<String, ? extends ServletRegistration> reg :
        servletContext.getServletRegistrations().entrySet()) {
      for (String mapping : reg.getValue().getMappings()) {
        if (suri.startsWith(mapping)) {
          int len = mapping.length();
          if ((suri.length() == len) || (suri.charAt(len) == '/')) {
            sp = mapping;
            if (suri.length() > len) {
              pi = suri.substring(len);
            }
            break;
          }
        }
      }

      if (sp != null) break;
    }

    pathInfo = sp == null ? suri : pi;
    servletPath = sp == null ? "" : sp;
  }

  @Override
  public String getAuthType() {
    return authType;
  }

  @Override
  public Cookie[] getCookies() {
    return cookies;
  }

  @Override
  public String getPathInfo() {
    return pathInfo;
  }

  @Override
  public String getServletPath() {
    return servletPath;
  }

  @Override
  public BarefootServletSession getSession(boolean bln) {
    if (bln && (httpSession == null)) {
      BarefootSessionCookieConfig sessionConfig = servletContext.getSessionCookieConfig();
      long now = System.currentTimeMillis();
      int maxAge = sessionConfig.getMaxAge();
      httpSession =
          new BarefootServletSession(this, now, now, true, null, maxAge > 0 ? maxAge : 1800);

      if (!servletContext.httpSessionListeners.isEmpty()) {
        HttpSessionEvent hse = new HttpSessionEvent((HttpSession) httpSession);
        servletContext.httpSessionListeners.forEach(
            (e) -> {
              e.sessionCreated(hse);
            });
      }
    }

    return (BarefootServletSession) httpSession;
  }

  @Override
  public HttpSession getSession() {
    return getSession(true);
  }

  @Override
  public boolean authenticate(HttpServletResponse hsr) throws IOException, ServletException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void login(String string, String string1) throws ServletException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void logout() throws ServletException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Collection<Part> getParts() throws IOException, ServletException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Part getPart(String string) throws IOException, ServletException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public <T extends HttpUpgradeHandler> T upgrade(Class<T> type)
      throws IOException, ServletException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public ServletInputStream getInputStream() throws IOException {
    return new BarefootServletInputStream(super.getInputStream());
  }

  @Override
  public RequestDispatcher getRequestDispatcher(String string) {
    return servletContext.getRequestDispatcher(string);
  }

  @Override
  public BarefootServletContext getServletContext() {
    return servletContext;
  }

  @Override
  public AsyncContext startAsync() throws IllegalStateException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public AsyncContext startAsync(ServletRequest sr, ServletResponse sr1)
      throws IllegalStateException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public AsyncContext getAsyncContext() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public DispatcherType getDispatcherType() {
    return DispatcherType.REQUEST;
  }

  /**
   * Create a request builder
   *
   * @return request builder
   */
  public static Builder builder() {
    return new Builder();
  }

  void sessionDestroyed(BarefootServletSession session) {
    if (httpSession == session) {
      httpSession = null;
    }
  }

  /** Builder for requests */
  public static final class Builder extends AbstractServletRequest.Builder {
    Builder() {}

    BarefootServletContext servletContext;
    Cookie[] cookies;

    /**
     * Sets the servlet context
     *
     * @param sc servlet context
     * @return this
     */
    public Builder servletContext(BarefootServletContext sc) {
      servletContext = sc;
      return this;
    }

    /**
     * Sets the request cookies
     *
     * @param c list of cookies
     * @return this
     */
    public Builder cookies(Cookie[] c) {
      cookies = c;
      return this;
    }

    /**
     * Creates the concrete request
     *
     * @return servlet request
     */
    @Override
    public BarefootServletRequest build() {
      return new BarefootServletRequest(this);
    }

    /**
     * Sets the request cookies
     *
     * @param c list of cookies
     * @return this
     */
    @Override
    public Builder cookies(List<String> c) {
      if (c != null) {
        cookies = BarefootCookieCutter.SINGLETON_INSTANCE.parseCookie(c);
      }
      return this;
    }

    /**
     * Sets the request cookies
     *
     * @param c list of cookies
     * @return this
     */
    @Override
    public Builder cookies(String c) {
      if (c != null) {
        cookies = BarefootCookieCutter.SINGLETON_INSTANCE.parseCookie(c);
      }
      return this;
    }
  }

  @Override
  public BarefootServletResponse.Builder getServletResponseBuilder() {
    return BarefootServletResponse.builder().request(this);
  }
}
