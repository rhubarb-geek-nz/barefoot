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

package net.sf.barefoot.context.javax;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import javax.servlet.http.Cookie;
import net.sf.barefoot.context.AbstractCookieCutter;

/**
 * Cookie Codec. This is based on rules defined in https://www.ietf.org/rfc/rfc2109.txt The servlet
 * API uses cookies as objects, HTTP requires a string representation. This object will encode and
 * decode as required by Cookie and Set-Cookie headers.
 */
public final class BarefootCookieCutter extends AbstractCookieCutter<Cookie> {

  public BarefootCookieCutter() {
    super(EMPTY_COOKIE_LIST);
  }

  /** Used for converting to typed lists of cookies */
  private static final Cookie[] EMPTY_COOKIE_LIST = new Cookie[0];

  /**
   * Convert one Cookie as for HTTP response.
   *
   * @param c cookie
   * @return list of name=value pairs
   */
  @Override
  public String toString(Cookie c) {
    StringBuilder sb = new StringBuilder();
    sb.append(c.getName());
    sb.append('=');
    sb.append(c.getValue());
    sb.append("; Max-Age=");
    final int age = c.getMaxAge();
    sb.append(Integer.toString(age));
    if (age > 0) {
      ZonedDateTime zdt = ZonedDateTime.now(GMT).plusSeconds(age);
      sb.append("; Expires=");
      sb.append(DateTimeFormatter.RFC_1123_DATE_TIME.format(zdt));
    }
    if (c.getSecure()) {
      sb.append("; Secure");
    }
    if (c.isHttpOnly()) {
      sb.append("; HttpOnly");
    }
    String domain = c.getDomain();
    if (domain != null) {
      sb.append("; Domain=");
      sb.append(domain);
    }
    String path = c.getPath();
    if (path != null) {
      sb.append("; Path=");
      sb.append(path);
    }
    String comment = c.getComment();
    if (comment != null) {
      sb.append("; Comment=");
      sb.append(comment);
    }
    return sb.toString();
  }

  static final BarefootCookieCutter SINGLETON_INSTANCE = new BarefootCookieCutter();

  public BarefootCookieCutter getInstance() {
    return SINGLETON_INSTANCE;
  }

  @Override
  protected Cookie newCookie(String name, String value) {
    return new Cookie(name, value);
  }

  @Override
  protected void setMaxAge(Cookie c, int v) {
    c.setMaxAge(v);
  }

  @Override
  protected void setDomain(Cookie c, String v) {
    c.setDomain(v);
  }

  @Override
  protected void setComment(Cookie c, String v) {
    c.setComment(v);
  }

  @Override
  protected void setPath(Cookie c, String v) {
    c.setPath(v);
  }

  @Override
  protected void setSecure(Cookie c, boolean v) {
    c.setSecure(v);
  }

  @Override
  protected void setHttpOnly(Cookie c, boolean v) {
    c.setHttpOnly(v);
  }
}
