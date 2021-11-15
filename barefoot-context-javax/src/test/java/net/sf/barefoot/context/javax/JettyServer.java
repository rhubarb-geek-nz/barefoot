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

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/** http server using jetty */
public class JettyServer {
  final Server server = new Server();
  final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
  int port;

  void start(String contextPath) throws Exception {
    ServerConnector connector = new ServerConnector(server);
    connector.setPort(0);
    connector.setHost("localhost");
    server.setConnectors(new Connector[] {connector});
    context.setContextPath(contextPath);
    server.setHandler(context);
    context.addServlet(new ServletHolder(new TestServlet()), "/*");
    context.addServlet(new ServletHolder(new TestServlet()), "/echo/*");
    context.addServlet(new ServletHolder(new TestServlet()), "/echo");
    server.start();
    port = connector.getLocalPort();
  }

  void stop() throws Exception {
    server.stop();
  }
}
