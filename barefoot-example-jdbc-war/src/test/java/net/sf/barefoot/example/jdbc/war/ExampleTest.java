package net.sf.barefoot.example.jdbc.war;

import javax.sql.DataSource;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

public class ExampleTest {

  /** Only one database shared throughout. */
  static DataSource dataSource;

  /** Create database if it does not exist. */
  public static void initDb() {
    if (dataSource == null) {
      dataSource =
          new EmbeddedDatabaseBuilder()
              .setType(EmbeddedDatabaseType.H2)
              .addScripts(
                  "org/springframework/session/jdbc/schema-h2.sql",
                  "net/sf/barefoot/example/jdbc/war/security.sql")
              .build();
    }
  }

  @Before
  public void before() {
    initDb();
  }

  @After
  public void after() {}

  @Test
  public void confirm() {}

  @Test
  public void propertyCheck() {
    Assert.assertEquals("org.h2.Driver", System.getProperty("JDBC_DRIVER_NAME"));
    Assert.assertEquals("jdbc:h2:mem:testdb;USER=sa", System.getProperty("JDBC_CONNECTION_STRING"));
  }
}
