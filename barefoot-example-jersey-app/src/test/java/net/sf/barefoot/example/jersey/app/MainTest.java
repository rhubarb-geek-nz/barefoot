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

package net.sf.barefoot.example.jersey.app;

import java.io.InputStream;
import java.net.URL;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.junit.Assert;
import org.junit.Test;

public class MainTest {
  final String port = System.getProperty("server.port");

  @Test
  public void test() throws Exception {
    Main main = new Main();
    try {
      main.start();
      URL url = new URL("http://localhost:" + port + "/api/HttpExample");
      JsonObject json;
      try (InputStream is = url.openStream()) {
        JsonReader reader = Json.createReader(is);

        json = reader.readObject();
      }

      Assert.assertEquals("method", "GET", json.getString("method"));
    } finally {
      main.stop();
    }
  }
}
