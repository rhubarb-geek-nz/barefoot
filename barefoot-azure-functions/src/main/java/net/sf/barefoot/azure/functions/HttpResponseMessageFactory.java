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

import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import java.util.List;
import net.sf.barefoot.context.AbstractServletResponse;

/** encode responses for Azure */
public class HttpResponseMessageFactory {
  static final String CONTENT_TYPE = "Content-Type";

  public HttpResponseMessage create(
      HttpRequestMessage req, AbstractServletResponse resp, Object body) {
    HttpStatus status = HttpStatus.valueOf(resp.getStatus());
    HttpResponseMessage.Builder builder = req.createResponseBuilder(status);
    boolean hasContentType = false;
    String contentType = resp.getContentType();

    for (String header : resp.getHeaderNames()) {
      hasContentType |= header.equalsIgnoreCase(CONTENT_TYPE);
      for (String value : resp.getHeaders(header)) {
        builder = builder.header(header, value);
      }
    }

    if (contentType != null && !hasContentType) {
      builder = builder.header(CONTENT_TYPE, contentType);
    }

    if (body != null && !status.equals((HttpStatus.NO_CONTENT))) {
      builder = builder.body(body);
    }

    List<String> cookies = resp.getSetCookieHeaders();

    if (cookies != null && !cookies.isEmpty()) {
      for (String c : cookies) {
        builder = builder.header("Set-Cookie", c);
      }
    }

    return builder.build();
  }
}
