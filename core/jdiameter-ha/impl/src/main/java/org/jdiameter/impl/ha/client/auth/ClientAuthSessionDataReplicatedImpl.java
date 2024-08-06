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

 package org.jdiameter.impl.ha.client.auth;

 import org.jdiameter.api.auth.ClientAuthSession;
 import org.jdiameter.client.impl.app.auth.IClientAuthSessionData;
 import org.jdiameter.common.api.app.auth.ClientAuthSessionState;
 import org.jdiameter.impl.ha.common.AppSessionDataReplicatedImpl;
 import org.jdiameter.impl.ha.data.CachedSessionDatasource;

 import java.io.Serializable;

 /**
  * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
  * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
  */
 public class ClientAuthSessionDataReplicatedImpl extends AppSessionDataReplicatedImpl implements IClientAuthSessionData
 {

     private static final String STATE = "STATE";
     private static final String DESTINATION_HOST = "DESTINATION_HOST";
     private static final String DESTINATION_REALM = "DESTINATION_REALM";
     private static final String STATELESS = "STATELESS";
     private static final String TS_TIMERID = "TS_TIMERID";

     public ClientAuthSessionDataReplicatedImpl(String sessionId, CachedSessionDatasource datasource)
     {
         super(sessionId, datasource);
         setAppSessionIface(ClientAuthSession.class);
         setClientAuthSessionState(ClientAuthSessionState.IDLE);
     }

     /*
      * (non-Javadoc)
      *
      * @see org.jdiameter.client.impl.app.auth.IClientAuthSessionData#setClientAuthSessionState(org.jdiameter.common.api.app.auth.
      * ClientAuthSessionState)
      */
     @Override
     public void setClientAuthSessionState(ClientAuthSessionState state)
     {
         setFieldValue(STATE, state);
     }

     /*
      * (non-Javadoc)
      *
      * @see org.jdiameter.client.impl.app.auth.IClientAuthSessionData#getClientAuthSessionState()
      */
     @Override
     public ClientAuthSessionState getClientAuthSessionState()
     {
         return getFieldValue(STATE);
     }

     /*
      * (non-Javadoc)
      *
      * @see org.jdiameter.client.impl.app.auth.IClientAuthSessionData#isStateless()
      */
     @Override
     public boolean isStateless()
     {
         return toPrimitive(getFieldValue(STATELESS), true);
     }

     /*
      * (non-Javadoc)
      *
      * @see org.jdiameter.client.impl.app.auth.IClientAuthSessionData#setStateless(boolean)
      */
     @Override
     public void setStateless(boolean b)
     {
         setFieldValue(STATELESS, b);
     }

     /*
      * (non-Javadoc)
      *
      * @see org.jdiameter.client.impl.app.auth.IClientAuthSessionData#getDestinationHost()
      */
     @Override
     public String getDestinationHost()
     {
         return getFieldValue(DESTINATION_HOST);
     }

     /*
      * (non-Javadoc)
      *
      * @see org.jdiameter.client.impl.app.auth.IClientAuthSessionData#setDestinationHost(java.lang.String)
      */
     @Override
     public void setDestinationHost(String host)
     {
         setFieldValue(DESTINATION_HOST, host);
     }

     /*
      * (non-Javadoc)
      *
      * @see org.jdiameter.client.impl.app.auth.IClientAuthSessionData#getDestinationRealm()
      */
     @Override
     public String getDestinationRealm()
     {
         return getFieldValue(DESTINATION_REALM);
     }

     /*
      * (non-Javadoc)
      *
      * @see org.jdiameter.client.impl.app.auth.IClientAuthSessionData#setDestinationRealm(java.lang.String)
      */
     @Override
     public void setDestinationRealm(String realm)
     {
         setFieldValue(DESTINATION_REALM, realm);
     }

     /*
      * (non-Javadoc)
      *
      * @see org.jdiameter.client.impl.app.auth.IClientAuthSessionData#getTsTimerId()
      */
     @Override
     public Serializable getTsTimerId()
     {
         return getFieldValue(TS_TIMERID);
     }

     /*
      * (non-Javadoc)
      *
      * @see org.jdiameter.client.impl.app.auth.IClientAuthSessionData#setTsTimerId(java.io.Serializable)
      */
     @Override
     public void setTsTimerId(Serializable tid)
     {
         setFieldValue(TS_TIMERID, tid);
     }

 }
