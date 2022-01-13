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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

class PkgFile {
  final String fileName;
  final PkgFile parent;
  final Map<String, PkgFile> classMap;
  final boolean isWar;
  final BarefootEnforcerMojo mojo;

  PkgFile(
      BarefootEnforcerMojo mojo, String fileName, PkgFile parent, Map<String, PkgFile> classMap) {
    this.mojo = mojo;
    this.fileName = fileName;
    this.parent = parent;
    this.classMap = classMap;
    isWar =
        parent == null
            ? fileName.endsWith(".war") || "war".equals(mojo.project.getPackaging())
            : parent.isWar;
  }

  static final String[] CLASS_PREFIX = {"WEB-INF/lib/"};

  static final Set<String> IGNORED_CLASSES =
      new HashSet<String>() {
        {
          add("module-info.class");
          add("META-INF/versions/9/module-info.class");
          add("META-INF/versions/11/module-info.class");
        }
      };

  boolean isZip(String name) {
    return name.endsWith(".jar") || name.endsWith(".zip") || name.endsWith(".war");
  }

  boolean processName(String name) {
    boolean success = true;

    if (name.endsWith(".class")) {
      String classFile = name;

      for (String p : CLASS_PREFIX) {
        if (classFile.startsWith(p)) {
          classFile = classFile.substring(p.length());
          break;
        }
      }

      if (!IGNORED_CLASSES.contains(classFile)) {
        PkgFile other = classMap.get(classFile);

        if (other == null) {
          classMap.put(classFile, this);
        } else {
          System.err.println("DUPLICATE CLASS " + classFile);
          System.err.println("CONTAINER " + other.getFilePath());
          System.err.println("CONTAINER " + this.getFilePath());
          success = false;
        }

        if (isWar) {
          switch (classFile) {
            case "javax/servlet/http/HttpServlet.class":
            case "jakarta/servlet/http/HttpServlet.class":
            case "net/sf/barefoot/context/AbstractServletContext.class":
            case "net/sf/barefoot/web/xml/jakarta/BarefootWebXmlHandler.class":
            case "net/sf/barefoot/web/xml/javax/BarefootWebXmlHandler.class":
            case "net/sf/barefoot/context/xml/BarefootContextXmlHandler.class":
              System.err.println("RESTRICTED CLASS " + classFile);
              System.err.println("CONTAINER " + this.getFilePath());
              success = false;
              break;
          }

          if (classFile.startsWith("org/apache/tomcat")) {
            System.err.println("TOMCAT CLASS " + classFile);
            System.err.println("CONTAINER " + this.getFilePath());
            success = false;
          }
        }
      }
    }

    return success;
  }

  boolean process(InputStream is) throws IOException {
    ZipInputStream zis = new ZipInputStream(is);
    ZipEntry entry;
    boolean success = true;

    while ((entry = zis.getNextEntry()) != null) {
      if (!entry.isDirectory()) {
        String name = entry.getName();

        if (isZip(name)) {
          PkgFile child = new PkgFile(mojo, name, this, classMap);
          success &= child.process(zis);
        } else {
          if (name.endsWith("/pom.properties")) {
            Properties props = new Properties();
            props.load(zis);
            String groupId = props.getProperty("groupId");
            String artefactId = props.getProperty("artifactId");
            String version = props.getProperty("version");
            if ("org.apache.logging.log4j".equals(groupId) && "log4j-core".equals(artefactId)) {
              String vers[] = version.split("\\.");
              if (2 == Integer.parseInt(vers[0]) && Integer.parseInt(vers[1]) < 17) {
                System.err.println("FOUND " + groupId + ":" + artefactId + ":" + version);
                success = false;
              }
            }
          } else {
            success &= processName(name);
          }
        }
      }
    }

    return success;
  }

  String getFilePath() {
    if (parent == null) return fileName;

    return parent.getFilePath() + " ! " + fileName;
  }

  boolean process(String path) throws IOException {
    boolean success = true;
    if (isZip(path)) {
      try (InputStream fis = new FileInputStream(path)) {
        success = process(fis);
      }
    } else {
      Path rootPath = Paths.get(path);
      String basePath = rootPath.toString();
      List<Path> list = Files.walk(rootPath).collect(Collectors.toList());

      for (Path item : list) {
        if (Files.isRegularFile(item)) {
          String fileName = item.toString();
          if (fileName.startsWith(basePath)) {
            fileName = fileName.substring(basePath.length());
          }

          if (fileName.startsWith(File.separator)) {
            fileName = fileName.substring(File.separator.length());
          }

          if (!"/".equals(File.separator)) {
            fileName = fileName.replace(File.separatorChar, '/');
          }

          if (isZip(fileName)) {
            try (InputStream fis = new FileInputStream(item.toFile())) {
              PkgFile pkg = new PkgFile(mojo, fileName, this, classMap);
              success &= pkg.process(fis);
            }
          } else {
            success &= processName(fileName);
          }
        }
      }
    }

    return success;
  }
}
