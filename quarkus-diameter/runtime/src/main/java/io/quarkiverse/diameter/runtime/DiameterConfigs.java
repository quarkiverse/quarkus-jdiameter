package io.quarkiverse.diameter.runtime;

import io.quarkiverse.diameter.DiameterConfig;
import io.quarkus.runtime.annotations.ConfigDocMapKey;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithParentName;
import io.smallrye.config.WithUnnamedKey;

import java.util.Map;

@ConfigMapping(prefix = "diameter")
@ConfigRoot(phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public interface DiameterConfigs
{
	/**
	 * The defined named diameter client config
	 *
	 * @asciidocx
	 */
	@WithParentName
	@WithUnnamedKey(DiameterConfig.DEFAULT_CONFIG_NAME)
	@ConfigDocMapKey("named-config")
	Map<String, DiameterDetailConfig> diameterConfigs();

	default DiameterDetailConfig getDiameterConfig(String clientName)
	{
		return diameterConfigs().get(clientName);
	}//getDiameterConfig
}
