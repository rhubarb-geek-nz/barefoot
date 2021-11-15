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

package net.sf.barefoot.example.cloud.google;

import com.google.cloud.functions.HttpRequest;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.sf.barefoot.google.concrete.ConcreteHttpRequest;
import net.sf.barefoot.google.concrete.ConcreteHttpResponse;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/** test harness for google rest example */
public class ExampleFunctionTest {

  /* Test underlying function */
  @Test
  public void testFunction() {
    try (AnnotationConfigApplicationContext context =
        new AnnotationConfigApplicationContext(ExampleApplication.class)) {
      Function http = context.getBean(ExampleFunction.class);

      Object result = http.apply(null);

      Assert.assertNotNull(result);
    }
  }

  @Test
  public void testRequest() throws Exception {
    FunctionInvoker launcher = new FunctionInvoker();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ByteArrayOutputStream writeStuff = new ByteArrayOutputStream();
    byte[] content = "{}".getBytes();
    Map<String, List<String>> headers = new HashMap<>();
    Map<String, List<String>> query = new HashMap<>();
    HttpRequest httpRequest =
        ConcreteHttpRequest.builder()
            .method("POST")
            .contentLength(content.length)
            .contentType(Optional.of("application/json"))
            .inputStream(new ByteArrayInputStream(content))
            .characterEncoding(Optional.empty())
            .path("/function")
            .query(Optional.empty())
            .queryParameters(query)
            .uri("/function")
            .headers(headers)
            .build();

    ConcreteHttpResponse httpResponse;

    try (BufferedWriter writer =
        new BufferedWriter(new OutputStreamWriter(writeStuff, StandardCharsets.UTF_8))) {
      httpResponse = ConcreteHttpResponse.builder().setOutputStream(baos).setWriter(writer).build();

      httpResponse.setStatusCode(200);

      launcher.service(httpRequest, httpResponse);
    }

    Assert.assertEquals(200, httpResponse.getStatusCode());

    byte[] output = baos.toByteArray();
    byte[] written = writeStuff.toByteArray();

    Assert.assertTrue(output.length + written.length > 0);
  }
}
