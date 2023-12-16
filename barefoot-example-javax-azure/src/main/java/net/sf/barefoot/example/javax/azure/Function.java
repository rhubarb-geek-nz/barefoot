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

package net.sf.barefoot.example.javax.azure;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import java.util.Optional;
import java.util.function.BiFunction;

/** Azure Functions with HTTP Trigger. */
public class Function {
  final BiFunction<HttpRequestMessage<?>, ExecutionContext, HttpResponseMessage> function;

  /**
   * Constructor for function
   *
   * @throws java.lang.Exception
   */
  public Function() throws Exception {
    function = new net.sf.barefoot.context.xml.azure.Function();
  }

  /**
   * This function listens at endpoint "/api/HttpExample".Two ways to invoke it using "curl" command
   * in bash: 1.curl -d "HTTP Body" {your host}/api/HttpExample 2. curl "{your
   * host}/api/HttpExample?name=HTTP%20Query"
   *
   * @param request client request
   * @param context runtime context
   * @return response contains header status and body
   */
  @FunctionName("HttpExample")
  public HttpResponseMessage run(
      @HttpTrigger(
              name = "req",
              methods = {HttpMethod.GET, HttpMethod.POST},
              authLevel = AuthorizationLevel.ANONYMOUS)
          HttpRequestMessage<Optional<String>> request,
      final ExecutionContext context) {
    return function.apply(request, context);
  }
}
