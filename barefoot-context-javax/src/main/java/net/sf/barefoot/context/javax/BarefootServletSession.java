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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionContext;
import javax.servlet.http.HttpSessionEvent;
import net.sf.barefoot.context.AbstractServletSession;

/** Servlet session with no backing */
public final class BarefootServletSession extends AbstractServletSession implements HttpSession {
  final BarefootServletRequest servletRequest;

  public BarefootServletSession(
      BarefootServletRequest req, long ct, long at, boolean in, String id, int mii) {
    super(ct, at, in, id, mii);
    servletRequest = req;
  }

  @Override
  public ServletContext getServletContext() {
    return servletRequest.getServletContext();
  }

  @Override
  public HttpSessionContext getSessionContext() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void invalidate() {
    if (!invalid) {
      invalid = true;
      servletRequest.sessionDestroyed(this);
      BarefootServletContext servletContext = servletRequest.getServletContext();
      if (!servletContext.httpSessionListeners.isEmpty()) {
        HttpSessionEvent hse = new HttpSessionEvent(this);
        servletContext.httpSessionListeners.forEach(
            (e) -> {
              e.sessionDestroyed(hse);
            });
      }
    }
  }

  @Override
  public void setAttribute(String name, Object value) {
    if (invalid) throw new IllegalStateException();
    BarefootServletContext servletContext = servletRequest.getServletContext();
    if (servletContext.httpSessionAttributeListeners.isEmpty()) {
      attr.put(name, value);
    } else {
      boolean replace = attr.containsKey(name);
      attr.put(name, value);
      HttpSessionBindingEvent hsbe = new HttpSessionBindingEvent(this, name, value);
      servletContext.httpSessionAttributeListeners.forEach(
          (e) -> {
            if (replace) {
              e.attributeReplaced(hsbe);
            } else {
              e.attributeAdded(hsbe);
            }
          });
    }
  }

  @Override
  public void removeAttribute(String name) {
    if (invalid) throw new IllegalStateException();
    BarefootServletContext servletContext = servletRequest.getServletContext();
    if (servletContext.httpSessionAttributeListeners.isEmpty()) {
      attr.remove(name);
    } else {
      if (attr.containsKey(name)) {
        Object value = attr.get(name);
        HttpSessionBindingEvent hsbe = new HttpSessionBindingEvent(this, name, value);
        attr.remove(name);
        servletContext.httpSessionAttributeListeners.forEach(
            (e) -> {
              e.attributeRemoved(hsbe);
            });
      }
    }
  }
}
