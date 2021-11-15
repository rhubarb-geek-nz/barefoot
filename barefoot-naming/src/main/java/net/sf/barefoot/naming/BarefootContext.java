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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.naming.*;
import javax.naming.spi.ObjectFactory;

/** Context object. This implements the root and children of the naming tree. */
final class BarefootContext implements Context {
  final Hashtable<String, Object> init = new Hashtable<>();
  final Hashtable<String, Object> environment = new Hashtable<>();
  final Hashtable<String, Object> bindings = new Hashtable<>();
  final Hashtable<String, Object> singletons = new Hashtable<>();
  final ClassLoader classLoader;

  BarefootContext(ClassLoader loader, Hashtable<?, ?> env) {
    classLoader = loader;
    if (env != null) {
      Iterator<? extends Map.Entry<?, ?>> it = env.entrySet().iterator();
      while (it.hasNext()) {
        Map.Entry<?, ?> entry = it.next();
        init.put(entry.getKey().toString(), entry.getValue());
      }
    }
  }

  @Override
  public Object lookup(Name name) throws NamingException {
    Enumeration<String> all = name.getAll();
    Object value;
    if (!all.hasMoreElements()) {
      value = this;
    } else {
      String key = all.nextElement();

      synchronized (this) {
        value = bindings.get(key);

        if (value == null && !bindings.containsKey(key)) {
          NameNotFoundException ex = new NameNotFoundException(key);
          ex.setRemainingName(name);
          throw ex;
        }
      }

      if (!all.hasMoreElements()) {
        if (value instanceof Reference) {
          Reference ref = (Reference) value;
          RefAddr singletonRef = ref.get("singleton");
          Boolean singletonValue = asBoolean(singletonRef);
          boolean isSingleton = singletonValue != null && singletonValue;

          if (isSingleton) {
            synchronized (ref) {
              synchronized (this) {
                value = singletons.get(key);
              }
              if (value == null) {
                value = newInstance(ref, name);
                synchronized (this) {
                  singletons.put(key, value);
                }
              }
            }
          } else {
            value = newInstance(ref, name);
          }
        }
      } else {
        CompositeName childName = new CompositeName();
        do {
          childName.add(all.nextElement());
        } while (all.hasMoreElements());

        if (!(value instanceof Context)) {
          NotContextException ex = new NotContextException(key);
          ex.setResolvedName(new CompositeName(key));
          ex.setRemainingName(childName);
          throw ex;
        }

        Context child = (Context) value;

        try {
          value = child.lookup(childName);
        } catch (NamingException ex) {
          value = throwPrefixedException(ex, key);
        }
      }
    }

    return value;
  }

  @Override
  public Object lookup(String name) throws NamingException {
    return lookup(new CompositeName(name));
  }

  @Override
  public void bind(Name name, Object value) throws NamingException {
    Enumeration<String> all = name.getAll();
    if (!all.hasMoreElements()) throw new InvalidNameException();
    String key = all.nextElement();
    if (!all.hasMoreElements()) {
      synchronized (this) {
        if (bindings.containsKey(key)) {
          NameAlreadyBoundException ex = new NameAlreadyBoundException(key);
          ex.setResolvedName(name);
          throw ex;
        }
        bindings.put(key, value);
      }
    } else {
      Object child;

      synchronized (this) {
        child = bindings.get(key);
      }

      if (child == null) {
        NameNotFoundException ex = new NameNotFoundException();
        ex.setRemainingName(name);
        throw ex;
      }

      CompositeName childName = new CompositeName();
      do {
        childName.add(all.nextElement());
      } while (all.hasMoreElements());

      if (!(child instanceof Context)) {
        NotContextException ex = new NotContextException();
        ex.setResolvedName(new CompositeName(key));
        ex.setRemainingName(childName);
        throw ex;
      }

      try {
        ((Context) child).bind(childName, value);
      } catch (NamingException ex) {
        throwPrefixedException(ex, key);
      }
    }
  }

  @Override
  public void bind(String name, Object value) throws NamingException {
    bind(new CompositeName(name), value);
  }

  @Override
  public void rebind(Name arg0, Object arg1) throws NamingException {
    throw new UnsupportedOperationException(
        "Not supported yet."); // To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void rebind(String name, Object value) throws NamingException {
    rebind(new CompositeName(name), value);
  }

  @Override
  public void unbind(Name name) throws NamingException {
    Enumeration<String> all = name.getAll();
    if (all.hasMoreElements()) {
      String key = all.nextElement();

      if (!all.hasMoreElements()) {
        synchronized (this) {
          if (!bindings.containsKey(key)) {
            NameNotFoundException ex = new NameNotFoundException();
            ex.setRemainingName(name);
            throw ex;
          }

          bindings.remove(key);
          singletons.remove(key);
        }
      } else {
        Object value;

        synchronized (this) {
          value = bindings.get(key);
        }

        if (value == null) {
          NameNotFoundException ex = new NameNotFoundException();
          ex.setRemainingName(name);
          throw ex;
        }

        CompositeName childName = new CompositeName();
        do {
          childName.add(all.nextElement());
        } while (all.hasMoreElements());

        if (!(value instanceof Context)) {
          NotContextException ex = new NotContextException();
          ex.setResolvedName(new CompositeName(key));
          ex.setRemainingName(childName);
          throw ex;
        }

        try {
          ((Context) value).unbind(childName);
        } catch (NamingException ex) {
          throwPrefixedException(ex, key);
        }
      }
    }
  }

  @Override
  public void unbind(String name) throws NamingException {
    unbind(new CompositeName(name));
  }

  @Override
  public void rename(Name arg0, Name arg1) throws NamingException {
    throw new UnsupportedOperationException(
        "Not supported yet."); // To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void rename(String arg0, String arg1) throws NamingException {
    rename(new CompositeName(arg0), new CompositeName(arg1));
  }

  @Override
  public NamingEnumeration<NameClassPair> list(Name name) throws NamingException {
    if (!name.isEmpty()) {
      return lookupContext(name).list(new CompositeName());
    }

    List<NameClassPair> result = new ArrayList<>();

    synchronized (this) {
      bindings.forEach(
          (key, value) -> {
            result.add(
                new NameClassPair(
                    key,
                    value instanceof Reference
                        ? ((Reference) value).getClassName()
                        : value.getClass().getCanonicalName()));
          });
    }

    return new BarefootNamingEnumeration(result.iterator());
  }

  @Override
  public NamingEnumeration<NameClassPair> list(String arg0) throws NamingException {
    return list(new CompositeName(arg0));
  }

  @Override
  public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {
    if (!name.isEmpty()) {
      return lookupContext(name).listBindings(new CompositeName());
    }

    List<Binding> result = new ArrayList<>();

    synchronized (this) {
      bindings.forEach(
          (key, value) -> {
            result.add(new Binding(key, value));
          });
    }

    return new BarefootNamingEnumeration(result.iterator());
  }

  @Override
  public NamingEnumeration<Binding> listBindings(String arg0) throws NamingException {
    return listBindings(new CompositeName(arg0));
  }

  @Override
  public void destroySubcontext(Name name) throws NamingException {
    Enumeration<String> all = name.getAll();
    if (!all.hasMoreElements()) {
      throw new NamingException("empty string");
    }
    String key = all.nextElement();

    if (!all.hasMoreElements()) {
      synchronized (this) {
        Object value = bindings.get(key);
        if (value == null) {
          NameNotFoundException ex = new NameNotFoundException();
          ex.setRemainingName(name);
          throw ex;
        }
        if (!(value instanceof Context)) {
          NotContextException ex = new NotContextException();
          ex.setResolvedName(name);
          throw ex;
        }
        bindings.remove(key);
      }
    } else {
      Object value;

      synchronized (this) {
        value = bindings.get(key);
      }

      if (value == null) {
        NameNotFoundException ex = new NameNotFoundException();
        ex.setRemainingName(name);
        throw ex;
      }

      CompositeName childName = new CompositeName();
      do {
        childName.add(all.nextElement());
      } while (all.hasMoreElements());

      if (!(value instanceof Context)) {
        NotContextException ex = new NotContextException();
        ex.setRemainingName(childName);
        ex.setResolvedName(new CompositeName(key));
        throw ex;
      }

      Context child = (Context) value;

      try {
        child.destroySubcontext(childName);
      } catch (NamingException ex) {
        throwPrefixedException(ex, key);
      }
    }
  }

  @Override
  public void destroySubcontext(String name) throws NamingException {
    destroySubcontext(new CompositeName(name));
  }

  @Override
  public Context createSubcontext(Name name) throws NamingException {
    Enumeration<String> all = name.getAll();
    if (!all.hasMoreElements()) throw new InvalidNameException();
    String key = all.nextElement();
    if (all.hasMoreElements()) {
      Context child = (Context) bindings.get(key);
      if (child == null) throw new NameNotFoundException(key);
      CompositeName childName = new CompositeName();
      do {
        childName.add(all.nextElement());
      } while (all.hasMoreElements());
      return child.createSubcontext(childName);
    }
    if (bindings.containsKey(key)) throw new NameAlreadyBoundException(key);
    BarefootContext context = new BarefootContext(classLoader, new Hashtable<>());
    bindings.put(key, context);
    return context;
  }

  @Override
  public Context createSubcontext(String name) throws NamingException {
    return createSubcontext(new CompositeName(name));
  }

  @Override
  public Object lookupLink(Name arg0) throws NamingException {
    throw new UnsupportedOperationException(
        "Not supported yet."); // To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Object lookupLink(String name) throws NamingException {
    return lookupLink(new CompositeName(name));
  }

  @Override
  public NameParser getNameParser(Name name) throws NamingException {
    if (name.isEmpty()) {
      return new NameParser() {
        @Override
        public Name parse(String arg0) throws NamingException {
          return new CompositeName(arg0);
        }
      };
    }
    Context child = (Context) lookup(name);
    return child.getNameParser(new CompositeName());
  }

  @Override
  public NameParser getNameParser(String arg0) throws NamingException {
    return getNameParser(new CompositeName(arg0));
  }

  @Override
  public Name composeName(Name name, Name prefix) throws NamingException {
    CompositeName result = new CompositeName();
    for (Name n : new Name[] {prefix, name}) {
      Enumeration<String> all = n.getAll();
      while (all.hasMoreElements()) {
        result.add(all.nextElement());
      }
    }

    return result;
  }

  @Override
  public String composeName(String name, String prefix) throws NamingException {
    return composeName(new CompositeName(name), new CompositeName(prefix)).toString();
  }

  @Override
  public synchronized Object addToEnvironment(String key, Object value) throws NamingException {
    Object original = environment.get(key);
    environment.put(key, value);
    return original;
  }

  @Override
  public synchronized Object removeFromEnvironment(String key) throws NamingException {
    Object value = null;
    if (environment.containsKey(key)) {
      value = environment.get(key);
      environment.remove(key);
    }
    return value;
  }

  @Override
  public synchronized Hashtable<?, ?> getEnvironment() throws NamingException {
    return (Hashtable<?, ?>) environment.clone();
  }

  @Override
  public void close() throws NamingException {}

  @Override
  public String getNameInNamespace() throws NamingException {
    throw new UnsupportedOperationException(
        "Not supported yet."); // To change body of generated methods, choose Tools | Templates.
  }

  private Boolean asBoolean(RefAddr addr) {
    Object content = addr == null ? null : addr.getContent();
    return content == null
        ? null
        : content instanceof Boolean ? (Boolean) content : Boolean.parseBoolean(content.toString());
  }

  private Object newInstance(Reference ref, Name name) throws LinkException {
    String className = ref.getFactoryClassName();
    Object value;

    try {
      Class cls = classLoader.loadClass(className);
      ObjectFactory factory = (ObjectFactory) cls.getConstructor().newInstance();
      value = factory.getObjectInstance(ref, name, this, new Hashtable<>());
    } catch (Exception ex) {
      LinkException ex2 = new LinkException();
      ex2.setResolvedName(name);
      ex2.initCause(ex);
      throw ex2;
    }

    return value;
  }

  private Context lookupContext(Name name) throws NamingException {
    Object obj = lookup(name);
    if (obj instanceof Context) return (Context) obj;
    NotContextException ex = new NotContextException();
    ex.setResolvedName(name);
    throw ex;
  }

  private Object throwPrefixedException(NamingException ex, String key) throws NamingException {
    Name resolved = ex.getResolvedName();
    ex.setResolvedName(
        resolved == null ? new CompositeName(key) : composeName(resolved, new CompositeName(key)));
    throw ex;
  }
}
