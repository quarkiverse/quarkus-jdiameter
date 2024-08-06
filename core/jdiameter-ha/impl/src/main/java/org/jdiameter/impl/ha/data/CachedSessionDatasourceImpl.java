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

 package org.jdiameter.impl.ha.data;

 import com.fasterxml.jackson.core.JsonProcessingException;
 import com.fasterxml.jackson.databind.DeserializationFeature;
 import com.fasterxml.jackson.databind.ObjectMapper;
 import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
 import io.quarkus.arc.Arc;
 import io.quarkus.arc.InstanceHandle;
 import org.infinispan.client.hotrod.RemoteCache;
 import org.infinispan.client.hotrod.RemoteCacheManager;
 import org.jdiameter.api.BaseSession;
 import org.jdiameter.api.IllegalDiameterStateException;
 import org.jdiameter.api.NetworkReqListener;
 import org.jdiameter.api.app.AppSession;
 import org.jdiameter.api.ha.data.CachingException;
 import org.jdiameter.client.api.IContainer;
 import org.jdiameter.client.api.ISessionFactory;
 import org.jdiameter.common.api.app.IAppSessionData;
 import org.jdiameter.common.api.app.IAppSessionDataFactory;
 import org.jdiameter.common.api.app.IAppSessionFactory;
 import org.jdiameter.common.api.app.acc.IAccSessionData;
 import org.jdiameter.common.api.app.auth.IAuthSessionData;
 import org.jdiameter.common.api.app.cca.ICCASessionData;
 import org.jdiameter.common.api.app.cxdx.ICxDxSessionData;
 import org.jdiameter.common.api.app.gx.IGxSessionData;
 import org.jdiameter.common.api.app.rf.IRfSessionData;
 import org.jdiameter.common.api.app.ro.IRoSessionData;
 import org.jdiameter.common.api.app.rx.IRxSessionData;
 import org.jdiameter.common.api.app.s13.IS13SessionData;
 import org.jdiameter.common.api.app.sh.IShSessionData;
 import org.jdiameter.common.api.data.ISessionDatasource;
 import org.jdiameter.common.impl.data.LocalDataSource;
 import org.jdiameter.impl.ha.common.AppSessionDataReplicatedImpl;
 import org.jdiameter.impl.ha.common.acc.AccReplicatedSessionDataFactory;
 import org.jdiameter.impl.ha.common.auth.AuthReplicatedSessionDataFactory;
 import org.jdiameter.impl.ha.common.cca.CCAReplicatedSessionDataFactory;
 import org.jdiameter.impl.ha.common.cxdx.CxDxReplicatedSessionDataFactory;
 import org.jdiameter.impl.ha.common.gx.GxReplicatedSessionDataFactory;
 import org.jdiameter.impl.ha.common.rf.RfReplicatedSessionDataFactory;
 import org.jdiameter.impl.ha.common.ro.RoReplicatedSessionDataFactory;
 import org.jdiameter.impl.ha.common.rx.RxReplicatedSessionDataFactory;
 import org.jdiameter.impl.ha.common.s13.S13ReplicatedSessionDataFactory;
 import org.jdiameter.impl.ha.common.sh.ShReplicatedSessionDataFactory;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 import java.util.HashMap;
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.ExecutorService;
 import java.util.concurrent.Executors;
 import java.util.concurrent.TimeUnit;

 import static org.jdiameter.server.impl.helpers.Parameters.CachingName;

 /**
  * Replicated datasource implementation for {@link ISessionDatasource}
  *
  * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
  * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
  */
 public class CachedSessionDatasourceImpl implements ISessionDatasource, CachedSessionDatasource
 {
     private static final Logger logger = LoggerFactory.getLogger(CachedSessionDatasourceImpl.class);
     private final IContainer container;
     private final ISessionDatasource localDataSource;
     private final RemoteCache<String, String> dataSessions;
     private final ObjectMapper mapper;

     private final ConcurrentHashMap<String, Map<String, Object>> sessionData = new ConcurrentHashMap<>();
     ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

     // provided by impl, no way to change that, no conf! :)
     protected HashMap<Class<? extends IAppSessionData>, IAppSessionDataFactory<? extends IAppSessionData>> appSessionDataFactories =
             new HashMap<>();


     public CachedSessionDatasourceImpl(IContainer container)
     {
         this.localDataSource = new LocalDataSource(container);

         String cachingName = container.getConfiguration().getStringValue(CachingName.ordinal(), (String) CachingName.defValue());

         InstanceHandle<RemoteCacheManager> vInstanceHandle = Arc.container().instance(RemoteCacheManager.class);
         if (!vInstanceHandle.isAvailable()) {
             throw new RuntimeException("Infinispan is not loaded");
         }//if

         RemoteCacheManager vRemoteCacheManager = vInstanceHandle.get();
         dataSessions = vRemoteCacheManager.getCache(cachingName);

         mapper = new ObjectMapper();
         mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
         mapper.registerModule(new JavaTimeModule());

         this.container = container;
         // this is coded, its tied to specific impl of SessionDatasource
         appSessionDataFactories.put(IAuthSessionData.class, new AuthReplicatedSessionDataFactory(this));
         appSessionDataFactories.put(IAccSessionData.class, new AccReplicatedSessionDataFactory(this));
         appSessionDataFactories.put(ICCASessionData.class, new CCAReplicatedSessionDataFactory(this));
         appSessionDataFactories.put(IRoSessionData.class, new RoReplicatedSessionDataFactory(this));
         appSessionDataFactories.put(IRfSessionData.class, new RfReplicatedSessionDataFactory(this));
         appSessionDataFactories.put(IShSessionData.class, new ShReplicatedSessionDataFactory(this));
         appSessionDataFactories.put(ICxDxSessionData.class, new CxDxReplicatedSessionDataFactory(this));
         appSessionDataFactories.put(IGxSessionData.class, new GxReplicatedSessionDataFactory(this));
         appSessionDataFactories.put(IRxSessionData.class, new RxReplicatedSessionDataFactory(this));
         appSessionDataFactories.put(IS13SessionData.class, new S13ReplicatedSessionDataFactory(this));

     }

     @Override
     public boolean exists(String sessionId)
     {
         return this.localDataSource.exists(sessionId) || this.existReplicated(sessionId);
     }

     /*
      * (non-Javadoc)
      *
      * @see org.jdiameter.common.api.ha.ISessionDatasource#addSession(org.jdiameter .api.BaseSession)
      */
     @Override
     public void addSession(BaseSession session)
     {
         // Simple as is, if its replicated, it will be already there :)
         this.localDataSource.addSession(session);

         //Clear the local cache so that it can be refreshed
         sessionData.remove(session.getSessionId());
     }

     /*
      * (non-Javadoc)
      *
      * @see org.jdiameter.common.api.ha.ISessionDatasource#getSession(java.lang.String )
      */
     @Override
     public BaseSession getSession(String sessionId)
     {
         if (this.localDataSource.exists(sessionId)) {
             return this.localDataSource.getSession(sessionId);
         } else if (this.existReplicated(sessionId)) {
             this.makeLocal(sessionId);
             return this.localDataSource.getSession(sessionId);
         }

         return null;
     }

     /*
      * (non-Javadoc)
      *
      * @see org.jdiameter.common.api.ha.ISessionDatasource#getSessionListener(java .lang.String)
      */
     @Override
     public NetworkReqListener getSessionListener(String sessionId)
     {
         if (this.localDataSource.exists(sessionId)) {
             return this.localDataSource.getSessionListener(sessionId);
         } else if (this.existReplicated(sessionId)) {
             this.makeLocal(sessionId);
             return this.localDataSource.getSessionListener(sessionId);
         }

         return null;
     }

     /*
      * (non-Javadoc)
      *
      * @see org.jdiameter.common.api.ha.ISessionDatasource#removeSession(java.lang .String)
      */
     @Override
     public void removeSession(String sessionId)
     {
         logger.debug("removeSession({}) in Local DataSource", sessionId);

         if (this.localDataSource.exists(sessionId)) {
             this.localDataSource.removeSession(sessionId);
         }

         if (this.existReplicated(sessionId)) {
             dataSessions.remove(sessionId);
             sessionData.remove(sessionId);
         }
     }

     /*
      * (non-Javadoc)
      *
      * @see org.jdiameter.common.api.ha.ISessionDatasource#removeSessionListener( java.lang.String)
      */
     @Override
     public NetworkReqListener removeSessionListener(String sessionId)
     {
         if (this.localDataSource.exists(sessionId)) {
             return this.localDataSource.removeSessionListener(sessionId);
         }
         return null;
     }

     /*
      * (non-Javadoc)
      *
      * @see org.jdiameter.common.api.ha.ISessionDatasource#setSessionListener(java .lang.String, org.jdiameter.api.NetworkReqListener)
      */
     @Override
     public void setSessionListener(String sessionId, NetworkReqListener data)
     {
         if (this.localDataSource.exists(sessionId)) {
             this.localDataSource.setSessionListener(sessionId, data);
         } else if (this.existReplicated(sessionId)) {
             this.makeLocal(sessionId);
             this.localDataSource.setSessionListener(sessionId, data);
         }
     }

     @Override
     public void start()
     {
         //NOOP
     }

     @Override
     public void stop()
     {
         //NOOP
     }

     /*
      * (non-Javadoc)
      *
      * @see org.jdiameter.common.api.data.ISessionDatasource#isClustered()
      */
     @Override
     public boolean isClustered()
     {
         return true;
     }

     /*
      * (non-Javadoc)
      *
      * @see org.jdiameter.common.api.data.ISessionDatasource#getDataFactory(java. lang.Class)
      */
     @Override
     public IAppSessionDataFactory<? extends IAppSessionData> getDataFactory(Class<? extends IAppSessionData> x)
     {
         return this.appSessionDataFactories.get(x);
     }

     private boolean existReplicated(String sessionId)
     {
         return sessionData.get(sessionId) != null;
     }

     @SuppressWarnings("unchecked")
     private void makeLocal(String sessionId)
     {
         try {
             // this is APP session, always
             String className = getFieldValue(sessionId, AppSessionDataReplicatedImpl.SIFACE);
             Class<? extends AppSession> appSessionInterfaceClass = (Class<? extends AppSession>) Thread.currentThread().getContextClassLoader().loadClass(className);

             // get factory;
             IAppSessionFactory fct = ((ISessionFactory) this.container.getSessionFactory()).getAppSessionFactory(appSessionInterfaceClass);
             if (fct == null) {
                 logger.warn("Session with id:{}, is in replicated data source, but no Application Session Factory for:{}.", sessionId, appSessionInterfaceClass);
             } else {
                 //Get the local cache to refresh
                 sessionData.remove(sessionId);

                 BaseSession session = fct.getSession(sessionId, appSessionInterfaceClass);
                 this.localDataSource.addSession(session);
                 // hmmm
                 this.localDataSource.setSessionListener(sessionId, (NetworkReqListener) session);
             }
         }
         catch (IllegalDiameterStateException | ClassNotFoundException e) {
             if (logger.isErrorEnabled()) {
                 logger.error("Failed to obtain factory from stack...");
             }
         }
     }

     @Override
     public IContainer getContainer()
     {
         return this.container;
     }


     @SuppressWarnings("unchecked")
     private Map<String, Object> getFieldValues(String sessionId)
     {
         logger.debug("{}: Loading field values", sessionId);
         try {
             Map<String, Object> values = sessionData.get(sessionId);
             if (values == null) {
                 logger.warn("{}: Not in local cache, checking remote cache", sessionId);
                 String jsonValues = dataSessions.get(sessionId);
                 if (jsonValues == null) {
                     values = new HashMap<>();
                 } else {
                     values = mapper.readValue(jsonValues, Map.class);
                 }

                 sessionData.put(sessionId, values);
             }
             logger.debug("{}: Loaded field values: {}", sessionId, values);
             return values;
         }
         catch (JsonProcessingException e) {
             throw new CachingException("Error setting field values", e);
         }
     }//getFieldValues

     private void setFieldValues(String sessionId, Map<String, Object> fieldValues)
     {
         logger.debug("{}: Saving the field values (to map): {}", sessionId, fieldValues);

         sessionData.put(sessionId, fieldValues);

         executorService.submit(() -> {
             try {
                 dataSessions.put(sessionId, mapper.writeValueAsString(fieldValues));
             }
             catch (JsonProcessingException e) {
                 throw new CachingException("Error setting field values", e);
             }
         });
     }//setFieldValues

     @Override
     @SuppressWarnings("unchecked")
     public <T> T getFieldValue(String sessionId, String fieldName)
     {
         return (T) getFieldValues(sessionId).get(fieldName);
     }//getFieldValue


     @Override
     public void setFieldValue(String sessionId, String fieldName, Object value)
     {
         logger.debug("{}: Setting field {} = {}", sessionId, fieldName, value);

         Map<String, Object> values = getFieldValues(sessionId);
         values.put(fieldName, value);

         setFieldValues(sessionId, values);
     }//setFieldValue

     @Override
     public void setExpiryTime(String sessionId, long expiryTime)
     {
         String value = dataSessions.get(sessionId);
         if (value != null) {
             dataSessions.replaceAsync(sessionId, value, -1, TimeUnit.SECONDS, expiryTime, TimeUnit.MILLISECONDS);
         }
     }
 }
