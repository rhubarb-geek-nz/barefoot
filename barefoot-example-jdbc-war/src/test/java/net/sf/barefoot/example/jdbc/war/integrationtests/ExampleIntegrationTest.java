package net.sf.barefoot.example.jdbc.war.integrationtests;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import org.apache.catalina.LifecycleException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class ExampleIntegrationTest extends BaseIntegrationTest {

  @Before
  @Override
  public void before() throws LifecycleException, IOException {
    super.before();
  }

  @After
  @Override
  public void after() throws LifecycleException {
    super.after();
  }

  @Test
  public void confirm() {}

  @Test
  public void getApiHttpExample() {
    RestTemplate restTemplate = new RestTemplate();

    ResponseEntity<Map> response =
        restTemplate.getForEntity(SERVER_URL + "/api/HttpExample", Map.class);

    Assert.assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void getIndex() {
    boolean caught = false;
    RestTemplate restTemplate = new RestTemplate();

    try {
      restTemplate.getForEntity(SERVER_URL, String.class);
    } catch (HttpClientErrorException ex) {
      Assert.assertEquals(401, ex.getRawStatusCode());
      caught = true;
    }

    Assert.assertTrue(caught);
  }

  @Test
  public void getSession() {
    String authStr = "barefoot:changeit";
    String base64Creds = Base64.getEncoder().encodeToString(authStr.getBytes());
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Basic " + base64Creds);
    HttpEntity request = new HttpEntity(headers);
    ResponseEntity<String> response =
        new RestTemplate().exchange(SERVER_URL + "/session", HttpMethod.GET, request, String.class);
    Assert.assertEquals(200, response.getStatusCodeValue());
    Assert.assertTrue(response.getBody().contains("barefoot"));
  }

  @Test
  public void getSession401() {
    boolean caught = false;

    try {
      String authStr = "barefoot:changed";
      String base64Creds = Base64.getEncoder().encodeToString(authStr.getBytes());
      HttpHeaders headers = new HttpHeaders();
      headers.add("Authorization", "Basic " + base64Creds);
      HttpEntity request = new HttpEntity(headers);
      ResponseEntity<String> response =
          new RestTemplate()
              .exchange(SERVER_URL + "/session", HttpMethod.GET, request, String.class);
    } catch (HttpClientErrorException ex) {
      Assert.assertEquals(401, ex.getRawStatusCode());
      caught = true;
    }

    Assert.assertTrue(caught);
  }
}
