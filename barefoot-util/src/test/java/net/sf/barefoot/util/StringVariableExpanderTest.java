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

import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

/** Test for string properties expander. */
public class StringVariableExpanderTest {

  static final Map<String, String> values =
      new HashMap<String, String>() {
        {
          put("ABC", "abcd");
          put("123", "1234");
          put("XYZ", "wxyz");
        }
      };

  @Test
  public void testSomeMethod() {
    StringVariableExpander expander = new StringVariableExpander((s) -> values.get(s));

    Assert.assertEquals("", expander.apply(""));
    Assert.assertEquals("A", expander.apply("A"));
    Assert.assertEquals("${}", expander.apply("${}"));
    Assert.assertEquals("ABC", expander.apply("ABC"));
    Assert.assertEquals("${ABC", expander.apply("${ABC"));
    Assert.assertEquals("abcd", expander.apply("${ABC}"));
    Assert.assertEquals("${NOSUCH}", expander.apply("${NOSUCH}"));
    Assert.assertEquals("1234", expander.apply("${123}"));
    Assert.assertEquals("4561234789", expander.apply("456${123}789"));
    Assert.assertEquals("456${123", expander.apply("456${123"));
  }
}
