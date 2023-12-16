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

package net.sf.barefoot.example.cxf.azure;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import net.sf.barefoot.azure.concrete.ConcreteExecutionContext;
import net.sf.barefoot.azure.concrete.ConcreteHttpRequestMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/** Unit test for Function class. */
public class FunctionTest {
  /**
   * Unit test for HttpTriggerJava method.
   *
   * @throws java.lang.Exception on error
   */
  @Test
  public void testHttpTriggerJava() throws Exception {
    // Setup
    URI uri = new URI("/services/ExampleService");
    HttpMethod method = HttpMethod.GET;

    final Map<String, String> headers = new HashMap<>();
    headers.put("Host", "level:42");

    final Map<String, String> queryParams = new HashMap<>();
    queryParams.put("wsdl", null);

    final Optional<String> queryBody = Optional.empty();
    final HttpRequestMessage<Optional<String>> req =
        ConcreteHttpRequestMessage.builder(queryBody)
            .uri(uri)
            .method(method)
            .queryParameters(queryParams)
            .headers(headers)
            .build();
    final ExecutionContext context = new ConcreteExecutionContext();

    // Invoke
    final HttpResponseMessage ret = new Function().run(req, context);

    // Verify
    Assertions.assertEquals(HttpStatus.OK, ret.getStatus());

    Object body = ret.getBody();

    Assertions.assertTrue(body instanceof byte[]);

    {
      byte[] asBytes = (byte[]) body;
      readXML(new ByteArrayInputStream(asBytes));
    }
  }

  @Test
  public void testGetMessage() throws Exception {
    // Setup
    URI uri = new URI("/services/ExampleService");
    HttpMethod method = HttpMethod.POST;

    Optional<String> body;
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      try (InputStream is = getClass().getResourceAsStream("getMessage.xml")) {
        int i;
        byte[] b = new byte[4096];
        while ((i = is.read(b)) > 0) {
          baos.write(b, 0, i);
        }
      }
      body = Optional.of(new String(baos.toByteArray(), StandardCharsets.UTF_8));
    }

    final Map<String, String> headers = new HashMap<>();
    headers.put("Host", "level:42");
    headers.put("Content-Type", "text/xml;charset=UTF-8");
    headers.put("SOAPAction", "\"https://wsdl.example.barefoot.sf.net/barefoot/getMessage\"");

    final HttpRequestMessage<Optional<String>> req =
        ConcreteHttpRequestMessage.builder(body).uri(uri).method(method).headers(headers).build();
    final ExecutionContext context = new ConcreteExecutionContext();

    // Invoke
    final HttpResponseMessage ret = new Function().run(req, context);

    Object response = ret.getBody();

    Assertions.assertTrue(response instanceof byte[]);

    {
      byte[] asBytes = (byte[]) response;
      readXML(new ByteArrayInputStream(asBytes));
    }

    // Verify
    Assertions.assertEquals(HttpStatus.OK, ret.getStatus());
  }

  Document readXML(InputStream is) throws ParserConfigurationException, SAXException, IOException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    return builder.parse(is);
  }
}
