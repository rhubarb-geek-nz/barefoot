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

import java.util.function.Function;

/** Expand properties in strings. */
public class StringVariableExpander implements Function<String, String> {
  final Function<String, String> properties;

  /**
   * Construct a variable expander.
   *
   * @param map a function mapping of variable name to value.
   */
  public StringVariableExpander(Function<String, String> map) {
    properties = map;
  }

  /**
   * Convert a string with dollar-curly-brace notation variables embedded.
   *
   * @param in source string with variables.
   * @return string with variables expanded.
   */
  @Override
  public String apply(String in) {
    if (in == null) return null;

    if (!in.contains("${")) return in;

    StringBuilder sb = new StringBuilder();
    int len = in.length();
    int i = 0;

    while (i < len) {
      int left = in.indexOf("${", i);

      if (left < 0) {
        sb.append(in.substring(i));
        break;
      }

      int right = in.indexOf("}", left + 2);

      if (right < 0) {
        sb.append(in.substring(i));
        break;
      }

      if (left + 2 == right) {
        sb.append(in.substring(i, right + 1));
      } else {
        String name = in.substring(left + 2, right);
        String value = properties.apply(name);
        if (value != null) {
          if (i != left) {
            sb.append(in.substring(i, left));
          }
          sb.append(value);
        } else {
          sb.append(in, i, right + 1);
        }
      }

      i = right + 1;
    }

    return sb.toString();
  }
}
