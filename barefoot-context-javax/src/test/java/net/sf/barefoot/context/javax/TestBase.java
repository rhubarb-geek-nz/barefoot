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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.barefoot.context.BarefootServletContextLogger;

/** Test Base class */
public class TestBase {

  protected JettyServer jettyServer;

  final BarefootServletContextLogger logger =
      new BarefootServletContextLogger() {
        @Override
        public void log(String msg) {
          Logger.getGlobal().info(msg);
        }

        @Override
        public void log(String msg, Throwable thr) {
          Logger.getGlobal().log(Level.INFO, msg, thr);
        }
      };

  protected Map<String, String> getHeaders(String req) throws IOException {
    Socket sock = new Socket("localhost", jettyServer.port);
    byte[] d = req.getBytes(StandardCharsets.UTF_8);

    String result = null;
    Map<String, String> headers = new HashMap<>();

    try (OutputStream os = sock.getOutputStream()) {
      os.write(d);
      os.flush();

      try (InputStream is = sock.getInputStream()) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String n;
        while (null != (n = reader.readLine())) {
          n = n.trim();
          if (n.isEmpty()) break;

          int f = n.indexOf(':');
          if (f > 0) {
            String name = n.substring(0, f);
            f++;
            while (' ' == n.charAt(f)) f++;
            String value = n.substring(f);
            headers.put(name.toLowerCase(), value);
          } else {
            headers.put(null, n);
          }
        }
      }
    }

    return headers;
  }

  protected void addHeader(Map<String, List<String>> headers, String name, String value) {
    List<String> list = headers.get(name);
    if (list == null) {
      list = new ArrayList();
      headers.put(name, list);
    }
    list.add(value);
  }

  String testWithSocket(String req) throws IOException {
    Socket sock = new Socket("localhost", jettyServer.port);
    byte[] d = req.getBytes(StandardCharsets.UTF_8);

    String result = null;
    List<String> lines = new ArrayList<>();
    Map<String, String> headers = new HashMap<>();

    try (OutputStream os = sock.getOutputStream()) {
      os.write(d);
      os.flush();

      try (InputStream is = sock.getInputStream()) {

        while (true) {
          String n = readLine(is);
          if (n.isEmpty()) break;

          if (!lines.isEmpty()) {
            int f = n.indexOf(':');
            String name = n.substring(0, f);
            f++;
            while (' ' == n.charAt(f)) f++;
            String value = n.substring(f);
            headers.put(name.toLowerCase(), value);
          }

          lines.add(n);
        }

        String encoding = headers.get("transfer-encoding");

        if ("chunked".equals(encoding)) {
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          byte[] buf = new byte[512];
          while (true) {
            String lenLine = readLine(is);
            int len = Integer.parseInt(lenLine, 16);
            if (len > 0) {
              int i = len;

              while (i > 0) {
                int j = is.read(buf, 0, (i > buf.length) ? buf.length : i);
                if (j <= 0) {
                  throw new IOException("chunked encoding error 2");
                }
                baos.write(buf, 0, j);
                i -= j;
              }
            }

            lenLine = readLine(is);
            if (!lenLine.isEmpty()) {
              throw new IOException("chunked encoding error 1");
            }
            if (len == 0) {
              break;
            }
          }

          result = new String(baos.toByteArray(), StandardCharsets.UTF_8);
        } else {
          String contentLength = headers.get("content-length");
          if (contentLength != null) {
            int len = Integer.parseInt(contentLength);
            if (len > 0) {
              ByteArrayOutputStream baos = new ByteArrayOutputStream();
              byte[] buf = new byte[512];
              while (len > 0) {
                int i = is.read(buf, 0, buf.length > len ? len : buf.length);
                if (i < 1) break;
                baos.write(buf, 0, i);
                len -= i;
              }
              if (len != 0) throw new IOException("length problem, still need " + len);
              result = new String(baos.toByteArray(), StandardCharsets.UTF_8);
            } else {
              if (len == 0) {
                result = "";
              }
            }
          }
        }
      }
    }

    return result;
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
