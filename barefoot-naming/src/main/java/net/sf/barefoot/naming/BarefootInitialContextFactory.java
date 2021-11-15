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

package net.sf.barefoot.naming;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

/**
 * Factory for context. This is intended to be installed using the java.naming.factory.initial
 * system property.
 */
public class BarefootInitialContextFactory implements InitialContextFactory {
  private static Context context;
  private static Object mutex = new Object();
  final ClassLoader classLoader;

  public BarefootInitialContextFactory() {
    classLoader = getClass().getClassLoader();
  }

  public BarefootInitialContextFactory(ClassLoader loader) {
    classLoader = loader;
  }

  @Override
  public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
    synchronized (mutex) {
      if (context == null) {
        Context rootContext = new BarefootContext(classLoader, environment);
        Context javaComp = rootContext.createSubcontext("java:comp");
        javaComp.createSubcontext("env");
        context = rootContext;
      }
      return context;
    }
  }
}
