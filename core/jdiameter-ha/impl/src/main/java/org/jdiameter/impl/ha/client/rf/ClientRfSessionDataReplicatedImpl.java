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

 package org.jdiameter.impl.ha.client.rf;

 import org.jdiameter.api.AvpDataException;
 import org.jdiameter.api.Request;
 import org.jdiameter.api.rf.ClientRfSession;
 import org.jdiameter.client.api.IMessage;
 import org.jdiameter.client.api.parser.IMessageParser;
 import org.jdiameter.client.api.parser.ParseException;
 import org.jdiameter.client.impl.app.rf.IClientRfSessionData;
 import org.jdiameter.common.api.app.rf.ClientRfSessionState;
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
 public class ClientRfSessionDataReplicatedImpl extends AppSessionDataReplicatedImpl implements IClientRfSessionData
 {

     private static final Logger logger = LoggerFactory.getLogger(ClientRfSessionDataReplicatedImpl.class);

     private static final String STATE = "STATE";
     private static final String BUFFER = "BUFFER";
     private static final String TS_TIMERID = "TS_TIMERID";
     private static final String DESTINATION_HOST = "DESTINATION_HOST";
     private static final String DESTINATION_REALM = "DESTINATION_REALM";

     private final IMessageParser messageParser;

     public ClientRfSessionDataReplicatedImpl(String sessionId, CachedSessionDatasource datasource)
     {
         super(sessionId, datasource);

         setAppSessionIface(ClientRfSession.class);
         setClientRfSessionState(ClientRfSessionState.IDLE);

         this.messageParser = datasource.getContainer().getAssemblerFacility().getComponentInstance(IMessageParser.class);
     }

     @Override
     public ClientRfSessionState getClientRfSessionState()
     {
         return ClientRfSessionState.valueOf(getFieldValue(STATE));
     }

     @Override
     public void setClientRfSessionState(ClientRfSessionState state)
     {
         setFieldValue(STATE, state.name());
     }

     @Override
     public Request getBuffer()
     {
         byte[] data = getByteFieldValue(BUFFER);
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

     /*
      * (non-Javadoc)
      *
      * @see org.jdiameter.client.impl.app.rf.IClientRfSessionData#getTsTimerId()
      */
     @Override
     public Serializable getTsTimerId()
     {
         return getFieldValue(TS_TIMERID);
     }

     /*
      * (non-Javadoc)
      *
      * @see org.jdiameter.client.impl.app.rf.IClientRfSessionData#setTsTimerId(java.io.Serializable)
      */
     @Override
     public void setTsTimerId(Serializable tid)
     {
         setFieldValue(TS_TIMERID, tid);
     }

     /*
      * (non-Javadoc)
      *
      * @see org.jdiameter.client.impl.app.rf.IClientRfSessionData#getDestinationHost()
      */
     @Override
     public String getDestinationHost()
     {
         return getFieldValue(DESTINATION_HOST);
     }

     /*
      * (non-Javadoc)
      *
      * @see org.jdiameter.client.impl.app.rf.IClientRfSessionData#setDestinationHost(java.lang.String)
      */
     @Override
     public void setDestinationHost(String destinationHost)
     {
         setFieldValue(DESTINATION_HOST, destinationHost);
     }

     /*
      * (non-Javadoc)
      *
      * @see org.jdiameter.client.impl.app.rf.IClientRfSessionData#getDestinationRealm()
      */
     @Override
     public String getDestinationRealm()
     {
         return getFieldValue(DESTINATION_REALM);
     }

     /*
      * (non-Javadoc)
      *
      * @see org.jdiameter.client.impl.app.rf.IClientRfSessionData#setDestinationRealm(java.lang.String)
      */
     @Override
     public void setDestinationRealm(String destinationRealm)
     {
         setFieldValue(DESTINATION_REALM, destinationRealm);
     }
 }
