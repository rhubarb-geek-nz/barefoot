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

package net.sf.barefoot.context.javax;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

/** Wraps a normal input stream as a servlet input stream */
final class BarefootServletInputStream extends ServletInputStream {
  final InputStream bais;

  /**
   * create stream with another stream as the source
   *
   * @param b existing stream to use as input
   */
  public BarefootServletInputStream(InputStream b) {
    bais = b == null ? newNullInputStream() : b;
  }

  /**
   * create a stream with a byte array
   *
   * @param b byte array to use as input
   */
  public BarefootServletInputStream(byte[] b) {
    bais = b == null ? newNullInputStream() : new ByteArrayInputStream(b);
  }

  @Override
  public boolean isFinished() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean isReady() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setReadListener(ReadListener rl) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public int read() throws IOException {
    return bais.read();
  }

  @Override
  public int read(byte[] b, int o, int l) throws IOException {
    return bais.read(b, o, l);
  }

  @Override
  public int read(byte[] b) throws IOException {
    return bais.read(b, 0, b.length);
  }

  @Override
  public void close() throws IOException {
    bais.close();
  }

  /**
   * create empty input stream
   *
   * @return empty stream
   */
  private static InputStream newNullInputStream() {
    return new InputStream() {
      @Override
      public int read() throws IOException {
        return -1;
      }
    };
  }
}
