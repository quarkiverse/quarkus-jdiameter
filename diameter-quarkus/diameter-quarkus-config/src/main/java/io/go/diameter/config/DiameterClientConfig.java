package io.go.diameter.config;

import io.quarkus.runtime.annotations.ConfigDocMapKey;
import io.quarkus.runtime.annotations.ConfigDocSection;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithParentName;

import java.util.Map;

@ConfigMapping(prefix = "diameter.client")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface DiameterClientConfig
{
	/**
	 * The default diameter client config
	 */
	@ConfigDocSection
	@WithParentName
	DiameterConfig defaultDiameterConfig();

	/**
	 * The defined named diameter client config
	 */
	@ConfigDocSection
	@ConfigDocMapKey("name")
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
