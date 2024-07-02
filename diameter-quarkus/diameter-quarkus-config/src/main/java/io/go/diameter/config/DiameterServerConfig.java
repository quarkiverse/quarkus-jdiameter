package io.go.diameter.config;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "diameter.server")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface DiameterServerConfig extends DiameterConfig
{
}
