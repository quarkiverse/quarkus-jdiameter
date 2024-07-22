package io.go.diameter.runtime.config;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithName;

@ConfigGroup
/*
 * The Overload Monitor configuration
 */
public interface OverloadMonitor
{
	/**
	 * Defines the index of this overload monitor, so priorities/orders can be specified.
	 */
	@WithName("index")
	int index();

	/**
	 * The low threshold for activation of the overload monitor.
	 */
	@WithName("low-threshold")
	double lowThreshold();

	/**
	 * The high threshold for activation of the overload monitor.
	 */
	@WithName("high-threshold")
	double highThreshold();

	/**
	 * The application that is overloaded
	 */
	@WithName("application-id")
	ApplicationId applicationId();
}
