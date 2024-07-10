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

package io.go.diameter;

import io.go.diameter.runtime.DiameterSetupException;
import io.quarkus.arc.Arc;
import io.quarkus.arc.InstanceHandle;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Priority;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.jdiameter.api.*;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.auth.ClientAuthSession;
import org.jdiameter.api.auth.ClientAuthSessionListener;
import org.jdiameter.api.auth.ServerAuthSession;
import org.jdiameter.api.auth.ServerAuthSessionListener;
import org.jdiameter.api.cca.ClientCCASession;
import org.jdiameter.api.cca.ClientCCASessionListener;
import org.jdiameter.api.cca.ServerCCASession;
import org.jdiameter.api.cca.ServerCCASessionListener;
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
import org.jdiameter.server.impl.app.cca.ServerCCASessionImpl;
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

	private NetworkReqListener createListener(ISessionFactory sessionFactory, Class<? extends AppSession> appSession, ApplicationId applicationId)
	{
		return request -> {
			try {
				ServerCCASessionImpl session = sessionFactory.getNewAppSession(request.getSessionId(), applicationId, appSession, Collections.emptyList());
				return session.processRequest(request);
			}
			catch (InternalException e) {
				LOG.error(">< Failure handling received request.", e);
			}

			return null;
		};
	}

	private Class<? extends AppSession> setupCAAFactory(ISessionFactory sessionFactory, ApplicationMode mode, Object target)
	{
		CCASessionFactoryImpl sessionFactoryImpl = new CCASessionFactoryImpl(sessionFactory);

		if (mode == ApplicationMode.CLIENT) {
			sessionFactory.registerAppFacory(ClientCCASession.class, sessionFactoryImpl);
			if (target instanceof ClientCCASessionListener clientSessionListener) {
				sessionFactoryImpl.setClientSessionListener(clientSessionListener);
			}
			else {
				throw new DiameterSetupException("Missing implementation of ClientCCASessionListener");
			}
			return ClientCCASession.class;
		}
		else {
			sessionFactory.registerAppFacory(ServerCCASession.class, sessionFactoryImpl);
			if (target instanceof ServerCCASessionListener serverSessionListener) {
				sessionFactoryImpl.setServerSessionListener(serverSessionListener);
			}
			else {
				throw new DiameterSetupException("Missing implementation of ServerCCASessionListener");
			}
			return ServerCCASession.class;
		}
	}

	private Class<? extends AppSession> setupRxFactory(ISessionFactory sessionFactory, ApplicationMode mode, Object target)
	{
		RxSessionFactoryImpl sessionFactoryImpl = new RxSessionFactoryImpl(sessionFactory);

		if (mode == ApplicationMode.CLIENT) {
			sessionFactory.registerAppFacory(ClientRxSession.class, sessionFactoryImpl);
			if (target instanceof ClientRxSessionListener clientSessionListener) {
				sessionFactoryImpl.setClientSessionListener(clientSessionListener);
			}
			else {
				throw new DiameterSetupException("Missing implementation of ClientRxSessionListener");
			}
			return ClientRxSession.class;
		}
		else {
			sessionFactory.registerAppFacory(ServerRxSession.class, sessionFactoryImpl);
			if (target instanceof ServerRxSessionListener serverSessionListener) {
				sessionFactoryImpl.setServerSessionListener(serverSessionListener);
			}
			else {
				throw new DiameterSetupException("Missing implementation of ServerRxSessionListener");
			}
			return ServerRxSession.class;
		}
	}


	private Class<? extends AppSession> setupS6aFactory(ISessionFactory sessionFactory, ApplicationMode mode, Object target)
	{
		S6aSessionFactoryImpl sessionFactoryImpl = new S6aSessionFactoryImpl(sessionFactory);

		if (mode == ApplicationMode.CLIENT) {
			sessionFactory.registerAppFacory(ClientS6aSession.class, sessionFactoryImpl);
			if (target instanceof ClientS6aSessionListener clientSessionListener) {
				sessionFactoryImpl.setClientSessionListener(clientSessionListener);
			}
			else {
				throw new DiameterSetupException("Missing implementation of ClientS6aSessionListener");
			}
			return ClientS6aSession.class;
		}
		else {
			sessionFactory.registerAppFacory(ServerS6aSession.class, sessionFactoryImpl);
			if (target instanceof ServerS6aSessionListener serverSessionListener) {
				sessionFactoryImpl.setServerSessionListener(serverSessionListener);
			}
			else {
				throw new DiameterSetupException("Missing implementation of ServerS6aSessionListener");
			}
			return ServerS6aSession.class;
		}
	}

	private Class<? extends AppSession> setupGqFactory(ISessionFactory sessionFactory, ApplicationMode mode, Object target)
	{
		GqSessionFactoryImpl sessionFactoryImpl = new GqSessionFactoryImpl(sessionFactory);

		if (mode == ApplicationMode.CLIENT) {
			sessionFactory.registerAppFacory(ClientAuthSession.class, sessionFactoryImpl);
			if (target instanceof ClientAuthSessionListener clientSessionListener) {
				sessionFactoryImpl.setClientSessionListener(clientSessionListener);
			}
			else {
				throw new DiameterSetupException("Missing implementation of ClientAuthSessionListener");
			}
			return ClientAuthSession.class;
		}
		else {
			sessionFactory.registerAppFacory(ServerAuthSession.class, sessionFactoryImpl);
			if (target instanceof ServerAuthSessionListener serverSessionListener) {
				sessionFactoryImpl.setServerSessionListener(serverSessionListener);
			}
			else {
				throw new DiameterSetupException("Missing implementation of ServerAuthSessionListener");
			}
			return ServerAuthSession.class;
		}
	}

	@PostConstruct
	public Object startStack(InvocationContext context) throws Exception
	{
		DiameterServiceOptions options = context.getTarget().getClass().getAnnotation(DiameterServiceOptions.class);
		if (options == null) {
			throw new DiameterSetupException("@DiameterServiceOptions is required for @DeviceService");
		}

		try (InstanceHandle<Stack> stackHandle = Arc.container().instance(Stack.class, new DiameterConfig.DiameterConfigLiteral(options.value()))) {
			if (stackHandle.isAvailable()) {
				Stack stack = stackHandle.get();

				ISessionFactory sessionFactory = (ISessionFactory) stack.getSessionFactory();

				Class<? extends AppSession> appSessionClass = switch (options.application()) {
					//3GPP CCA Application
					case CCA -> setupCAAFactory(sessionFactory, options.mode(), context.getTarget());
					//3GPP Rx Application
					case RX -> setupRxFactory(sessionFactory, options.mode(), context.getTarget());
					//3GPP S6a Application
					case S6A -> setupS6aFactory(sessionFactory, options.mode(), context.getTarget());
					//3GPP Gq Application
					case GQ -> setupGqFactory(sessionFactory, options.mode(), context.getTarget());
				};

				NetworkReqListener networkReqListener;
				//Check if the target implements their own listener. If they do, use their listener
				if ((context.getTarget() instanceof NetworkReqListener listener)) {
					networkReqListener = listener;
				}
				else {
					ApplicationId id = options.vendorId() == ApplicationId.UNDEFINED_VALUE ?
							(options.authApplId() != ApplicationId.UNDEFINED_VALUE ? ApplicationId.createByAuthAppId(options.authApplId()) : ApplicationId.createByAccAppId(options.acctApplId()))
							: ApplicationId.createByAuthAppId(options.vendorId(), options.authApplId());
					networkReqListener = createListener(sessionFactory, appSessionClass, id);
				}

				Network network = stack.unwrap(Network.class);
				Set<ApplicationId> applIds = stack.getMetaData().getLocalPeer().getCommonApplications();
				for (ApplicationId applId : applIds) {
					LOG.info("Diameter Service '{}': Adding Listener for [{}].", options.value(), applId);
					network.addNetworkReqListener(networkReqListener, applId);
				}
				LOG.info("Diameter Service '{}': Supporting {} applications.", options.value(), applIds.size());

				stack.start(Mode.ALL_PEERS, 30000, TimeUnit.MILLISECONDS);
				LOG.info("Starting the '{}' Diameter Stack.", options.value());
			}
		}

		return context.proceed();
	}
}
