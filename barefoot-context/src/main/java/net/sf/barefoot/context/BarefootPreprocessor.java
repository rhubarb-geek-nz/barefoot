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

import java.io.IOException;
import java.io.Reader;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Decoder for input formats */
public class BarefootPreprocessor {
  /**
   * read xform data from reader
   *
   * @param reader input
   * @param params parameter list
   * @param cs character set
   * @throws IOException on read
   */
  public void processApplicationFormUrlEncoded(
      Reader reader, Map<String, List<String>> params, Charset cs) throws IOException {
    StringBuilder nameBuilder = null, valueBuilder = null;
    String charset = cs.name();

    while (true) {
      int c = reader.read();

      if (c == -1 || c == '&') {
        if (nameBuilder != null) {
          String name = URLDecoder.decode(nameBuilder.toString(), charset);
          String value =
              valueBuilder == null ? null : URLDecoder.decode(valueBuilder.toString(), charset);
          List<String> list = params.get(name);
          if (list == null) {
            list = new ArrayList<>();
            params.put(name, list);
          }
          list.add(value);
        }
        if (c == -1) break;
        nameBuilder = null;
        valueBuilder = null;
      } else {
        if (c == '=' && valueBuilder == null) {
          valueBuilder = new StringBuilder();
        } else {
          if (valueBuilder != null) {
            valueBuilder.append((char) c);
          } else {
            if (nameBuilder == null) {
              nameBuilder = new StringBuilder();
            }
            nameBuilder.append((char) c);
          }
        }
      }
    }
  }

  /**
   * checks if content type can be parsed
   *
   * @param ct content type
   * @return true if xform data
   */
  public boolean isApplicationFormUrlEncoded(String ct) {
    final int len = BarefootContentType.APPLICATION_FORM_URLENCODED.length();
    return ct != null
        && (ct.length() == len
            ? BarefootContentType.APPLICATION_FORM_URLENCODED.equals(ct)
            : ct.length() > len
                && ct.charAt(len) == ';'
                && ct.startsWith(BarefootContentType.APPLICATION_FORM_URLENCODED));
  }
}
