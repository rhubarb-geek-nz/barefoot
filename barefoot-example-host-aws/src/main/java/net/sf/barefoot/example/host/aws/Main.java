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

package net.sf.barefoot.example.host.aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.barefoot.aws.concrete.ConcreteContext;

public class Main {
  final InputStream is;
  final OutputStream os;
  final Object target;

  Main(InputStream is, OutputStream os, Object target) {
    this.is = is;
    this.os = os;
    this.target = target;
  }

  public static void main(String[] args) throws Exception {
    Class cls = Class.forName(args[0]);
    Object target = cls.getConstructor().newInstance();

    if (args.length > 1) {
      runServer(target, Integer.parseInt(args[1]));
    } else {
      Main m = new Main(System.in, System.out, target);

      if (!m.start()) {
        System.exit(1);
      }
    }
  }

  boolean start() throws Exception {
    boolean responded = false;

    try {
      Context context =
          ConcreteContext.builder()
              .awsRequestId("test")
              .functionName("test")
              .functionVersion("test")
              .invokedFunctionArn("test")
              .logGroupName("test")
              .logStreamName("test")
              .memoryLimitInMB(100000)
              .remainingTimeInMillis(1000000)
              .build();

      if (target instanceof RequestStreamHandler) {
        RequestStreamHandler handler = (RequestStreamHandler) target;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int i;
        byte[] buf = new byte[4096];

        while ((i = is.read(buf)) > 0) {
          baos.write(buf, 0, i);
        }

        buf = baos.toByteArray();

        baos = new ByteArrayOutputStream();

        handler.handleRequest(new ByteArrayInputStream(buf), baos, context);

        responded = true;

        os.write(baos.toByteArray());
      } else {
        ObjectMapper mapper = new ObjectMapper();

        Method[] methodList = target.getClass().getMethods();
        Class<?> inputType = Map.class;
        Method method = null;

        for (Method m : methodList) {
          if ("handleRequest".equals(m.getName())) {
            Class<?>[] args = m.getParameterTypes();
            if (args.length == 2) {
              Class<?> ctxType = args[1];
              if (ctxType.isAssignableFrom(Context.class)) {
                inputType = args[0];
                method = m;
                break;
              }
            }
          }
        }

        Object input = mapper.readValue(is, inputType);

        Object result;

        if (method == null) {
          RequestHandler handler = (RequestHandler) target;
          result = handler.handleRequest(input, context);
        } else {
          result = method.invoke(target, input, context);
        }

        responded = true;

        mapper.writeValue(os, result);
      }

    } catch (Exception ex) {
      if (responded) {
        throw ex;
      }

      Logger.getGlobal().log(Level.WARNING, ex.getMessage(), ex);

      os.write("{\"errorMessage\":\"catch\"}".getBytes());
    }

    return responded;
  }

  static void runServer(Object target, int port) throws IOException {
    ServerSocket serverSocket = new ServerSocket(port);
    while (true) {
      try {
        try (Socket accept = serverSocket.accept()) {
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          int i;
          byte[] buf = new byte[4096];
          InputStream is = accept.getInputStream();
          while ((i = is.read(buf)) > 0) {
            baos.write(buf, 0, i);
          }
          buf = baos.toByteArray();
          OutputStream os = accept.getOutputStream();
          baos = new ByteArrayOutputStream();
          new Main(new ByteArrayInputStream(buf), baos, target).start();
          os.write(baos.toByteArray());
        }
      } catch (Exception ex) {
        Logger.getGlobal().log(Level.WARNING, ex.getMessage(), ex);
      }
    }
  }
}
