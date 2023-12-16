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

package net.sf.barefoot.context.xml.aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.spi.NamingManager;
import net.sf.barefoot.aws.lambda.BarefootAwsHandler;
import net.sf.barefoot.context.AbstractServletContext;
import net.sf.barefoot.context.xml.BarefootContextXmlLoader;
import net.sf.barefoot.naming.BarefootInitialContextFactoryBuilder;

/** Main entry for AWS Lambda. Context is loaded from META-INF/context.xml. */
public class Handler implements RequestHandler<Map<String, Object>, Map<String, Object>> {
  protected final RequestHandler<Map<String, Object>, Map<String, Object>> dispatcher;
  private static final Object mutex = new Object();
  private static RequestHandler<Map<String, Object>, Map<String, Object>> instance;

  public Handler() throws Exception {
    synchronized (mutex) {
      if (instance == null) {
        Thread thread = Thread.currentThread();
        ClassLoader originalClassLoader = thread.getContextClassLoader();

        try {
          ClassLoader localClassLoader = getClass().getClassLoader();
          thread.setContextClassLoader(localClassLoader);
          NamingManager.setInitialContextFactoryBuilder(
              new BarefootInitialContextFactoryBuilder(localClassLoader));
          javax.naming.Context context =
              (javax.naming.Context)
                  new InitialContext().lookup(BarefootContextXmlLoader.JAVA_COMP_ENV);
          BarefootContextXmlLoader.load(
              context, localClassLoader, BarefootContextXmlLoader.META_INF_CONTEXT_XML);
          AbstractServletContext servletContext =
              (AbstractServletContext) context.lookup("barefoot/context");
          instance = new BarefootAwsHandler(servletContext);
        } finally {
          thread.setContextClassLoader(originalClassLoader);
        }
      }

      dispatcher = instance;
    }
  }

  @Override
  public Map<String, Object> handleRequest(Map<String, Object> i, Context cntxt) {
    return dispatcher.handleRequest(i, cntxt);
  }
}
