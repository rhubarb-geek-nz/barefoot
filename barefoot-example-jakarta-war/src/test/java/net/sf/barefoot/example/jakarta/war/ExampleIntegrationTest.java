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

package net.sf.barefoot.example.jakarta.war;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

/** Integration test */
public class ExampleIntegrationTest {
  ObjectMapper objectMapper = new ObjectMapper();

  /**
   * Test simple HTTP GET
   *
   * @throws MalformedURLException
   * @throws IOException
   */
  @Test
  public void httpGet() throws MalformedURLException, IOException {
    String port = System.getProperty("barefoot.http.port");
    Assert.assertTrue(Integer.parseInt(port) > 0);
    String httpUrl = System.getProperty("barefoot.http.url");
    Assert.assertTrue(httpUrl.contains("://"));

    URL url = new URL(httpUrl + "/anything");

    HttpURLConnection http = (HttpURLConnection) url.openConnection();

    try (InputStream is = http.getInputStream()) {
      Assert.assertTrue("status", http.getResponseCode() == 200);

      Map map = objectMapper.readValue(is, Map.class);

      Assert.assertTrue("method", "GET".equals(map.get("method")));
    }
  }
}
