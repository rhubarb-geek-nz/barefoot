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

package net.sf.barefoot.naming;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.NotContextException;
import org.junit.Assert;
import org.junit.Test;

/** Test context handler. */
public class BarefootContextTest {

  public BarefootContextTest() {
    System.setProperty(
        Context.INITIAL_CONTEXT_FACTORY, BarefootInitialContextFactory.class.getCanonicalName());
  }

  @Test
  public void testConstruction() throws NamingException {
    InitialContext init = new InitialContext();
    Hashtable<?, ?> env = init.getEnvironment();
    Assert.assertNotNull(env);
    Iterator<? extends Map.Entry<?, ?>> it = env.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<?, ?> entry = it.next();
      Assert.assertNotNull(entry.getKey());
      Assert.assertNotNull(entry.getValue());
    }
  }

  @Test
  public void testEnvironment() throws NamingException {
    InitialContext init = new InitialContext();
    init.bind("foo", "bar");
    Object res = init.lookup("foo");
    Assert.assertEquals("bar", res);
  }

  @Test
  public void testCreateSub() throws NamingException {
    // java:comp/env/jdbc/hikari
    InitialContext init = new InitialContext();
    Context jdbc = init.createSubcontext("java:comp/env/jdbc");

    jdbc.bind("hikari", this);

    Object hikari = init.lookup("java:comp/env/jdbc/hikari");

    Assert.assertEquals(this, hikari);

    {
      boolean caught = false;

      try {
        init.lookup("java:comp/env/jdbc/some/thing/else");
      } catch (NameNotFoundException ex) {
        caught = true;

        Assert.assertEquals("java:comp/env/jdbc", ex.getResolvedName().toString());
        Assert.assertEquals("some/thing/else", ex.getRemainingName().toString());
      }

      Assert.assertTrue(caught);
    }

    {
      boolean caught = false;

      try {
        init.lookup("java:comp/env/jdbc/hikari/some/thing/else");
      } catch (NotContextException ex) {
        caught = true;

        Assert.assertEquals("java:comp/env/jdbc/hikari", ex.getResolvedName().toString());
        Assert.assertEquals("some/thing/else", ex.getRemainingName().toString());
      }

      Assert.assertTrue(caught);
    }

    {
      boolean caught = false;

      try {
        init.lookup("java:comp/env/jdbc/wibble/some/thing/else");
      } catch (NameNotFoundException ex) {
        caught = true;

        Assert.assertEquals("java:comp/env/jdbc", ex.getResolvedName().toString());
        Assert.assertEquals("wibble/some/thing/else", ex.getRemainingName().toString());
      }

      Assert.assertTrue(caught);
    }

    init.unbind("java:comp/env/jdbc");
    boolean mustCatch = false;
    try {
      init.unbind("java:comp/env/jdbc");
    } catch (NamingException ex) {
      mustCatch = true;
    }
    Assert.assertTrue(mustCatch);
  }
}
