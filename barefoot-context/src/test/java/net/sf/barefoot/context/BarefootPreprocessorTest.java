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

import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

/** preprocessor tests */
public class BarefootPreprocessorTest {
  Charset charset = StandardCharsets.UTF_8;
  BarefootPreprocessor pp = new BarefootPreprocessor();

  @Test
  public void testSingleValue() throws Exception {
    Map<String, List<String>> params = new HashMap<>();
    pp.processApplicationFormUrlEncoded(new StringReader("FOO=BAR"), params, charset);
    Assert.assertEquals(1, params.keySet().size());
    Assert.assertEquals("FOO", params.keySet().iterator().next());
    Assert.assertEquals("BAR", params.get("FOO").get(0));
  }

  @Test
  public void testNameOnly() throws Exception {
    Map<String, List<String>> params = new HashMap<>();
    pp.processApplicationFormUrlEncoded(new StringReader("FOO"), params, charset);
    Assert.assertEquals(1, params.keySet().size());
    Assert.assertEquals("FOO", params.keySet().iterator().next());
    Assert.assertEquals(null, params.get("FOO").get(0));
  }

  @Test
  public void testNameEqualsEmpty() throws Exception {
    Map<String, List<String>> params = new HashMap<>();
    pp.processApplicationFormUrlEncoded(new StringReader("FOO="), params, charset);
    Assert.assertEquals(1, params.keySet().size());
    Assert.assertEquals("FOO", params.keySet().iterator().next());
    Assert.assertEquals("", params.get("FOO").get(0));
  }

  @Test
  public void testMany() throws Exception {
    Map<String, List<String>> params = new HashMap<>();
    pp.processApplicationFormUrlEncoded(new StringReader("A=1&B=2&C=3&D=4&C=5"), params, charset);
    Assert.assertEquals(4, params.keySet().size());
  }

  @Test
  public void testCT1() {
    Assert.assertFalse(pp.isApplicationFormUrlEncoded(null));
  }

  @Test
  public void testCT2() {
    Assert.assertFalse(pp.isApplicationFormUrlEncoded("rubbish"));
  }

  @Test
  public void testCT3() {
    Assert.assertTrue(
        pp.isApplicationFormUrlEncoded(BarefootContentType.APPLICATION_FORM_URLENCODED));
  }

  @Test
  public void testCT4() {
    Assert.assertFalse(
        pp.isApplicationFormUrlEncoded(BarefootContentType.APPLICATION_FORM_URLENCODED + "!"));
  }

  @Test
  public void testCT5() {
    Assert.assertTrue(
        pp.isApplicationFormUrlEncoded(BarefootContentType.APPLICATION_FORM_URLENCODED + ";"));
  }
}
