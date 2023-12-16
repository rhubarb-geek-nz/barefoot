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

package net.sf.barefoot.example.metro.azure;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import net.sf.barefoot.azure.functions.HttpRequestMessageWrapper;

/** Azure Functions with HTTP Trigger. */
public class Function {
  final BiFunction<HttpRequestMessage<?>, ExecutionContext, HttpResponseMessage> function;

  public Function() throws Exception {
    function = new net.sf.barefoot.context.xml.azure.Function();
  }

  /**
   * The FunctionName needs to align with WebServices(serviceName) annotation on the service bean
   * and the Endpoint published in the bean factory
   *
   * @param request incoming request
   * @param context runtime context of the request
   * @return response status, body and headers
   */
  @FunctionName("ExampleService")
  public HttpResponseMessage run(
      @HttpTrigger(
              name = "req",
              methods = {HttpMethod.GET, HttpMethod.POST},
              authLevel = AuthorizationLevel.ANONYMOUS)
          HttpRequestMessage<Optional<String>> request,
      final ExecutionContext context) {

    if (request.getHttpMethod() == HttpMethod.GET) {
      Map<String, String> qp = request.getQueryParameters();
      if (qp == null || qp.isEmpty()) {
        /** Azure does not pass parameters if they have no value, we require this for WSDL */
        qp = new HashMap<>();
        qp.put("wsdl", null);
        request =
            new HttpRequestMessageWrapper.Builder<Optional<String>>()
                .request(request)
                .queryParameters(qp)
                .build();
      }
    }

    return function.apply(request, context);
  }
}
