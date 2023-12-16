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

package net.sf.barefoot.example.cloud.google;

import java.util.function.Function;
import net.sf.barefoot.example.bean.BarefootExampleBean;
import org.springframework.beans.factory.annotation.Autowired;

public class ExampleFunction implements Function<Object, String> {

  @Autowired BarefootExampleBean exampleBean;

  @Override
  public String apply(Object input) {
    return exampleBean.getDescription();
  }
}
