package io.quarkiverse.diameter.runtime.config;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigGroup
public interface ApplicationId
{
    /**
     * Specifies the vendor ID for application definition.
     */
    @WithDefault("0")
    @WithName("vendor-id")
    Long vendorId();

    /**
     * The Authentication Application ID for application definition.
     */
    @WithDefault("0")
    @WithName("auth-appl-id")
    Long authApplId();

    /**
     * The Account Application ID for application definition.
     */
    @WithDefault("0")
    @WithName("acct-appl-id")
    Long acctApplId();
}
