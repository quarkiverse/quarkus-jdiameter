package io.quarkiverse.diameter.deployment;

import io.quarkiverse.diameter.DiameterConfig;
import io.quarkiverse.diameter.DiameterService;
import io.quarkiverse.diameter.runtime.DiameterRecorder;
import io.quarkiverse.diameter.runtime.DiameterRunTimeConfig;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.arc.processor.DotNames;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.ServiceStartBuildItem;
import io.quarkus.deployment.builditem.ShutdownContextBuildItem;
import io.quarkus.tls.TlsRegistryBuildItem;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.interceptor.Interceptor;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.DotName;
import org.jdiameter.api.Configuration;
import org.jdiameter.api.Stack;
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
    public void discoverInjectedClients(CombinedIndexBuildItem index,
                                        BuildProducer<DiameterConfigBuildItem> profiles)
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

        profileNames.forEach(n -> profiles.produce(new DiameterConfigBuildItem(n)));
    }

    @Record(RUNTIME_INIT)
    @BuildStep
    public ServiceStartBuildItem generateDiameterConfiguration(DiameterRecorder recorder,
                                                               List<DiameterConfigBuildItem> profiles,
                                                               DiameterRunTimeConfig diameterRunTimeConfig,
                                                               ShutdownContextBuildItem shutdownContextBuildItem,
                                                               TlsRegistryBuildItem tlsRegistryBuildItem,
                                                               BuildProducer<SyntheticBeanBuildItem> syntheticBeanBuildItemBuildProducer)
    {
        profiles.forEach(profile -> {
            syntheticBeanBuildItemBuildProducer.produce(
                    createSyntheticBean(profile.getProfileName(),
                                        Configuration.class,
                                        DOTNAME_CONFIGURATION,
                                        profile.getProfileName().equals(DiameterConfig.DEFAULT_CONFIG_NAME))
                            .createWith(recorder.diameterConfiguration(tlsRegistryBuildItem.registry(), diameterRunTimeConfig, profile.getProfileName()))
                            .done());

            syntheticBeanBuildItemBuildProducer.produce(
                    createSyntheticBean(profile.getProfileName(),
                                        Stack.class,
                                        DOTNAME_STACK,
                                        profile.getProfileName().equals(DiameterConfig.DEFAULT_CONFIG_NAME))
                            .createWith(recorder.diameterStack(shutdownContextBuildItem, tlsRegistryBuildItem.registry(), diameterRunTimeConfig, profile.getProfileName()))
                            .done());
        });

        return new ServiceStartBuildItem("DiameterService");
    }

    @BuildStep
    public void declareDiameterServicesAsBean(CombinedIndexBuildItem index, BuildProducer<AdditionalBeanBuildItem> additionalBeans)
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
    }

    private static <T> SyntheticBeanBuildItem.ExtendedBeanConfigurator createSyntheticBean(String clientName, Class<T> type, DotName exposedType, boolean isDefaultConfig)
    {
        LOG.info("Creating Synthetic Bean to {} for @DiameterClient(\"{}\")", type.getSimpleName(), clientName);
        SyntheticBeanBuildItem.ExtendedBeanConfigurator configurator = SyntheticBeanBuildItem.configure(type).scope(ApplicationScoped.class).unremovable().setRuntimeInit().addType(exposedType);

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
