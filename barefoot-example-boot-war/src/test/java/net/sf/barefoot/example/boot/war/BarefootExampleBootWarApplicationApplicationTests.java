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

package net.sf.barefoot.example.boot.war;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class BarefootExampleBootWarApplicationApplicationTests {

  @Autowired private MockMvc mvc;

  @Test
  void contextLoads() {}

  @Test
  void GET_api_HttpExample() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/api/HttpExample"))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  void GET_forbidden() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/")).andDo(print()).andExpect(status().isForbidden());
  }

  @Test
  void GET_unknown() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/api/Unknown"))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  void GET_method() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/api/HttpExample2"))
        .andDo(print())
        .andExpect(status().isMethodNotAllowed());
  }
}
