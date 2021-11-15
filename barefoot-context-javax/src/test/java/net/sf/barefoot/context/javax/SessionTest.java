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

import java.io.IOException;
import java.util.Map;
import javax.servlet.http.Cookie;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/** Test Sessions. */
public class SessionTest extends TestBase {
  BarefootCookieCutter cookieCutter = new BarefootCookieCutter();

  @Before
  public void doBefore() throws Exception {
    jettyServer = new JettyServer();
    jettyServer.start("/");
  }

  @After
  public void doAfter() throws Exception {
    JettyServer jetty = jettyServer;
    jettyServer = null;
    if (jetty != null) jetty.stop();
  }

  static final String REQUEST_ONE =
      "GET /test/submit/debug?wibble=flim&wibble=egg HTTP/1.1\r\n"
          + "Host: 127.0.0.1:8080\r\n"
          + "User-Agent: curl/7.64.0\r\n"
          + "Accept: */*\r\n"
          + "barefoot-session: get-true\r\n"
          + "\r\n";

  @Test
  public void testGetSessionTrue() throws IOException {
    Map<String, String> map = getHeaders(REQUEST_ONE);

    String barefootSession = map.get("barefoot-session");
    Assert.assertNotNull(barefootSession);
    String barefootSessionId = map.get("barefoot-session-id");
    Assert.assertNotNull(barefootSessionId);
    boolean barefootSessionIsNew = Boolean.parseBoolean(map.get("barefoot-session-isnew"));
    Assert.assertTrue(barefootSessionIsNew);

    String setCookie = map.get("set-cookie");
    Assert.assertNotNull(setCookie);
    Cookie cookie = cookieCutter.parseSetCookie(setCookie);
    Assert.assertEquals("JSESSIONID", cookie.getName());
  }

  static final String REQUEST_TWO =
      "GET /test/submit/debug?wibble=flim&wibble=egg HTTP/1.1\r\n"
          + "Host: 127.0.0.1:8080\r\n"
          + "User-Agent: curl/7.64.0\r\n"
          + "Accept: */*\r\n"
          + "barefoot-session: get-false\r\n"
          + "\r\n";

  @Test
  public void testGetSessionFalse() throws IOException {
    Map<String, String> map = getHeaders(REQUEST_TWO);

    String barefootSession = map.get("barefoot-session");
    Assert.assertNull(barefootSession);
  }
}
