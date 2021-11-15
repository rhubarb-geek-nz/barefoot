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
import java.util.HashMap;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;

/** test cases for request */
public class ConcreteHttpRequestTest {

  @Test
  public void testBuilder() {
    HttpRequest result =
        ConcreteHttpRequest.builder()
            .method("GET")
            .path("/")
            .uri("http://127.0.0.1:8080/")
            .headers(new HashMap<>())
            .queryParameters(new HashMap<>())
            .contentType(Optional.empty())
            .build();
    Assert.assertNotNull(result);
    Assert.assertEquals("GET", result.getMethod());
    Assert.assertEquals("/", result.getPath());
    Assert.assertEquals("http://127.0.0.1:8080/", result.getUri());
    Assert.assertFalse(result.getContentType().isPresent());
    Assert.assertEquals(-1L, result.getContentLength());
  }
}
