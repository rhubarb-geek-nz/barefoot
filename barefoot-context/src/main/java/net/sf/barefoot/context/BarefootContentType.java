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

/** parser for content-type */
public class BarefootContentType {

  static final String CHAR_SEQ_EQ = "charset=";

  public static final String APPLICATION_JSON = "application/json",
      APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";

  public static String getCharsetFromContentType(String s) {
    String result = null;

    if (s != null) {
      int len = s.length();
      int off = 0;
      int matchLen = CHAR_SEQ_EQ.length();

      while (off < len) {
        if (s.regionMatches(true, off, CHAR_SEQ_EQ, 0, matchLen)) {
          int next = s.indexOf(';', off);
          result = next < 0 ? s.substring(off + matchLen) : s.substring(off + matchLen, next);
          break;
        }
        char c = s.charAt(off++);
        if (!Character.isWhitespace(c)) {
          int next = s.indexOf(';', off);
          if (next < 0) break;
          off = next + 1;
        }
      }
    }

    return result;
  }

  public static boolean isText(String contentType) {
    return contentType != null
        && (APPLICATION_JSON.equals(contentType)
            || APPLICATION_FORM_URLENCODED.equals(contentType)
            || contentType.startsWith("text/"));
  }
}
