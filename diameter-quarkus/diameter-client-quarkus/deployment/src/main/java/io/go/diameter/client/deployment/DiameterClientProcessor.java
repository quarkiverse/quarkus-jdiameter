package io.go.diameter.client.deployment;

import io.go.diameter.DiameterClient;
import io.go.diameter.client.runtime.DiameterClientConfig;
import io.go.diameter.client.runtime.DiameterClientFactory;
import io.go.diameter.client.runtime.DiameterClientRecorder;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import org.jboss.jandex.ClassType;
import org.jboss.jandex.DotName;
import org.jdiameter.api.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.quarkus.deployment.annotations.ExecutionTime.RUNTIME_INIT;

class DiameterClientProcessor
{
	private static final Logger LOG = LoggerFactory.getLogger(DiameterClientProcessor.class);
	private static final String DEFAULT_NAME = "<default>";
	private static final String FEATURE = "diameter-client";
	private static final DotName CONFIGURATION_DOTNAME = DotName.createSimple("org.jdiameter.api.Configuration");

	@BuildStep
	FeatureBuildItem feature()
	{
		return new FeatureBuildItem(FEATURE);
	}


	@Record(RUNTIME_INIT)
	@BuildStep
	void generateDiameterClient(DiameterClientRecorder recorder,
								DiameterClientConfig diameterConfig,
								BuildProducer<SyntheticBeanBuildItem> syntheticBeanBuildItemBuildProducer)
	{
		if (diameterConfig.defaultDiameterConfig() != null) {
			//Define the default diameter config producer
			syntheticBeanBuildItemBuildProducer
					.produce(createSyntheticBean(DEFAULT_NAME,
												 false,
												 true)
									 .createWith(recorder.clientConfiguration(DEFAULT_NAME))
									 .addInjectionPoint(ClassType.create(DotName.createSimple(DiameterClientFactory.class)))
									 .done());
		}//if


		if (diameterConfig.namedDiameterConfigs() != null) {
			for (String clientName : diameterConfig.namedDiameterConfigs().keySet()) {
				//Define the named diameter config producer
				syntheticBeanBuildItemBuildProducer
						.produce(createSyntheticBean(clientName,
													 true,
													 false)
										 .createWith(recorder.clientConfiguration(clientName))
										 .addInjectionPoint(ClassType.create(DotName.createSimple(DiameterClientFactory.class)))
										 .done());
			}//for
		}//if
	}

	private static SyntheticBeanBuildItem.ExtendedBeanConfigurator createSyntheticBean(String clientName,
																					   boolean isNamedPersistenceUnit,
																					   boolean defaultBean)
	{
		LOG.info("Creating Synthetic Bean for @DiameterClient(\"{}\")", clientName);
		SyntheticBeanBuildItem.ExtendedBeanConfigurator configurator = SyntheticBeanBuildItem
				.configure(Configuration.class)
				.scope(ApplicationScoped.class)
				.unremovable()
				.setRuntimeInit()
				.addType(CONFIGURATION_DOTNAME);


		if (defaultBean) {
			configurator.defaultBean();
		}

		if (isNamedPersistenceUnit) {
			configurator.addQualifier()
					.annotation(DiameterClient.class)
					.addValue("value", clientName)
					.done();
		}
		else {
			configurator.addQualifier(Default.class)
					.addQualifier(DiameterClient.class);
		}

		return configurator;
	}
}
