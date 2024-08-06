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

 package org.jdiameter.impl.ha.common;

 import org.jdiameter.api.ApplicationId;
 import org.jdiameter.api.app.AppSession;
 import org.jdiameter.common.api.app.IAppSessionData;
 import org.jdiameter.impl.ha.data.CachedSessionDatasource;

 import java.util.Base64;

 /**
  * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
  * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
  */
 public class AppSessionDataReplicatedImpl implements IAppSessionData
 {

     protected static final String APID = "APID";
     public static final String SIFACE = "SIFACE";

     private final CachedSessionDatasource datasource;
     private final String sessionId;

     public AppSessionDataReplicatedImpl(String sessionId, CachedSessionDatasource datasource)
     {
         this.sessionId  = sessionId;
         this.datasource = datasource;
     }

     public void setAppSessionIface(Class<? extends AppSession> iface)
     {
         datasource.setFieldValue(sessionId, SIFACE, iface.getCanonicalName());
     }

     @Override
     public String getSessionId()
     {
         return sessionId;
     }

     @Override
     public void setApplicationId(ApplicationId applicationId)
     {
         datasource.setFieldValue(sessionId, APID, applicationId.getVendorId() + ":" + applicationId.getAcctAppId() + ":" + applicationId.getAuthAppId());
     }

     @Override
     public ApplicationId getApplicationId()
     {
         String[] app = ((String) datasource.getFieldValue(sessionId, APID)).split(":");
         long venId = Long.parseLong(app[0]);
         long authId = Long.parseLong(app[1]);
         long acctId = Long.parseLong(app[2]);
         if (acctId == 0) {
             return ApplicationId.createByAuthAppId(venId, authId);
         }//if
         else {
             return ApplicationId.createByAccAppId(venId, acctId);
         }//else
     }

     @Override
     public boolean remove()
     {
         datasource.removeSession(sessionId);
         return true;
     }

     public <T> T getFieldValue(String fieldName)
     {
         return datasource.getFieldValue(sessionId, fieldName);
     }//getFieldValue

     public byte[] getByteFieldValue(String fieldName)
     {
         String value = getFieldValue(fieldName);
         if (value == null) {
             return null;
         }

         return Base64.getDecoder().decode(value);
     }

     public void setFieldValue(String fieldName, byte[] value)
     {
         datasource.setFieldValue(sessionId, fieldName, Base64.getEncoder().encodeToString(value));
     }//setFieldValue

     public void setFieldValue(String fieldName, Object value)
     {
         datasource.setFieldValue(sessionId, fieldName, value);
     }//setFieldValue


     public void remove(String fieldName)
     {
         datasource.setFieldValue(sessionId, fieldName, null);
     }

     public CachedSessionDatasource getDatasource()
     {
         return datasource;
     }

     protected boolean toPrimitive(Boolean b, boolean _default)
     {
         return b == null ? _default : b;
     }

     protected int toPrimitive(Integer i)
     {
         return i == null ? NON_INITIALIZED : i;
     }

     protected long toPrimitive(Long l)
     {
         return l == null ? NON_INITIALIZED : l;
     }
 }
