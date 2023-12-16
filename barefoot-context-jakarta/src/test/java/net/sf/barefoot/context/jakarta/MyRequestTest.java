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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

/** test harness */
public class MyRequestTest {
  final ObjectMapper mapper = new ObjectMapper();

  static final String REQUEST_ONE =
      "POST /submit/debug?wibble=flim&wibble=egg HTTP/1.1\r\n"
          + "Host: 127.0.0.1:8080\r\n"
          + "User-Agent: curl/7.64.0\r\n"
          + "Accept: */*\r\n"
          + "Content-Length: 24\r\n"
          + "Cookie: SESSION=ABCD-1234; WIBBLE=42\r\n"
          + "Content-Type: text/plain; charset=UTF-8\r\n"
          + "\r\n"
          + "SOME=THING&ANYTHING=ELSE";
  static final String ANSWER_ONE =
      "{\"method\":\"POST\",\"contentType\":\"text/plain;"
          + " charset=UTF-8\",\"authType\":null,\"content\":\"SOME=THING&ANYTHING=ELSE\",\"servletPath\":\"\",\"serverName\":\"127.0.0.1\",\"contextPath\":\"\",\"contentLength\":24,\"status\":200,\"serverPort\":8080,\"header\":{\"content-length\":\"24\",\"cookie\":\"SESSION=ABCD-1234;"
          + " WIBBLE=42\",\"host\":\"127.0.0.1:8080\",\"content-type\":\"text/plain;"
          + " charset=UTF-8\",\"user-agent\":\"curl/7.64.0\",\"accept\":\"*/*\"},\"headers\":{\"content-length\":[\"24\"],\"cookie\":[\"SESSION=ABCD-1234;"
          + " WIBBLE=42\"],\"host\":[\"127.0.0.1:8080\"],\"content-type\":[\"text/plain;"
          + " charset=UTF-8\"],\"user-agent\":[\"curl/7.64.0\"],\"accept\":[\"*/*\"]},\"characterEncoding\":\"UTF-8\",\"parameterMap\":{\"wibble\":[\"flim\",\"egg\"]},\"pathInfo\":\"/submit/debug\",\"pathTranslated\":null,\"requestURI\":\"/submit/debug\",\"requestURL\":\"http://127.0.0.1:8080/submit/debug\",\"queryString\":\"wibble=flim&wibble=egg\"}";

  static final String REQUEST_TWO =
      "POST /submit/debug?wibble=flim&wibble=egg HTTP/1.1\r\n"
          + "Host: 127.0.0.1:8080\r\n"
          + "User-Agent: curl/7.64.0\r\n"
          + "Accept: */*\r\n"
          + "Accept: text/plain\r\n"
          + "Accept: application/java\r\n"
          + "Content-Length: 24\r\n"
          + "Cookie: SESSION=ABCD-1234; WIBBLE=42\r\n"
          + "Content-Type: application/x-www-form-urlencoded\r\n"
          + "\r\n"
          + "SOME=THING&ANYTHING=ELSE";
  static final String ANSWER_TWO =
      "{\"method\":\"POST\",\"contentType\":\"application/x-www-form-urlencoded\",\"authType\":null,\"content\":\"\",\"servletPath\":\"\",\"serverName\":\"127.0.0.1\",\"contextPath\":\"\",\"contentLength\":24,\"status\":200,\"serverPort\":8080,\"header\":{\"content-length\":\"24\",\"cookie\":\"SESSION=ABCD-1234;"
          + " WIBBLE=42\",\"host\":\"127.0.0.1:8080\",\"content-type\":\"application/x-www-form-urlencoded\",\"user-agent\":\"curl/7.64.0\",\"accept\":\"*/*\"},\"headers\":{\"content-length\":[\"24\"],\"cookie\":[\"SESSION=ABCD-1234;"
          + " WIBBLE=42\"],\"host\":[\"127.0.0.1:8080\"],\"content-type\":[\"application/x-www-form-urlencoded\"],\"user-agent\":[\"curl/7.64.0\"],\"accept\":[\"*/*\",\"text/plain\",\"application/java\"]},\"characterEncoding\":null,\"parameterMap\":{\"ANYTHING\":[\"ELSE\"],\"SOME\":[\"THING\"],\"wibble\":[\"flim\",\"egg\"]},\"pathInfo\":\"/submit/debug\",\"pathTranslated\":null,\"requestURI\":\"/submit/debug\",\"requestURL\":\"http://127.0.0.1:8080/submit/debug\",\"queryString\":\"wibble=flim&wibble=egg\"}";

  @Test
  public void testManuallyConstructed() throws IOException {
    BarefootServletContext ctx = new BarefootServletContext("");

    BarefootServletRequest.Builder map = BarefootServletRequest.builder();
    String contentType = "text/plain; charset=UTF-8";
    map.method("POST");
    map.requestUri("/submit/debug");
    map.servletContext(ctx);
    map.queryString("wibble=flim&wibble=egg");
    map.contentType(contentType);
    map.contextPath("");

    String content = "SOME=THING&ANYTHING=ELSE";
    byte[] contentBytes = content.getBytes();
    map.contentLength(contentBytes.length);
    map.inputStream(() -> new ByteArrayInputStream(contentBytes));
    map.characterEncoding("UTF-8");
    map.parameters(new HashMap<>());
    map.isSecure(Boolean.FALSE);

    Map<String, List<String>> headers = new HashMap<>();

    map.headers(headers);

    {
      List<String> list = new ArrayList();
      list.add(Integer.toString(contentBytes.length));
      headers.put("content-length", list);
    }

    {
      List<String> list = new ArrayList();
      list.add("127.0.0.1:8080");
      headers.put("host", list);
    }

    {
      List<String> list = new ArrayList();
      list.add(contentType);
      headers.put("content-type", list);
    }

    {
      List<String> list = new ArrayList();
      list.add("curl/7.64.0");
      headers.put("user-agent", list);
    }

    {
      List<String> list = new ArrayList();
      list.add("*/*");
      headers.put("accept", list);
    }

    {
      List<String> list = new ArrayList();
      list.add("SESSION=ABCD-1234; WIBBLE=42");
      headers.put("cookie", list);
    }

    BarefootServletRequest req = new BarefootServletRequest(map);

    {
      req.getParameterMap().put("wibble", new String[] {"flim", "egg"});
    }

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    BarefootServletResponse resp =
        BarefootServletResponse.builder().outputStream(() -> baos).build();
    Assert.assertNotNull(resp);

    Assert.assertNotNull(req.getSession());
    Assert.assertEquals(req.getSession(), req.getSession());
  }

  private String readLine(InputStream is) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    int last = -1;

    while (true) {
      int c = is.read();
      if (c < 0) return null;
      if (c == 0xD) {
        last = c;
      } else {
        if (c == 0xA) {
          break;
        }
        baos.write(c);
      }
    }

    return new String(baos.toByteArray(), StandardCharsets.UTF_8);
  }
}
