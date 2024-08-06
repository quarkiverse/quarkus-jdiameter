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

 package org.jdiameter.impl.ha.client.acc;

 import org.jdiameter.api.AvpDataException;
 import org.jdiameter.api.Request;
 import org.jdiameter.api.acc.ClientAccSession;
 import org.jdiameter.client.api.IMessage;
 import org.jdiameter.client.api.parser.IMessageParser;
 import org.jdiameter.client.api.parser.ParseException;
 import org.jdiameter.client.impl.app.acc.IClientAccSessionData;
 import org.jdiameter.common.api.app.acc.ClientAccSessionState;
 import org.jdiameter.impl.ha.common.AppSessionDataReplicatedImpl;
 import org.jdiameter.impl.ha.data.CachedSessionDatasource;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 import java.io.Serializable;
 import java.nio.ByteBuffer;

 /**
  * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
  * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
  */
 public class ClientAccSessionDataReplicatedImpl extends AppSessionDataReplicatedImpl implements IClientAccSessionData
 {

     private static final Logger logger = LoggerFactory.getLogger(ClientAccSessionDataReplicatedImpl.class);

     private static final String STATE = "STATE";
     private static final String INTERIM_TIMERID = "INTERIM_TIMERID";
     private static final String DEST_HOST = "DEST_HOST";
     private static final String DEST_REALM = "DEST_REALM";
     private static final String BUFFER = "BUFFER";

     private final IMessageParser messageParser;

     public ClientAccSessionDataReplicatedImpl(String sessionId, CachedSessionDatasource datasource)
     {
         super(sessionId, datasource);

         setAppSessionIface(ClientAccSession.class);
         setClientAccSessionState(ClientAccSessionState.IDLE);

         this.messageParser = datasource.getContainer().getAssemblerFacility().getComponentInstance(IMessageParser.class);
     }

     /*
      * (non-Javadoc)
      *
      * @see org.jdiameter.client.impl.app.acc.IClientAccSessionData#setClientAccSessionState
      * (org.jdiameter.common.api.app.acc.ClientAccSessionState)
      */
     @Override
     public void setClientAccSessionState(ClientAccSessionState state)
     {
         setFieldValue(STATE, state);
     }

     /*
      * (non-Javadoc)
      *
      * @see org.jdiameter.client.impl.app.acc.IClientAccSessionData#getClientAccSessionState()
      */
     @Override
     public ClientAccSessionState getClientAccSessionState()
     {
         return getFieldValue(STATE);
     }

     /*
      * (non-Javadoc)
      *
      * @see org.jdiameter.client.impl.app.acc.IClientAccSessionData#setInterimTimerId(java.io.Serializable)
      */
     @Override
     public void setInterimTimerId(Serializable tid)
     {
         setFieldValue(INTERIM_TIMERID, tid);
     }

     /*
      * (non-Javadoc)
      *
      * @see org.jdiameter.client.impl.app.acc.IClientAccSessionData#getInterimTimerId()
      */
     @Override
     public Serializable getInterimTimerId()
     {
         return getFieldValue(INTERIM_TIMERID);
     }

     /*
      * (non-Javadoc)
      *
      * @see org.jdiameter.client.impl.app.acc.IClientAccSessionData#setDestinationHost(java.lang.String)
      */
     @Override
     public void setDestinationHost(String destHost)
     {
         setFieldValue(DEST_HOST, destHost);
     }

     /*
      * (non-Javadoc)
      *
      * @see org.jdiameter.client.impl.app.acc.IClientAccSessionData#getDestinationHost()
      */
     @Override
     public String getDestinationHost()
     {
         return getFieldValue(DEST_HOST);
     }

     /*
      * (non-Javadoc)
      *
      * @see org.jdiameter.client.impl.app.acc.IClientAccSessionData#setDestinationRealm(java.lang.String)
      */
     @Override
     public void setDestinationRealm(String destRealm)
     {
         setFieldValue(DEST_REALM, destRealm);
     }

     /*
      * (non-Javadoc)
      *
      * @see org.jdiameter.client.impl.app.acc.IClientAccSessionData#getDestinationRealm()
      */
     @Override
     public String getDestinationRealm()
     {
         return getFieldValue(DEST_REALM);
     }

     @Override
     public Request getBuffer()
     {
         byte[] data = getFieldValue(BUFFER);
         if (data != null) {
             try {
                 return this.messageParser.createMessage(ByteBuffer.wrap(data));
             }
             catch (AvpDataException e) {
                 logger.error("Unable to recreate message from buffer.");
                 return null;
             }
         } else {
             return null;
         }
     }

     @Override
     public void setBuffer(Request buffer)
     {
         if (buffer != null) {
             try {
                 byte[] data = this.messageParser.encodeMessage((IMessage) buffer).array();
                 setFieldValue(BUFFER, data);
             }
             catch (ParseException e) {
                 logger.error("Unable to encode message to buffer.");
             }
         } else {
             remove(BUFFER);
         }
     }

 }
