package io.go.diameter.config;

import io.go.diameter.config.mapping.Extension;
import io.go.diameter.config.mapping.LocalPeer;
import io.go.diameter.config.mapping.Network;
import io.go.diameter.config.mapping.Parameter;
import io.smallrye.config.WithName;

/**
 * The Diameter Config describes the entire Diameter configuration mapping for both the
 * server and client
 */
public interface DiameterConfig
{

	/**
	 * The localPeer element contains parameters that affect the local Diameter peer.
	 */
	@WithName("local-peer")
	LocalPeer localPeer();

	/**
	 * The Parameters element contains elements that specify parameters for the Diameter stack.
	 */
	@WithName("parameter")
	Parameter parameter();

	/**
	 * The Network< element contains elements that specify parameters for external peers.
	 */
	@WithName("network")
	Network network();

	/**
	 * The extensions elements contains elements that override existing components in the Diameter stack.
	 */
	@WithName("extensions")
	Extension extension();
}
