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

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Cookie Codec. This is based on rules defined in https://www.ietf.org/rfc/rfc2109.txt The servlet
 * API uses cookies as objects, HTTP requires a string representation. This object will encode and
 * decode as required by Cookie and Set-Cookie headers.
 *
 * @param <T> empty list of the type of object of which this is a template
 */
public abstract class AbstractCookieCutter<T> {
  final T[] EMPTY_COOKIE_LIST;
  protected final ZoneId GMT = ZoneId.of("UTC");

  protected AbstractCookieCutter(T[] e) {
    EMPTY_COOKIE_LIST = e;
  }

  /**
   * Parse a cookie as output from HTTP server
   *
   * @param s string
   * @return cookie
   */
  public T parseSetCookie(String s) {
    int i = 0, l = s.length();
    T cookie = null;
    while (i < l) {
      if (Character.isWhitespace(s.charAt(i))) {
        i++;
      } else {
        int k = s.indexOf("; ", i);
        if (k < 0) k = l;
        int m = s.indexOf('=', i);
        if (m < 0) m = k;
        if (m > k) m = k;
        String name = s.substring(i, m);
        String value = (m < k) ? s.substring(m + 1, k) : null;

        if (cookie == null && name.charAt(0) != '$') {
          cookie = newCookie(name, value);
        } else {
          if (cookie != null) {
            switch (name.toLowerCase()) {
              case "max-age":
                setMaxAge(cookie, Integer.parseInt(value));
                break;
              case "domain":
                setDomain(cookie, value);
                break;
              case "comment":
                setComment(cookie, value);
                break;
              case "path":
                setPath(cookie, value);
                break;
              case "secure":
                setSecure(cookie, (value == null) ? true : Boolean.parseBoolean(value));
              case "httponly":
                setHttpOnly(cookie, (value == null) ? true : Boolean.parseBoolean(value));
            }
          }
        }

        i = k + 2;
      }
    }

    return cookie;
  }

  /**
   * Read a list of cookies as encoded in a request.
   *
   * @param s header value containing one or more cookies
   * @return cookies list of cookies as output
   */
  public T[] parseCookie(String s) {
    List<T> cookies = new ArrayList<>();
    parseCookie(s, cookies);
    return cookies.toArray(EMPTY_COOKIE_LIST);
  }

  /**
   * Read a list of cookies as encoded in a request.
   *
   * @param s list of header values each containing one or more cookies
   * @return cookies list of cookies as output
   */
  public T[] parseCookie(List<String> s) {
    List<T> cookies = new ArrayList<>();
    s.forEach(
        (e) -> {
          parseCookie(e, cookies);
        });
    return cookies.toArray(EMPTY_COOKIE_LIST);
  }

  /**
   * Read a list of cookies as encoded in a request.
   *
   * @param s header value containing one or more cookies
   * @param cookies list of cookies as output
   */
  public void parseCookie(String s, List<T> cookies) {
    int i = 0, l = s.length();
    while (i < l) {
      if (Character.isWhitespace(s.charAt(i))) {
        i++;
      } else {
        int k = s.indexOf("; ", i);
        if (k < 0) k = l;
        int m = s.indexOf('=', i);
        if (m < 0) m = k;
        if (m > k) m = k;
        String name = s.substring(i, m);
        String value = (m < k) ? s.substring(m + 1, k) : null;

        cookies.add(newCookie(name, value));

        i = k + 2;
      }
    }
  }

  /**
   * Convert one Cookie as for HTTP response.
   *
   * @param c cookie
   * @return list of name=value pairs
   */
  public abstract String toString(T c);

  /**
   * create a concrete cookie
   *
   * @param name name of the cookie
   * @param value value of the cookie
   * @return the cookie
   */
  protected abstract T newCookie(String name, String value);

  protected abstract void setMaxAge(T c, int v);

  protected abstract void setDomain(T c, String v);

  protected abstract void setComment(T c, String v);

  protected abstract void setPath(T c, String v);

  protected abstract void setSecure(T c, boolean v);

  protected abstract void setHttpOnly(T c, boolean v);

  public List<String> toListString(List<T> cookies) {
    List<String> result = new ArrayList<>();
    cookies.forEach(
        (t) -> {
          result.add(toString(t));
        });
    return result;
  }
}
