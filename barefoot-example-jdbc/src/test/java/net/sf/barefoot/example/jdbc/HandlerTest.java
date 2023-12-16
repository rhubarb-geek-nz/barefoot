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

package net.sf.barefoot.example.jdbc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.Cookie;
import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import net.sf.barefoot.context.AbstractServletContext;
import net.sf.barefoot.context.AbstractServletRequest;
import net.sf.barefoot.context.AbstractServletResponse;
import net.sf.barefoot.context.BarefootContentType;
import net.sf.barefoot.context.javax.BarefootCookieCutter;
import net.sf.barefoot.context.javax.BarefootServletContext;
import net.sf.barefoot.context.xml.BarefootContextXmlLoader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.xml.sax.SAXException;

/** test harness for handler */
public class HandlerTest {
  final ObjectMapper mapper = new ObjectMapper();
  final AbstractServletContext servletContext = new BarefootServletContext("");
  final BarefootCookieCutter cookieCutter = new BarefootCookieCutter();
  final TypeReference<Map<String, Object>> typeMapStringObject =
      new TypeReference<Map<String, Object>>() {};

  /** Only one database shared throughout. */
  static DataSource dataSource;

  static boolean naming;

  public HandlerTest() throws Exception {
    if (!naming) {
      naming = true;

      InitialContext init = new InitialContext();

      BarefootContextXmlLoader.load(
          (Context) init.lookup(BarefootContextXmlLoader.JAVA_COMP_ENV),
          AnnotationConfigDispatcherServletInitializer.class.getClassLoader(),
          BarefootContextXmlLoader.META_INF_CONTEXT_XML);
    }

    servletContext.onStartup();
  }

  /** Create database if it does not exist. */
  static void initDb()
      throws NamingException, ParserConfigurationException, SAXException, IOException {
    if (dataSource == null) {
      dataSource =
          new EmbeddedDatabaseBuilder()
              .setType(EmbeddedDatabaseType.H2)
              .addScripts(
                  "org/springframework/session/jdbc/schema-h2.sql",
                  "net/sf/barefoot/example/jdbc/security.sql")
              .build();
    }
  }

  /** Check database before each test. */
  @Before
  public void before()
      throws NamingException, ParserConfigurationException, SAXException, IOException {
    initDb();
  }

  /**
   * Confirm test user exists.
   *
   * @throws java.sql.SQLException on error
   */
  @Test
  public void jdbcConnectionTest() throws SQLException {
    String url = System.getProperty("spring.datasource.url");
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
    Map<String, List<String>> hdrs = new HashMap<>();

    AbstractServletRequest req =
        servletContext
            .getServletRequestBuilder()
            .method("GET")
            .contextPath("")
            .headers(hdrs)
            .cookies(new ArrayList<>())
            .parameters(new HashMap<>())
            .requestUri("/api/HttpExample")
            .requestUrl("http://localhost/api/HttpExample")
            .build();

    AbstractServletResponse resp = req.getServletResponseBuilder().outputStream(() -> out).build();

    servletContext.dispatch(req, resp);

    Assert.assertEquals(200, resp.getStatus());
  }

  /**
   * Simple post.
   *
   * @throws java.lang.Exception on error.
   */
  @Test
  public void postApiHttpExample() throws Exception {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Map<String, List<String>> hdrs = new HashMap<>();

    AbstractServletRequest req =
        servletContext
            .getServletRequestBuilder()
            .method("POST")
            .contextPath("")
            .headers(hdrs)
            .cookies(new ArrayList<>())
            .parameters(new HashMap<>())
            .requestUri("/api/HttpExample")
            .requestUrl("http://localhost/api/HttpExample")
            .contentType(BarefootContentType.APPLICATION_JSON)
            .inputStream(() -> new ByteArrayInputStream("{}".getBytes()))
            .build();

    AbstractServletResponse resp = req.getServletResponseBuilder().outputStream(() -> out).build();

    servletContext.dispatch(req, resp);

    Assert.assertEquals(200, resp.getStatus());
  }

  /**
   * Confirm extra path nesting
   *
   * @throws java.lang.Exception on error
   */
  @Test
  public void getTestApiHttpExample() throws Exception {
    // getTestApiHttpExample.json
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Map<String, List<String>> hdrs = new HashMap<>();

    AbstractServletRequest req =
        servletContext
            .getServletRequestBuilder()
            .method("GET")
            .headers(hdrs)
            .cookies(new ArrayList<>())
            .parameters(new HashMap<>())
            .requestUri("/test/api/HttpExample")
            .requestUrl("http://localhost/test/api/HttpExample")
            .contextPath("/test")
            .build();

    AbstractServletResponse resp = req.getServletResponseBuilder().outputStream(() -> out).build();

    servletContext.dispatch(req, resp);

    Assert.assertEquals(200, resp.getStatus());

    Map state = mapper.readValue(out.toByteArray(), Map.class);

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
    // getApiSession.json
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Map<String, List<String>> hdrs = new HashMap<>();

    addHeader(hdrs, "authorization", "Basic YmFyZWZvb3Q6Y2hhbmdlaXQ=");
    addHeader(hdrs, "user-agent", "curl/7.64.0");
    addHeader(hdrs, "host", "localhost");
    addHeader(hdrs, "accept", "*/*");

    AbstractServletRequest req =
        servletContext
            .getServletRequestBuilder()
            .method("GET")
            .headers(hdrs)
            .parameters(new HashMap<>())
            .contextPath("/test")
            .requestUri("/test/session")
            .requestUrl("http://localhost/test/session")
            .serverName("localhost")
            .protocol("HTTP/1.1")
            .build();

    AbstractServletResponse resp = req.getServletResponseBuilder().outputStream(() -> out).build();

    servletContext.dispatch(req, resp);

    Assert.assertEquals(200, resp.getStatus());

    List<String> cookies = resp.getSetCookieHeaders();

    if (cookies.isEmpty()) {
      Collection<String> headers = resp.getHeaders("Set-Cookie");
      cookies.addAll(headers);
    }
    // session cookie should be here
    Assert.assertFalse(cookies.isEmpty());

    Cookie cookie = cookieCutter.parseSetCookie(cookies.get(0));

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
    Map<String, List<String>> hdrs = new HashMap<>();

    addHeader(hdrs, "authorization", "Basic dXNlcjpjaGFuZ2VpdA==");
    addHeader(hdrs, "user-agent", "curl/7.64.0");
    addHeader(hdrs, "host", "localhost");
    addHeader(hdrs, "accept", "*/*");

    AbstractServletRequest req =
        servletContext
            .getServletRequestBuilder()
            .method("GET")
            .headers(hdrs)
            .parameters(new HashMap<>())
            .contextPath("/test")
            .requestUri("/test/session")
            .requestUrl("http://localhost/test/session")
            .serverName("localhost")
            .protocol("HTTP/1.1")
            .build();

    AbstractServletResponse resp = req.getServletResponseBuilder().outputStream(() -> out).build();

    servletContext.dispatch(req, resp);

    Assert.assertEquals(401, resp.getStatus());
  }

  void addHeader(Map<String, List<String>> map, String name, String value) {
    List<String> list = map.get(name);
    if (list == null) {
      list = new ArrayList<>();
      map.put(name, list);
    }
    list.add(value);
  }
}
