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

package net.sf.barefoot.example.boot.aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import net.sf.barefoot.aws.concrete.ConcreteContext;
import net.sf.barefoot.aws.lambda.HttpServletRequestFactory;
import net.sf.barefoot.context.xml.aws.Handler;
import org.junit.Assert;
import org.junit.Test;

/** test harness for handler */
public class HandlerTest {
  final ObjectMapper mapper = new ObjectMapper();
  final HttpServletRequestFactory requestFactory = new HttpServletRequestFactory();
  final TypeReference<Map<String, Object>> typeMapStringObject =
      new TypeReference<Map<String, Object>>() {};

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
    Assert.assertNotNull(response);
    Assert.assertEquals(200, response.getStatusCode());
  }

  @Test
  public void getTestApiHttpExample() throws Exception {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Context cntxt = ConcreteContext.builder().build();
    Handler instance = new Handler();

    handleRequest(
        instance, getClass().getResourceAsStream("getTestApiHttpExample.json"), out, cntxt);
    byte[] outBytes = out.toByteArray();
    Assert.assertNotNull(outBytes);

    APIGatewayV2HTTPResponse response =
        mapper.readValue(new ByteArrayInputStream(outBytes), APIGatewayV2HTTPResponse.class);

    Assert.assertEquals(200, response.getStatusCode());
    String body = response.getBody();
    Assert.assertFalse(response.getIsBase64Encoded());

    Map state = mapper.readValue(body, Map.class);

    Assert.assertEquals("/api/HttpExample", state.get("pathInfo"));
    Assert.assertEquals("/test", state.get("contextPath"));
    Assert.assertEquals("/test/api/HttpExample", state.get("requestURI"));
  }

  private void handleRequest(
      Handler instance, InputStream in, ByteArrayOutputStream out, Context cntxt)
      throws IOException {
    Map<String, Object> request = mapper.readValue(in, typeMapStringObject);
    Map<String, Object> response = instance.handleRequest(request, cntxt);
    mapper.writeValue(out, response);
  }
}
