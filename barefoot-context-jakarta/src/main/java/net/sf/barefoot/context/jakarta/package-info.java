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

/**
 * Barefoot context for jakarta servlets. This project provides a framework for running Java
 * Servlets without networking code. It is intended to be embedded in small server-less modules such
 * as AWS Lambda or Azure Functions. The goal is to host simple servlets, Spring RestControllers and
 * WSDL servers using Apache CXF. The code is small, pure java, deliberately limited to Java 1.8.
 * The only dependency is on the servlet API itself.
 */
package net.sf.barefoot.context.jakarta;
