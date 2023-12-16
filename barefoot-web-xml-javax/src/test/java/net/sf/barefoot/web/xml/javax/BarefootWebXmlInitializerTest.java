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

import java.util.ServiceLoader;
import javax.servlet.ServletContainerInitializer;
import org.junit.Assert;
import org.junit.Test;

/** Test case for javax web.xml initializer . */
public class BarefootWebXmlInitializerTest {

  public BarefootWebXmlInitializerTest() {}

  @Test
  public void testSomeMethod() {
    ServiceLoader<ServletContainerInitializer> serviceLoader =
        ServiceLoader.load(ServletContainerInitializer.class);
    boolean found = true;

    for (ServletContainerInitializer init : serviceLoader) {
      found |= init instanceof BarefootWebXmlInitializer;
    }

    Assert.assertTrue(found);
  }
}
