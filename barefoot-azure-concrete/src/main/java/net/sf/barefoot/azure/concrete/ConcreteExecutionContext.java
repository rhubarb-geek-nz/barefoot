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

package net.sf.barefoot.azure.concrete;

import com.microsoft.azure.functions.ExecutionContext;
import java.util.logging.Logger;

/** Concrete execution context for testing */
public class ConcreteExecutionContext implements ExecutionContext {
  final String name, id;

  public ConcreteExecutionContext(String n, String i) {
    name = n;
    id = i;
  }

  public ConcreteExecutionContext() {
    name = null;
    id = null;
  }

  @Override
  public Logger getLogger() {
    return Logger.getGlobal();
  }

  @Override
  public String getInvocationId() {
    return id;
  }

  @Override
  public String getFunctionName() {
    return name;
  }
}
