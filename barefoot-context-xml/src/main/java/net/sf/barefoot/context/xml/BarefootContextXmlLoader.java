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
import javax.naming.Context;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import net.sf.barefoot.util.StringVariableExpander;
import org.xml.sax.SAXException;

/** Utility to load file with expansion. */
public class BarefootContextXmlLoader {
  public static void load(Context ctxt, ClassLoader cl, String path)
      throws ParserConfigurationException, SAXException, IOException {
    SAXParserFactory factory = SAXParserFactory.newInstance();
    SAXParser saxParser = factory.newSAXParser();
    BarefootContextXmlHandler handler =
        new BarefootContextXmlHandler(
            ctxt,
            new StringVariableExpander(
                (String name) -> {
                  String value = System.getProperty(name);
                  return value == null ? System.getenv(name) : value;
                }));

    try (InputStream is = cl.getResourceAsStream(path)) {
      if (is == null) throw new IOException("File not found: " + path);
      saxParser.parse(is, handler);
    }
  }

  public static final String META_INF_CONTEXT_XML = "META-INF/context.xml",
      JAVA_COMP_ENV = "java:comp/env";
}
