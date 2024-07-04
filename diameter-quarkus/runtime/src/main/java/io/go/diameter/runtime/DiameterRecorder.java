package io.go.diameter.runtime;

import io.quarkus.arc.SyntheticCreationalContext;
import io.quarkus.runtime.annotations.Recorder;
import io.smallrye.config.SmallRyeConfig;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jdiameter.api.Configuration;

import java.util.function.Function;

@Recorder
public class DiameterRecorder
{
	public Function<SyntheticCreationalContext<Configuration>, Configuration> diameterConfiguration(String clientName)
	{
		return context -> {
			SmallRyeConfig config = ConfigProvider.getConfig().unwrap(SmallRyeConfig.class);
			DiameterConfigs client = config.getConfigMapping(DiameterConfigs.class);

			DiameterDetailConfig diameterConfig = client.getDiameterConfig(clientName);
			if (diameterConfig == null) {
				throw new IllegalArgumentException("No Diameter configuration found for " + clientName);
			}
			return new DiameterConfiguration(diameterConfig);
		};
	}
}
