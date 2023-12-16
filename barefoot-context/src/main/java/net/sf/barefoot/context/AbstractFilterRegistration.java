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
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sf.barefoot.util.IteratorEnumeration;

/** Abstract filter registration. */
public abstract class AbstractFilterRegistration {
  protected final String name;
  boolean asyncSupported;
  final Map<String, String> initParameters = new HashMap<>();
  protected boolean isInitialised = false;
  protected final List<String> urlPatterns = new ArrayList<>(), servletMappings = new ArrayList();

  protected AbstractFilterRegistration(String n) {
    name = n;
  }

  public Collection<String> getServletNameMappings() {
    return servletMappings;
  }

  public Collection<String> getUrlPatternMappings() {
    return urlPatterns;
  }

  public String getName() {
    return name;
  }

  public boolean setInitParameter(String name, String value) {
    initParameters.put(name, value);
    return true;
  }

  public String getInitParameter(String name) {
    return initParameters.get(name);
  }

  public Set<String> setInitParameters(Map<String, String> map) {
    map.entrySet().stream().forEach(s -> initParameters.put(s.getKey(), s.getValue()));
    return map.keySet();
  }

  public Map<String, String> getInitParameters() {
    return initParameters;
  }

  public void setAsyncSupported(boolean arg0) {
    asyncSupported = arg0;
  }

  public String getFilterName() {
    return name;
  }

  public Enumeration<String> getInitParameterNames() {
    return new IteratorEnumeration(initParameters.keySet().iterator());
  }
}
