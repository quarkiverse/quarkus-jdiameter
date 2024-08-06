package io.quarkiverse.diameter.runtime.config;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;
import org.jdiameter.api.LocalAction;

import java.util.Optional;

/**
 * Parent element containing all realms that connect into the Diameter network.
 */
@ConfigGroup
public interface Realm
{
    /**
     * Contains attributes and elements that describe different realms configured for the Core.
     */
    @WithName("realm-name")
    @WithDefault("io.quarkiverse.diameter")
    String realmName();

    /**
     * Comma separated list of peers. Each peer is represented by an IP Address or FQDN.
     */
    @WithName("peers")
    @WithDefault("localhost")
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
    Optional<ApplicationId> applicationId();

    /**
     * The Agent configuration
     */
    @WithName("agent")
    Optional<Agent> agent();
}
