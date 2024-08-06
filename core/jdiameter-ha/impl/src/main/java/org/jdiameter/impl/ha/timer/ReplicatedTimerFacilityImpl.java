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

 package org.jdiameter.impl.ha.timer;

 import org.jdiameter.client.api.IContainer;
 import org.jdiameter.common.api.data.ISessionDatasource;
 import org.jdiameter.common.api.timer.ITimerFacility;
 import org.jdiameter.impl.ha.data.CachedSessionDatasource;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 import java.io.Serializable;

 /**
  * Replicated implementation of {@link ITimerFacility}
  *
  * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
  * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
  */
 public class ReplicatedTimerFacilityImpl implements ITimerFacility
 {

     private static final Logger logger = LoggerFactory.getLogger(ReplicatedTimerFacilityImpl.class);

     private final CachedSessionDatasource sessionDataSource;

     public ReplicatedTimerFacilityImpl(IContainer container)
     {
         super();
         ISessionDatasource datasource = container.getAssemblerFacility().getComponentInstance(ISessionDatasource.class);
         if (datasource instanceof CachedSessionDatasource cachedSessionDatasource) {
             this.sessionDataSource = cachedSessionDatasource;
         } else {
             throw new IllegalArgumentException("ReplicatedTimerFacilityImpl expects an ISessionDatasource of type 'CachedSessionDatasource' and is not compatible with " + datasource.getClass().getName());
         }
     }

     /*
      * (non-Javadoc)
      *
      * @see org.jdiameter.common.api.timer.ITimerFacility#cancel(java.io.Serializable)
      */
     @Override
     public void cancel(Serializable id)
     {
         logger.debug("Cancelling timer with id {}", id);
         //Nothing to do here?? Maybe we should just clear the expiry time. For now we leave it
     }

     /*
      * (non-Javadoc)
      *
      * @see org.jdiameter.common.api.timer.ITimerFacility#schedule(java.lang.String, java.lang.String, long)
      */
     @Override
     public Serializable schedule(String sessionId, String timerName, long milliseconds) throws IllegalArgumentException
     {
         logger.debug("Scheduling timer with sessionId {}", sessionId);

         sessionDataSource.setExpiryTime(sessionId, milliseconds);

         return sessionId;
     }
 }
