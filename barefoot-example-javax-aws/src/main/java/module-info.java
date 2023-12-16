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

module net.sf.barefoot.example.javax.aws {
  requires net.sf.barefoot.context.xml.aws;
  requires net.sf.barefoot.aws.lambda;
  requires net.sf.barefoot.context;
  requires net.sf.barefoot.context.xml;
  requires net.sf.barefoot.context.javax;
  requires net.sf.barefoot.example.javax;
  requires net.sf.barefoot.testtool;
  requires com.fasterxml.jackson.databind;
  requires com.fasterxml.jackson.annotation;
  requires com.fasterxml.jackson.core;
}
