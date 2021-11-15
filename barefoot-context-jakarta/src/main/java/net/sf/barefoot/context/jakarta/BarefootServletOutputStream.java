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

package net.sf.barefoot.context.jakarta;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import java.io.IOException;
import java.io.OutputStream;

/** Output stream for servlet requests */
final class BarefootServletOutputStream extends ServletOutputStream {
  final OutputStream baos;

  /**
   * Create a servlet output stream based on a normal output stream
   *
   * @param os stream to write to
   */
  public BarefootServletOutputStream(OutputStream os) {
    baos = os;
  }

  @Override
  public boolean isReady() {
    return true;
  }

  @Override
  public void setWriteListener(WriteListener wl) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void write(int b) throws IOException {
    baos.write(b);
  }

  @Override
  public void close() throws IOException {
    baos.close();
  }

  @Override
  public void write(byte[] b, int a, int c) throws IOException {
    baos.write(b, a, c);
  }

  @Override
  public void write(byte[] b) throws IOException {
    baos.write(b, 0, b.length);
  }

  @Override
  public void flush() throws IOException {
    baos.flush();
  }
}
