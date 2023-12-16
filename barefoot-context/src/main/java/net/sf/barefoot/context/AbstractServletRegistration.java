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

import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.sf.barefoot.util.IteratorEnumeration;

/** servlet registration base class */
public abstract class AbstractServletRegistration {
  protected final String name;
  protected final Map<String, String> initParams = new HashMap<>();
  protected boolean isInitialised = false;
  protected final Set<String> mappings = new HashSet<>();
  protected int loadOnStartup = 0;
  protected boolean asyncSupported;

  protected AbstractServletRegistration(String n) {
    name = n;
  }

  public void setLoadOnStartup(int arg0) {
    loadOnStartup = arg0;
  }

  public void setRunAsRole(String arg0) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public Set<String> addMapping(String... strings) {
    mappings.addAll(Arrays.asList(strings));
    return mappings;
  }

  public Collection<String> getMappings() {
    return mappings;
  }

  public String getRunAsRole() {
    return null;
  }

  public String getName() {
    return name;
  }

  public boolean setInitParameter(String string, String string1) {
    initParams.put(string, string1);
    return true;
  }

  public String getInitParameter(String string) {
    return initParams.get(string);
  }

  public Set<String> setInitParameters(Map<String, String> map) {
    map.entrySet()
        .forEach(
            (e) -> {
              initParams.put(e.getKey(), e.getValue());
            });
    return initParams.keySet();
  }

  public Map<String, String> getInitParameters() {
    return initParams;
  }

  public void setAsyncSupported(boolean arg0) {
    asyncSupported = arg0;
  }

  public String getServletName() {
    return name;
  }

  public Enumeration<String> getInitParameterNames() {
    return new IteratorEnumeration(initParams.keySet().iterator());
  }
}
