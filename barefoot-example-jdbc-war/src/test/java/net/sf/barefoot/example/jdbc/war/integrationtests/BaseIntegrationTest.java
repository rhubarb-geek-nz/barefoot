package net.sf.barefoot.example.jdbc.war.integrationtests;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import net.sf.barefoot.example.jdbc.war.ExampleTest;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.junit.*;

public abstract class BaseIntegrationTest {

  final String WEBAPPDIR = System.getProperty("WEBAPPDIR");
  final String JDBC_DRIVER_NAME = System.getProperty("JDBC_DRIVER_NAME");
  final String JDBC_CONNECTION_STRING = System.getProperty("JDBC_CONNECTION_STRING");
  final String SERVER_URL = System.getProperty("SERVER_URL");

  Tomcat tomcat;

  protected void before() throws LifecycleException, IOException {
    if (tomcat == null) {
      File temp = File.createTempFile("integration-test", ".tmp");
      int port = new URL(SERVER_URL).getPort();
      ExampleTest.initDb();
      tomcat = new Tomcat();
      tomcat.setBaseDir(temp.getParent());
      tomcat.setPort(port);
      tomcat.addWebapp("", WEBAPPDIR);
      tomcat.enableNaming();
      tomcat.start();
      tomcat.getConnector();
    }
  }

  protected void after() throws LifecycleException {
    Tomcat localTomcat = tomcat;
    tomcat = null;
    if (localTomcat != null) {
      localTomcat.stop();
      localTomcat.destroy();
      localTomcat.getServer().await();
    }
  }

  @Test
  public void propertyCheck() {
    Assert.assertEquals("org.h2.Driver", JDBC_DRIVER_NAME);
    Assert.assertEquals("jdbc:h2:mem:testdb;USER=sa", JDBC_CONNECTION_STRING);

    File file = new File(WEBAPPDIR + File.separator + "WEB-INF");

    Assert.assertTrue(file.getPath(), file.isDirectory());
  }
}
