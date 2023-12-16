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

import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.HttpStatusType;
import java.net.URI;
import java.util.Map;

/**
 * wrapper for request message
 *
 * @param <T> body type
 */
public class HttpRequestMessageWrapper<T extends Object> implements HttpRequestMessage<T> {
  private final HttpRequestMessage<T> original;
  private final Map<String, String> queryParameters, headers;
  private final T body;

  @Override
  public URI getUri() {
    return original.getUri();
  }

  @Override
  public HttpMethod getHttpMethod() {
    return original.getHttpMethod();
  }

  @Override
  public Map<String, String> getHeaders() {
    return headers == null ? original.getHeaders() : headers;
  }

  @Override
  public Map<String, String> getQueryParameters() {
    return queryParameters == null ? original.getQueryParameters() : queryParameters;
  }

  @Override
  public T getBody() {
    return body == null ? original.getBody() : body;
  }

  @Override
  public HttpResponseMessage.Builder createResponseBuilder(HttpStatus hs) {
    return original.createResponseBuilder(hs);
  }

  @Override
  public HttpResponseMessage.Builder createResponseBuilder(HttpStatusType hst) {
    return original.createResponseBuilder(hst);
  }

  public static class Builder<T> {
    HttpRequestMessage<T> original;
    Map<String, String> queryParameters, headers;
    T body;

    public Builder<T> queryParameters(Map<String, String> m) {
      queryParameters = m;
      return this;
    }

    public Builder<T> headers(Map<String, String> m) {
      headers = m;
      return this;
    }

    public Builder<T> request(HttpRequestMessage<T> r) {
      original = r;
      return this;
    }

    public Builder<T> body(T b) {
      body = b;
      return this;
    }

    public HttpRequestMessageWrapper<T> build() {
      return new HttpRequestMessageWrapper<>(this);
    }
  }

  HttpRequestMessageWrapper(Builder<T> builder) {
    original = builder.original;
    headers = builder.headers;
    queryParameters = builder.queryParameters;
    body = builder.body;
  }
}
