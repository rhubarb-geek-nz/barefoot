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

import java.util.Iterator;
import javax.naming.NamingEnumeration;

/** Iterator wrapper. */
final class BarefootNamingEnumeration<E> implements NamingEnumeration {
  final Iterator<E> it;

  BarefootNamingEnumeration(Iterator<E> it) {
    this.it = it;
  }

  @Override
  public boolean hasMoreElements() {
    return it.hasNext();
  }

  @Override
  public E nextElement() {
    return it.next();
  }

  @Override
  public E next() {
    return it.next();
  }

  @Override
  public boolean hasMore() {
    return it.hasNext();
  }

  @Override
  public void close() {}
}
