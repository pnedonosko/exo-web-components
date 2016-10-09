
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

import org.exoplatform.container.ExoContainer;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.application.RequestNavigationData;
import org.exoplatform.portal.webui.container.UIContainer;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.web.ControllerContext;
import org.exoplatform.web.application.Application;
import org.exoplatform.web.application.ApplicationLifecycle;
import org.exoplatform.web.application.RequestFailure;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIComponentDecorator;

/**
 * Created by The eXo Platform SAS
 * 
 * @author <a href="mailto:pnedonosko@exoplatform.com">Peter Nedonosko</a>
 * @version $Id: WebComponentLifecycle.java 00000 Oct 2, 2016 pnedonosko $
 * 
 */
public class WebComponentLifecycle implements ApplicationLifecycle<WebuiRequestContext> {

  protected static final Log           LOG         = ExoLogger.getLogger(WebComponentLifecycle.class);

  protected final ThreadLocal<Boolean> navRendered = new ThreadLocal<Boolean>();

  /**
  * 
  */
  public WebComponentLifecycle() {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onInit(Application app) throws Exception {
    // TODO something?
    if (LOG.isDebugEnabled()) {
      LOG.debug("> onInit: " + app);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onDestroy(Application app) throws Exception {
    // TODO something?
    if (LOG.isDebugEnabled()) {
      LOG.debug("> onDestroy: " + app);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onStartRequest(Application app, WebuiRequestContext context) throws Exception {
    if (LOG.isDebugEnabled()) {
      LOG.debug("> onStartRequest: " + app + " " + context.getRequestContextPath() + " " + context.getSessionId());
    }
    // activate(app, context);
    UIContainer navPortlet = findNavigationPortlet(app, context);
    if (navPortlet != null) {
      boolean activated = WebComponentState.isActivated(context.getSessionId());
      if (activated) {
        navRendered.set(navPortlet.isRendered());
        navPortlet.setRendered(false);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onEndRequest(Application app, WebuiRequestContext context) throws Exception {
    if (LOG.isDebugEnabled()) {
      LOG.debug("> onEndRequest: " + app + " " + context.getRequestContextPath() + " " + context.getSessionId());
    }
    // deactivate(app, context);
    UIContainer navPortlet = findNavigationPortlet(app, context);
    if (navPortlet != null) {
      boolean activated = WebComponentState.isActivated(context.getSessionId());
      if (activated) {
        Boolean render = navRendered.get();
        // restore rendered if not set previously or was a such explicitly
        navPortlet.setRendered(render == null || render.booleanValue());
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onFailRequest(Application app, WebuiRequestContext context, RequestFailure failureType) {
    try {
      deactivate(app, context);
    } catch (Exception e) {
      LOG.warn("Error deactivating Web Component app on request failure", e);
    }
  }

  // ******* internals *******

  protected void activate(Application app, WebuiRequestContext context) throws Exception {
    ExoContainer container = app.getApplicationServiceContainer();
    if (container != null) {
      // WebComponentPOMDataStorage portalDS = (WebComponentPOMDataStorage)
      // container.getComponentInstanceOfType(WebComponentPOMDataStorage.class);
      // TODO cleanup
      // PortalApplication portalApp = (PortalApplication) app;
      // PortalRequestContext prContext = (PortalRequestContext) context;
      if (LOG.isDebugEnabled()) {
        UIApplication uiApp = context.getUIApplication();
        LOG.debug(">> activate: " + uiApp);
      }
      // portalDS.startComponentRequest(context.getSessionId());
      PortalRequestContext pcontext = (PortalRequestContext) context;
      ControllerContext controllerContext = pcontext.getControllerContext();
      // controllerContext.getParameter(RequestNavigationData.REQUEST_SITE_TYPE);
      // controllerContext.getParameter(RequestNavigationData.REQUEST_SITE_NAME);
      String navPath = controllerContext.getParameter(RequestNavigationData.REQUEST_PATH);
      if (navPath != null && navPath.length() > 0) {
        if (navPath.toUpperCase().toLowerCase().startsWith("documents")) {
          // TODO set driveName in portlet preferences
          // String driveName = pcontext.getRequestParameter("driveName");
          // activate(context.getSessionId());
        }
      }
    }
  }

  protected void deactivate(Application app, WebuiRequestContext context) throws Exception {
    ExoContainer container = app.getApplicationServiceContainer();
    if (container != null) {
      if (LOG.isDebugEnabled()) {
        UIApplication uiApp = context.getUIApplication();
        LOG.debug(">> deactivate: " + uiApp);
      }
      PortalRequestContext pcontext = (PortalRequestContext) context;
      ControllerContext controllerContext = pcontext.getControllerContext();
      String navPath = controllerContext.getParameter(RequestNavigationData.REQUEST_PATH);
      if (navPath != null && navPath.length() > 0) {
        if (navPath.toUpperCase().toLowerCase().startsWith("documents")) {
          // deactivate(context.getSessionId());
        }
      }
    }
  }

  protected UIContainer findNavigationPortlet(Application app, WebuiRequestContext context) throws Exception {
    ExoContainer container = app.getApplicationServiceContainer();
    if (container != null) {
      // PortalApplication portalApp = (PortalApplication) app;
      // PortalRequestContext prContext = (PortalRequestContext) context;
      UIApplication uiApp = context.getUIApplication();
      if (LOG.isDebugEnabled()) {
        LOG.debug(">> findNavigationPortlet: " + uiApp);
      }
      UIComponentDecorator uiViewWS = uiApp.findComponentById(UIPortalApplication.UI_VIEWING_WS_ID);
      if (uiViewWS != null) {
        UIContainer uiContainer = (UIContainer) uiViewWS.getUIComponent();
        if (uiContainer != null) {
          return uiContainer.getChildById("NavigationPortlet");
        }
      }
    }
    return null;
  }

}
