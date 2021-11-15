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

package net.sf.barefoot.aws.lambda;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import net.sf.barefoot.aws.concrete.ConcreteContext;
import net.sf.barefoot.context.AbstractServletContext;
import net.sf.barefoot.context.AbstractServletRequest;
import net.sf.barefoot.context.BarefootContentType;
import net.sf.barefoot.context.javax.BarefootServletResponse;
import net.sf.barefoot.testtool.HttpServletRequestState;
import net.sf.barefoot.testtool.javax.HttpServletRequestStateFactory;
import org.junit.Assert;
import org.junit.Test;

/** test harness for request factory */
public class HttpServletRequestFactoryTest {
  ObjectMapper mapper = new ObjectMapper();
  HttpServletRequestFactory requestFactory = new HttpServletRequestFactory();
  AbstractServletContext servletContext =
      new net.sf.barefoot.context.javax.BarefootServletContext("");
  final TypeReference<Map<String, Object>> typeMapStringObject =
      new TypeReference<Map<String, Object>>() {};

  public HttpServletRequestFactoryTest() {}

  @Test
  public void testParseQueryString() throws JsonProcessingException, UnsupportedEncodingException {
    HttpServletRequestFactory instance = new HttpServletRequestFactory();
    Map<String, List<String>> result =
        instance.parseQueryString("parameter1=value1&parameter1=value2&parameter2=value");
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(2, result.get("parameter1").size());
    Assert.assertEquals(1, result.get("parameter2").size());
  }

  @Test
  public void createRequestV1() throws IOException {
    Map<String, Object> map =
        mapper.readValue(
            getClass().getResourceAsStream("testHandleRequestV1.json"), typeMapStringObject);
    AbstractServletRequest request =
        requestFactory.create(servletContext, map, ConcreteContext.builder().build());

    Assert.assertEquals("HTTP/1.1", request.getProtocol());
    Assert.assertEquals("GET", request.getMethod());
    Assert.assertEquals("/my/path", request.getPathInfo());
    Assert.assertEquals("", request.getContextPath());
    Assert.assertEquals("http://localhost:8080/my/path", request.getRequestURL().toString());
  }

  @Test
  public void createRequestV2() throws IOException {
    Map<String, Object> map =
        mapper.readValue(
            getClass().getResourceAsStream("testHandleRequestV2.json"), typeMapStringObject);
    AbstractServletRequest request =
        requestFactory.create(servletContext, map, ConcreteContext.builder().build());

    Assert.assertEquals("HTTP/1.1", request.getProtocol());
    Assert.assertEquals("POST", request.getMethod());
    Assert.assertEquals("/my/path", request.getPathInfo());
    Assert.assertEquals("", request.getContextPath());
    Assert.assertEquals("https://level:42/my/path", request.getRequestURL().toString());
  }

  @Test
  public void createRequestActual() throws IOException {
    Map<String, Object> map =
        mapper.readValue(
            getClass().getResourceAsStream("actualRequestV2.json"), typeMapStringObject);
    AbstractServletRequest request =
        requestFactory.create(servletContext, map, ConcreteContext.builder().build());

    Assert.assertEquals("HTTP/1.1", request.getProtocol());
    Assert.assertEquals("GET", request.getMethod());
    Assert.assertEquals("/", request.getPathInfo());
    Assert.assertEquals("/test", request.getContextPath());
    Assert.assertEquals("/test/", request.getRequestURI());
    Assert.assertEquals(
        "https://74i30a9lbi.execute-api.ap-southeast-2.amazonaws.com/test/",
        request.getRequestURL().toString());

    BarefootServletResponse response =
        BarefootServletResponse.builder().outputStream(() -> new ByteArrayOutputStream()).build();
    HttpServletRequestStateFactory factory = new HttpServletRequestStateFactory();
    HttpServletRequestState<Cookie> state = factory.create((HttpServletRequest) request, response);

    Assert.assertEquals(
        "https://74i30a9lbi.execute-api.ap-southeast-2.amazonaws.com/test/", state.getRequestURL());
  }

  @Test
  public void createRequestPath() throws IOException {
    Map<String, Object> map =
        mapper.readValue(getClass().getResourceAsStream("pathRequest.json"), typeMapStringObject);
    AbstractServletRequest request =
        requestFactory.create(servletContext, map, ConcreteContext.builder().build());

    Assert.assertEquals("HTTP/1.1", request.getProtocol());
    Assert.assertEquals("GET", request.getMethod());
    Assert.assertEquals("/wibble2", request.getPathInfo());
    Assert.assertEquals("/test", request.getContextPath());
    Assert.assertEquals("/test/wibble2", request.getRequestURI());
    Assert.assertEquals(
        "https://74i30a9lbi.execute-api.ap-southeast-2.amazonaws.com/test/wibble2",
        request.getRequestURL().toString());
  }

  @Test
  public void createParamsList() throws IOException {
    Map<String, Object> map =
        mapper.readValue(getClass().getResourceAsStream("paramsList.json"), typeMapStringObject);
    AbstractServletRequest request =
        requestFactory.create(servletContext, map, ConcreteContext.builder().build());

    Assert.assertEquals("HTTP/1.1", request.getProtocol());
    Assert.assertEquals("GET", request.getMethod());
    Assert.assertEquals("/wibble2", request.getPathInfo());
    Assert.assertEquals("/test", request.getContextPath());
    Assert.assertEquals("/test/wibble2", request.getRequestURI());
    Assert.assertEquals(
        "https://74i30a9lbi.execute-api.ap-southeast-2.amazonaws.com/test/wibble2",
        request.getRequestURL().toString());

    Map<String, String[]> params = request.getParameterMap();

    Assert.assertEquals(1, params.keySet().size());
    Assert.assertArrayEquals(new String[] {"alpha"}, params.get("A"));
  }

  @Test
  public void createXFormBody() throws IOException {
    Map<String, Object> map =
        mapper.readValue(getClass().getResourceAsStream("xformBody.json"), typeMapStringObject);
    AbstractServletRequest request =
        requestFactory.create(servletContext, map, ConcreteContext.builder().build());

    Assert.assertEquals("HTTP/1.1", request.getProtocol());
    Assert.assertEquals("POST", request.getMethod());
    Assert.assertEquals("/wibble2", request.getPathInfo());
    Assert.assertEquals("/test", request.getContextPath());
    Assert.assertEquals("/test/wibble2", request.getRequestURI());
    Assert.assertEquals(
        "https://74i30a9lbi.execute-api.ap-southeast-2.amazonaws.com/test/wibble2",
        request.getRequestURL().toString());

    Map<String, String[]> params = request.getParameterMap();

    Assert.assertEquals(BarefootContentType.APPLICATION_FORM_URLENCODED, request.getContentType());

    Assert.assertEquals(2, params.keySet().size());
    Assert.assertArrayEquals(new String[] {"alpha"}, params.get("A"));
    Assert.assertArrayEquals(new String[] {"bravo"}, params.get("B"));
  }
}
