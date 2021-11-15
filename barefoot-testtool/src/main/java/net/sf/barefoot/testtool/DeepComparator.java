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

package net.sf.barefoot.testtool;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.junit.Assert;

/** compares two objects, deeply */
public class DeepComparator {

  public void compareObjects(String text, Object x, Object y) {
    if (x == null) {
      Assert.assertNull(text + " must be null", y);
      return;
    }

    if (x instanceof Number) {
      Assert.assertTrue(text + " must be number", y instanceof Number);
      Assert.assertEquals(text, x, y);
      return;
    }

    if (x instanceof Boolean) {
      Assert.assertTrue(text + " must be boolean", y instanceof Boolean);
      Assert.assertEquals(text, x, y);
      return;
    }

    if (x instanceof String) {
      Assert.assertTrue(text + " must be string", y instanceof String);
      Assert.assertEquals(text, x, y);
      return;
    }

    if (x instanceof List) {
      Assert.assertTrue(text + " must be list", y instanceof List);
      List lx = (List) x, ly = (List) y;
      Assert.assertEquals(text + " array size", lx.size(), ly.size());
      int i = 0;
      while (i < lx.size()) {
        compareObjects(text + "[" + i + "]", lx.get(i), ly.get(i));
        i++;
      }
      return;
    }

    if (x instanceof Map) {
      Assert.assertTrue(text + " must be map", y instanceof Map);
      Map<String, Object> mx = (Map<String, Object>) x, my = (Map<String, Object>) y;
      Assert.assertEquals(text + " map size", mx.keySet().size(), my.keySet().size());

      {
        Iterator<String> it = mx.keySet().iterator();
        while (it.hasNext()) {
          String key = it.next();
          compareObjects(text + "." + key, mx.get(key), my.get(key));
        }
      }

      {
        Iterator<String> it = my.keySet().iterator();
        while (it.hasNext()) {
          String key = it.next();
          compareObjects(text + "." + key, mx.get(key), my.get(key));
        }
      }

      return;
    }

    Assert.assertTrue(text + " can handle object type " + x.getClass().getCanonicalName(), false);
  }
}
