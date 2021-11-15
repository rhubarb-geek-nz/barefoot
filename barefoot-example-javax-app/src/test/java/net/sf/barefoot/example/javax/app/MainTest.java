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

package net.sf.barefoot.example.javax.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

public class MainTest {
  final String port = System.getProperty("server.port");
  final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void test() throws Exception {
    Main main = new Main();
    try {
      main.start();
      URL url = new URL("http://localhost:" + port);
      Map map;
      try (InputStream is = url.openStream()) {
        map = objectMapper.readValue(is, Map.class);
      }
      Assert.assertEquals("GET", map.get("method"));
    } finally {
      main.stop();
    }
  }
}
