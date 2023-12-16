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

package net.sf.barefoot.example.cxf.aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import net.sf.barefoot.aws.concrete.ConcreteContext;
import net.sf.barefoot.aws.lambda.HttpServletRequestFactory;
import net.sf.barefoot.context.javax.BarefootServletContext;
import net.sf.barefoot.context.xml.aws.Handler;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/** test harness for lambda */
public class HandlerTest {
  ObjectMapper mapper = new ObjectMapper();
  final TypeReference<Map<String, Object>> typeMapStringObject =
      new TypeReference<Map<String, Object>>() {};
  HttpServletRequestFactory requestFactory = new HttpServletRequestFactory();
  ServletContext servletContext = new BarefootServletContext("");

  public HandlerTest() {}

  @Test
  public void testHandleRequestV1() throws Exception {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Context cntxt = ConcreteContext.builder().build();
    Handler instance = new Handler();

    handleRequest(instance, getClass().getResourceAsStream("testHandleRequestV1.json"), out, cntxt);
    byte[] outBytes = out.toByteArray();
    Assert.assertNotNull(outBytes);

    APIGatewayProxyResponseEvent response =
        mapper.readValue(new ByteArrayInputStream(outBytes), APIGatewayProxyResponseEvent.class);
    Assert.assertNotNull(response);

    Assert.assertEquals(200, response.getStatusCode().intValue());
    Assert.assertEquals("text/xml; charset=UTF-8", response.getHeaders().get("Content-Type"));
    Assert.assertEquals(Boolean.FALSE, response.getIsBase64Encoded());

    String body = response.getBody();
    Assert.assertNotNull(body);

    readXML(body);
  }

  @Test
  public void testHandleRequestV2() throws Exception {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Context cntxt = ConcreteContext.builder().build();
    Handler instance = new Handler();

    handleRequest(instance, getClass().getResourceAsStream("testHandleRequestV2.json"), out, cntxt);
    byte[] outBytes = out.toByteArray();
    Assert.assertNotNull(outBytes);

    APIGatewayV2HTTPResponse response =
        mapper.readValue(new ByteArrayInputStream(outBytes), APIGatewayV2HTTPResponse.class);

    Assert.assertEquals(200, response.getStatusCode());
    Assert.assertEquals("text/xml; charset=UTF-8", response.getHeaders().get("Content-Type"));
    Assert.assertEquals(false, response.getIsBase64Encoded());

    String body = response.getBody();
    readXML(body);
  }

  Document readXML(InputStream is) throws ParserConfigurationException, SAXException, IOException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    return builder.parse(is);
  }

  Document readXML(String s) throws ParserConfigurationException, SAXException, IOException {
    return readXML(new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8)));
  }

  void handleRequest(
      RequestHandler<Map<String, Object>, Map<String, Object>> instance,
      InputStream resourceAsStream,
      ByteArrayOutputStream out,
      Context cntxt)
      throws IOException {
    Map<String, Object> in = mapper.readValue(resourceAsStream, typeMapStringObject);
    Map<String, Object> map = instance.handleRequest(in, cntxt);
    mapper.writeValue(out, map);
  }
}
