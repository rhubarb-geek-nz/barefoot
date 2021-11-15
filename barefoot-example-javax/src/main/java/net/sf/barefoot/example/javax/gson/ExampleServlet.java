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

package net.sf.barefoot.example.javax.gson;

import com.google.gson.Gson;
import net.sf.barefoot.example.javax.AbstractExampleServlet;

/** very simple test servlet */
public class ExampleServlet extends AbstractExampleServlet {
  final Gson gson;

  public ExampleServlet(Gson g) {
    gson = g;
  }

  public ExampleServlet() {
    gson = new Gson();
  }

  @Override
  protected String writeValueAsString(Object value) {
    return gson.toJson(value);
  }
}
