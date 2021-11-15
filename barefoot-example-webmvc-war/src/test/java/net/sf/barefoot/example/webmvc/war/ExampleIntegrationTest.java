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

package net.sf.barefoot.example.webmvc.war;

import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/** Integration test */
public class ExampleIntegrationTest {

  RestTemplate restTemplate = new RestTemplate();

  /** Test simple HTTP GET */
  @Test
  public void httpGet() {
    String port = System.getProperty("barefoot.http.port");
    Assert.assertTrue(Integer.parseInt(port) > 0);
    String httpUrl = System.getProperty("barefoot.http.url");
    Assert.assertTrue(httpUrl.contains("://"));

    ResponseEntity<Map> response =
        restTemplate.getForEntity(httpUrl + "/api/HttpExample", Map.class);

    Assert.assertEquals("status", 200, response.getStatusCodeValue());
    Assert.assertEquals("method", "GET", response.getBody().get("method"));
  }

  @Test
  public void httpGet404() {
    String port = System.getProperty("barefoot.http.port");
    Assert.assertTrue(Integer.parseInt(port) > 0);
    String httpUrl = System.getProperty("barefoot.http.url");
    Assert.assertTrue(httpUrl.contains("://"));
    boolean caught = false;

    try {
      ResponseEntity<Map> response =
          restTemplate.getForEntity(httpUrl + "/api/DoesNotExist", Map.class);
    } catch (HttpClientErrorException.NotFound ex) {
      caught = true;
    }

    Assert.assertTrue("caught", caught);
  }

  @Test
  public void httpGet405() {
    String port = System.getProperty("barefoot.http.port");
    Assert.assertTrue(Integer.parseInt(port) > 0);
    String httpUrl = System.getProperty("barefoot.http.url");
    Assert.assertTrue(httpUrl.contains("://"));
    boolean caught = false;

    try {
      ResponseEntity<Map> response =
          restTemplate.getForEntity(httpUrl + "/api/HttpExample2", Map.class);
    } catch (HttpClientErrorException.MethodNotAllowed ex) {
      caught = true;
    }

    Assert.assertTrue("caught", caught);
  }
}
