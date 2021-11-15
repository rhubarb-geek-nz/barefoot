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

package net.sf.barefoot.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

/** Read from a reader as an input stream. */
public class ReaderInputStream extends InputStream {
  private final Reader reader;
  private final byte[] singleByte = {0};
  private final CharsetEncoder encoder;
  private ByteBuffer byteBuffer;
  private final char[] charBuffer = new char[4096];

  /**
   * Creates an input stream from a character stream.
   *
   * @param r character data to be converted to a stream.
   * @param cs character set of the input stream representation.
   */
  public ReaderInputStream(Reader r, Charset cs) {
    reader = r;
    encoder = cs.newEncoder();
  }

  @Override
  public int read() throws IOException {
    int i = read(singleByte);
    if (i > 0) return 0xFF & singleByte[0];
    return -1;
  }

  @Override
  public int read(byte[] buf) throws IOException {
    return read(buf, 0, buf.length);
  }

  @Override
  public int read(byte[] buf, int off, int len) throws IOException {
    int total = 0;

    while (len > 0) {
      if (byteBuffer != null) {
        int gulp = byteBuffer.remaining();
        if (gulp > len) {
          gulp = len;
        }
        byteBuffer.get(buf, off, gulp);
        total += gulp;
        off += gulp;
        len -= gulp;
        if (byteBuffer.remaining() == 0) {
          byteBuffer = null;
        }
      } else {
        int i = reader.read(charBuffer, 0, len > charBuffer.length ? charBuffer.length : len);

        if (i <= 0) {
          break;
        }

        byteBuffer = encoder.encode(CharBuffer.wrap(charBuffer, 0, i));
      }
    }

    return total > 0 ? total : -1;
  }

  @Override
  public void close() throws IOException {
    byteBuffer = null;
    reader.close();
  }
}
