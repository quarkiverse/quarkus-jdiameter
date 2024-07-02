package io.go.diameter.config.mapping;

import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

import java.util.Optional;

public interface ApplicationId
{
	/**
	 * Specifies the vendor ID for application definition.
	 */
	@WithDefault("0")
	@WithName("vendor-id")
	Optional<Long> vendorId();

	/**
	 * The Authentication Application ID for application definition.
	 */
	@WithDefault("0")
	@WithName("auth-appl-id")
	Optional<Long> authApplId();

	/**
	 * The Account Application ID for application definition.
	 */
	@WithDefault("0")
	@WithName("acct-appl-id")
	Optional<Long> acctApplId();
}
