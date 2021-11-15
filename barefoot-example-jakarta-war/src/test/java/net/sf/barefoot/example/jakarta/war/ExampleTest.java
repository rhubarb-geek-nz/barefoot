/*
 *
 *  Copyright 2021, Roger Brown
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

package net.sf.barefoot.example.jakarta.war;

import java.net.MalformedURLException;
import java.net.URL;
import org.junit.Assert;
import org.junit.Test;

/** Normal test phase */
public class ExampleTest {

  /**
   * Test system properties
   *
   * @throws MalformedURLException
   */
  @Test
  public void systemPropertyVariables() throws MalformedURLException {
    String port = System.getProperty("barefoot.http.port");
    Assert.assertTrue(Integer.parseInt(port) > 0);
    String httpUrl = System.getProperty("barefoot.http.url");
    URL url = new URL(httpUrl);
  }
}
