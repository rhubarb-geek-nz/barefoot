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
package net.sf.barefoot.azure.concrete;

import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatusType;
import java.util.HashMap;
import java.util.Map;

/** Concrete response for testing */
public class ConcreteHttpResponseMessage implements HttpResponseMessage {
  private final HttpStatusType httpStatus;
  private final Object body;
  private final Map<String, String> headers;

  private ConcreteHttpResponseMessage(
      HttpStatusType status, Map<String, String> headers, Object body) {
    this.httpStatus = status;
    this.headers = headers;
    this.body = body;
  }

  @Override
  public HttpStatusType getStatus() {
    return httpStatus;
  }

  @Override
  public int getStatusCode() {
    return httpStatus.value();
  }

  @Override
  public String getHeader(String key) {
    return headers.get(key);
  }

  @Override
  public Object getBody() {
    return body;
  }

  static class Builder implements HttpResponseMessage.Builder {
    private Object body;
    private final Map<String, String> headers = new HashMap<>();
    private HttpStatusType httpStatus;

    Builder(HttpStatusType httpStatusType) {
      this.httpStatus = httpStatusType;
    }

    @Override
    public Builder status(HttpStatusType httpStatusType) {
      this.httpStatus = httpStatusType;
      return this;
    }

    @Override
    public Builder header(String key, String value) {
      this.headers.put(key, value);
      return this;
    }

    @Override
    public Builder body(Object body) {
      this.body = body;
      return this;
    }

    @Override
    public HttpResponseMessage build() {
      return new ConcreteHttpResponseMessage(httpStatus, headers, body);
    }
  }
}
