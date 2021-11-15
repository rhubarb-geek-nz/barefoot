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

package net.sf.barefoot.example.jersey.azure;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.sf.barefoot.azure.concrete.ConcreteExecutionContext;
import net.sf.barefoot.azure.concrete.ConcreteHttpRequestMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Unit test for Function class. */
public class FunctionTest {
  /**
   * Unit test for HttpTriggerJava method.
   *
   * @throws java.lang.Exception on error
   */
  @Test
  public void testHttpTriggerJava() throws Exception {
    // Setup
    URI uri = new URI("/api/HttpExample");
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

    // Invoke
    final HttpResponseMessage ret = new Function().run(req, context);

    // Verify
    Assertions.assertEquals(HttpStatus.OK, ret.getStatus());

    Assertions.assertNotNull(ret.getBody());
  }
}
