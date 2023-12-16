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

package net.sf.barefoot.azure.functions;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import net.sf.barefoot.azure.concrete.ConcreteExecutionContext;
import net.sf.barefoot.azure.concrete.ConcreteHttpRequestMessage;
import net.sf.barefoot.context.AbstractServletContext;
import net.sf.barefoot.context.AbstractServletRequest;
import net.sf.barefoot.context.BarefootContentType;
import org.junit.Assert;
import org.junit.Test;

/** test cases for request factory */
public class HttpServletRequestFactoryTest {

  public HttpServletRequestFactoryTest() {}

  AbstractServletContext servletContext =
      new net.sf.barefoot.context.javax.BarefootServletContext("");

  @Test
  public void testGetLocalhost() throws Exception {
    HttpServletRequestFactory factory = new HttpServletRequestFactory(null);
    ExecutionContext execContext = new ConcreteExecutionContext();
    String body = null;
    Map<String, String> headers = new HashMap<>();
    Map<String, String> params = new HashMap<>();
    URI uri = new URI("/testUri");
    headers.put("host", "localhost");
    HttpRequestMessage<String> req =
        ConcreteHttpRequestMessage.builder(body)
            .headers(headers)
            .queryParameters(params)
            .method(HttpMethod.GET)
            .uri(uri)
            .build();
    AbstractServletRequest result = factory.create(servletContext, req, execContext);
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
  public void testGetLocalhost8080() throws Exception {
    HttpServletRequestFactory factory = new HttpServletRequestFactory(null);
    ExecutionContext execContext = new ConcreteExecutionContext();
    String body = null;
    Map<String, String> headers = new HashMap<>();
    Map<String, String> params = new HashMap<>();
    URI uri = new URI("/testUri");
    headers.put("host", "localhost:8080");
    HttpRequestMessage<String> req =
        ConcreteHttpRequestMessage.builder(body)
            .headers(headers)
            .queryParameters(params)
            .method(HttpMethod.GET)
            .uri(uri)
            .build();
    AbstractServletRequest result = factory.create(servletContext, req, execContext);
    Assert.assertNotNull(result);
    Assert.assertEquals("", result.getContextPath());
    Assert.assertEquals("/testUri", result.getPathInfo());
    Assert.assertEquals("/testUri", result.getRequestURI());
    Assert.assertEquals("http://localhost:8080/testUri", result.getRequestURL().toString());
    Assert.assertEquals(8080, result.getServerPort());
    Assert.assertEquals("localhost", result.getServerName());
    Assert.assertFalse(result.isSecure());
  }

  @Test
  public void testGetServer8080() throws Exception {
    HttpServletRequestFactory factory = new HttpServletRequestFactory(null);
    ExecutionContext execContext = new ConcreteExecutionContext();
    String body = null;
    Map<String, String> headers = new HashMap<>();
    Map<String, String> params = new HashMap<>();
    URI uri = new URI("/testUri");
    headers.put("host", "server:8080");
    HttpRequestMessage<String> req =
        ConcreteHttpRequestMessage.builder(body)
            .headers(headers)
            .queryParameters(params)
            .method(HttpMethod.GET)
            .uri(uri)
            .build();
    AbstractServletRequest result = factory.create(servletContext, req, execContext);
    Assert.assertNotNull(result);
    Assert.assertEquals("", result.getContextPath());
    Assert.assertEquals("/testUri", result.getPathInfo());
    Assert.assertEquals("/testUri", result.getRequestURI());
    Assert.assertEquals("https://server:8080/testUri", result.getRequestURL().toString());
    Assert.assertEquals(8080, result.getServerPort());
    Assert.assertEquals("server", result.getServerName());
    Assert.assertTrue(result.isSecure());
  }

  @Test
  public void testGetAnonymous() throws Exception {
    HttpServletRequestFactory factory = new HttpServletRequestFactory(null);
    ExecutionContext execContext = new ConcreteExecutionContext();
    String body = null;
    Map<String, String> headers = new HashMap<>();
    Map<String, String> params = new HashMap<>();
    URI uri = new URI("/testUri");
    HttpRequestMessage<String> req =
        ConcreteHttpRequestMessage.builder(body)
            .headers(headers)
            .queryParameters(params)
            .method(HttpMethod.GET)
            .uri(uri)
            .build();
    AbstractServletRequest result = factory.create(servletContext, req, execContext);
    Assert.assertNotNull(result);
    Assert.assertEquals("", result.getContextPath());
    Assert.assertEquals("/testUri", result.getPathInfo());
    Assert.assertEquals("/testUri", result.getRequestURI());
    Assert.assertEquals("/testUri", result.getRequestURL().toString());
    Assert.assertEquals(-1, result.getServerPort());
    Assert.assertNull(result.getServerName());
    Assert.assertFalse(result.isSecure());
  }

  @Test
  public void testGetWithPrefix() throws Exception {
    HttpServletRequestFactory factory = new HttpServletRequestFactory(null);
    ExecutionContext execContext = new ConcreteExecutionContext();
    String body = null;
    Map<String, String> headers = new HashMap<>();
    Map<String, String> params = new HashMap<>();
    URI uri = new URI("/testUri");
    headers.put("host", "localhost");
    headers.put(HttpServletRequestFactory.X_FORWARD_PREFIX, "/myPrefix");
    HttpRequestMessage<String> req =
        ConcreteHttpRequestMessage.builder(body)
            .headers(headers)
            .queryParameters(params)
            .method(HttpMethod.GET)
            .uri(uri)
            .build();
    AbstractServletRequest result = factory.create(servletContext, req, execContext);
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
    HttpServletRequestFactory factory = new HttpServletRequestFactory(null);
    ExecutionContext execContext = new ConcreteExecutionContext();
    String body = null;
    Map<String, String> headers = new HashMap<>();
    Map<String, String> params = new HashMap<>();
    URI uri = new URI("/testUri");
    headers.put("host", "localhost:1234");
    headers.put(HttpServletRequestFactory.X_FORWARD_HOST, "barefoot.sf.net");
    headers.put(HttpServletRequestFactory.X_FORWARD_PORT, "8080");
    headers.put(HttpServletRequestFactory.X_FORWARD_PROTO, "http");
    HttpRequestMessage<String> req =
        ConcreteHttpRequestMessage.builder(body)
            .headers(headers)
            .queryParameters(params)
            .method(HttpMethod.GET)
            .uri(uri)
            .build();
    AbstractServletRequest result = factory.create(servletContext, req, execContext);
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
    HttpServletRequestFactory factory = new HttpServletRequestFactory(null);
    ExecutionContext execContext = new ConcreteExecutionContext();
    String body = null;
    Map<String, String> headers = new HashMap<>();
    Map<String, String> params = new HashMap<>();
    URI uri = new URI("/testUri");
    headers.put("host", "localhost:1234");
    headers.put(HttpServletRequestFactory.X_FORWARD_HOST, "barefoot.sf.net");
    headers.put(HttpServletRequestFactory.X_FORWARD_PORT, "8443");
    headers.put(HttpServletRequestFactory.X_FORWARD_PROTO, "https");
    headers.put(HttpServletRequestFactory.X_FORWARD_PREFIX, "/myPrefix");
    HttpRequestMessage<String> req =
        ConcreteHttpRequestMessage.builder(body)
            .headers(headers)
            .queryParameters(params)
            .method(HttpMethod.GET)
            .uri(uri)
            .build();
    AbstractServletRequest result = factory.create(servletContext, req, execContext);
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

  @Test
  public void testXformWithQP() throws Exception {
    HttpServletRequestFactory factory = new HttpServletRequestFactory(null);
    ExecutionContext execContext = new ConcreteExecutionContext();

    String body = "B=bravo";
    Map<String, String> headers = new HashMap<>();
    Map<String, String> params = new HashMap<>();
    headers.put("Content-Type", BarefootContentType.APPLICATION_FORM_URLENCODED);
    params.put("A", "alpha");
    URI uri = new URI("/testUri");
    HttpRequestMessage<String> req =
        ConcreteHttpRequestMessage.builder(body)
            .headers(headers)
            .queryParameters(params)
            .method(HttpMethod.POST)
            .uri(uri)
            .build();
    AbstractServletRequest result = factory.create(servletContext, req, execContext);
    Assert.assertNotNull(result);

    Map<String, String[]> reqParams = result.getParameterMap();
    Assert.assertEquals(2, reqParams.size());
    Assert.assertEquals("A=alpha", result.getQueryString());
  }

  @Test
  public void testXform() throws Exception {
    HttpServletRequestFactory factory = new HttpServletRequestFactory(null);
    ExecutionContext execContext = new ConcreteExecutionContext();

    String body = "B=bravo";
    Map<String, String> headers = new HashMap<>();
    Map<String, String> params = new HashMap<>();
    headers.put("Content-Type", BarefootContentType.APPLICATION_FORM_URLENCODED);
    URI uri = new URI("/testUri");
    HttpRequestMessage<String> req =
        ConcreteHttpRequestMessage.builder(body)
            .headers(headers)
            .method(HttpMethod.POST)
            .uri(uri)
            .build();
    AbstractServletRequest result = factory.create(servletContext, req, execContext);
    Assert.assertNotNull(result);

    Map<String, String[]> reqParams = result.getParameterMap();
    Assert.assertEquals(1, reqParams.size());
    Assert.assertNull(result.getQueryString());
  }
}
