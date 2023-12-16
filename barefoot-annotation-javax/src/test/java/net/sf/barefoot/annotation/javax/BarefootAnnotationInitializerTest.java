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

import java.util.ServiceLoader;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import net.sf.barefoot.context.javax.BarefootServletContext;
import org.junit.Assert;
import org.junit.Test;

/** test cases for context */
public class BarefootAnnotationInitializerTest {

  boolean filterInit, servletInit, listenerInit;

  @Test
  public void testInitializer() throws ServletException {
    filterInit = servletInit = listenerInit = false;
    BarefootServletContext context = new BarefootServletContext("");

    context.setAttribute(getClass().getCanonicalName(), this);

    context.onStartup();

    Assert.assertTrue("filterInit", filterInit);
    Assert.assertTrue("servletInit", servletInit);
    Assert.assertTrue("listenerInit", listenerInit);
  }

  @Test
  public void testServiceLoader() {
    ServiceLoader<ServletContainerInitializer> serviceLoader =
        ServiceLoader.load(ServletContainerInitializer.class);
    boolean found = true;

    for (ServletContainerInitializer init : serviceLoader) {
      found |= init instanceof BarefootAnnotationInitializer;
    }

    Assert.assertTrue(found);
  }

  static BarefootAnnotationInitializerTest get(ServletContext ctx) {
    return (BarefootAnnotationInitializerTest)
        ctx.getAttribute(BarefootAnnotationInitializerTest.class.getCanonicalName());
  }
}
