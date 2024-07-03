package io.go.diameter.server.runtime;

import io.go.diameter.config.DiameterConfig;
import io.quarkus.runtime.annotations.ConfigDocIgnore;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithParentName;

@ConfigMapping(prefix = "diameter.server")
@ConfigRoot(phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public interface DiameterServerConfig
{
	/**
	 * The config
	 */
	@ConfigDocIgnore
	@WithParentName
	DiameterConfig getConfig();
}
