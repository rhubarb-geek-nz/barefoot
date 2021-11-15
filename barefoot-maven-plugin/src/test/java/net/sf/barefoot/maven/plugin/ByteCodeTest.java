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

package net.sf.barefoot.maven.plugin;

import org.apache.bcel.Const;
import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;
import org.junit.Assert;
import org.junit.Test;

/** Confirm that using Java 1.8 byte code */
public class ByteCodeTest {

  @Test
  public void testByteCode() throws ClassNotFoundException {
    JavaClass co = Repository.lookupClass(BarefootEnforcerMojo.class);
    Assert.assertEquals("class version for 1.8", Const.MAJOR_1_8, co.getMajor());
    Assert.assertEquals("class version for 1.8", Const.MINOR_1_8, co.getMinor());
  }
}
