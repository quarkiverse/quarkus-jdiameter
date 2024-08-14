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
import org.jdiameter.api.acc.ClientAccSession;
import org.jdiameter.api.acc.ClientAccSessionListener;
import org.jdiameter.api.acc.ServerAccSession;
import org.jdiameter.api.acc.ServerAccSessionListener;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.auth.ClientAuthSession;
import org.jdiameter.api.auth.ClientAuthSessionListener;
import org.jdiameter.api.auth.ServerAuthSession;
import org.jdiameter.api.auth.ServerAuthSessionListener;
import org.jdiameter.api.cca.ClientCCASession;
import org.jdiameter.api.cca.ClientCCASessionListener;
import org.jdiameter.api.cca.ServerCCASession;
import org.jdiameter.api.cca.ServerCCASessionListener;
import org.jdiameter.api.cxdx.ClientCxDxSession;
import org.jdiameter.api.cxdx.ClientCxDxSessionListener;
import org.jdiameter.api.cxdx.ServerCxDxSession;
import org.jdiameter.api.cxdx.ServerCxDxSessionListener;
import org.jdiameter.api.gq.ClientGqSessionListener;
import org.jdiameter.api.gq.ServerGqSessionListener;
import org.jdiameter.api.gx.ClientGxSession;
import org.jdiameter.api.gx.ClientGxSessionListener;
import org.jdiameter.api.gx.ServerGxSession;
import org.jdiameter.api.gx.ServerGxSessionListener;
import org.jdiameter.api.rf.ClientRfSession;
import org.jdiameter.api.rf.ClientRfSessionListener;
import org.jdiameter.api.rf.ServerRfSession;
import org.jdiameter.api.rf.ServerRfSessionListener;
import org.jdiameter.api.ro.ClientRoSession;
import org.jdiameter.api.ro.ClientRoSessionListener;
import org.jdiameter.api.ro.ServerRoSession;
import org.jdiameter.api.ro.ServerRoSessionListener;
import org.jdiameter.api.rx.ClientRxSession;
import org.jdiameter.api.rx.ClientRxSessionListener;
import org.jdiameter.api.rx.ServerRxSession;
import org.jdiameter.api.rx.ServerRxSessionListener;
import org.jdiameter.api.s13.ClientS13Session;
import org.jdiameter.api.s13.ClientS13SessionListener;
import org.jdiameter.api.s13.ServerS13Session;
import org.jdiameter.api.s13.ServerS13SessionListener;
import org.jdiameter.api.s6a.ClientS6aSession;
import org.jdiameter.api.s6a.ClientS6aSessionListener;
import org.jdiameter.api.s6a.ServerS6aSession;
import org.jdiameter.api.s6a.ServerS6aSessionListener;
import org.jdiameter.api.sh.ClientShSession;
import org.jdiameter.api.sh.ClientShSessionListener;
import org.jdiameter.api.sh.ServerShSession;
import org.jdiameter.api.sh.ServerShSessionListener;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.impl.app.acc.AccSessionFactoryImpl;
import org.jdiameter.common.impl.app.auth.AuthSessionFactoryImpl;
import org.jdiameter.common.impl.app.cca.CCASessionFactoryImpl;
import org.jdiameter.common.impl.app.cxdx.CxDxSessionFactoryImpl;
import org.jdiameter.common.impl.app.gq.GqSessionFactoryImpl;
import org.jdiameter.common.impl.app.gx.GxSessionFactoryImpl;
import org.jdiameter.common.impl.app.rf.RfSessionFactoryImpl;
import org.jdiameter.common.impl.app.ro.RoSessionFactoryImpl;
import org.jdiameter.common.impl.app.rx.RxSessionFactoryImpl;
import org.jdiameter.common.impl.app.s13.S13SessionFactoryImpl;
import org.jdiameter.common.impl.app.s6a.S6aSessionFactoryImpl;
import org.jdiameter.common.impl.app.sh.ShSessionFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;

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

    private void setupClientGxFactory(ISessionFactory sessionFactory, String configProfile, ClientGxSessionListener sessionListener)
    {
        LOG.info("Staring Gx Diameter Client Service [{}].", configProfile);
        GxSessionFactoryImpl sessionFactoryImpl = new GxSessionFactoryImpl(sessionFactory);
        sessionFactory.registerAppFacory(ClientGxSession.class, sessionFactoryImpl);
        sessionFactoryImpl.setClientSessionListener(sessionListener);
    }

    private void setupServerGxFactory(Stack stack, ISessionFactory sessionFactory, String configProfile, ServerGxSessionListener sessionListener) throws ApplicationAlreadyUseException, InternalException
    {
        LOG.info("Staring Gx Diameter Server Service [{}].", configProfile);
        GxSessionFactoryImpl sessionFactoryImpl = new GxSessionFactoryImpl(sessionFactory);
        sessionFactory.registerAppFacory(ServerGxSession.class, sessionFactoryImpl);
        sessionFactoryImpl.setServerSessionListener(sessionListener);
        setupDiameterServer(stack, sessionFactory, ServerGxSession.class, sessionListener, configProfile);
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

    private void clientSetupShFactory(ISessionFactory sessionFactory, String configProfile, ClientShSessionListener listener)
    {
        LOG.info("Staring Sh Diameter Client Service [{}].", configProfile);
        ShSessionFactoryImpl sessionFactoryImpl = new ShSessionFactoryImpl(sessionFactory);
        sessionFactory.registerAppFacory(ClientShSession.class, sessionFactoryImpl);
        sessionFactoryImpl.setClientShSessionListener(listener);
    }

    private void serverSetupShFactory(Stack stack, ISessionFactory sessionFactory, String configProfile, ServerShSessionListener listener) throws ApplicationAlreadyUseException, InternalException
    {
        LOG.info("Staring Sh Diameter Server Service [{}].", configProfile);
        ShSessionFactoryImpl sessionFactoryImpl = new ShSessionFactoryImpl(sessionFactory);
        sessionFactory.registerAppFacory(ServerShSession.class, sessionFactoryImpl);
        sessionFactoryImpl.setServerShSessionListener(listener);
        setupDiameterServer(stack, sessionFactory, ServerShSession.class, listener, configProfile);
    }

    private void clientSetupCxDxFactory(ISessionFactory sessionFactory, String configProfile, ClientCxDxSessionListener listener)
    {
        LOG.info("Staring CxDx Diameter Client Service [{}].", configProfile);
        CxDxSessionFactoryImpl sessionFactoryImpl = new CxDxSessionFactoryImpl(sessionFactory);
        sessionFactory.registerAppFacory(ClientCxDxSession.class, sessionFactoryImpl);
        sessionFactoryImpl.setClientSessionListener(listener);
    }

    private void serverSetupCxDxFactory(Stack stack, ISessionFactory sessionFactory, String configProfile, ServerCxDxSessionListener listener) throws ApplicationAlreadyUseException, InternalException
    {
        LOG.info("Staring CxDx Diameter Server Service [{}].", configProfile);
        CxDxSessionFactoryImpl sessionFactoryImpl = new CxDxSessionFactoryImpl(sessionFactory);
        sessionFactory.registerAppFacory(ServerCxDxSession.class, sessionFactoryImpl);
        sessionFactoryImpl.setServerSessionListener(listener);
        setupDiameterServer(stack, sessionFactory, ServerShSession.class, listener, configProfile);
    }

    private void clientSetupS13Factory(ISessionFactory sessionFactory, String configProfile, ClientS13SessionListener listener)
    {
        LOG.info("Staring S13 Diameter Client Service [{}].", configProfile);
        S13SessionFactoryImpl sessionFactoryImpl = new S13SessionFactoryImpl(sessionFactory);
        sessionFactory.registerAppFacory(ClientS13Session.class, sessionFactoryImpl);
        sessionFactoryImpl.setClientSessionListener(listener);
    }

    private void serverSetupS13Factory(Stack stack, ISessionFactory sessionFactory, String configProfile, ServerS13SessionListener listener) throws ApplicationAlreadyUseException, InternalException
    {
        LOG.info("Staring S13 Diameter Server Service [{}].", configProfile);
        S13SessionFactoryImpl sessionFactoryImpl = new S13SessionFactoryImpl(sessionFactory);
        sessionFactory.registerAppFacory(ServerS13Session.class, sessionFactoryImpl);
        sessionFactoryImpl.setServerSessionListener(listener);
        setupDiameterServer(stack, sessionFactory, ServerS13Session.class, listener, configProfile);
    }

    private void clientSetupRoFactory(ISessionFactory sessionFactory, String configProfile, ClientRoSessionListener listener)
    {
        LOG.info("Staring Ro Diameter Client Service [{}].", configProfile);
        RoSessionFactoryImpl sessionFactoryImpl = new RoSessionFactoryImpl(sessionFactory);
        sessionFactory.registerAppFacory(ClientRoSession.class, sessionFactoryImpl);
        sessionFactoryImpl.setClientSessionListener(listener);
    }

    private void serverSetupRoFactory(Stack stack, ISessionFactory sessionFactory, String configProfile, ServerRoSessionListener listener) throws ApplicationAlreadyUseException, InternalException
    {
        LOG.info("Staring Ro Diameter Server Service [{}].", configProfile);
        RoSessionFactoryImpl sessionFactoryImpl = new RoSessionFactoryImpl(sessionFactory);
        sessionFactory.registerAppFacory(ServerRoSession.class, sessionFactoryImpl);
        sessionFactoryImpl.setServerSessionListener(listener);
        setupDiameterServer(stack, sessionFactory, ServerRoSession.class, listener, configProfile);
    }

    private void clientSetupRfFactory(ISessionFactory sessionFactory, String configProfile, ClientRfSessionListener listener)
    {
        LOG.info("Staring Rf Diameter Client Service [{}].", configProfile);
        RfSessionFactoryImpl sessionFactoryImpl = new RfSessionFactoryImpl(sessionFactory);
        sessionFactory.registerAppFacory(ClientRfSession.class, sessionFactoryImpl);
        sessionFactoryImpl.setClientSessionListener(listener);
    }

    private void serverSetupRfFactory(Stack stack, ISessionFactory sessionFactory, String configProfile, ServerRfSessionListener listener) throws ApplicationAlreadyUseException, InternalException
    {
        LOG.info("Staring Rf Diameter Server Service [{}].", configProfile);
        RfSessionFactoryImpl sessionFactoryImpl = new RfSessionFactoryImpl(sessionFactory);
        sessionFactory.registerAppFacory(ServerRfSession.class, sessionFactoryImpl);
        sessionFactoryImpl.setServerSessionListener(listener);
        setupDiameterServer(stack, sessionFactory, ServerRfSession.class, listener, configProfile);
    }

    private void clientSetupAuthFactory(ISessionFactory sessionFactory, String configProfile, ClientAuthSessionListener listener)
    {
        LOG.info("Staring Auth Diameter Client Service [{}].", configProfile);
        AuthSessionFactoryImpl sessionFactoryImpl = new AuthSessionFactoryImpl(sessionFactory);
        sessionFactory.registerAppFacory(ClientAuthSession.class, sessionFactoryImpl);
        sessionFactoryImpl.setClientSessionListener(listener);
    }

    private void serverSetupAuthFactory(Stack stack, ISessionFactory sessionFactory, String configProfile, ServerAuthSessionListener listener) throws ApplicationAlreadyUseException, InternalException
    {
        LOG.info("Staring Auth Diameter Server Service [{}].", configProfile);
        AuthSessionFactoryImpl sessionFactoryImpl = new AuthSessionFactoryImpl(sessionFactory);
        sessionFactory.registerAppFacory(ServerAuthSession.class, sessionFactoryImpl);
        sessionFactoryImpl.setServerSessionListener(listener);
        setupDiameterServer(stack, sessionFactory, ServerAuthSession.class, listener, configProfile);
    }

    private void clientSetupAccFactory(ISessionFactory sessionFactory, String configProfile, ClientAccSessionListener listener)
    {
        LOG.info("Staring Acc Diameter Client Service [{}].", configProfile);
        AccSessionFactoryImpl sessionFactoryImpl = new AccSessionFactoryImpl(sessionFactory);
        sessionFactory.registerAppFacory(ClientAccSession.class, sessionFactoryImpl);
        sessionFactoryImpl.setClientSessionListener(listener);
    }

    private void serverSetupAccFactory(Stack stack, ISessionFactory sessionFactory, String configProfile, ServerAccSessionListener listener) throws ApplicationAlreadyUseException, InternalException
    {
        LOG.info("Staring Auth Diameter Server Service [{}].", configProfile);
        AccSessionFactoryImpl sessionFactoryImpl = new AccSessionFactoryImpl(sessionFactory);
        sessionFactory.registerAppFacory(ServerAccSession.class, sessionFactoryImpl);
        sessionFactoryImpl.setServerSessionListener(listener);
        setupDiameterServer(stack, sessionFactory, ServerAccSession.class, listener, configProfile);
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

                if (context.getTarget() instanceof ClientGxSessionListener listener) {
                    setupClientGxFactory(sessionFactory, configProfile, listener);
                }

                if (context.getTarget() instanceof ServerGxSessionListener listener) {
                    setupServerGxFactory(stack, sessionFactory, configProfile, listener);
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

                if (context.getTarget() instanceof ClientShSessionListener listener) {
                    clientSetupShFactory(sessionFactory, configProfile, listener);
                }

                if (context.getTarget() instanceof ServerShSessionListener listener) {
                    serverSetupShFactory(stack, sessionFactory, configProfile, listener);
                }

                if (context.getTarget() instanceof ClientCxDxSessionListener listener) {
                    clientSetupCxDxFactory(sessionFactory, configProfile, listener);
                }

                if (context.getTarget() instanceof ServerCxDxSessionListener listener) {
                    serverSetupCxDxFactory(stack, sessionFactory, configProfile, listener);
                }

                if (context.getTarget() instanceof ClientS13SessionListener listener) {
                    clientSetupS13Factory(sessionFactory, configProfile, listener);
                }

                if (context.getTarget() instanceof ServerS13SessionListener listener) {
                    serverSetupS13Factory(stack, sessionFactory, configProfile, listener);
                }

                if (context.getTarget() instanceof ClientRoSessionListener listener) {
                    clientSetupRoFactory(sessionFactory, configProfile, listener);
                }

                if (context.getTarget() instanceof ServerRoSessionListener listener) {
                    serverSetupRoFactory(stack, sessionFactory, configProfile, listener);
                }

                if (context.getTarget() instanceof ClientRfSessionListener listener) {
                    clientSetupRfFactory(sessionFactory, configProfile, listener);
                }

                if (context.getTarget() instanceof ServerRfSessionListener listener) {
                    serverSetupRfFactory(stack, sessionFactory, configProfile, listener);
                }

                if (context.getTarget() instanceof ClientAuthSessionListener listener) {
                    clientSetupAuthFactory(sessionFactory, configProfile, listener);
                }

                if (context.getTarget() instanceof ServerAuthSessionListener listener) {
                    serverSetupAuthFactory(stack, sessionFactory, configProfile, listener);
                }

                if (context.getTarget() instanceof ClientAccSessionListener listener) {
                    clientSetupAccFactory(sessionFactory, configProfile, listener);
                }

                if (context.getTarget() instanceof ServerAccSessionListener listener) {
                    serverSetupAccFactory(stack, sessionFactory, configProfile, listener);
                }

                if (!stack.isActive()) {
                    LOG.debug("Starting the Diameter Stack [{}].", configProfile);
                    if (options != null && options.timeOut() > 0) {
                        stack.start(Mode.ALL_PEERS, options.timeOut(), options.timeUnit());
                    } else {
                        stack.start();
                    }
                } else {
                    LOG.debug("The Diameter Stack is already started [{}].", configProfile);
                }
            }
        }

        return context.proceed();
    }
}
