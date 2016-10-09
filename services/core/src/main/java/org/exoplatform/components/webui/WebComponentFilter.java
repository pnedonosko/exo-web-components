
/*
 * Copyright (C) 2003-2016 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.components.webui;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.application.PortalApplication;
import org.exoplatform.web.WebAppController;
import org.exoplatform.web.filter.Filter;
import org.gatein.common.logging.Logger;
import org.gatein.common.logging.LoggerFactory;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * Filter to catch Web Component requests and configure its context. This filter does two things:
 * * activate and deactivate {@link WebComponentState} in the request, its state later will be used by
 * application lifecycle listeners in context of a request to a web component.
 * <br>
 * 
 * Created by The eXo Platform SAS
 * 
 * @author <a href="mailto:pnedonosko@exoplatform.com">Peter Nedonosko</a>
 * @version $Id: WebComponentFilter.java 00000 Oct 3, 2016 pnedonosko $
 * 
 */
public class WebComponentFilter implements Filter {

  protected static final Logger LOG = LoggerFactory.getLogger(WebComponentFilter.class);

  /**
   * {@inheritDoc}
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
                                                                                            ServletException {
    HttpServletRequest httpReq = (HttpServletRequest) request;

    PortalContainer container = PortalContainer.getInstance();
    WebAppController controller = (WebAppController) container.getComponentInstanceOfType(WebAppController.class);
    PortalApplication app = controller.getApplication(PortalApplication.PORTAL_APPLICATION_ID);

    final WebComponentLifecycle lifecycle = new WebComponentLifecycle();
    final String sessionId = httpReq.getSession().getId();
    try {
      WebComponentState.activate(sessionId);
      // TODO add portal app lifecycle once in configuration of Platform extension
      app.getApplicationLifecycle().add(lifecycle);
      chain.doFilter(request, response);
    } finally {
      WebComponentState.deactivate(sessionId);
      app.getApplicationLifecycle().remove(lifecycle);
    }
  }

}
