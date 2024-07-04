package io.go.diameter.runtime.config;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithDefaults;
import io.smallrye.config.WithName;

import java.util.List;

@ConfigGroup
public interface Network
{
	/**
	 * List of external peers and the way they connect.
	 */
	@WithName("peers")
	@WithDefaults
	List<Peer> peers();

	/**
	 * List of all realms that connect into the Diameter network.
	 */
	@WithName("realms")
	@WithDefaults
	List<Realm> realms();
}
