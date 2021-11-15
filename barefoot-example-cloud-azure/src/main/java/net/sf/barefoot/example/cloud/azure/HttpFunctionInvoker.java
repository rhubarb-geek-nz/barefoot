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
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import java.util.Optional;

/** Azure Functions with HTTP Trigger. */
public class HttpFunctionInvoker
    extends org.springframework.cloud.function.adapter.azure.HttpFunctionInvoker<Optional> {

  /** initialise bootstrap spring cloud */
  static {
    System.setProperty("MAIN_CLASS", ExampleApplication.class.getCanonicalName());
  }

  /**
   * Entry for function. It is also used as part of the URL.
   *
   * @param request incoming request
   * @param context execution context
   * @return response
   */
  @FunctionName("ExampleFunction")
  public HttpResponseMessage run(
      @HttpTrigger(
              name = "req",
              methods = {HttpMethod.GET, HttpMethod.POST},
              authLevel = AuthorizationLevel.ANONYMOUS,
              dataType = "string")
          HttpRequestMessage request,
      final ExecutionContext context) {
    return handleRequest(request, context);
  }
}
