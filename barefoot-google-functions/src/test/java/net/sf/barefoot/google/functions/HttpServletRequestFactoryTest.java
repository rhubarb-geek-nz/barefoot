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

package net.sf.barefoot.google.functions;

import com.google.cloud.functions.HttpRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.sf.barefoot.context.AbstractServletContext;
import net.sf.barefoot.context.AbstractServletRequest;
import net.sf.barefoot.google.concrete.ConcreteHttpRequest;
import org.junit.Assert;
import org.junit.Test;

/** test cases for request factory */
public class HttpServletRequestFactoryTest {

  public HttpServletRequestFactoryTest() {}

  AbstractServletContext servletContext =
      new net.sf.barefoot.context.javax.BarefootServletContext("");

  @Test
  public void testGetNoHost() throws Exception {
    HttpServletRequestFactory factory = new HttpServletRequestFactory();
    Map<String, List<String>> headers = new HashMap<>();
    Map<String, List<String>> params = new HashMap<>();
    HttpRequest req =
        ConcreteHttpRequest.builder()
            .headers(headers)
            .queryParameters(params)
            .method("GET")
            .contentType(Optional.empty())
            .path("/testUri")
            .uri("http://localhost/testUri")
            .query(Optional.empty())
            .characterEncoding(Optional.empty())
            .build();
    AbstractServletRequest result = factory.create(servletContext, req, null);
    Assert.assertNotNull(result);
    Assert.assertEquals("", result.getContextPath());
    Assert.assertEquals("/testUri", result.getPathInfo());
    Assert.assertEquals("/testUri", result.getRequestURI());
    Assert.assertEquals("http://localhost/testUri", result.getRequestURL().toString());
    Assert.assertEquals(-1, result.getServerPort());
    Assert.assertEquals("localhost", result.getServerName());
    Assert.assertFalse(result.isSecure());
  }

  @Test
  public void testGetAnonymous() throws Exception {
    HttpServletRequestFactory factory = new HttpServletRequestFactory();
    Map<String, List<String>> headers = new HashMap<>();
    Map<String, List<String>> params = new HashMap<>();
    HttpRequest req =
        ConcreteHttpRequest.builder()
            .headers(headers)
            .queryParameters(params)
            .method("GET")
            .contentType(Optional.empty())
            .path("/testUri")
            .uri("http:///testUri")
            .query(Optional.empty())
            .characterEncoding(Optional.empty())
            .build();
    AbstractServletRequest result = factory.create(servletContext, req, null);
    Assert.assertNotNull(result);
    Assert.assertEquals("", result.getContextPath());
    Assert.assertEquals("/testUri", result.getPathInfo());
    Assert.assertEquals("/testUri", result.getRequestURI());
    Assert.assertEquals("http:///testUri", result.getRequestURL().toString());
    Assert.assertEquals(-1, result.getServerPort());
    Assert.assertEquals("", result.getServerName());
    Assert.assertFalse(result.isSecure());
  }

  @Test
  public void testGetWithPrefix() throws Exception {
    HttpServletRequestFactory factory = new HttpServletRequestFactory();
    Map<String, List<String>> headers = new HashMap<>();
    Map<String, List<String>> params = new HashMap<>();
    add(headers, HttpServletRequestFactory.X_FORWARD_PREFIX, "/myPrefix");
    HttpRequest req =
        ConcreteHttpRequest.builder()
            .headers(headers)
            .queryParameters(params)
            .method("GET")
            .contentType(Optional.empty())
            .path("/testUri")
            .uri("http://localhost/testUri")
            .query(Optional.empty())
            .characterEncoding(Optional.empty())
            .build();
    AbstractServletRequest result = factory.create(servletContext, req, null);
    Assert.assertNotNull(result);
    Assert.assertEquals("/myPrefix", result.getContextPath());
    Assert.assertEquals("/testUri", result.getPathInfo());
    Assert.assertEquals("/myPrefix/testUri", result.getRequestURI());
    Assert.assertEquals("http://localhost/myPrefix/testUri", result.getRequestURL().toString());
    Assert.assertEquals(-1, result.getServerPort());
    Assert.assertEquals("localhost", result.getServerName());
    Assert.assertFalse(result.isSecure());
  }

  @Test
  public void testGetWithForwardHost() throws Exception {
    HttpServletRequestFactory factory = new HttpServletRequestFactory();
    Map<String, List<String>> headers = new HashMap<>();
    Map<String, List<String>> params = new HashMap<>();
    add(headers, HttpServletRequestFactory.X_FORWARD_HOST, "barefoot.sf.net");
    add(headers, HttpServletRequestFactory.X_FORWARD_PORT, "8080");
    add(headers, HttpServletRequestFactory.X_FORWARD_PROTO, "http");
    HttpRequest req =
        ConcreteHttpRequest.builder()
            .headers(headers)
            .queryParameters(params)
            .method("GET")
            .contentType(Optional.empty())
            .path("/testUri")
            .uri("http://localhost/testUri")
            .query(Optional.empty())
            .characterEncoding(Optional.empty())
            .build();
    AbstractServletRequest result = factory.create(servletContext, req, null);
    Assert.assertNotNull(result);
    Assert.assertEquals("", result.getContextPath());
    Assert.assertEquals("/testUri", result.getPathInfo());
    Assert.assertEquals("/testUri", result.getRequestURI());
    Assert.assertEquals("http://barefoot.sf.net:8080/testUri", result.getRequestURL().toString());
    Assert.assertEquals(8080, result.getServerPort());
    Assert.assertEquals("barefoot.sf.net", result.getServerName());
    Assert.assertFalse(result.isSecure());
  }

  @Test
  public void testGetWithForwardHostAndPrefix() throws Exception {
    HttpServletRequestFactory factory = new HttpServletRequestFactory();
    Map<String, List<String>> headers = new HashMap<>();
    Map<String, List<String>> params = new HashMap<>();
    add(headers, HttpServletRequestFactory.X_FORWARD_HOST, "barefoot.sf.net");
    add(headers, HttpServletRequestFactory.X_FORWARD_PORT, "8443");
    add(headers, HttpServletRequestFactory.X_FORWARD_PROTO, "https");
    add(headers, HttpServletRequestFactory.X_FORWARD_PREFIX, "/myPrefix");
    HttpRequest req =
        ConcreteHttpRequest.builder()
            .headers(headers)
            .queryParameters(params)
            .method("GET")
            .contentType(Optional.empty())
            .path("/testUri")
            .uri("http://localhost/testUri")
            .query(Optional.empty())
            .characterEncoding(Optional.empty())
            .build();
    AbstractServletRequest result = factory.create(servletContext, req, null);
    Assert.assertNotNull(result);
    Assert.assertEquals("/myPrefix", result.getContextPath());
    Assert.assertEquals("/testUri", result.getPathInfo());
    Assert.assertEquals("/myPrefix/testUri", result.getRequestURI());
    Assert.assertEquals(
        "https://barefoot.sf.net:8443/myPrefix/testUri", result.getRequestURL().toString());
    Assert.assertEquals(8443, result.getServerPort());
    Assert.assertEquals("barefoot.sf.net", result.getServerName());
    Assert.assertTrue(result.isSecure());
  }

  void add(Map<String, List<String>> map, String key, String value) {
    List<String> list = map.get(key);
    if (list == null) {
      list = new ArrayList();
      map.put(key, list);
    }
    list.add(value);
  }
}
