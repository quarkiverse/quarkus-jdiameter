package io.go.diameter.runtime.config;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

import java.util.Optional;

@ConfigGroup
public interface Peer
{
	/**
	 * Specifies the name of the peer in the form of a URI.
	 * The structure is "aaa://[fqdn|ip]:port" (for example, "aaa://192.168.1.1:3868").
	 */
	@WithDefault("aaa://localhost:3868")
	@WithName("peer-uri")
	String peerUri();

	/**
	 * Specifies the rating of this peer in order to achieve peer priorities/sorting.
	 */
	@WithDefault("1")
	@WithName("rating")
	int rating();

	/**
	 * Specifies the actual ip for the peer-uri, for example 192.168.1.1
	 */
	@WithName("ip")
	Optional<String> ip();

	/**
	 * Specifies a port range to accept connection override the port number in peer-uri
	 */
	@WithName("port-range")
	Optional<String> portRange();

	/**
	 * Determines if the stack should try to connect to this peer.
	 */
	@WithDefault("false")
	@WithName("attempt-connect")
	Boolean attemptConnect();
}
