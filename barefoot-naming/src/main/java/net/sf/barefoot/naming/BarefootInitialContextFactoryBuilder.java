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
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;

/**
 * Factory builder. This is intended to be installed using
 * javax.naming.spi.NamingManager.setInitialContextFactoryBuilder.
 */
public class BarefootInitialContextFactoryBuilder implements InitialContextFactoryBuilder {
  final ClassLoader classLoader;

  public BarefootInitialContextFactoryBuilder() {
    classLoader = getClass().getClassLoader();
  }

  public BarefootInitialContextFactoryBuilder(ClassLoader loader) {
    classLoader = loader;
  }

  /**
   * Factory method for factory.
   *
   * @param arg0 environment
   * @return factory
   * @throws NamingException on error
   */
  @Override
  public InitialContextFactory createInitialContextFactory(Hashtable<?, ?> arg0)
      throws NamingException {
    return new BarefootInitialContextFactory(classLoader);
  }
}
