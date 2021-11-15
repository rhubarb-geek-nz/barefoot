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

import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BarefootExampleServletAppApplicationTests {

  @Autowired private ServletWebServerApplicationContext webServerAppCtxt;

  @Test
  void contextLoads() {}

  @Test
  void simpleGet() {
    int port = webServerAppCtxt.getWebServer().getPort();
    TestRestTemplate testRestTemplate = new TestRestTemplate();
    ResponseEntity<Map> response =
        testRestTemplate.getForEntity("http://localhost:" + port, Map.class);
    Assertions.assertTrue(response.getStatusCodeValue() == 200);
    Assertions.assertTrue("GET".equals(response.getBody().get("method")));
  }
}
