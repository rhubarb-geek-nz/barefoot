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

package net.sf.barefoot.example.jdbc.aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.sql.DataSource;
import net.sf.barefoot.aws.concrete.ConcreteContext;
import net.sf.barefoot.aws.lambda.HttpServletRequestFactory;
import net.sf.barefoot.context.javax.BarefootCookieCutter;
import net.sf.barefoot.context.javax.BarefootServletContext;
import net.sf.barefoot.context.xml.aws.Handler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

/** test harness for handler */
public class HandlerTest {
  final ObjectMapper mapper = new ObjectMapper();
  final HttpServletRequestFactory requestFactory = new HttpServletRequestFactory();
  final ServletContext servletContext = new BarefootServletContext("");
  final BarefootCookieCutter cookieCutter = new BarefootCookieCutter();
  final TypeReference<Map<String, Object>> typeMapStringObject =
      new TypeReference<Map<String, Object>>() {};

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
                  "net/sf/barefoot/example/jdbc/aws/security.sql")
              .build();
    }
  }

  /** Check database before each test. */
  @Before
  public void before() {
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
        Assert.assertTrue(rs.next());
        Assert.assertEquals(1, rs.getInt(1));
      }
    }
  }

  /* Confirm database driver exists. */
  @Test
  public void driverExists() {
    Assert.assertNotNull(org.h2.Driver.class);
  }

  /**
   * Simple get.
   *
   * @throws java.lang.Exception on error.
   */
  @Test
  public void getApiHttpExample() throws Exception {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Context cntxt = ConcreteContext.builder().build();
    Handler instance = new Handler();

    handleRequest(instance, getClass().getResourceAsStream("getApiHttpExample.json"), out, cntxt);
    byte[] outBytes = out.toByteArray();
    Assert.assertNotNull(outBytes);
    APIGatewayProxyResponseEvent response =
        mapper.readValue(new ByteArrayInputStream(outBytes), APIGatewayProxyResponseEvent.class);
    Assert.assertNotNull(response);
  }

  /**
   * Simple post.
   *
   * @throws java.lang.Exception on error.
   */
  @Test
  public void postApiHttpExample() throws Exception {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Context cntxt = ConcreteContext.builder().build();
    Handler instance = new Handler();

    handleRequest(instance, getClass().getResourceAsStream("postApiHttpExample.json"), out, cntxt);
    byte[] outBytes = out.toByteArray();
    Assert.assertNotNull(outBytes);

    APIGatewayV2HTTPResponse response =
        mapper.readValue(new ByteArrayInputStream(outBytes), APIGatewayV2HTTPResponse.class);
    Assert.assertNotNull(response);
  }

  /**
   * Confirm extra path nesting
   *
   * @throws java.lang.Exception on error
   */
  @Test
  public void getTestApiHttpExample() throws Exception {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Context cntxt = ConcreteContext.builder().build();
    Handler instance = new Handler();

    handleRequest(
        instance, getClass().getResourceAsStream("getTestApiHttpExample.json"), out, cntxt);
    byte[] outBytes = out.toByteArray();
    Assert.assertNotNull(outBytes);

    APIGatewayV2HTTPResponse response =
        mapper.readValue(new ByteArrayInputStream(outBytes), APIGatewayV2HTTPResponse.class);

    String body = response.getBody();
    Assert.assertFalse(response.getIsBase64Encoded());

    Map state = mapper.readValue(body, Map.class);

    Assert.assertEquals("/api/HttpExample", state.get("pathInfo"));
    Assert.assertEquals("/test", state.get("contextPath"));
    Assert.assertEquals("/test/api/HttpExample", state.get("requestURI"));
  }

  /**
   * Should get an session HttpCookie back.
   *
   * @throws java.lang.Exception on error
   */
  @Test
  public void getApiSession() throws Exception {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Context cntxt = ConcreteContext.builder().build();
    Handler instance = new Handler();

    handleRequest(instance, getClass().getResourceAsStream("getApiSession.json"), out, cntxt);
    byte[] outBytes = out.toByteArray();
    Assert.assertNotNull(outBytes);

    APIGatewayV2HTTPResponse response =
        mapper.readValue(new ByteArrayInputStream(outBytes), APIGatewayV2HTTPResponse.class);

    String body = response.getBody();
    Assert.assertFalse(response.getIsBase64Encoded());

    Assert.assertEquals(200, response.getStatusCode());
    // session cookie should be here
    Assert.assertFalse(response.getCookies().isEmpty());

    Cookie cookie = cookieCutter.parseSetCookie(response.getCookies().get(0));

    Assert.assertEquals("SESSION", cookie.getName());
  }

  /**
   * Authorisation should be refused
   *
   * @throws java.lang.Exception on error
   */
  @Test
  public void getApiSession401() throws Exception {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Context cntxt = ConcreteContext.builder().build();
    Handler instance = new Handler();

    handleRequest(instance, getClass().getResourceAsStream("getApiSession401.json"), out, cntxt);
    byte[] outBytes = out.toByteArray();
    Assert.assertNotNull(outBytes);

    APIGatewayV2HTTPResponse response =
        mapper.readValue(new ByteArrayInputStream(outBytes), APIGatewayV2HTTPResponse.class);

    Assert.assertEquals(401, response.getStatusCode());
  }

  private void handleRequest(
      Handler instance, InputStream in, ByteArrayOutputStream out, Context cntxt)
      throws IOException {
    Map<String, Object> request = mapper.readValue(in, typeMapStringObject);
    Map<String, Object> response = instance.handleRequest(request, cntxt);
    mapper.writeValue(out, response);
  }
}
