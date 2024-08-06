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

 package org.jdiameter.impl.ha.client.ro;

 import org.jdiameter.api.AvpDataException;
 import org.jdiameter.api.Request;
 import org.jdiameter.api.ro.ClientRoSession;
 import org.jdiameter.client.api.IMessage;
 import org.jdiameter.client.api.parser.IMessageParser;
 import org.jdiameter.client.api.parser.ParseException;
 import org.jdiameter.client.impl.app.ro.IClientRoSessionData;
 import org.jdiameter.common.api.app.ro.ClientRoSessionState;
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
 public class ClientRoSessionDataReplicatedImpl extends AppSessionDataReplicatedImpl implements IClientRoSessionData
 {

     private static final Logger logger = LoggerFactory.getLogger(ClientRoSessionDataReplicatedImpl.class);

     private static final String EVENT_BASED = "EVENT_BASED";
     private static final String REQUEST_TYPE = "REQUEST_TYPE";
     private static final String STATE = "STATE";
     private static final String TXTIMER_ID = "TXTIMER_ID";
     private static final String TXTIMER_REQUEST = "TXTIMER_REQUEST";
     private static final String BUFFER = "BUFFER";
     private static final String GRA = "GRA";
     private static final String GDDFH = "GDDFH";
     private static final String GCCFH = "GCCFH";

     private final IMessageParser messageParser;

     public ClientRoSessionDataReplicatedImpl(String sessionId, CachedSessionDatasource datasource)
     {
         super(sessionId, datasource);

         setAppSessionIface(ClientRoSession.class);
         setClientRoSessionState(ClientRoSessionState.IDLE);

         this.messageParser = datasource.getContainer().getAssemblerFacility().getComponentInstance(IMessageParser.class);
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
     public ClientRoSessionState getClientRoSessionState()
     {
         return ClientRoSessionState.valueOf(getFieldValue(STATE));
     }

     @Override
     public void setClientRoSessionState(ClientRoSessionState state)
     {
         setFieldValue(STATE, state.name());
     }

     @Override
     public Serializable getTxTimerId()
     {
         return getFieldValue(TXTIMER_ID);
     }

     @Override
     public void setTxTimerId(Serializable txTimerId)
     {
         setFieldValue(TXTIMER_ID, txTimerId);
     }

     @Override
     public Request getTxTimerRequest()
     {
         byte[] data = getFieldValue(TXTIMER_REQUEST);
         if (data != null) {
             try {
                 return this.messageParser.createMessage(ByteBuffer.wrap(data));
             }
             catch (AvpDataException e) {
                 logger.error("Unable to recreate Tx Timer Request from buffer.");
                 return null;
             }
         } else {
             return null;
         }
     }

     @Override
     public void setTxTimerRequest(Request txTimerRequest)
     {
         if (txTimerRequest != null) {
             try {
                 byte[] data = this.messageParser.encodeMessage((IMessage) txTimerRequest).array();
                 setFieldValue(TXTIMER_REQUEST, data);
             }
             catch (ParseException e) {
                 logger.error("Unable to encode Tx Timer Request to buffer.");
             }
         } else {
             remove(TXTIMER_REQUEST);
         }
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

     @Override
     public int getGatheredRequestedAction()
     {
         return toPrimitive((Integer) getFieldValue(GRA));
     }

     @Override
     public void setGatheredRequestedAction(int gatheredRequestedAction)
     {
         setFieldValue(GRA, gatheredRequestedAction);
     }

     @Override
     public int getGatheredCCFH()
     {
         return toPrimitive((Integer) getFieldValue(GCCFH));
     }

     @Override
     public void setGatheredCCFH(int gatheredCCFH)
     {
         setFieldValue(GCCFH, gatheredCCFH);
     }

     @Override
     public int getGatheredDDFH()
     {
         return toPrimitive((Integer) getFieldValue(GDDFH));
     }

     @Override
     public void setGatheredDDFH(int gatheredDDFH)
     {
         setFieldValue(GDDFH, gatheredDDFH);
     }

 }
