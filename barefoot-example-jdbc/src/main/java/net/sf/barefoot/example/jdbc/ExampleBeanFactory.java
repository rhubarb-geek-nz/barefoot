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

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import net.sf.barefoot.example.bean.BarefootExampleBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;

/** Demonstration configuration providing beans */
@Configuration
public class ExampleBeanFactory {

  @Value("${barefoot.database.jndi}")
  String databaseJndi;

  @Bean
  public BarefootExampleBean exampleBean() {
    return new BarefootExampleBean();
  }

  /**
   * Provide DataSource used by function
   *
   * @return data source
   * @throws javax.naming.NamingException on error
   */
  @Bean
  public DataSource dataSource() throws NamingException {
    return (DataSource) new InitialContext().lookup(databaseJndi);
  }

  /**
   * Password encoded used by Spring authentication system
   *
   * @return password encoder
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Create transaction manager required by Spring
   *
   * @param dataSource dataSource used by environment
   * @return platform transaction manager
   */
  @Bean
  public PlatformTransactionManager transactionManager(DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
  }
}
