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

package net.sf.barefoot.google.concrete;

import com.google.cloud.functions.HttpRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** concrete request for testing */
public class ConcreteHttpRequest implements HttpRequest {
  final String method, uri, path;
  final Optional<String> query, contentType, characterEncoding;
  final Map<String, List<String>> headers, queryParameters;
  final long contentLength;
  final InputStream inputStream;

  private ConcreteHttpRequest(Builder builder) {
    method = builder.method;
    uri = builder.uri;
    path = builder.path;
    headers = builder.headers;
    queryParameters = builder.queryParameters;
    contentLength = builder.contentLength;
    inputStream = builder.inputStream;
    query = builder.query;
    contentType = builder.contentType;
    characterEncoding = builder.characterEncoding;
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public String getMethod() {
    return method;
  }

  @Override
  public String getUri() {
    return uri;
  }

  @Override
  public String getPath() {
    return path;
  }

  @Override
  public Optional<String> getQuery() {
    return query;
  }

  @Override
  public Map<String, List<String>> getQueryParameters() {
    return queryParameters;
  }

  @Override
  public Map<String, HttpPart> getParts() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Optional<String> getContentType() {
    return contentType;
  }

  @Override
  public long getContentLength() {
    return contentLength;
  }

  @Override
  public Optional<String> getCharacterEncoding() {
    return characterEncoding;
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return inputStream;
  }

  @Override
  public BufferedReader getReader() throws IOException {
    Charset cs =
        characterEncoding.isPresent()
            ? Charset.forName(characterEncoding.get())
            : StandardCharsets.UTF_8;
    return new BufferedReader(new InputStreamReader(getInputStream(), cs));
  }

  @Override
  public Map<String, List<String>> getHeaders() {
    return headers;
  }

  public static class Builder {
    String uri, method, path;
    Map<String, List<String>> headers, queryParameters;
    InputStream inputStream;
    long contentLength = -1L;
    Optional<String> query, contentType, characterEncoding;

    public ConcreteHttpRequest build() {
      return new ConcreteHttpRequest(this);
    }

    public Builder uri(String u) {
      uri = u;
      return this;
    }

    public Builder method(String u) {
      method = u;
      return this;
    }

    public Builder path(String u) {
      path = u;
      return this;
    }

    public Builder inputStream(InputStream u) {
      inputStream = u;
      return this;
    }

    public Builder query(Optional<String> u) {
      query = u;
      return this;
    }

    public Builder contentType(Optional<String> u) {
      contentType = u;
      return this;
    }

    public Builder characterEncoding(Optional<String> u) {
      characterEncoding = u;
      return this;
    }

    public Builder contentLength(long u) {
      contentLength = u;
      return this;
    }

    public Builder headers(Map<String, List<String>> h) {
      headers = h;
      return this;
    }

    public Builder queryParameters(Map<String, List<String>> h) {
      queryParameters = h;
      return this;
    }
  }
}
