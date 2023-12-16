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

package net.sf.barefoot.azure.functions;

import com.google.gson.Gson;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.BiFunction;
import java.util.logging.Level;
import net.sf.barefoot.context.AbstractServletContext;
import net.sf.barefoot.context.AbstractServletRequest;
import net.sf.barefoot.context.AbstractServletResponse;
import net.sf.barefoot.context.BarefootServletException;

/** dispatcher for Azure messages */
public class BarefootAzureFunction
    implements BiFunction<HttpRequestMessage<?>, ExecutionContext, HttpResponseMessage> {
  final AbstractServletContext servletContext;
  final HttpServletRequestFactory requestFactory;
  final HttpResponseMessageFactory responseFactory;

  public BarefootAzureFunction(AbstractServletContext ctx, Gson gson) {
    servletContext = ctx;
    requestFactory = new HttpServletRequestFactory(gson);
    responseFactory = new HttpResponseMessageFactory();
  }

  public BarefootAzureFunction(AbstractServletContext ctx) {
    servletContext = ctx;
    requestFactory = new HttpServletRequestFactory(null);
    responseFactory = new HttpResponseMessageFactory();
  }

  @Override
  public HttpResponseMessage apply(HttpRequestMessage<?> request, final ExecutionContext context) {
    HttpResponseMessage response;

    try {
      Output output = new Output();
      AbstractServletRequest sreq = requestFactory.create(servletContext, request, context);
      AbstractServletResponse resp =
          sreq.getServletResponseBuilder()
              .outputStream(() -> output.getOutputStream())
              .writer(() -> output.getWriter())
              .build();

      servletContext.dispatch(sreq, resp);

      response = responseFactory.create(request, resp, output.getBody());
    } catch (RuntimeException | IOException | BarefootServletException ex) {
      context.getLogger().log(Level.INFO, ex.getMessage(), ex);
      response = request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    return response;
  }

  private static class Output {
    ByteArrayOutputStream outputStream;
    StringWriter writer;
    PrintWriter printer;

    OutputStream getOutputStream() {
      if (writer != null) throw new IllegalStateException("writer already open");
      if (outputStream == null) outputStream = new ByteArrayOutputStream();
      return outputStream;
    }

    PrintWriter getWriter() {
      if (outputStream != null) throw new IllegalStateException("stream already open");
      if (writer == null) {
        writer = new StringWriter();
        printer = new PrintWriter(writer);
      }
      return printer;
    }

    Object getBody() {
      return outputStream == null
          ? writer == null ? null : writer.toString()
          : outputStream.toByteArray();
    }
  }
}
