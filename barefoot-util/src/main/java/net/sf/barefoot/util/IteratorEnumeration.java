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

package net.sf.barefoot.util;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * Simple wrapper to create an enumerator from an iterator.
 *
 * @param <E> type of object that is iterated.
 */
public class IteratorEnumeration<E> implements Enumeration<E> {
  final Iterator<E> it;

  public IteratorEnumeration(Iterator<E> it) {
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
}
