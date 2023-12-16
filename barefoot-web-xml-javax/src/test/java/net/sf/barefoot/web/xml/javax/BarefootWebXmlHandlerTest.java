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

package net.sf.barefoot.web.xml.javax;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import net.sf.barefoot.context.javax.BarefootServletContext;
import net.sf.barefoot.context.javax.BarefootSessionCookieConfig;
import org.junit.Assert;
import org.junit.Test;

/** test cases for context */
public class BarefootWebXmlHandlerTest {

  boolean filterInit, servletInit, listenerInit;

  @Test
  public void testBuilder() throws ServletException {
    filterInit = servletInit = listenerInit = false;
    BarefootServletContext context = new BarefootServletContext("");

    context.setAttribute(getClass().getCanonicalName(), this);

    context.onStartup();

    Assert.assertTrue(filterInit);
    Assert.assertTrue(servletInit);
    Assert.assertTrue(listenerInit);

    BarefootSessionCookieConfig cookieConfig = context.getSessionCookieConfig();
    Assert.assertTrue(cookieConfig.isHttpOnly());
    Assert.assertTrue(cookieConfig.isSecure());
    Assert.assertEquals("cookie-path", cookieConfig.getPath());
    Assert.assertEquals("cookie-domain", cookieConfig.getDomain());
    Assert.assertEquals("cookie-comment", cookieConfig.getComment());
    Assert.assertEquals("cookie-name", cookieConfig.getName());
    Assert.assertEquals(300, cookieConfig.getMaxAge());
  }

  static BarefootWebXmlHandlerTest get(ServletContext ctx) {
    return (BarefootWebXmlHandlerTest)
        ctx.getAttribute(BarefootWebXmlHandlerTest.class.getCanonicalName());
  }
}
