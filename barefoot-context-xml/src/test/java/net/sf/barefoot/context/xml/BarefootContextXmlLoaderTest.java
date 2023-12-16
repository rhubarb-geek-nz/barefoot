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
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

/** Test loader */
public class BarefootContextXmlLoaderTest {

  @Test
  public void testLoader()
      throws ParserConfigurationException, SAXException, IOException, NamingException {
    Context init = new InitialContext();
    Hashtable<?, ?> env = init.getEnvironment();
    Assert.assertNotNull(env);
    init = (Context) init.lookup("java:comp/env");
    ClassLoader cl = getClass().getClassLoader();
    BarefootContextXmlLoader.load(init, cl, BarefootContextXmlLoader.META_INF_CONTEXT_XML);

    String value = init.lookup("net.sf.barefoot.context.xml.bar").toString();

    Assert.assertEquals("bar value", value);

    Object dataSource = init.lookup("jdbc/dbcp");

    Assert.assertNotNull(dataSource);

    Object barefoot = init.lookup("barefoot/context");

    Assert.assertNotNull(barefoot);

    init.unbind("jdbc");
    init.unbind("barefoot");
  }
}
