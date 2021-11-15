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

import java.io.IOException;
import java.util.HashMap;
import org.apache.maven.project.MavenProject;
import org.junit.Assert;
import org.junit.Test;

public class PkgFileTest {

  @Test
  public void testZip() throws IOException {
    BarefootEnforcerMojo barefootMojo = new BarefootEnforcerMojo();
    barefootMojo.project = new MavenProject();
    String dir = "target/classes";

    PkgFile pkgFile = new PkgFile(barefootMojo, dir, null, new HashMap<>());

    boolean result = pkgFile.process(dir);

    Assert.assertTrue(result);
  }
}
