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

package net.sf.barefoot.context.jakarta;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.sf.barefoot.context.AbstractServletRequest;
import net.sf.barefoot.context.AbstractServletResponse;

/** Standard response handler for servlets */
public final class BarefootServletResponse extends AbstractServletResponse
    implements HttpServletResponse {
  final List<Cookie> cookies = new ArrayList<>();

  /**
   * Creates a response with output suppliers and list of cookies
   *
   * @param builder initial state
   */
  public BarefootServletResponse(Builder builder) {
    super(builder);
  }

  @Override
  public void addCookie(Cookie cookie) {
    cookies.add(cookie);
  }

  @Override
  public ServletOutputStream getOutputStream() throws IOException {
    return new BarefootServletOutputStream(super.getOutputStream());
  }

  @Override
  public List<String> getSetCookieHeaders() {
    return BarefootCookieCutter.SINGLETON_INSTANCE.toListString(cookies);
  }

  /** Builder class for servlet response */
  public static final class Builder extends AbstractServletResponse.Builder {
    @Override
    public BarefootServletResponse build() {
      return new BarefootServletResponse(this);
    }

    @Override
    public Builder outputStream(Supplier<OutputStream> os) {
      outputStream = os;
      return this;
    }

    @Override
    public Builder writer(Supplier<PrintWriter> w) {
      writer = w;
      return this;
    }

    @Override
    public Builder request(AbstractServletRequest req) {
      request = req;
      return this;
    }
  }

  /**
   * create an builder object for this response
   *
   * @return new builder object
   */
  public static Builder builder() {
    return new Builder();
  }
}
