package io.go.diameter.deployment;

import io.go.diameter.DiameterConfig;
import io.go.diameter.runtime.DiameterConfigs;
import io.go.diameter.runtime.DiameterRecorder;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import org.jboss.jandex.DotName;
import org.jdiameter.api.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.quarkus.deployment.annotations.ExecutionTime.RUNTIME_INIT;

class DiameterProcessor
{
	private static final Logger LOG = LoggerFactory.getLogger(DiameterProcessor.class);
	private static final String FEATURE = "diameter";
	private static final DotName CONFIGURATION_DOTNAME = DotName.createSimple("org.jdiameter.api.Configuration");

	@BuildStep
	FeatureBuildItem feature()
	{
		return new FeatureBuildItem(FEATURE);
	}


	@Record(RUNTIME_INIT)
	@BuildStep
	void generateDiameterConfiguration(DiameterRecorder recorder,
									   DiameterConfigs diameterConfig,
									   BuildProducer<SyntheticBeanBuildItem> syntheticBeanBuildItemBuildProducer)
	{
		if (diameterConfig.diameterConfigs() != null) {
			for (String clientName : diameterConfig.diameterConfigs().keySet()) {
				if (Boolean.TRUE.equals(diameterConfig.diameterConfigs().get(clientName).enabled())) {
					syntheticBeanBuildItemBuildProducer
							.produce(createSyntheticBean(clientName,
														 clientName.equals(DiameterConfig.DEFAULT_CONFIG_NAME))
											 .createWith(recorder.diameterConfiguration(clientName))
											 .done());
				}//if
			}//for
		}//if
	}

	private static SyntheticBeanBuildItem.ExtendedBeanConfigurator createSyntheticBean(String clientName,
																					   boolean isDefaultConfig)
	{
		LOG.info("Creating Synthetic Bean for @DiameterClient(\"{}\")", clientName);
		SyntheticBeanBuildItem.ExtendedBeanConfigurator configurator = SyntheticBeanBuildItem
				.configure(Configuration.class)
				.scope(ApplicationScoped.class)
				.unremovable()
				.setRuntimeInit()
				.addType(CONFIGURATION_DOTNAME);

		if (isDefaultConfig) {
			configurator.defaultBean();
			configurator.addQualifier(Default.class)
					.addQualifier(DiameterConfig.class);
		}
		else {
			configurator.addQualifier()
					.annotation(DiameterConfig.class)
					.addValue("value", clientName)
					.done();
		}

		return configurator;
	}
}
