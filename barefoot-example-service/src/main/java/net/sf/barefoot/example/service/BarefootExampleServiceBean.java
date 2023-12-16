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

package net.sf.barefoot.example.service;

import java.util.logging.Logger;
import javax.jws.WebService;
import net.sf.barefoot.example.bean.BarefootExampleBean;
import net.sf.barefoot.example.wsdl.BarefootExampleService;
import org.springframework.beans.factory.annotation.Autowired;

/** example implementation of a WSDL service */
@WebService(
    serviceName = "ExampleService",
    portName = "myExamplePort",
    endpointInterface = "net.sf.barefoot.example.wsdl.BarefootExampleService")
public class BarefootExampleServiceBean implements BarefootExampleService {
  static final Logger LOGGER = Logger.getLogger(BarefootExampleServiceBean.class.getName());

  @Autowired BarefootExampleBean exampleBean;

  public BarefootExampleServiceBean() {}

  @Override
  public String getMessage() {
    return exampleBean.getDescription();
  }
}
