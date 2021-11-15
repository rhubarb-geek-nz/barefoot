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

package net.sf.barefoot.context.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

/** Test context.xml handler. */
public class BarefootContextXmlHandlerTest {

  @Test
  public void testParser()
      throws ParserConfigurationException, SAXException, IOException, NamingException {
    Context init = new InitialContext();
    Hashtable<?, ?> env = init.getEnvironment();
    Assert.assertNotNull(env);
    init = (Context) init.lookup("java:comp/env");
    SAXParserFactory factory = SAXParserFactory.newInstance();
    SAXParser saxParser = factory.newSAXParser();
    ClassLoader cl = getClass().getClassLoader();
    BarefootContextXmlHandler handler = new BarefootContextXmlHandler(init, null);

    try (InputStream is = cl.getResourceAsStream(BarefootContextXmlLoader.META_INF_CONTEXT_XML)) {
      saxParser.parse(is, handler);
    }

    String value = init.lookup("net.sf.barefoot.context.xml.foo").toString();

    Assert.assertEquals("foo value", value);

    init.unbind("jdbc");
    init.unbind("barefoot");
  }
}
