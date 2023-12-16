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

import jakarta.servlet.ServletContextAttributeEvent;
import jakarta.servlet.ServletContextAttributeListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionAttributeListener;
import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.junit.Assert;
import org.junit.Test;

/** test the listeners */
public class ListenerTest {

  class SCAL implements ServletContextAttributeListener {
    boolean added, removed, replaced;

    @Override
    public void attributeAdded(ServletContextAttributeEvent scae) {
      added = true;
    }

    @Override
    public void attributeRemoved(ServletContextAttributeEvent scae) {
      removed = true;
    }

    @Override
    public void attributeReplaced(ServletContextAttributeEvent scae) {
      replaced = true;
    }
  }

  @Test
  public void testAttributeListener() throws ServletException {
    BarefootServletContext ctx = new BarefootServletContext("");
    SCAL scal = new SCAL();
    ctx.addListener(scal);
    ctx.onStartup();
    String ATTR_NAME = "foo";

    Assert.assertFalse(scal.added);
    Assert.assertFalse(scal.removed);
    Assert.assertFalse(scal.replaced);

    ctx.setAttribute(ATTR_NAME, "bar");

    Assert.assertTrue(scal.added);

    ctx.setAttribute(ATTR_NAME, "changeit");

    Assert.assertTrue(scal.replaced);

    ctx.removeAttribute(ATTR_NAME);

    Assert.assertTrue(scal.removed);
  }

  class HSL implements HttpSessionListener {
    boolean created, destroyed;

    @Override
    public void sessionCreated(HttpSessionEvent hse) {
      created = true;
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent hse) {
      destroyed = true;
    }
  }

  @Test
  public void testSessionListener() {
    BarefootServletContext ctx = new BarefootServletContext("");
    BarefootServletRequest.Builder builder = ctx.getServletRequestBuilder();
    builder.contextPath("");
    builder.requestUri("/");
    BarefootServletRequest request = builder.build();

    HSL hsl = new HSL();
    ctx.addListener(hsl);

    Assert.assertFalse(hsl.created);
    Assert.assertFalse(hsl.destroyed);

    HttpSession session = request.getSession();

    Assert.assertTrue(hsl.created);

    session.invalidate();
    Assert.assertTrue(hsl.destroyed);
  }

  class HSAL implements HttpSessionAttributeListener {
    boolean added, removed, replaced;

    @Override
    public void attributeAdded(HttpSessionBindingEvent hsbe) {
      added = true;
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent hsbe) {
      removed = true;
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent hsbe) {
      replaced = true;
    }
  }

  @Test
  public void testSessionAttributeListener() {
    BarefootServletContext ctx = new BarefootServletContext("");
    BarefootServletRequest.Builder builder = ctx.getServletRequestBuilder();
    builder.contextPath("");
    builder.requestUri("/");
    BarefootServletRequest request = builder.build();

    HSAL hsal = new HSAL();
    ctx.addListener(hsal);

    Assert.assertFalse(hsal.added);
    Assert.assertFalse(hsal.removed);
    Assert.assertFalse(hsal.replaced);

    HttpSession session = request.getSession();

    String ATTR_NAME = "foo";

    session.setAttribute(ATTR_NAME, "bar");

    Assert.assertTrue(hsal.added);
    session.setAttribute(ATTR_NAME, "change");
    Assert.assertTrue(hsal.replaced);
    session.removeAttribute(ATTR_NAME);
    Assert.assertTrue(hsal.removed);
  }
}
