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

package net.sf.barefoot.context;

/** Cookie configuration base class */
public abstract class AbstractSessionCookieConfig {
  protected String name = null, domain, path, comment;
  protected int maxAge = -1;
  protected boolean httpOnly, secure;

  public void setName(String string) {
    name = string;
  }

  public String getName() {
    return name;
  }

  public void setDomain(String string) {
    domain = string;
  }

  public String getDomain() {
    return domain;
  }

  public void setPath(String string) {
    path = string;
  }

  public String getPath() {
    return path;
  }

  public void setComment(String string) {
    comment = string;
  }

  public String getComment() {
    return comment;
  }

  public void setHttpOnly(boolean bln) {
    httpOnly = bln;
  }

  public boolean isHttpOnly() {
    return httpOnly;
  }

  public void setSecure(boolean bln) {
    secure = bln;
  }

  public boolean isSecure() {
    return secure;
  }

  public void setMaxAge(int i) {
    maxAge = i;
  }

  public int getMaxAge() {
    return maxAge;
  }
}
