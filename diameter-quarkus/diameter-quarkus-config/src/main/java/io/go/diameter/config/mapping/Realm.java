package io.go.diameter.config.mapping;

import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;
import org.jdiameter.api.LocalAction;

public interface Realm
{
	/**
	 * Contains attributes and elements that describe different realms configured for the Core.
	 */
	@WithName("realm-name")
	String realmName();

	/**
	 * Comma separated list of peers. Each peer is represented by an IP Address or FQDN.
	 */
	@WithName("peers")
	String peers();

	/**
	 * Determines the action the Local Peer will play on the specified realm: Act as a LOCAL peer.
	 */
	@WithDefault("LOCAL")
	@WithName("local-action")
	LocalAction localAction();

	/**
	 * Specifies if this realm is dynamic. That is, peers that
	 * connect to peers with this realm name will be added to the realm peer
	 * list if not present already.
	 */
	@WithDefault("false")
	@WithName("dynamic")
	Boolean dynamic();

	/**
	 * The time before a peer belonging to this realm is removed if no connection is available.
	 * The time is in seconds.
	 */
	@WithDefault("1")
	@WithName("exp-time")
	long expTime();

	/**
	 * The applications supported.
	 */
	@WithName("application-id")
	ApplicationId applicationId();
}
