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

import com.google.cloud.functions.HttpResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** concrete response for testing */
public class ConcreteHttpResponse implements HttpResponse {
  Optional<String> contentType;
  final OutputStream outputStream;
  final BufferedWriter writer;
  int statusCode;
  final Map<String, List<String>> headers = new HashMap<>();

  private ConcreteHttpResponse(Builder builder) {
    outputStream = builder.outputStream;
    writer = builder.writer;
  }

  @Override
  public void setStatusCode(int code) {
    statusCode = code;
  }

  @Override
  public void setStatusCode(int code, String message) {
    statusCode = code;
  }

  public int getStatusCode() {
    return statusCode;
  }

  @Override
  public void setContentType(String ct) {
    contentType = ct == null ? Optional.empty() : Optional.of(ct);
  }

  @Override
  public Optional<String> getContentType() {
    return contentType == null ? Optional.empty() : contentType;
  }

  @Override
  public void appendHeader(String header, String value) {
    List<String> list = headers.get(header);
    if (list == null) {
      list = new ArrayList<>();
      headers.put(header, list);
    }
    list.add(value);
  }

  @Override
  public Map<String, List<String>> getHeaders() {
    return headers;
  }

  @Override
  public OutputStream getOutputStream() throws IOException {
    return outputStream;
  }

  @Override
  public BufferedWriter getWriter() throws IOException {
    return writer == null
        ? new BufferedWriter(new OutputStreamWriter(getOutputStream(), StandardCharsets.UTF_8))
        : writer;
  }

  public static class Builder {
    OutputStream outputStream;
    BufferedWriter writer;

    public Builder setOutputStream(OutputStream os) {
      outputStream = os;
      return this;
    }

    public Builder setWriter(BufferedWriter wr) {
      writer = wr;
      return this;
    }

    public ConcreteHttpResponse build() {
      return new ConcreteHttpResponse(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
