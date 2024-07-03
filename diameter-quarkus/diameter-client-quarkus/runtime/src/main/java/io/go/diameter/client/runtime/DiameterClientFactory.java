package io.go.diameter.client.runtime;

import io.go.diameter.config.DiameterConfig;
import io.go.diameter.config.DiameterConfiguration;
import io.smallrye.config.SmallRyeConfig;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jdiameter.api.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class DiameterClientFactory
{
	private final Map<String, Configuration> configurationList = new ConcurrentHashMap<>();

	public Configuration getConfiguration(String clientName)
	{
		return configurationList.computeIfAbsent(clientName, k -> {
			SmallRyeConfig config = ConfigProvider.getConfig().unwrap(SmallRyeConfig.class);
			DiameterClientConfig client = config.getConfigMapping(DiameterClientConfig.class);

			DiameterConfig diameterConfig = client.getDiameterConfig(k);
			if (diameterConfig == null) {
				throw new IllegalArgumentException("No client configuration found for " + k);
			}
			return new DiameterConfiguration(diameterConfig);
		});
	}
}
