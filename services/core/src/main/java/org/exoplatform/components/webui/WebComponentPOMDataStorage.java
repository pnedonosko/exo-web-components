
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

import org.exoplatform.commons.utils.IOUtil;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.config.model.ModelObject;
import org.exoplatform.portal.pom.config.POMSessionManager;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.gatein.common.transaction.JTAUserTransactionLifecycleService;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.impl.UnmarshallingContext;

import java.io.ByteArrayInputStream;
import java.util.UUID;

/**
 * THIS CLASS DOESN NOT USED! We keep it here for history.
 * TODO remove this class. 
 * 
 * Created by The eXo Platform SAS
 * 
 * @author <a href="mailto:pnedonosko@exoplatform.com">Peter Nedonosko</a>
 * @version $Id: WebComponentPOMDataStorage.java 00000 Oct 3, 2016 pnedonosko $
 * 
 */
@Deprecated
public class WebComponentPOMDataStorage extends org.exoplatform.portal.pom.config.POMDataStorage {

  protected static final Log           LOG = ExoLogger.getLogger(WebComponentPOMDataStorage.class);

  protected final ConfigurationManager confManager;

  /**
   * @param pomMgr
   * @param confManager
   * @param jtaUserTransactionLifecycleService
   * @param listenerService
   */
  public WebComponentPOMDataStorage(POMSessionManager pomMgr,
                                    ConfigurationManager confManager,
                                    JTAUserTransactionLifecycleService jtaUserTransactionLifecycleService,
                                    ListenerService listenerService) {
    super(pomMgr, confManager, jtaUserTransactionLifecycleService, listenerService);
    this.confManager = confManager;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Container getSharedLayout() throws Exception {
    final String sessionId = WebuiRequestContext.<WebuiRequestContext> getCurrentInstance().getSessionId();
    if (WebComponentState.isActivated(sessionId)) {
      if (LOG.isDebugEnabled()) {
        LOG.debug(">> getSharedLayout: " + sessionId);
      }
      // FYI code copied from super's method
      String path = "war:/conf/_component/portal/componentlayout.xml"; // TODO
      String out = IOUtil.getStreamContentAsString(confManager.getInputStream(path));
      ByteArrayInputStream is = new ByteArrayInputStream(out.getBytes("UTF-8"));
      IBindingFactory bfact = BindingDirectory.getFactory(Container.class);
      UnmarshallingContext uctx = (UnmarshallingContext) bfact.createUnmarshallingContext();
      uctx.setDocument(is, null, "UTF-8", false);
      Container container = (Container) uctx.unmarshalElement();
      generateStorageName(container);
      return container;
    }
    return super.getSharedLayout();
  }

  /**
   * TODO it's a private method from super.
   * This is a hack and should be removed, it is only used temporarily. This is because the objects are loaded
   * from files and
   * don't have name.
   */
  private void generateStorageName(ModelObject obj) {
    if (obj instanceof Container) {
      for (ModelObject child : ((Container) obj).getChildren()) {
        generateStorageName(child);
      }
    } else if (obj instanceof org.exoplatform.portal.config.model.Application) {
      obj.setStorageName(UUID.randomUUID().toString());
    }
  }
}
