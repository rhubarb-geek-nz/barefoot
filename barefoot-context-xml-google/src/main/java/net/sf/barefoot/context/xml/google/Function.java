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

package net.sf.barefoot.context.xml.google;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.spi.NamingManager;
import net.sf.barefoot.context.AbstractServletContext;
import net.sf.barefoot.context.xml.BarefootContextXmlLoader;
import net.sf.barefoot.google.functions.BarefootGoogleFunction;
import net.sf.barefoot.naming.BarefootInitialContextFactoryBuilder;

/**
 * Main entry point for the function. On initialization loads Barefoot Context from
 * META-INF/context.xml.
 */
public class Function implements HttpFunction {
  private final HttpFunction dispatcher;
  private static HttpFunction instance;
  private static final Object mutex = new Object();

  public Function() throws Exception {
    synchronized (mutex) {
      if (instance == null) {
        Thread thread = Thread.currentThread();
        ClassLoader original = thread.getContextClassLoader();
        try {
          ClassLoader loader = getClass().getClassLoader();
          thread.setContextClassLoader(loader);
          NamingManager.setInitialContextFactoryBuilder(
              new BarefootInitialContextFactoryBuilder(loader));
          Context context =
              (Context) new InitialContext().lookup(BarefootContextXmlLoader.JAVA_COMP_ENV);
          BarefootContextXmlLoader.load(
              context, loader, BarefootContextXmlLoader.META_INF_CONTEXT_XML);
          AbstractServletContext servletContext =
              (AbstractServletContext) context.lookup("barefoot/context");
          instance = new BarefootGoogleFunction(servletContext);
        } finally {
          thread.setContextClassLoader(original);
        }
      }
      dispatcher = instance;
    }
  }

  @Override
  public void service(HttpRequest request, HttpResponse response) throws Exception {
    dispatcher.service(request, response);
  }
}
