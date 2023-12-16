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

package net.sf.barefoot.context;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/** Base class for servlet responses */
public abstract class AbstractServletResponse {
  int status = 200;
  final Map<String, List<String>> headers = new HashMap<>();
  private String contentType, charEncoding;
  private Locale locale;
  private final Supplier<OutputStream> outputStream;
  private final Supplier<PrintWriter> writer;
  protected long contentLength = -1L;
  protected final AbstractServletRequest request;

  protected AbstractServletResponse(Builder builder) {
    outputStream = builder.outputStream;
    writer = builder.writer;
    request = builder.request;
  }

  public boolean containsHeader(String string) {
    return headers.containsKey(string);
  }

  public String encodeURL(String string) {
    return string;
  }

  public String encodeRedirectURL(String location) {
    try {
      URI uri = new URI(location);

      if (!uri.isAbsolute()) {
        String url = request.url;
        if (location.startsWith("/")) {
          URL urlObj = new URL(url);
          URL u = new URL(urlObj.getProtocol(), urlObj.getHost(), urlObj.getPort(), location);
          location = u.toExternalForm();
        } else {
          int index = url.lastIndexOf('/');

          location = url.substring(0, index + 1) + location;
        }
      }
    } catch (URISyntaxException | MalformedURLException ex) {
      throw new RuntimeException(ex);
    }

    return location;
  }

  public String encodeUrl(String string) {
    return string;
  }

  public String encodeRedirectUrl(String location) {
    return encodeRedirectURL(location);
  }

  public void sendError(int i, String string) throws IOException {
    status = i;
  }

  public void sendError(int i) throws IOException {
    status = i;
  }

  public void sendRedirect(String location) throws IOException {
    setHeader("Location", encodeRedirectURL(location));
    status = 302;
  }

  private static String encodeDate(long t) {
    ZoneId GMT = ZoneId.of("UTC");
    Instant i = Instant.ofEpochSecond(t);
    ZonedDateTime z = ZonedDateTime.ofInstant(i, GMT);
    return DateTimeFormatter.RFC_1123_DATE_TIME.format(z);
  }

  public void setDateHeader(String name, long t) {
    setHeader(name, encodeDate(t));
  }

  public void addDateHeader(String name, long t) {
    addHeader(name, encodeDate(t));
  }

  public void setHeader(String name, String value) {
    List<String> h = headers.get(name);
    if (h == null) {
      h = new ArrayList<>();
      headers.put(name, h);
      h.add(value);
    } else {
      h.set(0, value);
    }
  }

  public void addHeader(String name, String value) {
    List<String> h = headers.get(name);
    if (h == null) {
      h = new ArrayList<>();
      headers.put(name, h);
    }
    h.add(value);
  }

  public void setIntHeader(String string, int i) {
    setHeader(string, Integer.toString(i));
  }

  public void addIntHeader(String string, int i) {
    addHeader(string, Integer.toString(i));
  }

  public void setStatus(int i) {
    status = i;
  }

  public void setStatus(int i, String string) {
    status = i;
  }

  public int getStatus() {
    return status;
  }

  public String getHeader(String string) {
    List<String> h = headers.get(string);
    return (h == null) || h.isEmpty() ? null : h.get(0);
  }

  public Collection<String> getHeaders(String string) {
    return headers.get(string);
  }

  public Collection<String> getHeaderNames() {
    return headers.keySet().stream().collect(Collectors.toList());
  }

  public String getCharacterEncoding() {
    if (charEncoding == null) {
      return BarefootContentType.getCharsetFromContentType(contentType);
    }

    return charEncoding;
  }

  public String getContentType() {
    return contentType;
  }

  public void setCharacterEncoding(String string) {
    charEncoding = string;
  }

  public void setContentLength(int i) {
    contentLength = i;
  }

  public void setContentLengthLong(long l) {
    contentLength = l;
  }

  public void setContentType(String string) {
    contentType = string;
  }

  public void setBufferSize(int i) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public int getBufferSize() {
    return 0;
  }

  public void flushBuffer() throws IOException {}

  public void resetBuffer() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public boolean isCommitted() {
    return false;
  }

  public void reset() {
    headers.clear();
  }

  public void setLocale(Locale l) {
    locale = l;
  }

  public Locale getLocale() {
    return locale;
  }

  public OutputStream getOutputStream() throws IOException {
    if (outputStream != null) {
      try {
        return outputStream.get();

      } catch (RuntimeException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof IOException) {
          throw (IOException) cause;
        }
        throw ex;
      }
    }

    return null;
  }

  public PrintWriter getWriter() throws IOException {
    try {
      if (writer != null) {
        return writer.get();
      } else {
        if (outputStream != null) {
          String csn = getCharacterEncoding();

          return new PrintWriter(
              csn == null
                  ? new OutputStreamWriter(outputStream.get(), StandardCharsets.UTF_8)
                  : new OutputStreamWriter(outputStream.get(), csn));
        }

        return null;
      }
    } catch (RuntimeException ex) {
      Throwable cause = ex.getCause();
      if (cause instanceof IOException) {
        throw (IOException) cause;
      }
      throw ex;
    }
  }

  public abstract List<String> getSetCookieHeaders();

  public abstract static class Builder {
    protected Supplier<OutputStream> outputStream;
    protected Supplier<PrintWriter> writer;
    protected AbstractServletRequest request;

    public abstract Builder outputStream(Supplier<OutputStream> os);

    public abstract Builder writer(Supplier<PrintWriter> w);

    public abstract Builder request(AbstractServletRequest req);

    public abstract AbstractServletResponse build();
  }
}
