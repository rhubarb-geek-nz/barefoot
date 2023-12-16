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

package net.sf.barefoot.example.metro.google;

import com.google.cloud.functions.HttpRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import net.sf.barefoot.context.xml.google.Function;
import net.sf.barefoot.google.concrete.ConcreteHttpRequest;
import net.sf.barefoot.google.concrete.ConcreteHttpResponse;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/** test class for example */
public class FunctionTest {

  public FunctionTest() {}

  @Test
  public void testSimpleGet() throws Exception {
    Function instance = new Function();
    Assert.assertNotNull(instance);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    Map<String, List<String>> queryParameters = new HashMap<>();
    List<String> list = new ArrayList<>();
    list.add(null);
    queryParameters.put("wsdl", list);

    HttpRequest req =
        ConcreteHttpRequest.builder()
            .path("/services/ExampleService")
            .uri("http://localhost/services/ExampleService")
            .method("GET")
            .characterEncoding(Optional.empty())
            .contentType(Optional.empty())
            .query(Optional.of("wsdl"))
            .queryParameters(queryParameters)
            .headers(new HashMap<>())
            .build();
    ConcreteHttpResponse resp = ConcreteHttpResponse.builder().setOutputStream(baos).build();

    instance.service(req, resp);

    Assert.assertEquals(200, resp.getStatusCode());

    String ct = resp.getContentType().get();

    Assert.assertTrue(ct.startsWith("text/xml"));

    byte[] body = baos.toByteArray();

    readXML(new ByteArrayInputStream(body));
  }

  Document readXML(InputStream is) throws ParserConfigurationException, SAXException, IOException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    return builder.parse(is);
  }
}
