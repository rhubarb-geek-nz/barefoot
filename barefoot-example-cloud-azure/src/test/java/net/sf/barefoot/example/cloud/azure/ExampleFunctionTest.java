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

package net.sf.barefoot.example.cloud.azure;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.sf.barefoot.azure.concrete.ConcreteExecutionContext;
import net.sf.barefoot.azure.concrete.ConcreteHttpRequestMessage;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import reactor.core.publisher.Mono;

public class ExampleFunctionTest {
  /**
   * Test HTTP invocation of the function The invoker depends on the system properties to find the
   * main class and function name
   *
   * @throws URISyntaxException
   */
  @Test
  public void testInvoker() throws URISyntaxException {
    URI uri = new URI("/api/ExampleFunction");
    HttpMethod method = HttpMethod.GET;

    final Map<String, String> queryParams = new HashMap<>();
    queryParams.put("name", "Azure");

    final Optional<String> queryBody = Optional.empty();
    final HttpRequestMessage<Optional<String>> req =
        ConcreteHttpRequestMessage.builder(queryBody)
            .uri(uri)
            .method(method)
            .queryParameters(queryParams)
            .build();
    final ExecutionContext context = new ConcreteExecutionContext();
    HttpFunctionInvoker invoker = new HttpFunctionInvoker();

    try {
      HttpResponseMessage ret = invoker.run(req, context);
      Assert.assertEquals(HttpStatus.OK, ret.getStatus());
      Assert.assertNotNull(ret.getBody());
    } finally {
      invoker.close();
    }
  }

  /* Test underlying function */
  @Test
  public void testFunction() {
    try (AnnotationConfigApplicationContext context =
        new AnnotationConfigApplicationContext(ExampleConfiguration.class)) {
      ExampleFunction http = context.getBean(ExampleFunction.class);

      Mono<String> result = http.apply(Mono.just(Optional.empty()));

      Assert.assertNotNull(result);
    }
  }
}
