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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import net.sf.barefoot.context.AbstractServletRequest;
import org.junit.Assert;
import org.junit.Test;

/** test cases for request object */
public class BarefootServletRequestTest {

  public BarefootServletRequestTest() {}

  BarefootServletContext context = new BarefootServletContext("");

  @Test
  public void testReaderShort() throws IOException {
    AbstractServletRequest.Builder builder = context.getServletRequestBuilder();

    String msg = "Hello World";
    byte[] mb = msg.getBytes(StandardCharsets.UTF_8);

    builder.reader(() -> new StringReader(msg));
    builder.contextPath("");
    builder.requestUri("/");

    AbstractServletRequest request = builder.build();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (InputStream is = request.getInputStream()) {
      byte[] ba = new byte[4096];
      int i;
      while ((i = is.read(ba)) > 0) {
        baos.write(ba, 0, i);
      }
    }
    byte[] res = baos.toByteArray();

    Assert.assertArrayEquals(mb, res);
  }

  @Test
  public void testReaderLong() throws IOException {
    AbstractServletRequest.Builder builder = context.getServletRequestBuilder();

    StringBuilder sb = new StringBuilder();
    int i = 16384;
    Random rand = new Random();
    while (0 != i--) {
      sb.append(Integer.toString(rand.nextInt()));
    }
    String msg = sb.toString();
    byte[] mb = msg.getBytes(StandardCharsets.UTF_8);

    builder.reader(() -> new StringReader(msg));
    builder.contextPath("");
    builder.requestUri("/");

    AbstractServletRequest request = builder.build();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (InputStream is = request.getInputStream()) {
      byte[] ba = new byte[4096];
      while ((i = is.read(ba)) > 0) {
        baos.write(ba, 0, i);
      }
    }
    byte[] res = baos.toByteArray();

    Assert.assertArrayEquals(mb, res);
  }
}
