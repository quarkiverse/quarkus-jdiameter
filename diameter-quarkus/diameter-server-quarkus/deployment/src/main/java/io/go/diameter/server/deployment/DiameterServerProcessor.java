package io.go.diameter.server.deployment;

import io.go.diameter.DiameterServer;
import io.go.diameter.server.runtime.DiameterServerRecorder;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import org.jboss.jandex.DotName;
import org.jdiameter.api.Configuration;

import static io.quarkus.deployment.annotations.ExecutionTime.RUNTIME_INIT;

class DiameterServerProcessor
{
	private static final DotName CONFIGURATION_DOTNAME = DotName.createSimple("org.jdiameter.api.Configuration");
	private static final String FEATURE = "diameter-server";

	@BuildStep
	FeatureBuildItem feature()
	{
		return new FeatureBuildItem(FEATURE);
	}

	@Record(RUNTIME_INIT)
	@BuildStep
	void generateDiameterServer(DiameterServerRecorder recorder,
								BuildProducer<SyntheticBeanBuildItem> syntheticBeanBuildItemBuildProducer)
	{
		SyntheticBeanBuildItem.ExtendedBeanConfigurator configurator = SyntheticBeanBuildItem
				.configure(Configuration.class)
				.scope(ApplicationScoped.class)
				.unremovable()
				.setRuntimeInit()
				.addType(CONFIGURATION_DOTNAME)
				.defaultBean()
				.addQualifier(Default.class)
				.addQualifier().annotation(DiameterServer.class)
				.done();

		//Define the diameter server config producer
		syntheticBeanBuildItemBuildProducer
				.produce(configurator
								 .createWith(recorder.serverConfiguration())
								 .done());
	}
}
