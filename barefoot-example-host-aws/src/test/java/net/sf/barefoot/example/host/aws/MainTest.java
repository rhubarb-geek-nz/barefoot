/*
 *
 *  Copyright 2021, Roger Brown
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

package net.sf.barefoot.example.host.aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

public class MainTest {

  public static class StreamHandler implements RequestStreamHandler {

    @Override
    public void handleRequest(InputStream in, OutputStream out, Context ctxt) throws IOException {
      out.write("{}".getBytes());
    }
  }

  public static class MapHandler
      implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> map, Context context) {
      return map;
    }
  }

  public static class EventHandler
      implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(
        APIGatewayProxyRequestEvent req, Context ctx) {
      return new APIGatewayProxyResponseEvent();
    }
  }

  @Test
  public void testStreamHandler() throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    boolean b;

    try (InputStream is = getClass().getResourceAsStream("testHandleRequestV1.json")) {
      b = new Main(is, baos, new StreamHandler()).start();
    }

    Assert.assertTrue(b);

    System.out.write(baos.toByteArray());
  }

  @Test
  public void testMapHandler() throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    boolean b;

    try (InputStream is = getClass().getResourceAsStream("testHandleRequestV1.json")) {
      b = new Main(is, baos, new MapHandler()).start();
    }

    Assert.assertTrue(b);

    System.out.write(baos.toByteArray());
  }

  @Test
  public void testEventHandler() throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    boolean b;

    try (InputStream is = getClass().getResourceAsStream("testHandleRequestV1.json")) {
      b = new Main(is, baos, new EventHandler()).start();
    }

    Assert.assertTrue(b);

    System.out.write(baos.toByteArray());
  }
}
