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

 package org.jdiameter.impl.ha.client.rx;

 import org.jdiameter.api.rx.ClientRxSession;
 import org.jdiameter.client.impl.app.rx.IClientRxSessionData;
 import org.jdiameter.common.api.app.rx.ClientRxSessionState;
 import org.jdiameter.impl.ha.common.AppSessionDataReplicatedImpl;
 import org.jdiameter.impl.ha.data.CachedSessionDatasource;

 /**
  * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
  * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
  */
 public class ClientRxSessionDataReplicatedImpl extends AppSessionDataReplicatedImpl implements IClientRxSessionData
 {

     private static final String EVENT_BASED = "EVENT_BASED";
     private static final String REQUEST_TYPE = "REQUEST_TYPE";
     private static final String STATE = "STATE";

     public ClientRxSessionDataReplicatedImpl(String sessionId, CachedSessionDatasource datasource)
     {
         super(sessionId, datasource);

         setAppSessionIface(ClientRxSession.class);
         setClientRxSessionState(ClientRxSessionState.IDLE);
     }

     @Override
     public boolean isEventBased()
     {
         return toPrimitive(getFieldValue(EVENT_BASED), true);
     }

     @Override
     public void setEventBased(boolean isEventBased)
     {
         setFieldValue(EVENT_BASED, isEventBased);
     }

     @Override
     public boolean isRequestTypeSet()
     {
         return toPrimitive(getFieldValue(REQUEST_TYPE), false);
     }

     @Override
     public void setRequestTypeSet(boolean requestTypeSet)
     {
         setFieldValue(REQUEST_TYPE, requestTypeSet);
     }

     @Override
     public ClientRxSessionState getClientRxSessionState()
     {
         return ClientRxSessionState.valueOf(getFieldValue(STATE));
     }

     @Override
     public void setClientRxSessionState(ClientRxSessionState state)
     {
         setFieldValue(STATE, state.name());
     }
 }
