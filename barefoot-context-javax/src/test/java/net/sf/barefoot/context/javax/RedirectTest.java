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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import net.sf.barefoot.context.AbstractServletRequest;
import net.sf.barefoot.context.AbstractServletResponse;
import net.sf.barefoot.context.BarefootServletException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/** Redirect test cases. */
public class RedirectTest extends TestBase {

  @Before
  public void doBefore() throws Exception {
    jettyServer = new JettyServer();
    jettyServer.start("/test");
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
          + "barefoot-redirect: simple\r\n"
          + "\r\n";

  @Test
  public void testSimple() throws IOException {
    Map<String, String> map = getHeaders(REQUEST_ONE);

    String location = map.get("location");
    URL url = new URL(location);
    Assert.assertEquals(8080, url.getPort());
    Assert.assertEquals("/test/submit/simple", url.getPath());
  }

  static final String REQUEST_TWO =
      "GET /test/submit/debug?wibble=flim&wibble=egg HTTP/1.1\r\n"
          + "Host: 127.0.0.1:8080\r\n"
          + "User-Agent: curl/7.64.0\r\n"
          + "Accept: */*\r\n"
          + "barefoot-redirect: /simple\r\n"
          + "\r\n";

  @Test
  public void testSlashSimple() throws IOException {
    Map<String, String> map = getHeaders(REQUEST_TWO);

    String location = map.get("location");
    URL url = new URL(location);
    Assert.assertEquals(8080, url.getPort());
    Assert.assertEquals("/simple", url.getPath());

    String header = map.get(null);
    Assert.assertEquals("302", header.split(" ")[1]);
  }

  static final String REQUEST_THREE =
      "GET /test/one/two/three/four/debug?wibble=flim&wibble=egg HTTP/1.1\r\n"
          + "Host: 127.0.0.1:8080\r\n"
          + "User-Agent: curl/7.64.0\r\n"
          + "Accept: */*\r\n"
          + "barefoot-redirect: simple\r\n"
          + "\r\n";

  @Test
  public void testVeryNested() throws IOException {
    Map<String, String> map = getHeaders(REQUEST_THREE);

    String location = map.get("location");
    URL url = new URL(location);
    Assert.assertEquals(8080, url.getPort());
    Assert.assertEquals("/test/one/two/three/four/simple", url.getPath());

    String header = map.get(null);
    Assert.assertEquals("302", header.split(" ")[1]);
  }

  static final String REQUEST_FOUR =
      "GET /test/one/two/three/four/?wibble=flim&wibble=egg HTTP/1.1\r\n"
          + "Host: 127.0.0.1:8080\r\n"
          + "User-Agent: curl/7.64.0\r\n"
          + "Accept: */*\r\n"
          + "barefoot-redirect: simple\r\n"
          + "\r\n";

  @Test
  public void testContainer() throws IOException {
    Map<String, String> map = getHeaders(REQUEST_FOUR);

    String location = map.get("location");
    URL url = new URL(location);
    Assert.assertEquals(8080, url.getPort());
    Assert.assertEquals("/test/one/two/three/four/simple", url.getPath());

    String header = map.get(null);
    Assert.assertEquals("302", header.split(" ")[1]);
  }

  static final String REQUEST_FIVE =
      "GET /test/one/two/three/four/?wibble=flim&wibble=egg HTTP/1.1\r\n"
          + "Host: 127.0.0.1:8080\r\n"
          + "User-Agent: curl/7.64.0\r\n"
          + "Accept: */*\r\n"
          + "barefoot-redirect: http://net.sf.barefoot/barefoot/simple\r\n"
          + "\r\n";

  @Test
  public void testAbsolute() throws IOException {
    Map<String, String> map = getHeaders(REQUEST_FIVE);

    String location = map.get("location");
    Assert.assertEquals("http://net.sf.barefoot/barefoot/simple", location);

    String header = map.get(null);
    Assert.assertEquals("302", header.split(" ")[1]);
  }

  @Test
  public void testURI() throws URISyntaxException {
    {
      URI uri = new URI("/something");
      Assert.assertFalse(uri.isAbsolute());
    }

    {
      URI uri = new URI("something");
      Assert.assertFalse(uri.isAbsolute());
    }

    {
      URI uri = new URI("http://something/something");
      Assert.assertTrue(uri.isAbsolute());
    }
  }

  @Test
  public void testBarefootSimple() throws ServletException, IOException, BarefootServletException {
    BarefootServletContext context =
        new BarefootServletContext("/test", getClass().getClassLoader());

    BarefootServletRegistration reg = context.addServlet("test", TestServlet.class);
    reg.addMapping("/");

    context.onStartup();

    {
      Map<String, List<String>> headers = new HashMap<>();

      addHeader(headers, "barefoot-redirect", "simple");

      AbstractServletRequest req =
          context
              .getServletRequestBuilder()
              .headers(headers)
              .contextPath("/test")
              .requestUri("/test/req")
              .requestUrl("http://localhost/test/req")
              .build();

      AbstractServletResponse resp = req.getServletResponseBuilder().build();

      context.dispatch(req, resp);

      String location = resp.getHeader("Location");

      Assert.assertEquals(302, resp.getStatus());
      Assert.assertEquals("http://localhost/test/simple", location);
    }

    {
      Map<String, List<String>> headers = new HashMap<>();

      addHeader(headers, "barefoot-redirect", "/simple");

      AbstractServletRequest req =
          context
              .getServletRequestBuilder()
              .headers(headers)
              .contextPath("/test")
              .requestUri("/test/req")
              .requestUrl("http://localhost/test/req")
              .build();

      AbstractServletResponse resp = req.getServletResponseBuilder().build();

      context.dispatch(req, resp);

      String location = resp.getHeader("Location");

      Assert.assertEquals(302, resp.getStatus());
      Assert.assertEquals("http://localhost/simple", location);
    }

    {
      Map<String, List<String>> headers = new HashMap<>();

      addHeader(headers, "barefoot-redirect", "http://net.sf.barefoot/simple");

      AbstractServletRequest req =
          context
              .getServletRequestBuilder()
              .headers(headers)
              .contextPath("/test")
              .requestUri("/test/req")
              .requestUrl("http://localhost/test/req")
              .build();

      AbstractServletResponse resp = req.getServletResponseBuilder().build();

      context.dispatch(req, resp);

      String location = resp.getHeader("Location");

      Assert.assertEquals(302, resp.getStatus());
      Assert.assertEquals("http://net.sf.barefoot/simple", location);
    }
  }
}
