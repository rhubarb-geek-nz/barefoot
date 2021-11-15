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

import org.junit.Assert;
import org.junit.Test;

/** test cases for content type parser */
public class BarefootContentTypeTest {

  public BarefootContentTypeTest() {}

  @Test
  public void testNothing() {
    Assert.assertNull(
        BarefootContentType.getCharsetFromContentType(BarefootContentType.APPLICATION_JSON));
  }

  @Test
  public void testRubbish() {
    Assert.assertNull(
        BarefootContentType.getCharsetFromContentType("application/json;sadfjsdf;weweer;asdsa"));
  }

  @Test
  public void testMiddleSpaceUTF8() {
    Assert.assertEquals(
        "UTF-8",
        BarefootContentType.getCharsetFromContentType(
            "application/json; charset=UTF-8;sadfjsdf;weweer;asdsa"));
  }

  @Test
  public void testMiddleUTF8() {
    Assert.assertEquals(
        "UTF-8",
        BarefootContentType.getCharsetFromContentType(
            "application/json;charset=UTF-8;sadfjsdf;weweer;asdsa"));
  }

  @Test
  public void testEndUTF8() {
    Assert.assertEquals(
        "UTF-8",
        BarefootContentType.getCharsetFromContentType(
            "application/json;sadfjsdf;weweer;asdsa;charset=UTF-8"));
  }

  @Test
  public void testEndSpaceUTF8() {
    Assert.assertEquals(
        "UTF-8",
        BarefootContentType.getCharsetFromContentType(
            "application/json;sadfjsdf;weweer;asdsa; charset=UTF-8"));
  }
}
