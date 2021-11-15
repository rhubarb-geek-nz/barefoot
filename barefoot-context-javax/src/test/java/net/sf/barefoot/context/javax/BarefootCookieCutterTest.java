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

package net.sf.barefoot.context.javax;

import javax.servlet.http.Cookie;
import org.junit.Assert;
import org.junit.Test;

/** test harness for cookie cutter */
public class BarefootCookieCutterTest {
  final BarefootCookieCutter cookieCutter = new BarefootCookieCutter();

  @Test
  public void testFooBar() {
    Cookie c = cookieCutter.parseSetCookie("FOO=BAR");
    Assert.assertEquals("FOO", c.getName());
    Assert.assertEquals("BAR", c.getValue());
  }

  @Test
  public void testFooBarSecure() {
    Cookie c = cookieCutter.parseSetCookie("FOO=BAR; Secure");
    Assert.assertEquals("FOO", c.getName());
    Assert.assertEquals("BAR", c.getValue());
    Assert.assertTrue(c.getSecure());
  }

  @Test
  public void testFooBarSecureTrue() {
    Cookie c = cookieCutter.parseSetCookie("FOO=BAR; Secure=true");
    Assert.assertEquals("FOO", c.getName());
    Assert.assertEquals("BAR", c.getValue());
    Assert.assertTrue(c.getSecure());
  }

  @Test
  public void testFooBarSecureFalse() {
    Cookie c = cookieCutter.parseSetCookie("FOO=BAR; Secure=false");
    Assert.assertEquals("FOO", c.getName());
    Assert.assertEquals("BAR", c.getValue());
    Assert.assertFalse(c.getSecure());
  }

  @Test
  public void testTheWorks() {
    Cookie c =
        cookieCutter.parseSetCookie(
            "FOO=BAR; Secure; Max-Age=42; Path=/foo/bar; Domain=foo.bar; Comment=foo this, bar"
                + " that");
    Assert.assertEquals("FOO", c.getName());
    Assert.assertEquals("BAR", c.getValue());
    Assert.assertTrue(c.getSecure());
    Assert.assertEquals(42, c.getMaxAge());
    Assert.assertEquals("/foo/bar", c.getPath());
    Assert.assertEquals("foo.bar", c.getDomain());
    Assert.assertEquals("foo this, bar that", c.getComment());
    String setCookie = cookieCutter.toString(c);
    Assert.assertTrue(setCookie.contains(" GMT;"));
  }

  @Test
  public void testCookieList() {
    Cookie[] c = cookieCutter.parseCookie("FOO=BAR; HIGH=LOW; BLACK=WHITE");
    Assert.assertEquals("FOO", c[0].getName());
    Assert.assertEquals("BAR", c[0].getValue());
    Assert.assertEquals("HIGH", c[1].getName());
    Assert.assertEquals("LOW", c[1].getValue());
    Assert.assertEquals("BLACK", c[2].getName());
    Assert.assertEquals("WHITE", c[2].getValue());
  }
}
