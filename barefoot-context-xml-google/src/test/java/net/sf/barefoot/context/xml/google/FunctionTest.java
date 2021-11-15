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

package net.sf.barefoot.context.xml.google;

import com.google.cloud.functions.HttpRequest;
import com.google.gson.Gson;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Optional;
import net.sf.barefoot.google.concrete.ConcreteHttpRequest;
import net.sf.barefoot.google.concrete.ConcreteHttpResponse;
import org.junit.Assert;
import org.junit.Test;

/** test harness for google jakarta example */
public class FunctionTest {
  final Gson gson = new Gson();

  public FunctionTest() {}

  @Test
  public void testSimpleGet() throws Exception {
    Function instance = new Function();
    Assert.assertNotNull(instance);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    HttpRequest req =
        ConcreteHttpRequest.builder()
            .path("/")
            .uri("http://localhost/")
            .method("GET")
            .characterEncoding(Optional.empty())
            .contentType(Optional.empty())
            .query(Optional.empty())
            .queryParameters(new HashMap<>())
            .headers(new HashMap<>())
            .build();
    ConcreteHttpResponse resp = ConcreteHttpResponse.builder().setOutputStream(baos).build();

    instance.service(req, resp);

    Assert.assertEquals(404, resp.getStatusCode());
  }
}
