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

package net.sf.barefoot.example.jdbc.app;

import java.sql.*;
import java.util.Map;
import javax.sql.DataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BarefootExampleJdbcAppApplicationTests {

  @Autowired private ServletWebServerApplicationContext webServerAppCtxt;

  /** Only one database shared throughout. */
  static DataSource dataSource;

  /** Create database if it does not exist. */
  static void initDb() {
    if (dataSource == null) {
      dataSource =
          new EmbeddedDatabaseBuilder()
              .setType(EmbeddedDatabaseType.H2)
              .addScripts(
                  "org/springframework/session/jdbc/schema-h2.sql",
                  "net/sf/barefoot/example/jdbc/app/security.sql")
              .build();
    }
  }

  /** Check database before each test. */
  @BeforeAll
  public static void before() {
    initDb();
  }

  /**
   * Confirm test user exists.
   *
   * @throws java.sql.SQLException on error
   */
  @Test
  public void jdbcConnectionTest() throws SQLException {
    String url = System.getProperty("JDBC_CONNECTION_STRING");
    try (Connection conn = DriverManager.getConnection(url);
        PreparedStatement stmt =
            conn.prepareStatement("SELECT count(*) FROM users WHERE username=?")) {
      stmt.setString(1, "barefoot");
      try (ResultSet rs = stmt.executeQuery()) {
        Assertions.assertTrue(rs.next());
        Assertions.assertEquals(1, rs.getInt(1));
      }
    }
  }

  @Test
  void contextLoads() {}

  @Test
  void simpleGet() {
    int port = webServerAppCtxt.getWebServer().getPort();
    TestRestTemplate testRestTemplate = new TestRestTemplate();
    ResponseEntity<Map> response =
        testRestTemplate.getForEntity("http://localhost:" + port + "/api/HttpExample", Map.class);
    Assertions.assertTrue(response.getStatusCodeValue() == 200);
    Assertions.assertTrue("GET".equals(response.getBody().get("method")));
  }

  @Test
  void simpleGetSession401noauth() {
    int port = webServerAppCtxt.getWebServer().getPort();
    TestRestTemplate testRestTemplate = new TestRestTemplate();
    ResponseEntity<Map> response =
        testRestTemplate.getForEntity("http://localhost:" + port + "/session", Map.class);
    Assertions.assertTrue(response.getStatusCodeValue() == 401);
  }

  @Test
  void simpleGetSession200() {
    int port = webServerAppCtxt.getWebServer().getPort();
    TestRestTemplate testRestTemplate = new TestRestTemplate();
    ResponseEntity<String> response =
        testRestTemplate
            .withBasicAuth("barefoot", "changeit")
            .getForEntity("http://localhost:" + port + "/session", String.class);
    Assertions.assertTrue(response.getStatusCodeValue() == 200);
  }

  @Test
  void simpleGetSession401changed() {
    int port = webServerAppCtxt.getWebServer().getPort();
    TestRestTemplate testRestTemplate = new TestRestTemplate();
    ResponseEntity<String> response =
        testRestTemplate
            .withBasicAuth("barefoot", "changed")
            .getForEntity("http://localhost:" + port + "/session", String.class);
    Assertions.assertTrue(response.getStatusCodeValue() == 401);
  }
}
