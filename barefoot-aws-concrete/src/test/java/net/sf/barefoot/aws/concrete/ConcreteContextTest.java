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

package net.sf.barefoot.aws.concrete;

import com.amazonaws.services.lambda.runtime.Context;
import org.junit.Assert;
import org.junit.Test;

/** test cases for context */
public class ConcreteContextTest {

  @Test
  public void testBuilder() {
    Context result =
        ConcreteContext.builder()
            .awsRequestId("id")
            .functionName("fn")
            .functionVersion("fv")
            .invokedFunctionArn("ifa")
            .logGroupName("lgn")
            .logStreamName("lsn")
            .memoryLimitInMB(500)
            .remainingTimeInMillis(1000)
            .build();
    Assert.assertNotNull(result);
    Assert.assertEquals("id", result.getAwsRequestId());
    Assert.assertEquals("fn", result.getFunctionName());
    Assert.assertEquals("fv", result.getFunctionVersion());
    Assert.assertEquals("lgn", result.getLogGroupName());
    Assert.assertEquals("lsn", result.getLogStreamName());
    Assert.assertEquals("ifa", result.getInvokedFunctionArn());
    Assert.assertEquals(500, result.getMemoryLimitInMB());
  }
}
