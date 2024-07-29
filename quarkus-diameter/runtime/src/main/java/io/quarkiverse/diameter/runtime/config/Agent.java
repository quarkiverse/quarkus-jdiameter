package io.quarkiverse.diameter.runtime.config;

import io.smallrye.config.WithParentName;

import java.util.Map;

public interface Agent
{
    /**
     * Retrieves the properties of the agent configuration.
     */
    @WithParentName
    Map<String, String> properties();
}
