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

package net.sf.barefoot.web.xml.javax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import javax.servlet.SessionCookieConfig;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/** Handler for web.xml file. */
final class BarefootWebXmlHandler extends DefaultHandler {
  protected final ServletContext context;
  protected final Map<String, FilterRegistration> filters = new HashMap<>();
  protected final Map<String, ServletRegistration> servlets = new HashMap<>();
  protected final Map<String, String> values = new HashMap<>(), initParams = new HashMap<>();
  protected final List<String> urls = new ArrayList<>();
  protected final Function<String, String> valueExpander;
  protected StringBuilder stringBuilder;
  protected SessionCookieConfig cookieConfig;
  protected String elementName, paramName, paramValue;
  static final String FILTER = "filter",
      SERVLET = "servlet",
      LISTENER = "listener",
      FILTER_NAME = "filter-name",
      SERVLET_NAME = "servlet-name",
      SERVLET_CLASS = "servlet-class",
      FILTER_CLASS = "filter-class",
      FILTER_MAPPING = "filter-mapping",
      SERVLET_MAPPING = "servlet-mapping",
      URL_PATTERN = "url-pattern",
      LOAD_ON_STARTUP = "load-on-startup",
      LISTENER_CLASS = "listener-class",
      DESCRIPTION = "description",
      SESSION_CONFIG = "session-config",
      SESSION_TIMEOUT = "session-timeout",
      CONTEXT_PARAM = "context-param",
      INIT_PARAM = "init-param",
      PARAM_NAME = "param-name",
      PARAM_VALUE = "param-value",
      WEB_APP = "web-app",
      COOKIE_CONFIG = "cookie-config",
      PATH = "path",
      HTTP_ONLY = "http-only",
      MAX_AGE = "max-age",
      NAME = "name",
      COMMENT = "comment",
      DOMAIN = "domain",
      SECURE = "secure";

  public BarefootWebXmlHandler(ServletContext c, Function<String, String> f) {
    context = c;
    valueExpander = f == null ? (s) -> s : f;
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    if (stringBuilder != null) {
      stringBuilder.append(ch, start, length);
    }
  }

  @Override
  public void startElement(String uri, String lName, String qName, Attributes attr)
      throws SAXException {
    switch (qName) {
      case WEB_APP:
        break;
      case FILTER:
      case SERVLET:
      case SESSION_CONFIG:
      case LISTENER:
      case FILTER_MAPPING:
      case SERVLET_MAPPING:
        values.clear();
        urls.clear();
        initParams.clear();
        break;
      case CONTEXT_PARAM:
      case INIT_PARAM:
        paramName = null;
        paramValue = null;
        break;
      case FILTER_NAME:
      case SERVLET_NAME:
      case SERVLET_CLASS:
      case FILTER_CLASS:
      case URL_PATTERN:
      case LOAD_ON_STARTUP:
      case LISTENER_CLASS:
      case DESCRIPTION:
      case SESSION_TIMEOUT:
      case PARAM_NAME:
      case PARAM_VALUE:
      case PATH:
      case HTTP_ONLY:
      case MAX_AGE:
      case NAME:
      case COMMENT:
      case DOMAIN:
      case SECURE:
        stringBuilder = new StringBuilder();
        break;
      case COOKIE_CONFIG:
        cookieConfig = context.getSessionCookieConfig();
        break;
      default:
        throw new SAXException("Unknown element " + qName);
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    switch (qName) {
      case WEB_APP:
        break;
      case LISTENER:
        {
          String className = values.get(LISTENER_CLASS);
          context.addListener(className);
        }
        values.clear();
        break;
      case FILTER:
        {
          String filterName = values.get(FILTER_NAME);
          String className = values.get(FILTER_CLASS);
          FilterRegistration filter = context.addFilter(filterName, className);
          filters.put(filterName, filter);
        }
        values.clear();
        break;
      case SERVLET:
        {
          String servletName = values.get(SERVLET_NAME);
          String className = values.get(SERVLET_CLASS);
          String loadOnStartup = values.get(LOAD_ON_STARTUP);
          ServletRegistration.Dynamic servlet = context.addServlet(servletName, className);
          servlets.put(servletName, servlet);
          if (!initParams.isEmpty()) {
            servlet.getInitParameters().putAll(initParams);
          }
          if (loadOnStartup != null) {
            servlet.setLoadOnStartup(Integer.parseInt(loadOnStartup));
          }
        }
        initParams.clear();
        values.clear();
        break;
      case FILTER_MAPPING:
        {
          String filterName = values.get(FILTER_NAME);
          FilterRegistration filter = filters.get(filterName);
          filter.getUrlPatternMappings().addAll(urls);
        }
        urls.clear();
        values.clear();
        break;
      case SERVLET_MAPPING:
        {
          String servletName = values.get(SERVLET_NAME);
          ServletRegistration servlet = servlets.get(servletName);
          servlet.getMappings().addAll(urls);
        }
        urls.clear();
        values.clear();
        break;
      case SESSION_CONFIG:
        urls.clear();
        values.clear();
        break;
      case URL_PATTERN:
        urls.add(getValue());
        break;
      case PARAM_NAME:
        paramName = getValue();
        break;
      case PARAM_VALUE:
        paramValue = getValue();
        break;
      case CONTEXT_PARAM:
        if (!context.setInitParameter(paramName, paramValue)) {
          throw new SAXException("context-param exists " + paramName);
        }
        paramName = null;
        paramValue = null;
        break;
      case INIT_PARAM:
        if (initParams.containsKey(paramName)) {
          throw new SAXException("init-param exists " + paramName);
        }
        initParams.put(paramName, paramValue);
        paramName = null;
        paramValue = null;
        break;
      case PATH:
        cookieConfig.setPath(getValue());
        break;
      case HTTP_ONLY:
        cookieConfig.setHttpOnly(Boolean.parseBoolean(getValue()));
        break;
      case MAX_AGE:
        cookieConfig.setMaxAge(Integer.parseInt(getValue()));
        break;
      case NAME:
        cookieConfig.setName(getValue());
        break;
      case COMMENT:
        cookieConfig.setComment(getValue());
        break;
      case DOMAIN:
        cookieConfig.setDomain(getValue());
        break;
      case SECURE:
        cookieConfig.setSecure(Boolean.parseBoolean(getValue()));
        break;
      case COOKIE_CONFIG:
        cookieConfig = null;
        break;
      default:
        values.put(qName, getValue());
        break;
    }
  }

  protected String getValue() {
    String value = valueExpander.apply(stringBuilder.toString());
    stringBuilder = null;
    return value;
  }
}
