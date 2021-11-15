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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "enforce", defaultPhase = LifecyclePhase.VERIFY)
public class BarefootEnforcerMojo extends AbstractMojo {
  @Parameter(defaultValue = "${project}", required = true, readonly = true)
  MavenProject project;

  @Parameter(property = "extension")
  String extension;

  @Parameter(property = "directory")
  String directory;

  @Parameter(property = "file")
  String file;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    String target = project.getBuild().getDirectory();
    String finalName = project.getBuild().getFinalName();
    getLog().info("BarefootMojo.target: " + target);
    getLog().info("BarefootMojo.finalName: " + finalName);

    if (file == null && directory == null) {
      file = target + File.separator + finalName;
      if (extension != null) {
        file = file + extension;
      }
    }

    try {
      if (file != null) {
        PkgFile pkgFile = new PkgFile(this, file, null, new HashMap<>());

        getLog().info("BarefootMojo processing: " + file);
        try (InputStream is = new FileInputStream(file)) {
          if (!pkgFile.process(is)) {
            getLog().info("BarefootMojo failed: " + file);
            throw new MojoFailureException(file);
          }
        }
      } else {
        if (directory != null) {
          PkgFile pkgFile = new PkgFile(this, directory, null, new HashMap<>());

          getLog().info("BarefootMojo processing: " + directory);

          if (!pkgFile.process(directory)) {
            getLog().info("BarefootMojo failed: " + directory);
            throw new MojoFailureException(directory);
          }
        } else {
          throw new MojoFailureException("barefoot - nothing to do");
        }
      }
    } catch (IOException ex) {
      throw new MojoExecutionException("barefoot IO error", ex);
    }
  }
}
