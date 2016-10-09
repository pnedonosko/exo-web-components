
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

import org.exoplatform.commons.utils.Safe;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.ecm.webui.component.explorer.UIJCRExplorer;
import org.exoplatform.ecm.webui.component.explorer.UIJCRExplorerPortlet;
import org.exoplatform.ecm.webui.component.explorer.UIJcrExplorerContainer;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.application.replication.ApplicationState;
import org.exoplatform.services.cms.drives.DriveData;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.web.application.Application;
import org.exoplatform.web.application.ApplicationLifecycle;
import org.exoplatform.web.application.RequestContext;
import org.exoplatform.web.application.RequestFailure;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.application.portlet.PortletRequestContext;
import org.exoplatform.webui.core.UIApplication;

/**
 * Document lifecycle designed for {@link UIJCRExplorerPortlet} as its application listener. This lifecycle
 * will check for <code>driveName</code> request parameter and if found it, will set it's value to the same
 * name portlet preference that used by the JCR explorer internally. If explorer portlet app already and
 * stored in the session, if driveName given in the request, then it will call
 * {@link UIJcrExplorerContainer#initExplorer()} to reflect the portlket actual request.<br>
 * 
 * Created by The eXo Platform SAS
 * 
 * @author <a href="mailto:pnedonosko@exoplatform.com">Peter Nedonosko</a>
 * @version $Id: DocumentsLifecycle.java 00000 Oct 2, 2016 pnedonosko $
 * 
 */
public class DocumentsLifecycle implements ApplicationLifecycle<WebuiRequestContext> {

  protected static final Log LOG = ExoLogger.getLogger(DocumentsLifecycle.class);

  /**
   * 
   */
  public DocumentsLifecycle() {
    //
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
    String sessionId = context.getSessionId();
    boolean activated = WebComponentState.isActivated(sessionId);
    if (LOG.isDebugEnabled()) {
      LOG.debug("> onStartRequest: " + app + " " + context.getRequestContextPath() + " " + sessionId + " activated:"
          + activated);
    }
    if (activated) {
      ExoContainer container = app.getApplicationServiceContainer();
      if (container != null) {
        UIApplication uiApp = context.getUIApplication();
        if (LOG.isDebugEnabled()) {
          LOG.debug(">> onStartRequest: " + uiApp);
        }
        if (PortletRequestContext.class.isAssignableFrom(context.getClass())) {
          PortletRequestContext portletContext = PortletRequestContext.class.cast(context);
          String driveName = portletContext.getParentAppRequestContext().getRequestParameter("driveName");
          boolean hasDriveName;
          // TODO do more precise, if null then do nothingm if emopty them assume default
          if (driveName != null) {
            hasDriveName = true;
            driveName = driveName.trim();
            if (driveName.length() > 0) {
              portletContext.getRequest().getPreferences().setValue("driveName", driveName);
              // portletContext.getRequest().getPreferences().setValue("nodePath", "");
            } else {
              driveName = portletContext.getRequest().getPreferences().getValue("driveName", null);
            }
          } else {
            hasDriveName = false;
          }
          if (uiApp == null) {
            // if UI app not found, and it seems will happen always here in lifecycle, we want see existing
            // UI app stored in the portal session
            // FYI below logic based on code of PortalStateManager.restoreUIRootComponent()
            RequestContext parentContext = portletContext.getParentAppRequestContext();
            if (parentContext != null && PortalRequestContext.class.isAssignableFrom(parentContext.getClass())) {
              PortalRequestContext portalContext = PortalRequestContext.class.cast(parentContext);
              String key = new StringBuilder("psm.").append(portletContext.getApplication().getApplicationId())
                                                    .append('/')
                                                    .append(portletContext.getWindowId())
                                                    .toString();
              ApplicationState appState = (ApplicationState) portalContext.getRequest().getSession(false).getAttribute(key);
              if (appState != null) {
                if (Safe.equals(context.getRemoteUser(), appState.getUserName())) {
                  uiApp = appState.getApplication();
                }
              }
            }
          }

          if (hasDriveName && uiApp != null) {
            UIJcrExplorerContainer explorerController = uiApp.findFirstComponentOfType(UIJcrExplorerContainer.class);
            UIJCRExplorer explorer = explorerController.getChild(UIJCRExplorer.class);
            DriveData drive = explorer.getDriveData();
            // if UI app exists and its drive not the same as drive name in the request - force the explorer
            // to show the request drive (in case of request with query parameter it will show exactly what
            // request asks, not the last drive user chosen in the portlet)
            if (drive != null) {
              if (!drive.getName().equals(driveName)) {
                explorerController.initExplorer();
              }
            }
          }
        }
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
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onFailRequest(Application app, WebuiRequestContext context, RequestFailure failureType) {
    // TODO?
  }

}
