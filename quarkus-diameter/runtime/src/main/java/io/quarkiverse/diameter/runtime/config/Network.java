package io.quarkiverse.diameter.runtime.config;

import io.quarkus.runtime.annotations.ConfigDocSection;
import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithDefaults;
import io.smallrye.config.WithName;
import io.smallrye.config.WithUnnamedKey;

import java.util.Map;

/**
 * The Network element contains elements that specify parameters for external peers.
 * The available elements and attributes are listed for reference.
 */
@ConfigGroup
public interface Network
{
	/**
	 * List of external peers and the way they connect.
	 */
	@WithName("peers")
	@WithDefaults
	@ConfigDocSection
	@WithUnnamedKey
	Map<String, Peer> peers();

	/**
	 * List of all realms that connect into the Diameter network.
	 */
	@WithName("realms")
	@WithDefaults
	@ConfigDocSection
	@WithUnnamedKey
	Map<String, Realm> realms();
}
