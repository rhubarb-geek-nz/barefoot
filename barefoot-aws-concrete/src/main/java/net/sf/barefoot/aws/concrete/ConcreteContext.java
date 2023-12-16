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

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import java.nio.charset.StandardCharsets;
import org.apache.logging.log4j.LogManager;

/** concrete request for testing */
public class ConcreteContext implements Context {
  private final String awsRequestId,
      logGroupName,
      logStreamName,
      functionName,
      functionVersion,
      invokedFunctionArn;
  private final int memoryLimitInMB;
  private final long obituaryInMillis;

  private ConcreteContext(Builder builder) {
    awsRequestId = builder.awsRequestId;
    logGroupName = builder.logGroupName;
    logStreamName = builder.logStreamName;
    functionName = builder.functionName;
    functionVersion = builder.functionVersion;
    invokedFunctionArn = builder.invokedFunctionArn;
    memoryLimitInMB = builder.memoryLimitInMB;
    obituaryInMillis = System.currentTimeMillis() + builder.remainingTimeInMillis;
  }

  public static class Builder {
    String awsRequestId,
        logGroupName,
        logStreamName,
        functionName,
        functionVersion,
        invokedFunctionArn;
    int remainingTimeInMillis, memoryLimitInMB;

    protected Builder() {}

    public Context build() {
      return new ConcreteContext(this);
    }

    public Builder awsRequestId(String s) {
      awsRequestId = s;
      return this;
    }

    public Builder logGroupName(String s) {
      logGroupName = s;
      return this;
    }

    public Builder logStreamName(String s) {
      logStreamName = s;
      return this;
    }

    public Builder functionVersion(String s) {
      functionVersion = s;
      return this;
    }

    public Builder functionName(String s) {
      functionName = s;
      return this;
    }

    public Builder invokedFunctionArn(String s) {
      invokedFunctionArn = s;
      return this;
    }

    public Builder remainingTimeInMillis(int i) {
      remainingTimeInMillis = i;
      return this;
    }

    public Builder memoryLimitInMB(int i) {
      memoryLimitInMB = i;
      return this;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public String getAwsRequestId() {
    return awsRequestId;
  }

  @Override
  public String getLogGroupName() {
    return logGroupName;
  }

  @Override
  public String getLogStreamName() {
    return logStreamName;
  }

  @Override
  public String getFunctionName() {
    return functionName;
  }

  @Override
  public String getFunctionVersion() {
    return functionVersion;
  }

  @Override
  public String getInvokedFunctionArn() {
    return invokedFunctionArn;
  }

  @Override
  public CognitoIdentity getIdentity() {
    return null;
  }

  @Override
  public ClientContext getClientContext() {
    return null;
  }

  @Override
  public int getRemainingTimeInMillis() {
    long val = obituaryInMillis - System.currentTimeMillis();
    return (val > Integer.MAX_VALUE)
        ? Integer.MAX_VALUE
        : (val < Integer.MIN_VALUE) ? Integer.MIN_VALUE : (int) val;
  }

  @Override
  public int getMemoryLimitInMB() {
    return memoryLimitInMB;
  }

  @Override
  public LambdaLogger getLogger() {
    return new LambdaLogger() {
      @Override
      public void log(String string) {
        LogManager.getLogger().info(string);
      }

      @Override
      public void log(byte[] bytes) {
        LogManager.getLogger().info(new String(bytes, StandardCharsets.UTF_8));
      }
    };
  }
}
