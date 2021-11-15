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

package net.sf.barefoot.context.xml.aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import net.sf.barefoot.aws.concrete.ConcreteContext;
import net.sf.barefoot.aws.lambda.HttpServletRequestFactory;
import org.junit.Assert;
import org.junit.Test;

/** test harness for lambda */
public class HandlerTest {
  ObjectMapper mapper = new ObjectMapper();
  final TypeReference<Map<String, Object>> typeMapStringObject =
      new TypeReference<Map<String, Object>>() {};

  HttpServletRequestFactory requestFactory = new HttpServletRequestFactory();

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
    Assert.assertEquals(200, response.getStatusCode().intValue());
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
