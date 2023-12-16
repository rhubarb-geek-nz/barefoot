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

package net.sf.barefoot.example.rest;

import java.io.IOException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.barefoot.example.bean.BarefootExampleBean;
import net.sf.barefoot.testtool.HttpServletRequestState;
import net.sf.barefoot.testtool.javax.HttpServletRequestStateFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/** rest calls for example */
@RestController
public class BarefootExampleRestController {
  @Autowired BarefootExampleBean exampleBean;

  HttpServletRequestStateFactory factory = new HttpServletRequestStateFactory();

  @PostMapping(value = "/api/HttpExample*")
  public ResponseEntity postExample(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {

    if (exampleBean == null) {
      throw new NullPointerException("@Autowired BarefootExampleBean exampleBean failed");
    }

    HttpServletRequestState<Cookie> body = factory.create(req, resp);

    int gap = 300;
    long now = System.currentTimeMillis();

    {
      Cookie cookie2 = new Cookie("BAREFOOT2", Long.toString(now));
      cookie2.setPath("/javax.servlet");
      cookie2.setMaxAge(gap);
      resp.addCookie(cookie2);
    }

    HttpCookie cookie =
        ResponseCookie.from("BAREFOOT1", Long.toString(now))
            .path("/org.springframework")
            .maxAge(gap)
            .build();

    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(body);
  }

  @GetMapping(value = "/api/HttpExample")
  public @ResponseBody HttpServletRequestState getExample(
      HttpServletRequest req, HttpServletResponse resp) throws IOException {

    if (exampleBean == null) {
      throw new NullPointerException("@Autowired BarefootExampleBean exampleBean failed");
    }

    return factory.create(req, resp);
  }
}
