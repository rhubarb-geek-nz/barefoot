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

package net.sf.barefoot.context.xml;

import java.util.Enumeration;
import java.util.function.Function;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/** Collect Context element attributes. */
final class BarefootContextXmlHandler extends DefaultHandler {
  protected final Context context;
  protected final Function<String, String> valueExpander;

  public BarefootContextXmlHandler(Context c, Function<String, String> f) {
    context = c;
    valueExpander = f == null ? (s) -> s : f;
  }

  @Override
  public void startElement(String uri, String lName, String qName, Attributes attr)
      throws SAXException {
    try {
      String name = valueExpander.apply(attr.getValue("name"));

      switch (qName) {
        case "Resource":
          {
            String type = valueExpander.apply(attr.getValue("type"));
            String factory = valueExpander.apply(attr.getValue("factory"));
            Name bindName = new CompositeName(name);
            Reference reference = new Reference(type, factory, null);

            String fcn = reference.getFactoryClassName();

            int i = attr.getLength();

            while (0 != i--) {
              String n = attr.getLocalName(i);

              switch (n) {
                case "name":
                case "type":
                case "factory":
                  break;
                default:
                  String v = valueExpander.apply(attr.getValue(i));
                  reference.add(new StringRefAddr(n, v));
                  break;
              }
            }

            if (!makeTree(bindName)) {
              context.bind(bindName, reference);
            }
          }
          break;
        case "Environment":
          {
            String type = valueExpander.apply(attr.getValue("type"));
            String value = valueExpander.apply(attr.getValue("value"));
            Name bindName = new CompositeName(name);

            if (!makeTree(bindName)) {
              context.bind(bindName, getValue(type, value));
            }
          }
          break;
        case "Context":
          break;
        default:
          throw new SAXException("Unknown element " + qName);
      }
    } catch (NamingException ex) {
      throw new SAXException(ex);
    }
  }

  private Object getValue(String type, String value) {
    switch (type) {
      case "java.lang.String":
        return value;
      case "java.lang.Character":
        if (value.length() != 1) {
          throw new IllegalArgumentException("value should have exactly one character: " + value);
        }
        return value.charAt(0);
      case "java.lang.Short":
        return Short.parseShort(value);
      case "java.lang.Integer":
        return Integer.parseInt(value);
      case "java.lang.Long":
        return Long.parseLong(value);
      case "java.lang.Boolean":
        return Boolean.parseBoolean(value);
      case "java.lang.Double":
        return Double.parseDouble(value);
      case "java.lang.Float":
        return Float.parseFloat(value);
      case "null":
        return null;
    }

    throw new TypeNotPresentException(type, null);
  }

  private boolean makeTree(Name bindName) throws NamingException {
    boolean result = false;
    try {
      context.lookup(bindName);
      result = true;
    } catch (NamingException ex) {
      Name resolved = ex.getResolvedName();
      Name remaining = ex.getRemainingName();

      if (remaining.size() != 1) {
        Context parent =
            (resolved == null || resolved.isEmpty()) ? context : (Context) context.lookup(resolved);
        Enumeration<String> all = remaining.getAll();
        while (all.hasMoreElements()) {
          String element = all.nextElement();
          if (!all.hasMoreElements()) {
            break;
          }
          parent = parent.createSubcontext(element);
        }
      }
    }
    return result;
  }
}
