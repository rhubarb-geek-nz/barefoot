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

package net.sf.barefoot.example.jakarta.war;

import net.sf.barefoot.example.jakarta.jackson.ExampleServlet;
import org.junit.Assert;
import org.junit.Test;

/** Test cases for servlet. */
public class ExampleServletTest {

  @Test
  public void testSomeMethod() {
    ExampleServlet servlet = new ExampleServlet();
    Assert.assertNotNull(servlet);
  }
}
