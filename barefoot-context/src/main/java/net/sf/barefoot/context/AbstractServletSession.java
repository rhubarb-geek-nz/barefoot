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

package net.sf.barefoot.context;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.barefoot.util.IteratorEnumeration;

/** basic servlet session */
public abstract class AbstractServletSession {
  final long createdTime, lastAccessedTime;
  protected int maxInactiveInterval;
  String id;
  protected boolean isNew;
  protected final Map<String, Object> attr = new HashMap<>();
  protected boolean invalid;

  public AbstractServletSession(long ct, long at, boolean in, String i, int mii) {
    createdTime = ct;
    lastAccessedTime = at;
    isNew = in;
    id = i;
    maxInactiveInterval = mii;
  }

  public long getCreationTime() {
    if (invalid) throw new IllegalStateException();
    return createdTime;
  }

  public long getLastAccessedTime() {
    if (invalid) throw new IllegalStateException();
    return lastAccessedTime;
  }

  public String getId() {
    if (invalid) throw new IllegalStateException();
    return id;
  }

  public int getMaxInactiveInterval() {
    return maxInactiveInterval;
  }

  public void setMaxInactiveInterval(int x) {
    maxInactiveInterval = x;
  }

  public boolean isNew() {
    return isNew;
  }

  public abstract void setAttribute(String name, Object value);

  public abstract void removeAttribute(String name);

  public void putValue(String name, Object value) {
    setAttribute(name, value);
  }

  public void removeValue(String name) {
    removeAttribute(name);
  }

  public Object getAttribute(String name) {
    if (invalid) throw new IllegalStateException();
    return attr.get(name);
  }

  public Object getValue(String name) {
    return getAttribute(name);
  }

  public Enumeration getAttributeNames() {
    if (invalid) throw new IllegalStateException();
    return new IteratorEnumeration(attr.keySet().iterator());
  }

  public String[] getValueNames() {
    List<String> list = new ArrayList();

    Enumeration e = getAttributeNames();

    while (e.hasMoreElements()) {
      list.add(e.nextElement().toString());
    }

    return list.toArray(new String[0]);
  }
}
