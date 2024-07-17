package io.go.diameter.runtime;


import io.go.diameter.runtime.config.Extension;
import io.go.diameter.runtime.config.LocalPeer;
import io.go.diameter.runtime.config.Network;
import io.go.diameter.runtime.config.Parameter;
import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithDefaults;
import io.smallrye.config.WithName;

import java.util.Optional;

/**
 * The Diameter Config describes the entire Diameter configuration mapping for both the
 * server and client
 */
@ConfigGroup
public interface DiameterDetailConfig
{
	/**
	 * Indicates if the defined diameter configuration is enabled. If not enabled the configuration will be ignored.*
	 */
	@WithDefault("true")
	Boolean enabled();

	/**
	 * The localPeer element contains parameters that affect the local Diameter peer.
	 */
	@WithName("local-peer")
	@WithDefaults
	LocalPeer localPeer();

	/**
	 * The Parameters element contains elements that specify parameters for the Diameter stack.
	 */
	@WithName("parameter")
	@WithDefaults
	Parameter parameter();

	/**
	 * The Network< element contains elements that specify parameters for external peers.
	 */
	@WithName("network")
	@WithDefaults
	Network network();

	/**
	 * The extensions elements contains elements that override existing components in the Diameter stack.
	 */
	@WithName("extensions")
	Optional<Extension> extension();
}
