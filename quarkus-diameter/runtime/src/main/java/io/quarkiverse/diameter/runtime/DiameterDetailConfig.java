package io.quarkiverse.diameter.runtime;


import io.quarkiverse.diameter.runtime.config.Extension;
import io.quarkiverse.diameter.runtime.config.LocalPeer;
import io.quarkiverse.diameter.runtime.config.Network;
import io.quarkiverse.diameter.runtime.config.Parameter;
import io.quarkus.runtime.annotations.ConfigDocSection;
import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithDefaults;
import io.smallrye.config.WithName;

/**
 * The Diameter Config describes the entire Diameter configuration mapping for both the
 * server and client
 */
@ConfigGroup
public interface DiameterDetailConfig
{
    /**
     * The localPeer element contains parameters that affect the local Diameter peer.
     */
    @WithName("local-peer")
    @WithDefaults
    @ConfigDocSection
    LocalPeer localPeer();

    /**
     * The Parameters element contains elements that specify parameters for the Diameter stack.
     */
    @WithName("parameter")
    @WithDefaults
    @ConfigDocSection
    Parameter parameter();

    /**
     * The Network< element contains elements that specify parameters for external peers.
     */
    @WithName("network")
    @WithDefaults
    @ConfigDocSection
    Network network();

    /**
     * The extensions elements contains elements that override existing components in the Diameter stack.
     */
    @WithName("extensions")
    @ConfigDocSection
    @WithDefaults
    Extension extensions();
}
