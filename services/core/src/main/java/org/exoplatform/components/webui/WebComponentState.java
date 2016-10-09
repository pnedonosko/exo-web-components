
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

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

/**
 * Simple holder of fact that it's a request to Web Component URL (a special site in the portal w/o navigation
 * toolbar and other not related UI stuff). <br>
 * 
 * Created by The eXo Platform SAS
 * 
 * @author <a href="mailto:pnedonosko@exoplatform.com">Peter Nedonosko</a>
 * @version $Id: WebComponentState.java 00000 Oct 4, 2016 pnedonosko $
 * 
 */
public class WebComponentState {

  protected static final Log                 LOG     = ExoLogger.getLogger(WebComponentState.class);

  protected static final ThreadLocal<String> session = new ThreadLocal<String>();

  static void activate(String sessionId) {
    session.set(sessionId);
  }

  static void deactivate(String sessionId) {
    if (sessionId != null && sessionId.equals(session.get())) {
      session.remove();
    }
  }

  static boolean isActivated(String sessionId) {
    return sessionId != null && sessionId.equals(session.get());
  }

  /**
   * 
   */
  private WebComponentState() {
    // TODO something?
  }
}
