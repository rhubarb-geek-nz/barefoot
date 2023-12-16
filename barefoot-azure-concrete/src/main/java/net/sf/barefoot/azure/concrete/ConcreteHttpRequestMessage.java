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

import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.HttpStatusType;
import java.net.URI;
import java.util.Map;

/**
 * concrete request for testing
 *
 * @param <T> body type
 */
public class ConcreteHttpRequestMessage<T> implements HttpRequestMessage<T> {
  final URI uri;
  final HttpMethod method;
  final Map<String, String> headers;
  final Map<String, String> params;
  final T body;

  private ConcreteHttpRequestMessage(
      URI u, HttpMethod m, Map<String, String> h, Map<String, String> p, T b) {
    uri = u;
    method = m;
    headers = h;
    params = p;
    body = b;
  }

  @Override
  public URI getUri() {
    return uri;
  }

  @Override
  public HttpMethod getHttpMethod() {
    return method;
  }

  @Override
  public Map<String, String> getHeaders() {
    return headers;
  }

  @Override
  public Map<String, String> getQueryParameters() {
    return params;
  }

  @Override
  public T getBody() {
    return body;
  }

  @Override
  public HttpResponseMessage.Builder createResponseBuilder(HttpStatus hs) {
    return new ConcreteHttpResponseMessage.Builder(hs);
  }

  @Override
  public HttpResponseMessage.Builder createResponseBuilder(HttpStatusType hst) {
    return new ConcreteHttpResponseMessage.Builder(hst);
  }

  /**
   * create a builder
   *
   * @param <T> type body
   * @param body body
   * @return builder
   */
  public static <T> Builder<T> builder(T body) {
    return new Builder(body);
  }

  public static <T> Builder<T> builder(Class<T> body) {
    return new Builder(null);
  }

  public static <T> Builder<T> builder() {
    return new Builder(null);
  }

  public static class Builder<T> {
    URI uri;
    HttpMethod method;
    Map<String, String> headers;
    Map<String, String> params;
    T body;

    public ConcreteHttpRequestMessage<T> build() {
      return new ConcreteHttpRequestMessage<>(uri, method, headers, params, body);
    }

    Builder(T b) {
      body = b;
    }

    public Builder<T> method(HttpMethod m) {
      method = m;
      return this;
    }

    public Builder<T> uri(URI u) {
      uri = u;
      return this;
    }

    public Builder<T> body(T b) {
      body = b;
      return this;
    }

    public Builder<T> headers(Map<String, String> h) {
      headers = h;
      return this;
    }

    public Builder<T> queryParameters(Map<String, String> h) {
      params = h;
      return this;
    }
  }
}
