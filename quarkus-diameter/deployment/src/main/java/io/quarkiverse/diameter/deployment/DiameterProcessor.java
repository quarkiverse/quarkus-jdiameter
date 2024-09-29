package io.quarkiverse.diameter.deployment;

import io.quarkiverse.diameter.DiameterConfig;
import io.quarkiverse.diameter.DiameterService;
import io.quarkiverse.diameter.runtime.DiameterRecorder;
import io.quarkiverse.diameter.runtime.DiameterRunTimeConfig;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.AnnotationsTransformerBuildItem;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.arc.processor.DotNames;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.ServiceStartBuildItem;
import io.quarkus.deployment.builditem.ShutdownContextBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.Startup;
import io.quarkus.tls.TlsRegistryBuildItem;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.interceptor.Interceptor;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTransformation;
import org.jboss.jandex.DotName;
import org.jdiameter.api.Configuration;
import org.jdiameter.api.Stack;
import org.jdiameter.client.impl.SessionFactoryImpl;
import org.jdiameter.client.impl.controller.RealmTableImpl;
import org.jdiameter.client.impl.helpers.AssemblerImpl;
import org.jdiameter.client.impl.parser.MessageParser;
import org.jdiameter.client.impl.router.WeightedLeastConnectionsRouter;
import org.jdiameter.client.impl.router.WeightedRoundRobinRouter;
import org.jdiameter.client.impl.transport.tcp.TCPClientConnection;
import org.jdiameter.common.impl.concurrent.ConcurrentEntityFactory;
import org.jdiameter.common.impl.concurrent.ConcurrentFactory;
import org.jdiameter.common.impl.data.LocalDataSource;
import org.jdiameter.common.impl.statistic.StatisticManagerImpl;
import org.jdiameter.common.impl.statistic.StatisticProcessorImpl;
import org.jdiameter.common.impl.timer.LocalTimerFacilityImpl;
import org.jdiameter.common.impl.validation.DictionaryImpl;
import org.jdiameter.server.impl.*;
import org.jdiameter.server.impl.agent.AgentConfigurationImpl;
import org.jdiameter.server.impl.agent.ProxyAgentImpl;
import org.jdiameter.server.impl.agent.RedirectAgentImpl;
import org.jdiameter.server.impl.fsm.FsmFactoryImpl;
import org.jdiameter.server.impl.io.TransportLayerFactory;
import org.jdiameter.server.impl.io.tcp.NetworkGuard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static io.quarkus.deployment.annotations.ExecutionTime.RUNTIME_INIT;

class DiameterProcessor
{
	private static final Logger LOG = LoggerFactory.getLogger(DiameterProcessor.class);
	private static final String FEATURE = "diameter";
	private static final DotName DOTNAME_CONFIGURATION = DotName.createSimple("org.jdiameter.api.Configuration");
	private static final DotName DOTNAME_STACK = DotName.createSimple("org.jdiameter.api.Stack");
	private static final DotName DOTNAME_DIAMETER_SERVICE = DotName.createSimple("io.quarkiverse.diameter.DiameterService");
	private static final DotName DOTNAME_DIAMETER_SERVICE_OPTIONS = DotName.createSimple("io.quarkiverse.diameter.DiameterServiceOptions");
	private static final DotName DOTNAME_DIAMETER_CONFIG = DotName.createSimple("io.quarkiverse.diameter.DiameterConfig");
	private static final DotName DOTNAME_DIAMETER_SERVICE_INTERCEPTOR = DotName.createSimple("io.quarkiverse.diameter.DiameterServiceInterceptor");

	@BuildStep
	public FeatureBuildItem feature()
	{
		return new FeatureBuildItem(FEATURE);
	}

	@BuildStep
	NativeImageResourceBuildItem nativeImageResourceBuildItem()
	{
		return new NativeImageResourceBuildItem("META-INF/jdiameter-client.xsd",
				"META-INF/jdiameter-server.xsd",
				"META-INF/version.properties",
				"dictionary.xml");
	}

	@BuildStep
	ReflectiveClassBuildItem reflection()
	{
		return ReflectiveClassBuildItem.builder(ConcurrentEntityFactory.class,
				                               DictionaryImpl.class,
				                               StatisticProcessorImpl.class,
				                               ConcurrentFactory.class,
				                               LocalTimerFacilityImpl.class,
				                               LocalDataSource.class,
				                               ProxyAgentImpl.class,
				                               AgentConfigurationImpl.class,
				                               RedirectAgentImpl.class,
				                               MutablePeerTableImpl.class,
				                               RealmTableImpl.class,
				                               StatisticManagerImpl.class,
				                               SessionFactoryImpl.class,
				                               NetworkGuard.class,
				                               TCPClientConnection.class,
				                               OverloadManagerImpl.class,
				                               NetworkImpl.class,
				                               RouterImpl.class,
				                               org.jdiameter.client.impl.router.RouterImpl.class,
				                               FsmFactoryImpl.class,
				                               org.jdiameter.client.impl.fsm.FsmFactoryImpl.class,
				                               TransportLayerFactory.class,
				                               org.jdiameter.client.impl.transport.TransportLayerFactory.class,
				                               MetaDataImpl.class,
				                               org.jdiameter.client.impl.MetaDataImpl.class,
				                               AssemblerImpl.class,
				                               MessageParser.class,
				                               WeightedRoundRobinRouter.class,
				                               WeightedLeastConnectionsRouter.class
		                                       )
		                               .methods()
		                               .fields()
		                               .constructors()
		                               .build();
	}

	@Record(RUNTIME_INIT)
	@BuildStep
	public void discoverInjectedClients(DiameterRecorder recorder,
	                                    CombinedIndexBuildItem index,
	                                    TlsRegistryBuildItem tlsRegistryBuildItem,
	                                    DiameterRunTimeConfig diameterRunTimeConfig,
	                                    ShutdownContextBuildItem shutdownContextBuildItem,
	                                    BuildProducer<DiameterBuildItem> diameterStacks)
	{
		Set<String> profileNames = new HashSet<>();
		for (AnnotationInstance annotation : index.getIndex().getAnnotations(DOTNAME_DIAMETER_CONFIG)) {
			if (annotation.value() == null) {
				profileNames.add(DiameterConfig.DEFAULT_CONFIG_NAME);
			} else {
				profileNames.add((String) annotation.value().value());
			}
		}

		for (AnnotationInstance annotation : index.getIndex().getAnnotations(DOTNAME_DIAMETER_SERVICE_OPTIONS)) {
			if (annotation.value() == null) {
				profileNames.add(DiameterConfig.DEFAULT_CONFIG_NAME);
			} else {
				profileNames.add((String) annotation.value().value());
			}
		}

		for (AnnotationInstance annotation : index.getIndex().getAnnotations(DOTNAME_DIAMETER_SERVICE)) {
			if (!annotation.target().asClass().name().equals(DOTNAME_DIAMETER_SERVICE_INTERCEPTOR) &&
			    annotation.target().annotation(DOTNAME_DIAMETER_SERVICE_OPTIONS) == null) {
				profileNames.add(DiameterConfig.DEFAULT_CONFIG_NAME);
			}

		}

		profileNames.forEach(n -> {
			RuntimeValue<Configuration> configuration = recorder.loadDiameterConfiguration(tlsRegistryBuildItem.registry(), diameterRunTimeConfig, n);
			RuntimeValue<Stack> stack = recorder.loadDiameterStack(shutdownContextBuildItem, configuration, n);
			diameterStacks.produce(new DiameterBuildItem(n, stack, configuration));
		});
	}

	@BuildStep
	public ServiceStartBuildItem generateDiameterConfiguration(List<DiameterBuildItem> stacks,
	                                                           BuildProducer<SyntheticBeanBuildItem> syntheticBeanBuildItemBuildProducer)
	{
		stacks.forEach(stack -> {
			syntheticBeanBuildItemBuildProducer.produce(
					createSyntheticBean(stack.getName(),
							Configuration.class,
							DOTNAME_CONFIGURATION,
							stack.getName().equals(DiameterConfig.DEFAULT_CONFIG_NAME))
							.runtimeValue(stack.getConfiguration())
							.done());

			syntheticBeanBuildItemBuildProducer.produce(
					createSyntheticBean(stack.getName(),
							Stack.class,
							DOTNAME_STACK,
							stack.getName().equals(DiameterConfig.DEFAULT_CONFIG_NAME))
							.runtimeValue(stack.getStack())
							.done());

			//Handle the case where the default name is explicitly injected.
			if (stack.getName().equals(DiameterConfig.DEFAULT_CONFIG_NAME)) {
				syntheticBeanBuildItemBuildProducer.produce(
						createSyntheticBean(stack.getName(),
								Configuration.class,
								DOTNAME_CONFIGURATION,
								false)
								.runtimeValue(stack.getConfiguration())
								.done());

				syntheticBeanBuildItemBuildProducer.produce(
						createSyntheticBean(stack.getName(),
								Stack.class,
								DOTNAME_STACK,
								false)
								.runtimeValue(stack.getStack())
								.done());
			}
		});

		return new ServiceStartBuildItem("DiameterService");
	}

	@BuildStep
	public void declareDiameterServicesAsBean(CombinedIndexBuildItem index,
	                                          BuildProducer<AdditionalBeanBuildItem> additionalBeans,
	                                          BuildProducer<AnnotationsTransformerBuildItem> transformer)
	{
		List<String> diameterServices = index.getIndex()
		                                     .getKnownClasses()
				.stream()
				.filter(ci -> ci.hasAnnotation(DiameterService.class) && !ci.hasAnnotation(Interceptor.class))
				.map(ci -> ci.name().toString())
				.collect(Collectors.toList());

		additionalBeans.produce(new AdditionalBeanBuildItem.Builder()
				.addBeanClasses(diameterServices)
				.setUnremovable()
				.setDefaultScope(DotNames.SINGLETON)
				.build());

		transformer.produce(new AnnotationsTransformerBuildItem(AnnotationTransformation.forClasses()
		                                                                                .whenClass(c -> diameterServices.contains(c.name().toString()))
		                                                                                .transform(c -> c.add(AnnotationInstance.builder(Startup.class).build()))));
	}

	private static <T> SyntheticBeanBuildItem.ExtendedBeanConfigurator createSyntheticBean(String clientName, Class<T> type, DotName exposedType, boolean isDefaultConfig)
	{
		LOG.info("Creating Synthetic Bean to {} for @DiameterClient(\"{}\")", type.getSimpleName(), clientName);
		SyntheticBeanBuildItem.ExtendedBeanConfigurator configurator = SyntheticBeanBuildItem
				.configure(type)
				.scope(ApplicationScoped.class)
				.unremovable()
				.setRuntimeInit()
				.addType(exposedType);

		if (isDefaultConfig) {
			configurator.defaultBean();
			configurator.addQualifier(Default.class)
			            .addQualifier(DiameterConfig.class);
		} else {
			configurator.addQualifier()
			            .annotation(DiameterConfig.class)
			            .addValue("value", clientName).done();
		}

		return configurator;
	}
}
