package io.go.diameter.config.mapping;

import io.smallrye.config.WithName;

import java.util.List;

public interface Network
{
	/**
	 * List of external peers and the way they connect.
	 */
	@WithName("peers")
	List<Peer> peers();

	/**
	 * List of all realms that connect into the Diameter network.
	 */
	@WithName("realms")
	List<Realm> realms();
}
