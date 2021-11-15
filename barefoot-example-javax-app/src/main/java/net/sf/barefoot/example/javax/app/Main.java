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

package net.sf.barefoot.example.javax.app;

import net.sf.barefoot.example.javax.jackson.ExampleServlet;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class Main {
  final Server server = new Server();
  final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
  int port;

  public static void main(String[] args) throws Exception {
    Main m = new Main();
    m.start();
    synchronized (m) {
      m.wait();
    }
  }

  void start() throws Exception {
    ServerConnector connector = new ServerConnector(server);
    connector.setPort(Integer.parseInt(System.getProperty("server.port", "8080")));
    server.setConnectors(new Connector[] {connector});
    context.setContextPath("");
    server.setHandler(context);
    context.addServlet(new ServletHolder(new ExampleServlet()), "/*");
    server.start();
    this.port = connector.getLocalPort();
  }

  void stop() throws Exception {
    server.stop();
  }
}
