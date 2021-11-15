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

package net.sf.barefoot.example.jaxws;

import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.ServletContext;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import net.sf.barefoot.example.bean.BarefootExampleBean;
import net.sf.barefoot.example.wsdl.BarefootExampleService;

/** example implementation of a WSDL service */
@WebService(
    serviceName = "ExampleService",
    portName = "myExamplePort",
    endpointInterface = "net.sf.barefoot.example.wsdl.BarefootExampleService")
public class BarefootExampleWebService implements BarefootExampleService {
  static final Logger LOGGER = Logger.getLogger(BarefootExampleWebService.class.getName());

  final String BEAN_KEY = BarefootExampleBean.class.getCanonicalName();

  @Resource public WebServiceContext wsContext;

  public BarefootExampleWebService() {}

  @Override
  public String getMessage() {
    ServletContext ctx =
        (ServletContext) wsContext.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
    BarefootExampleBean exampleBean = (BarefootExampleBean) ctx.getAttribute(BEAN_KEY);
    return exampleBean.getDescription();
  }
}
