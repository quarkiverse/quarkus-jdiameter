package io.go.diameter.client.runtime;

import io.go.diameter.config.DiameterConfig;
import io.quarkus.runtime.annotations.ConfigDocIgnore;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithParentName;

import java.util.Map;

@ConfigMapping(prefix = "diameter.client")
@ConfigRoot(phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public interface DiameterClientConfig
{
	/**
	 * The default diameter client config
	 */
	@ConfigDocIgnore
	@WithParentName
	DiameterConfig defaultDiameterConfig();

	/**
	 * The defined named diameter client config
	 */
	@ConfigDocIgnore
	@WithParentName
	Map<String, DiameterConfig> namedDiameterConfigs();


	default DiameterConfig getDiameterConfig(String clientName)
	{
		if ("<default>".equals(clientName)) {
			return defaultDiameterConfig();
		}//if

		return namedDiameterConfigs().get(clientName);
	}//getDiameterConfig
}
