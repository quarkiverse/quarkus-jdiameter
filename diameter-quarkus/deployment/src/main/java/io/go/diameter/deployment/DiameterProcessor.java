package io.go.diameter.deployment;

import io.go.diameter.DiameterConfig;
import io.go.diameter.DiameterService;
import io.go.diameter.runtime.DiameterConfigs;
import io.go.diameter.runtime.DiameterRecorder;
import io.go.diameter.runtime.DiameterSetupException;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.arc.deployment.ValidationPhaseBuildItem;
import io.quarkus.arc.processor.DotNames;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.ServiceStartBuildItem;
import io.quarkus.deployment.builditem.ShutdownContextBuildItem;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.interceptor.Interceptor;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.DotName;
import org.jdiameter.api.Configuration;
import org.jdiameter.api.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import static io.quarkus.deployment.annotations.ExecutionTime.RUNTIME_INIT;

class DiameterProcessor
{
	private static final Logger LOG = LoggerFactory.getLogger(DiameterProcessor.class);
	private static final String FEATURE = "diameter";
	private static final DotName DOTNAME_CONFIGURATION = DotName.createSimple("org.jdiameter.api.Configuration");
	private static final DotName DOTNAME_STACK = DotName.createSimple("org.jdiameter.api.Stack");
	private static final DotName DOTNAME_DIAMETER_SERVICE = DotName.createSimple("io.go.diameter.DiameterService");
	private static final DotName DOTNAME_DIAMETER_SERVICE_OPTIONS = DotName.createSimple("io.go.diameter.DiameterServiceOptions");
	private static final DotName DOTNAME_DIAMETER_SERVICE_INTERCEPTOR = DotName.createSimple("io.go.diameter.DiameterServiceInterceptor");

	@BuildStep
	FeatureBuildItem feature()
	{
		return new FeatureBuildItem(FEATURE);
	}


	@Record(RUNTIME_INIT)
	@BuildStep
	ServiceStartBuildItem generateDiameterConfiguration(DiameterRecorder recorder,
														DiameterConfigs diameterConfig,
														ShutdownContextBuildItem shutdownContextBuildItem,
														BuildProducer<SyntheticBeanBuildItem> syntheticBeanBuildItemBuildProducer)
	{
		if (diameterConfig.diameterConfigs() != null) {
			for (String configName : diameterConfig.diameterConfigs().keySet()) {
				if (Boolean.TRUE.equals(diameterConfig.diameterConfigs().get(configName).enabled())) {
					syntheticBeanBuildItemBuildProducer
							.produce(createSyntheticBean(configName,
														 Configuration.class,
														 DOTNAME_CONFIGURATION,
														 configName.equals(DiameterConfig.DEFAULT_CONFIG_NAME))
											 .createWith(recorder.diameterConfiguration(configName))
											 .done());

					syntheticBeanBuildItemBuildProducer
							.produce(createSyntheticBean(configName,
														 Stack.class,
														 DOTNAME_STACK,
														 configName.equals(DiameterConfig.DEFAULT_CONFIG_NAME))
											 .createWith(recorder.diameterStack(shutdownContextBuildItem, configName))
											 .done());
				}//if
			}//for
		}//if

		return new ServiceStartBuildItem("DiameterService");
	}

	@BuildStep
	public void validateDiameterService(CombinedIndexBuildItem index,
										BuildProducer<ValidationPhaseBuildItem.ValidationErrorBuildItem> errors)
	{
		for (AnnotationInstance annotation : index.getIndex().getAnnotations(DOTNAME_DIAMETER_SERVICE)) {
			if (!annotation.target().asClass().name().equals(DOTNAME_DIAMETER_SERVICE_INTERCEPTOR)) {
				if (annotation.target().annotation(DOTNAME_DIAMETER_SERVICE_OPTIONS) == null) {
					errors.produce(new ValidationPhaseBuildItem.ValidationErrorBuildItem(
							new DiameterSetupException("Missing @DiameterServiceOptions annotation on class " + annotation.target().asClass().name().withoutPackagePrefix())));
				}
			}
		}
	}

	@BuildStep
	void declareDiameterServicesAsBean(CombinedIndexBuildItem index,
									   BuildProducer<AdditionalBeanBuildItem> additionalBeans)
	{
		List<String> diameterServices = index.getIndex().getKnownClasses().stream()
				.filter(ci -> ci.hasAnnotation(DiameterService.class) && !ci.hasAnnotation(Interceptor.class))
				.map(ci -> ci.name().toString())
				.collect(Collectors.toList());

		additionalBeans.produce(new AdditionalBeanBuildItem.Builder()
										.addBeanClasses(diameterServices)
										.setUnremovable()
										.setDefaultScope(DotNames.APPLICATION_SCOPED)
										.build());
	}

	private static <T> SyntheticBeanBuildItem.ExtendedBeanConfigurator createSyntheticBean(String clientName,
																						   Class<T> type,
																						   DotName exposedType,
																						   boolean isDefaultConfig)
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
