 /*
  * TeleStax, Open Source Cloud Communications
  * Copyright 2011-2016, TeleStax Inc. and individual contributors
  * by the @authors tag.
  *
  * This program is free software: you can redistribute it and/or modify
  * under the terms of the GNU Affero General Public License as
  * published by the Free Software Foundation; either version 3 of
  * the License, or (at your option) any later version.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU Affero General Public License for more details.
  *
  * You should have received a copy of the GNU Affero General Public License
  * along with this program.  If not, see <http://www.gnu.org/licenses/>
  *
  * This file incorporates work covered by the following copyright and
  * permission notice:
  *
  *   JBoss, Home of Professional Open Source
  *   Copyright 2007-2011, Red Hat, Inc. and individual contributors
  *   by the @authors tag. See the copyright.txt in the distribution for a
  *   full listing of individual contributors.
  *
  *   This is free software; you can redistribute it and/or modify it
  *   under the terms of the GNU Lesser General Public License as
  *   published by the Free Software Foundation; either version 2.1 of
  *   the License, or (at your option) any later version.
  *
  *   This software is distributed in the hope that it will be useful,
  *   but WITHOUT ANY WARRANTY; without even the implied warranty of
  *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  *   Lesser General Public License for more details.
  *
  *   You should have received a copy of the GNU Lesser General Public
  *   License along with this software; if not, write to the Free
  *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */

 package org.jdiameter.impl.ha.server.ro;

 import org.jdiameter.api.ro.ServerRoSession;
 import org.jdiameter.common.api.app.ro.ServerRoSessionState;
 import org.jdiameter.impl.ha.common.AppSessionDataReplicatedImpl;
 import org.jdiameter.impl.ha.data.CachedSessionDatasource;
 import org.jdiameter.server.impl.app.ro.IServerRoSessionData;

 import java.io.Serializable;

 /**
  * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
  * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
  */
 public class ServerRoSessionDataReplicatedImpl extends AppSessionDataReplicatedImpl implements IServerRoSessionData
 {

     private static final String TCCID = "TCCID";
     private static final String STATELESS = "STATELESS";
     private static final String STATE = "STATE";

     public ServerRoSessionDataReplicatedImpl(String sessionId, CachedSessionDatasource datasource)
     {
         super(sessionId, datasource);

         setAppSessionIface(ServerRoSession.class);
         setServerRoSessionState(ServerRoSessionState.IDLE);
     }

     /*
      * (non-Javadoc)
      *
      * @see org.jdiameter.server.impl.app.cca.IServerCCASessionData#isStateless()
      */
     @Override
     public boolean isStateless()
     {
         return toPrimitive(getFieldValue(STATELESS), true);
     }

     /*
      * (non-Javadoc)
      *
      * @see org.jdiameter.server.impl.app.cca.IServerCCASessionData#setStateless( boolean)
      */
     @Override
     public void setStateless(boolean stateless)
     {
         setFieldValue(STATELESS, stateless);
     }

     /*
      * (non-Javadoc)
      *
      * @see org.jdiameter.server.impl.app.cca.IServerCCASessionData# getServerCCASessionState()
      */
     @Override
     public ServerRoSessionState getServerRoSessionState()
     {
         return ServerRoSessionState.valueOf(getFieldValue(STATE));
     }

     /*
      * (non-Javadoc)
      *
      * @see org.jdiameter.server.impl.app.cca.IServerCCASessionData# setServerCCASessionState
      * (org.jdiameter.common.api.app.cca.ServerCCASessionState)
      */
     @Override
     public void setServerRoSessionState(ServerRoSessionState state)
     {
         setFieldValue(STATE, state.name());
     }

     /*
      * (non-Javadoc)
      *
      * @see org.jdiameter.server.impl.app.cca.IServerCCASessionData#setTccTimerId (java.io.Serializable)
      */
     @Override
     public void setTccTimerId(Serializable tccTimerId)
     {
         setFieldValue(TCCID, tccTimerId);
     }

     /*
      * (non-Javadoc)
      *
      * @see org.jdiameter.server.impl.app.cca.IServerCCASessionData#getTccTimerId()
      */
     @Override
     public Serializable getTccTimerId()
     {
         return getFieldValue(TCCID);
     }

 }
