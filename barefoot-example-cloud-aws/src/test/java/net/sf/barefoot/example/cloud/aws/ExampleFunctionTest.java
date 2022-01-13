/*
 *
 *  Copyright 2021, Roger Brown
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

package net.sf.barefoot.example.cloud.aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;
import net.sf.barefoot.aws.concrete.ConcreteContext;
import org.junit.Assert;
import org.junit.Test;

/** test harness for handler */
public class ExampleFunctionTest {
  final ObjectMapper mapper = new ObjectMapper();
  final TypeReference<Map<String, Object>> typeMapStringObject =
      new TypeReference<Map<String, Object>>() {};

  public ExampleFunctionTest() {}

  @Test
  public void testHandleRequestV1() throws Exception {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Context cntxt = ConcreteContext.builder().build();
    FunctionInvoker instance = new FunctionInvoker();

    try (InputStream is = getClass().getResourceAsStream("testHandleRequestV1.json")) {
      instance.handleRequest(is, out, cntxt);
    }

    byte[] outBytes = out.toByteArray();
    Assert.assertNotNull(outBytes);
    Map<String, Object> response =
        mapper.readValue(new ByteArrayInputStream(outBytes), typeMapStringObject);
    Assert.assertNotNull("response", response);
    Assert.assertEquals("statusCode", Integer.valueOf(200), (Integer) response.get("statusCode"));
    Assert.assertEquals("Example Bean", response.get("body"));
    Assert.assertFalse("isBase64Encoded", (Boolean) response.get("isBase64Encoded"));
  }

  @Test
  public void getTestApiHttpExample() throws Exception {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Context cntxt = ConcreteContext.builder().build();
    FunctionInvoker instance = new FunctionInvoker();

    try (InputStream is = getClass().getResourceAsStream("getTestApiHttpExample.json")) {
      instance.handleRequest(is, out, cntxt);
    }

    byte[] outBytes = out.toByteArray();
    Assert.assertNotNull(outBytes);

    Map<String, Object> response =
        mapper.readValue(new ByteArrayInputStream(outBytes), typeMapStringObject);

    Assert.assertNotNull("response", response);
    Assert.assertEquals("statusCode", Integer.valueOf(200), (Integer) response.get("statusCode"));
    Assert.assertEquals("Example Bean", response.get("body"));
    Assert.assertFalse("isBase64Encoded", (Boolean) response.get("isBase64Encoded"));
  }
}
