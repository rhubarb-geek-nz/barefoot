package net.sf.barefoot.example.cxf.app;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.ResponseEntity;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BarefootExampleCxfAppApplicationTests {

  @Autowired private ServletWebServerApplicationContext webServerAppCtxt;

  @Test
  void contextLoads() {}

  @Test
  void getWsdl() throws ParserConfigurationException, IOException, SAXException {
    int port = webServerAppCtxt.getWebServer().getPort();
    TestRestTemplate testRestTemplate = new TestRestTemplate();
    ResponseEntity<byte[]> response =
        testRestTemplate.getForEntity(
            "http://localhost:" + port + "/services/ExampleService?wsdl", byte[].class);
    Assertions.assertTrue(response.getStatusCodeValue() == 200);
    readXML(new ByteArrayInputStream(response.getBody()));
  }

  Document readXML(InputStream is) throws ParserConfigurationException, SAXException, IOException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    return builder.parse(is);
  }
}
