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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import net.sf.barefoot.util.IteratorEnumeration;
import net.sf.barefoot.util.ReaderInputStream;

/** Base class for servlet requests */
public abstract class AbstractServletRequest {
  public static final String ATTR_ORIGINAL_REQUEST = "net.sf.barefoot.original.request",
      ATTR_ORIGINAL_CONTEXT = "net.sf.barefoot.original.context",
      ATTR_ORIGINAL_RESPONSE = "net.sf.barefoot.original.response";
  protected static final List<String> EMPTY_STRING_LIST = new ArrayList<>();
  protected final Map<String, Object> attributes = new HashMap<>();
  protected final Map<String, List<String>> headers; // keys should be lower-case
  protected final Map<String, String[]> parameters;
  protected final String method,
      uri,
      url,
      contentType,
      queryString,
      authType,
      remoteAddr,
      remoteHost,
      serverName,
      protocol,
      contextPath;
  protected final int serverPort;
  protected final long contentLength;
  protected final Principal userPrincipal;
  protected final boolean isSecure;
  protected final BarefootServletContextLogger logger;
  protected final Supplier<InputStream> inputStream;
  protected final Supplier<Reader> reader;
  protected boolean alreadyOpen = false;
  protected AbstractServletSession httpSession;
  protected String charEncoding;

  protected AbstractServletRequest(Builder builder) {
    method = builder.method;
    contentLength = builder.contentLength;
    userPrincipal = builder.userPrincipal;
    contentType = builder.contentType;
    queryString = builder.queryString;
    uri = builder.uri;
    url = builder.url;
    charEncoding = builder.charEncoding;
    authType = builder.authType;
    remoteAddr = builder.remoteAddr;
    remoteHost = builder.remoteHost;
    headers = builder.headers;
    parameters = builder.parameters;
    isSecure = builder.isSecure;
    serverName = builder.serverName;
    protocol = builder.protocol;
    serverPort = builder.serverPort;
    logger = builder.logger;
    contextPath = builder.contextPath;
    inputStream = builder.inputStream;
    reader = builder.reader;
  }

  public abstract String getPathInfo();

  public String getContextPath() {
    return contextPath;
  }

  public String getAuthType() {
    return authType;
  }

  public long getDateHeader(String string) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public String getHeader(String string) {
    List<String> list = headers.get(string.toLowerCase());
    return (list == null || list.isEmpty()) ? null : list.get(0);
  }

  public Enumeration<String> getHeaders(String string) {
    List<String> list = headers.get(string.toLowerCase());
    return new IteratorEnumeration((list == null ? EMPTY_STRING_LIST : list).iterator());
  }

  public Enumeration<String> getHeaderNames() {
    return new IteratorEnumeration(headers.keySet().iterator());
  }

  public int getIntHeader(String string) {
    String val = getHeader(string);
    return val == null ? -1 : Integer.parseInt(val);
  }

  public String getMethod() {
    if (method == null) throw new NullPointerException("Bad Method");
    return method;
  }

  public String getPathTranslated() {
    return null; // not needing this
  }

  public String getQueryString() {
    return queryString;
  }

  public String getRemoteUser() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public boolean isUserInRole(String string) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public Principal getUserPrincipal() {
    return userPrincipal;
  }

  public String getRequestedSessionId() {
    return null;
  }

  public String getRequestURI() {
    if (uri == null) throw new NullPointerException("Bad URI");
    return uri;
  }

  public StringBuffer getRequestURL() {
    if (url == null) throw new NullPointerException("Bad URL");
    return new StringBuffer(url);
  }

  public String changeSessionId() {
    if (httpSession == null) throw new IllegalStateException();
    String id = UUID.randomUUID().toString();
    httpSession.id = id;
    return id;
  }

  public boolean isRequestedSessionIdValid() {
    return false;
  }

  public boolean isRequestedSessionIdFromCookie() {
    return false;
  }

  public boolean isRequestedSessionIdFromURL() {
    return false;
  }

  public boolean isRequestedSessionIdFromUrl() {
    return false;
  }

  public Object getAttribute(String string) {
    return attributes.get(string);
  }

  public Enumeration<String> getAttributeNames() {
    Iterator<String> it = attributes.keySet().iterator();
    return new IteratorEnumeration(it);
  }

  public String getCharacterEncoding() {
    return charEncoding;
  }

  public void setCharacterEncoding(String string) throws UnsupportedEncodingException {
    charEncoding = string;
  }

  public int getContentLength() {
    return (int) contentLength;
  }

  public long getContentLengthLong() {
    return contentLength;
  }

  public String getContentType() {
    return contentType;
  }

  public String getParameter(String string) {
    String[] val = parameters.get(string);
    return (val == null || val.length == 0) ? null : val[0];
  }

  public Enumeration<String> getParameterNames() {
    return new IteratorEnumeration(parameters.keySet().iterator());
  }

  public String[] getParameterValues(String string) {
    return parameters.get(string);
  }

  public Map<String, String[]> getParameterMap() {
    return parameters;
  }

  public String getProtocol() {
    return protocol;
  }

  public String getScheme() {
    return isSecure ? "https" : "http";
  }

  public String getServerName() {
    return serverName;
  }

  public int getServerPort() {
    return serverPort;
  }

  public String getRemoteAddr() {
    return remoteAddr;
  }

  public String getRemoteHost() {
    return remoteHost;
  }

  public void setAttribute(String string, Object o) {
    attributes.put(string, o);
  }

  public void removeAttribute(String string) {
    attributes.remove(string);
  }

  public Locale getLocale() {
    /* should be based on Accept-Language  */
    return Locale.getDefault();
  }

  public Enumeration<Locale> getLocales() {
    List<Locale> list = new ArrayList<>();
    list.add(getLocale());
    return Collections.enumeration(list);
  }

  public boolean isSecure() {
    return isSecure;
  }

  public String getRealPath(String string) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public int getRemotePort() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public String getLocalName() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public String getLocalAddr() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public int getLocalPort() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public boolean isAsyncStarted() {
    return false;
  }

  public boolean isAsyncSupported() {
    return false;
  }

  void log(String msg) {
    logger.log(msg);
  }

  void log(String msg, Throwable thr) {
    logger.log(msg, thr);
  }

  private static final byte[] NO_BYTES = {};

  public InputStream getInputStream() throws IOException {
    if (alreadyOpen) {
      throw new IllegalStateException("stream already open");
    }

    try {
      if (inputStream != null) {
        alreadyOpen = true;
        return inputStream.get();
      }

      if (reader != null) {
        String encoding = getCharacterEncoding();
        Charset cs = encoding == null ? StandardCharsets.UTF_8 : Charset.forName(encoding);
        alreadyOpen = true;
        return new ReaderInputStream(reader.get(), cs);
      }
    } catch (RuntimeException ex) {
      Throwable cause = ex.getCause();
      if (cause instanceof IOException) {
        throw (IOException) cause;
      }
      throw ex;
    }

    return null;
  }

  public BufferedReader getReader() throws IOException {
    if (alreadyOpen) {
      throw new IllegalStateException("stream already open");
    }

    try {
      if (reader != null) {
        alreadyOpen = true;
        Reader rdr = reader.get();
        if (rdr instanceof BufferedReader) {
          return (BufferedReader) rdr;
        }
        return new BufferedReader(rdr);
      }

      if (inputStream != null) {
        String encoding = getCharacterEncoding();
        Charset cs = encoding == null ? StandardCharsets.UTF_8 : Charset.forName(encoding);
        alreadyOpen = true;
        return new BufferedReader(new InputStreamReader(inputStream.get(), cs));
      }
    } catch (RuntimeException ex) {
      Throwable cause = ex.getCause();
      if (cause instanceof IOException) {
        throw (IOException) cause;
      }
      throw ex;
    }

    return null;
  }

  public abstract AbstractServletSession getSession(boolean bln);

  public abstract AbstractServletContext getServletContext();

  /**
   * create an object to capture the response
   *
   * @return response builder
   */
  public abstract AbstractServletResponse.Builder getServletResponseBuilder();

  public abstract static class Builder {
    Map<String, List<String>> headers; // keys should be lower-case
    Map<String, String[]> parameters;
    String method,
        uri,
        url,
        contentType,
        queryString,
        charEncoding,
        authType,
        remoteAddr,
        remoteHost,
        serverName,
        contextPath,
        protocol;
    int serverPort = -1;
    long contentLength = -1;
    Principal userPrincipal;
    boolean isSecure;
    BarefootServletContextLogger logger;
    Supplier<InputStream> inputStream;
    Supplier<Reader> reader;

    public abstract AbstractServletRequest build();

    /**
     * Set request headers, keys should all be lower-case
     *
     * @param hdrs map of headers with lower-case keys
     * @return this
     */
    public Builder headers(Map<String, List<String>> hdrs) {
      headers = hdrs;
      return this;
    }

    /**
     * Set list of parameters for the request
     *
     * @param param list of parameters
     * @return this
     */
    public Builder parameters(Map<String, String[]> param) {
      parameters = param;
      return this;
    }

    /**
     * Set the request method
     *
     * @param m method
     * @return this
     */
    public Builder method(String m) {
      method = m;
      return this;
    }

    /**
     * Set the query string
     *
     * @param s query string
     * @return this
     */
    public Builder queryString(String s) {
      queryString = s;
      return this;
    }

    /**
     * Sets the protocol
     *
     * @param s protocol
     * @return this
     */
    public Builder protocol(String s) {
      protocol = s;
      return this;
    }

    public Builder contextPath(String s) {
      contextPath = s;
      return this;
    }

    /**
     * Sets the server name
     *
     * @param s server name
     * @return this
     */
    public Builder serverName(String s) {
      serverName = s;
      return this;
    }

    /**
     * Sets the server port
     *
     * @param s server port
     * @return this
     */
    public Builder serverPort(int s) {
      serverPort = s;
      return this;
    }

    /**
     * Sets the remote address
     *
     * @param s remote address
     * @return this
     */
    public Builder remoteAddr(String s) {
      remoteAddr = s;
      return this;
    }

    /**
     * Sets the remote host
     *
     * @param s remote host
     * @return this
     */
    public Builder remoteHost(String s) {
      remoteHost = s;
      return this;
    }

    /**
     * Sets the character encoding
     *
     * @param s character encoding
     * @return this
     */
    public Builder characterEncoding(String s) {
      charEncoding = s;
      return this;
    }

    /**
     * Sets the content length
     *
     * @param i content length
     * @return this
     */
    public Builder contentLength(long i) {
      contentLength = i;
      return this;
    }

    /**
     * Sets the content length
     *
     * @param i content length
     * @return this
     */
    public Builder contentLength(int i) {
      contentLength = i;
      return this;
    }

    /**
     * Sets the content type
     *
     * @param s content type
     * @return this
     */
    public Builder contentType(String s) {
      contentType = s;
      return this;
    }

    /**
     * Set the request URI
     *
     * @param s request URI
     * @return this
     */
    public Builder requestUri(String s) {
      uri = s;
      return this;
    }

    /**
     * Sets the request URL
     *
     * @param s request URL
     * @return this
     */
    public Builder requestUrl(String s) {
      url = s;
      return this;
    }

    /**
     * Sets the user principal
     *
     * @param p user principal
     * @return this
     */
    public Builder userPrincipal(Principal p) {
      userPrincipal = p;
      return this;
    }

    /**
     * Sets the isSecure flag
     *
     * @param b isSecure flag
     * @return this
     */
    public Builder isSecure(boolean b) {
      isSecure = b;
      return this;
    }

    /**
     * sets the logger for the request
     *
     * @param l logger instance
     * @return this
     */
    public Builder logger(BarefootServletContextLogger l) {
      logger = l;
      return this;
    }

    /**
     * Sets the request cookies
     *
     * @param c list of cookies
     * @return this
     */
    public abstract Builder cookies(List<String> c);

    /**
     * Sets the request cookies
     *
     * @param c list of cookies
     * @return this
     */
    public abstract Builder cookies(String c);

    /**
     * sets the supplier for the input stream
     *
     * @param is input stream
     * @return this
     */
    public Builder inputStream(Supplier<InputStream> is) {
      inputStream = is;
      return this;
    }

    /**
     * sets the supplier for the reader
     *
     * @param r reader
     * @return this
     */
    public Builder reader(Supplier<Reader> r) {
      reader = r;
      return this;
    }
  }
}
