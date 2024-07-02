package io.go.diameter.client.runtime;

import io.go.diameter.config.DiameterClientConfig;
import io.go.diameter.config.DiameterClientConfiguration;
import io.go.diameter.config.DiameterConfig;
import io.quarkus.arc.SyntheticCreationalContext;
import io.quarkus.runtime.annotations.Recorder;
import io.smallrye.config.SmallRyeConfig;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jdiameter.api.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Recorder
@Slf4j
public class DiameterClientRecorder
{
	private final Map<String, Configuration> configurationList = new ConcurrentHashMap<>();

	public Function<SyntheticCreationalContext<Configuration>, Configuration> clientConfiguration(String clientName)
	{
		return context -> configurationList.computeIfAbsent(clientName, k -> {
			SmallRyeConfig config = ConfigProvider.getConfig().unwrap(SmallRyeConfig.class);
			DiameterClientConfig client = config.getConfigMapping(DiameterClientConfig.class);

			DiameterConfig diameterConfig = client.getDiameterConfig(k);
			if (diameterConfig == null) {
				throw new IllegalArgumentException("No client configuration found for " + k);
			}
			return new DiameterClientConfiguration(diameterConfig);
		});
	}
}
