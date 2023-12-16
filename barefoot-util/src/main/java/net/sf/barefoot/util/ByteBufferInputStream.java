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
import java.nio.ByteBuffer;

/** Read a byte buffer as input stream. */
public class ByteBufferInputStream extends InputStream {
  private final ByteBuffer byteBuffer;

  /**
   * Constructs an InputStream from a byte buffer.
   *
   * @param bb bytes to be read by the input stream.
   */
  public ByteBufferInputStream(ByteBuffer bb) {
    byteBuffer = bb;
  }

  @Override
  public int read() throws IOException {
    return byteBuffer.hasRemaining() ? (0xFF & byteBuffer.get()) : -1;
  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    if (len != 0) {
      len = Math.min(len, byteBuffer.remaining());
      if (len > 0) {
        byteBuffer.get(b, off, len);
      } else {
        len = -1;
      }
    }
    return len;
  }
}
