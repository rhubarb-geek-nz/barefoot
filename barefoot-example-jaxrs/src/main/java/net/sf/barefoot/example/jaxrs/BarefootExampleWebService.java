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

package net.sf.barefoot.example.jaxrs;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/** example implementation of a JAX-RS service */
@Path("/HttpExample")
public class BarefootExampleWebService {
  @Context HttpServletRequest request;
  @Context HttpServletResponse response;

  public static class State {
    public String method, path, url, uri;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public State getHttpExample() throws IOException {
    State state = new State();
    state.method = request.getMethod();
    state.path = request.getPathInfo();
    state.uri = request.getRequestURI();
    state.url = request.getRequestURL().toString();
    return state;
  }
}
