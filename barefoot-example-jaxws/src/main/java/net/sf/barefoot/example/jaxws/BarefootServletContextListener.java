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

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import net.sf.barefoot.example.bean.BarefootExampleBean;

/** Listener to install bean in context */
public class BarefootServletContextListener implements ServletContextListener {

  /**
   * on startup create and install bean
   *
   * @param sce context event
   */
  @Override
  public void contextInitialized(ServletContextEvent sce) {
    BarefootExampleBean bean = new BarefootExampleBean();
    sce.getServletContext().setAttribute(bean.getClass().getCanonicalName(), bean);
  }

  /**
   * on shutdown
   *
   * @param sce context event
   */
  @Override
  public void contextDestroyed(ServletContextEvent sce) {}
}
