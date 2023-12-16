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

package net.sf.barefoot.aws.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.barefoot.context.AbstractServletContext;
import net.sf.barefoot.context.AbstractServletRequest;
import net.sf.barefoot.context.AbstractServletResponse;
import net.sf.barefoot.context.BarefootContentType;
import net.sf.barefoot.context.BarefootServletException;

/** Dispatcher for requests received from Gateway */
public class BarefootAwsHandler
    implements RequestHandler<Map<String, Object>, Map<String, Object>> {
  final AbstractServletContext servletContext;
  final HttpServletRequestFactory requestFactory = new HttpServletRequestFactory();
  static final String CONTENT_TYPE = "Content-Type",
      STATUS_CODE = "statusCode",
      IS_BASE64_ENCODED = "isBase64Encoded",
      BODY = "body",
      HEADERS = "headers",
      COOKIES = "cookies";

  public BarefootAwsHandler(AbstractServletContext sc) {
    servletContext = sc;
  }

  public AbstractServletContext getServletContext() {
    return servletContext;
  }

  Map<String, Object> writeResponse(AbstractServletResponse response, Output output)
      throws IOException {
    Map<String, Object> reply = new HashMap<>();

    reply.put(STATUS_CODE, response.getStatus());

    List<String> cookies = response.getSetCookieHeaders();
    String contentType = response.getContentType();
    Map<String, String> headers = new HashMap<>();
    for (String name : response.getHeaderNames()) {
      switch (name.toLowerCase()) {
        case "content-type":
          contentType = response.getHeader(name);
          break;
        case "set-cookie":
          Collection<String> values = response.getHeaders(name);
          if (!values.isEmpty()) {
            cookies.addAll(values);
          }
          break;
        default:
          String value = response.getHeader(name);
          if (value != null) {
            headers.put(name, value);
          }
      }
    }

    if (output.outputStream != null) {
      byte[] toByteArray = output.outputStream.toByteArray();
      String csn = response.getCharacterEncoding();
      if (csn != null) {
        Charset charSet = Charset.forName(csn);
        reply.put(BODY, new String(toByteArray, charSet));
        reply.put(IS_BASE64_ENCODED, false);
      } else {
        if (BarefootContentType.isText(contentType)) {
          reply.put(BODY, new String(toByteArray, StandardCharsets.UTF_8));
          reply.put(IS_BASE64_ENCODED, false);
        } else {
          reply.put(BODY, Base64.getEncoder().encodeToString(toByteArray));
          reply.put(IS_BASE64_ENCODED, true);
        }
      }
    } else {
      if (output.writer != null) {
        reply.put(BODY, output.writer.toString());
        reply.put(IS_BASE64_ENCODED, false);
      }
    }

    if (contentType != null) {
      headers.put(CONTENT_TYPE, contentType);
    }

    if (!headers.isEmpty()) {
      reply.put(HEADERS, headers);
    }

    if (!cookies.isEmpty()) {
      reply.put(COOKIES, cookies);
    }

    return reply;
  }

  @Override
  public Map<String, Object> handleRequest(Map<String, Object> in, Context cntxt) {
    Map<String, Object> out;
    try {
      AbstractServletRequest request = requestFactory.create(servletContext, in, cntxt);
      Output output = new Output();
      AbstractServletResponse response =
          request
              .getServletResponseBuilder()
              .outputStream(() -> output.getOutputStream())
              .writer(() -> output.getWriter())
              .build();

      servletContext.dispatch(request, response);

      out = writeResponse(response, output);
    } catch (RuntimeException | IOException | BarefootServletException ex) {
      cntxt.getLogger().log(ex.getMessage());
      Logger.getGlobal().log(Level.INFO, "dispatch", ex);
      out = new HashMap<>();
      out.put(STATUS_CODE, 500);
    }

    return out;
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
  }
}
