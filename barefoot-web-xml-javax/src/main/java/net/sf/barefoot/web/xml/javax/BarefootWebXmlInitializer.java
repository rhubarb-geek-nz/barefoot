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

package net.sf.barefoot.web.xml.javax;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.function.Function;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import net.sf.barefoot.util.StringVariableExpander;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/** Initializer for web.xml. */
public final class BarefootWebXmlInitializer implements ServletContainerInitializer {

  @Override
  public void onStartup(Set<Class<?>> classSet, ServletContext servletContext)
      throws ServletException {
    try {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser saxParser = factory.newSAXParser();

      DefaultHandler handler =
          new BarefootWebXmlHandler(
              servletContext, new StringVariableExpander(new VariableMapper()));

      try (InputStream is = servletContext.getResourceAsStream("/WEB-INF/web.xml")) {
        if (is == null) {
          throw new ServletException("/WEB-INF/web.xml not found");
        }
        saxParser.parse(is, handler);
      }
    } catch (IOException | ParserConfigurationException | SAXException ex) {
      throw new ServletException(ex);
    }
  }

  private static class VariableMapper implements Function<String, String> {
    final Context context;

    VariableMapper() {
      Context result;
      try {
        result = (Context) new InitialContext().lookup("java:comp/env");
      } catch (NamingException ex) {
        result = null;
      }
      context = result;
    }

    @Override
    public String apply(String name) {
      if (context != null) {
        try {
          Object value = context.lookup(name);
          return value == null ? null : value.toString();
        } catch (NamingException ex) {
        }
      }
      String value = System.getProperty(name);
      return value == null ? System.getenv(name) : value;
    }
  }
}
