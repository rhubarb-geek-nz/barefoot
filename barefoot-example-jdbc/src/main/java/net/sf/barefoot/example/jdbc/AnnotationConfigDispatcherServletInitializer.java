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

package net.sf.barefoot.example.jdbc;

import javax.servlet.Filter;
import net.sf.barefoot.example.rest.BarefootExampleRestController;
import org.springframework.security.config.BeanIds;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/** Define configuration classes for Spring Web */
public class AnnotationConfigDispatcherServletInitializer
    extends AbstractAnnotationConfigDispatcherServletInitializer {

  /**
   * Root configuration providing low level Beans
   *
   * @return list of classes
   */
  @Override
  protected Class<?>[] getRootConfigClasses() {
    return new Class<?>[] {
      ExampleBeanFactory.class,
      HttpSessionApplicationInitializer.class,
      WebSecurityConfiguration.class
    };
  }

  /**
   * Classes used at servlet layer
   *
   * @return list of classes
   */
  @Override
  protected Class<?>[] getServletConfigClasses() {
    return new Class<?>[] {
      BarefootExampleRestController.class, DemoController.class, WebConfig.class
    };
  }

  @Override
  protected String[] getServletMappings() {
    return new String[] {"/"};
  }

  /**
   * Get the list of filters to add at startup
   *
   * @return list of filters
   */
  @Override
  protected Filter[] getServletFilters() {
    return new Filter[] {
      new DelegatingFilterProxy(BeanIds.SPRING_SECURITY_FILTER_CHAIN),
    };
  }
}
