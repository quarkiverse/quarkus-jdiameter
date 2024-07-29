package io.quarkiverse.diameter.deployment;

import io.quarkiverse.diameter.DiameterConfig;
import io.quarkiverse.diameter.DiameterService;
import io.quarkiverse.diameter.runtime.DiameterConfigs;
import io.quarkiverse.diameter.runtime.DiameterRecorder;
import io.quarkiverse.diameter.runtime.DiameterSetupException;
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

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static io.quarkus.deployment.annotations.ExecutionTime.RUNTIME_INIT;

class DiameterProcessor
{
    private static final Logger LOG = LoggerFactory.getLogger(DiameterProcessor.class);
    private static final String FEATURE = "diameter";
    private static final DotName DOTNAME_CONFIGURATION = DotName.createSimple("org.jdiameter.api.Configuration");
    private static final DotName DOTNAME_STACK = DotName.createSimple("org.jdiameter.api.Stack");
    private static final DotName DOTNAME_DIAMETER_SERVICE = DotName.createSimple("io.quarkiverse.diameter.DiameterService");
    private static final DotName DOTNAME_DIAMETER_SERVICE_INTERCEPTOR = DotName.createSimple("io.quarkiverse.diameter.DiameterServiceInterceptor");

    private static final DotName DOTNAME_CLIENT_CAA_SESSION_LISTENER = DotName.createSimple("org.jdiameter.api.cca.ClientCCASessionListener");
    private static final DotName DOTNAME_SERVER_CAA_SESSION_LISTENER = DotName.createSimple("org.jdiameter.api.cca.ServerCCASessionListener");
    private static final DotName DOTNAME_CLIENT_GQ_SESSION_LISTENER = DotName.createSimple("org.jdiameter.api.gq.ClientGqSessionListener");
    private static final DotName DOTNAME_SERVER_GQ_SESSION_LISTENER = DotName.createSimple("org.jdiameter.api.gq.ServerGqSessionListener");
    private static final DotName DOTNAME_CLIENT_RX_SESSION_LISTENER = DotName.createSimple("org.jdiameter.api.rx.ClientRxSessionListener");
    private static final DotName DOTNAME_SERVER_RX_SESSION_LISTENER = DotName.createSimple("org.jdiameter.api.rx.ServerRxSessionListener");
    private static final DotName DOTNAME_CLIENT_S6A_SESSION_LISTENER = DotName.createSimple("org.jdiameter.api.s6a.ClientS6aSessionListener");
    private static final DotName DOTNAME_SERVER_S6A_SESSION_LISTENER = DotName.createSimple("org.jdiameter.api.s6a.ServerS6aSessionListener");

    private static final List<DotName> DIAMETER_SESSION_LISTENERS = List.of(DOTNAME_CLIENT_CAA_SESSION_LISTENER, DOTNAME_SERVER_CAA_SESSION_LISTENER, DOTNAME_CLIENT_GQ_SESSION_LISTENER, DOTNAME_SERVER_GQ_SESSION_LISTENER, DOTNAME_CLIENT_RX_SESSION_LISTENER, DOTNAME_SERVER_RX_SESSION_LISTENER, DOTNAME_CLIENT_S6A_SESSION_LISTENER, DOTNAME_SERVER_S6A_SESSION_LISTENER);

    @BuildStep
    public FeatureBuildItem feature()
    {
        return new FeatureBuildItem(FEATURE);
    }


    @Record(RUNTIME_INIT)
    @BuildStep
    public ServiceStartBuildItem generateDiameterConfiguration(DiameterRecorder recorder,
                                                               DiameterConfigs diameterConfig,
                                                               ShutdownContextBuildItem shutdownContextBuildItem,
                                                               TlsRegistryBuildItem tlsRegistryBuildItem,
                                                               BuildProducer<SyntheticBeanBuildItem> syntheticBeanBuildItemBuildProducer)
    {
        if (diameterConfig.diameterConfigs() != null) {
            for (String configName : diameterConfig.diameterConfigs().keySet()) {
                if (Boolean.TRUE.equals(diameterConfig.diameterConfigs().get(configName).enabled())) {
                    syntheticBeanBuildItemBuildProducer.produce(
                            createSyntheticBean(configName,
                                                Configuration.class,
                                                DOTNAME_CONFIGURATION,
                                                configName.equals(DiameterConfig.DEFAULT_CONFIG_NAME))
                                    .createWith(recorder.diameterConfiguration(tlsRegistryBuildItem.registry(), configName))
                                    .done());

                    syntheticBeanBuildItemBuildProducer.produce(
                            createSyntheticBean(configName,
                                                Stack.class,
                                                DOTNAME_STACK,
                                                configName.equals(DiameterConfig.DEFAULT_CONFIG_NAME))
                                    .createWith(recorder.diameterStack(shutdownContextBuildItem, tlsRegistryBuildItem.registry(), configName))
                                    .done());
                }//if
            }//for
        }//if

        return new ServiceStartBuildItem("DiameterService");
    }

    @BuildStep
    public void validateDiameterService(CombinedIndexBuildItem index, BuildProducer<ValidationPhaseBuildItem.ValidationErrorBuildItem> errors)
    {
        for (AnnotationInstance annotation : index.getIndex().getAnnotations(DOTNAME_DIAMETER_SERVICE)) {
            if (!annotation.target().asClass().name().equals(DOTNAME_DIAMETER_SERVICE_INTERCEPTOR)) {
                List<DotName> interfaces = annotation.target().asClass().interfaceNames();

                AtomicInteger count = new AtomicInteger(0);
                interfaces.forEach(n -> {
                    if (DIAMETER_SESSION_LISTENERS.contains(n)) {
                        count.incrementAndGet();
                    }
                });

                if (count.get() > 1) {
                    errors.produce(new ValidationPhaseBuildItem.ValidationErrorBuildItem(
                            new DiameterSetupException("Only one XXXXSessionListener can be implemented per Diameter Service. " +
                                                       "You will need to split the services into multiple services implementing only one XXXXSessionListener. " +
                                                       "Remember that you cannot have more than one service implementing the same XXXXSessionListener.")));
                }
            }
        }
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
