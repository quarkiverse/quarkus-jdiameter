/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.quarkiverse.diameter;

import io.quarkus.arc.Arc;
import io.quarkus.arc.InstanceHandle;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Priority;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.jdiameter.api.*;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.auth.ClientAuthSession;
import org.jdiameter.api.auth.ServerAuthSession;
import org.jdiameter.api.cca.ClientCCASession;
import org.jdiameter.api.cca.ClientCCASessionListener;
import org.jdiameter.api.cca.ServerCCASession;
import org.jdiameter.api.cca.ServerCCASessionListener;
import org.jdiameter.api.gq.ClientGqSessionListener;
import org.jdiameter.api.gq.ServerGqSessionListener;
import org.jdiameter.api.rx.ClientRxSession;
import org.jdiameter.api.rx.ClientRxSessionListener;
import org.jdiameter.api.rx.ServerRxSession;
import org.jdiameter.api.rx.ServerRxSessionListener;
import org.jdiameter.api.s6a.ClientS6aSession;
import org.jdiameter.api.s6a.ClientS6aSessionListener;
import org.jdiameter.api.s6a.ServerS6aSession;
import org.jdiameter.api.s6a.ServerS6aSessionListener;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.impl.app.cca.CCASessionFactoryImpl;
import org.jdiameter.common.impl.app.gq.GqSessionFactoryImpl;
import org.jdiameter.common.impl.app.rx.RxSessionFactoryImpl;
import org.jdiameter.common.impl.app.s6a.S6aSessionFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@DiameterService
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class DiameterServiceInterceptor
{
    private static final Logger LOG = LoggerFactory.getLogger(DiameterServiceInterceptor.class);

    private void setupClientCAAFactory(ISessionFactory sessionFactory, String configProfile, ClientCCASessionListener sessionListener)
    {
        LOG.info("Staring CAA Diameter Client Service [{}].", configProfile);
        CCASessionFactoryImpl sessionFactoryImpl = new CCASessionFactoryImpl(sessionFactory);
        sessionFactory.registerAppFacory(ClientCCASession.class, sessionFactoryImpl);
        sessionFactoryImpl.setClientSessionListener(sessionListener);
    }

    private void setupServerCAAFactory(Stack stack, ISessionFactory sessionFactory, String configProfile, ServerCCASessionListener sessionListener) throws ApplicationAlreadyUseException, InternalException
    {
        LOG.info("Staring CAA Diameter Server Service [{}].", configProfile);
        CCASessionFactoryImpl sessionFactoryImpl = new CCASessionFactoryImpl(sessionFactory);
        sessionFactory.registerAppFacory(ServerCCASession.class, sessionFactoryImpl);
        sessionFactoryImpl.setServerSessionListener(sessionListener);
        setupDiameterServer(stack, sessionFactory, ServerCCASession.class, sessionListener, configProfile);
    }

    private void clientSetupRxFactory(ISessionFactory sessionFactory, String configProfile, ClientRxSessionListener listener)
    {
        LOG.info("Staring Rx Diameter Client Service [{}].", configProfile);
        RxSessionFactoryImpl sessionFactoryImpl = new RxSessionFactoryImpl(sessionFactory);
        sessionFactory.registerAppFacory(ClientRxSession.class, sessionFactoryImpl);
        sessionFactoryImpl.setClientSessionListener(listener);
    }

    private void serverSetupRxFactory(Stack stack, ISessionFactory sessionFactory, String configProfile, ServerRxSessionListener listener) throws ApplicationAlreadyUseException, InternalException
    {
        LOG.info("Staring Rx Diameter Server Service [{}].", configProfile);
        RxSessionFactoryImpl sessionFactoryImpl = new RxSessionFactoryImpl(sessionFactory);
        sessionFactory.registerAppFacory(ServerRxSession.class, sessionFactoryImpl);
        sessionFactoryImpl.setServerSessionListener(listener);
        setupDiameterServer(stack, sessionFactory, ServerRxSession.class, listener, configProfile);
    }


    private void clientSetupS6aFactory(ISessionFactory sessionFactory, String configProfile, ClientS6aSessionListener listener)
    {
        LOG.info("Staring S6a Diameter Client Service [{}].", configProfile);
        S6aSessionFactoryImpl sessionFactoryImpl = new S6aSessionFactoryImpl(sessionFactory);
        sessionFactory.registerAppFacory(ClientS6aSession.class, sessionFactoryImpl);
        sessionFactoryImpl.setClientSessionListener(listener);
    }

    private void serverSetupS6aFactory(Stack stack, ISessionFactory sessionFactory, String configProfile, ServerS6aSessionListener listener) throws ApplicationAlreadyUseException, InternalException
    {
        LOG.info("Staring S6a Diameter Server Service [{}].", configProfile);
        S6aSessionFactoryImpl sessionFactoryImpl = new S6aSessionFactoryImpl(sessionFactory);
        sessionFactory.registerAppFacory(ServerS6aSession.class, sessionFactoryImpl);
        sessionFactoryImpl.setServerSessionListener(listener);
        setupDiameterServer(stack, sessionFactory, ServerS6aSession.class, listener, configProfile);
    }

    private void clientSetupGqFactory(ISessionFactory sessionFactory, String configProfile, ClientGqSessionListener listener)
    {
        LOG.info("Staring Gq Diameter Client Service [{}].", configProfile);
        GqSessionFactoryImpl sessionFactoryImpl = new GqSessionFactoryImpl(sessionFactory);
        sessionFactory.registerAppFacory(ClientAuthSession.class, sessionFactoryImpl);
        sessionFactoryImpl.setClientSessionListener(listener);
    }

    private void serverSetupGqFactory(Stack stack, ISessionFactory sessionFactory, String configProfile, ServerGqSessionListener listener) throws ApplicationAlreadyUseException, InternalException
    {
        LOG.info("Staring Gq Diameter Server Service [{}].", configProfile);
        GqSessionFactoryImpl sessionFactoryImpl = new GqSessionFactoryImpl(sessionFactory);
        sessionFactory.registerAppFacory(ServerAuthSession.class, sessionFactoryImpl);
        sessionFactoryImpl.setServerSessionListener(listener);
        setupDiameterServer(stack, sessionFactory, ServerAuthSession.class, listener, configProfile);
    }

    private NetworkReqListener createListener(ISessionFactory sessionFactory, Class<? extends AppSession> appSession)
    {
        return request -> {
            try {
                ApplicationId appId = request.getApplicationIdAvps().isEmpty() ? null : request.getApplicationIdAvps().getFirst();
                LOG.debug("Received request for {} and {}", appSession.getSimpleName(), appId);
                NetworkReqListener listener = sessionFactory.getNewAppSession(request.getSessionId(), appId, appSession, Collections.emptyList());
                return listener.processRequest(request);
            }
            catch (InternalException e) {
                LOG.error(">< Failure handling received request.", e);
            }

            return null;
        };
    }

    private void setupDiameterServer(Stack stack, ISessionFactory sessionFactory, Class<? extends AppSession> appSessionClass, Object target, String configProfile) throws ApplicationAlreadyUseException, InternalException
    {
        NetworkReqListener networkReqListener;
        //Check if the target implements their own listener. If they do, use their listener
        if ((target instanceof NetworkReqListener listener)) {
            networkReqListener = listener;
        } else {
            networkReqListener = createListener(sessionFactory, appSessionClass);
        }

        Network network = stack.unwrap(Network.class);
        Set<ApplicationId> applIds = stack.getMetaData().getLocalPeer().getCommonApplications();
        if (!applIds.isEmpty()) {
            LOG.info("Diameter Server [{}]: Registering {} configured application(s)", configProfile, applIds.size());
            for (ApplicationId applId : applIds) {
                LOG.debug("Diameter Server [{}]: Registering {}", configProfile, applId);
                network.addNetworkReqListener(networkReqListener, applId);
            }
        }
    }

    @PostConstruct
    public Object startStack(InvocationContext context) throws Exception
    {
        String configProfile;
        DiameterServiceOptions options = context.getTarget().getClass().getAnnotation(DiameterServiceOptions.class);
        if (options == null) {
            configProfile = DiameterConfig.DEFAULT_CONFIG_NAME;
        } else {
            configProfile = options.value();
        }

        try (InstanceHandle<Stack> stackHandle = Arc.container().instance(Stack.class, new DiameterConfig.DiameterConfigLiteral(configProfile))) {
            if (stackHandle.isAvailable()) {
                Stack stack = stackHandle.get();

                ISessionFactory sessionFactory = (ISessionFactory) stack.getSessionFactory();

                if (context.getTarget() instanceof ClientCCASessionListener listener) {
                    setupClientCAAFactory(sessionFactory, configProfile, listener);
                }

                if (context.getTarget() instanceof ServerCCASessionListener listener) {
                    setupServerCAAFactory(stack, sessionFactory, configProfile, listener);
                }

                if (context.getTarget() instanceof ClientRxSessionListener listener) {
                    clientSetupRxFactory(sessionFactory, configProfile, listener);
                }

                if (context.getTarget() instanceof ServerRxSessionListener listener) {
                    serverSetupRxFactory(stack, sessionFactory, configProfile, listener);
                }

                if (context.getTarget() instanceof ClientS6aSessionListener listener) {
                    clientSetupS6aFactory(sessionFactory, configProfile, listener);
                }

                if (context.getTarget() instanceof ServerS6aSessionListener listener) {
                    serverSetupS6aFactory(stack, sessionFactory, configProfile, listener);
                }

                if (context.getTarget() instanceof ClientGqSessionListener listener) {
                    clientSetupGqFactory(sessionFactory, configProfile, listener);
                }

                if (context.getTarget() instanceof ServerGqSessionListener listener) {
                    serverSetupGqFactory(stack, sessionFactory, configProfile, listener);
                }

                if (!stack.isActive()) {
                    stack.start(Mode.ALL_PEERS, 30000, TimeUnit.MILLISECONDS);
                    LOG.debug("Starting the Diameter Stack [{}].", configProfile);
                } else {
                    LOG.debug("The Diameter Stack is already started [{}].", configProfile);
                }
            }
        }

        return context.proceed();
    }
}
