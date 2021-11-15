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

package net.sf.barefoot.azure.concrete;

import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;

/** test cases for http request */
public class ConcreteHttpRequestMessageTest {

  @Test
  public void testBuilderWithClass() {
    HttpRequestMessage<Optional<String>> result =
        ConcreteHttpRequestMessage.builder((Class<Optional<String>>) (Object) Optional.class)
            .build();
    Assert.assertNotNull(result);
  }

  @Test
  public void testBuilderWithObject() {
    HttpRequestMessage<Optional<String>> result =
        ConcreteHttpRequestMessage.builder(Optional.of("body")).method(HttpMethod.GET).build();
    Assert.assertNotNull(result);
  }
}
