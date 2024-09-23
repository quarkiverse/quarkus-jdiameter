package io.quarkiverse.diameter.runtime;

import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.ShutdownContext;
import io.quarkus.runtime.annotations.Recorder;
import io.quarkus.tls.TlsConfigurationRegistry;
import org.jdiameter.api.*;
import org.jdiameter.common.impl.validation.DictionaryImpl;
import org.jdiameter.server.impl.StackImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Recorder
public class DiameterRecorder
{
	private static final Logger LOG = LoggerFactory.getLogger(DiameterRecorder.class);

	public RuntimeValue<Configuration> loadDiameterConfiguration(Supplier<TlsConfigurationRegistry> registrySupplier,
	                                                             DiameterRunTimeConfig runtimeConfig,
	                                                             String configName)
	{
		LOG.info("Building Diameter configuration for profile '{}'", configName);
		DiameterDetailConfig diameterConfig = runtimeConfig.getDiameterConfig(configName);
		if (diameterConfig == null) {
			throw new IllegalArgumentException("No Diameter configuration found for profile '" + configName + "'");
		}

		return new RuntimeValue<>(new DiameterConfiguration(diameterConfig, registrySupplier.get()));
	}

	public RuntimeValue<Stack> loadDiameterStack(ShutdownContext shutdownContext,
	                                             RuntimeValue<Configuration> diameterConfig,
	                                             String configName)
	{
		try {
			DictionaryImpl.INSTANCE.setEnabled(true);

			LOG.info("Building Diameter Stack for configuration profile '{}'", configName);
			Stack stack = new StackImpl();
			stack.init(diameterConfig.getValue());
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
			return new RuntimeValue<>(stack);
		}
		catch (IllegalDiameterStateException | InternalException ex) {
			LOG.error("Error creating '{}' Diameter Stack", configName, ex);
			throw new DiameterSetupException("Error creating '" + configName + "' Diameter stack");
		}
	}
}
