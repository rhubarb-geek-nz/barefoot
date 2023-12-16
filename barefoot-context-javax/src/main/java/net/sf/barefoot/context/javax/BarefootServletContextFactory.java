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

import java.util.Enumeration;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

/** Factory for context. */
public final class BarefootServletContextFactory implements ObjectFactory {

  @Override
  public BarefootServletContext getObjectInstance(
      Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {
    String path = "";
    ClassLoader loader = getClass().getClassLoader();

    if (obj != null) {
      Reference ref = (Reference) obj;

      Enumeration<RefAddr> all = ref.getAll();

      while (all.hasMoreElements()) {
        RefAddr item = all.nextElement();

        switch (item.getType()) {
          case "path":
            path = item.getContent().toString();
            break;
        }
      }
    }

    BarefootServletContext context = new BarefootServletContext(path, loader);
    context.onStartup();
    return context;
  }
}
