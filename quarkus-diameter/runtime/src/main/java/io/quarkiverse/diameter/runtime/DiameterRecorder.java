package io.quarkiverse.diameter.runtime;

import io.quarkus.arc.SyntheticCreationalContext;
import io.quarkus.runtime.ShutdownContext;
import io.quarkus.runtime.annotations.Recorder;
import io.quarkus.tls.TlsConfigurationRegistry;
import io.smallrye.config.SmallRyeConfig;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jdiameter.api.*;
import org.jdiameter.common.impl.validation.DictionaryImpl;
import org.jdiameter.server.impl.StackImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

@Recorder
public class DiameterRecorder
{
    private static final Logger LOG = LoggerFactory.getLogger(DiameterRecorder.class);

    public Function<SyntheticCreationalContext<Configuration>, Configuration> diameterConfiguration(Supplier<TlsConfigurationRegistry> registrySupplier, String configName)
    {
        return context -> {
            LOG.info("Building Diameter configuration for profile '{}'", configName);
            SmallRyeConfig config = ConfigProvider.getConfig().unwrap(SmallRyeConfig.class);
            DiameterConfigs client = config.getConfigMapping(DiameterConfigs.class);

            DiameterDetailConfig diameterConfig = client.getDiameterConfig(configName);
            if (diameterConfig == null) {
                throw new IllegalArgumentException("No Diameter configuration found for profile '" + configName + "'");
            }

            return new DiameterConfiguration(diameterConfig, registrySupplier.get());
        };
    }

    public Function<SyntheticCreationalContext<Stack>, Stack> diameterStack(ShutdownContext shutdownContext, Supplier<TlsConfigurationRegistry> registrySupplier, String configName)
    {
        return context -> {
            try {
                DictionaryImpl.INSTANCE.setEnabled(true);
				
                LOG.info("Building Diameter Stack for configuration profile '{}'", configName);
                SmallRyeConfig config = ConfigProvider.getConfig().unwrap(SmallRyeConfig.class);
                DiameterConfigs client = config.getConfigMapping(DiameterConfigs.class);

                DiameterDetailConfig diameterConfig = client.getDiameterConfig(configName);
                if (diameterConfig == null) {
                    throw new IllegalArgumentException("No Diameter configuration found for profile '" + configName + "'");
                }

                Stack stack = new StackImpl();
                stack.init(new DiameterConfiguration(diameterConfig, registrySupplier.get()));
                shutdownContext.addShutdownTask(() -> {
                    LOG.info("Stopping '{}' Diameter Stack", configName);
                    if (stack.isActive()) {
                        try {
                            stack.stop(10, TimeUnit.SECONDS, DisconnectCause.REBOOTING);
                        }
                        catch (IllegalDiameterStateException | InternalException ex) {
                            LOG.error("Error stopping Diameter Stack", ex);
                        }

                        stack.destroy();
                    }
                });
                return stack;
            }
            catch (IllegalDiameterStateException | InternalException ex) {
                LOG.error("Error creating '{}' Diameter Stack", configName, ex);
                throw new DiameterSetupException("Error creating '" + configName + "' Diameter stack");
            }
        };
    }
}
