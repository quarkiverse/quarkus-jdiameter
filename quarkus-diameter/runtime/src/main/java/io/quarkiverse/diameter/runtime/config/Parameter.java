package io.quarkiverse.diameter.runtime.config;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

import java.util.Optional;

@ConfigGroup
public interface Parameter
{
    /**
     * Specifies whether the stack will accept connections from undefined peers.
     * The default value is `false`
     */
    @WithDefault("false")
    @WithName("accept-undefined-peer")
    Boolean acceptUndefinedPeer();

    /**
     * Specifies whether duplicate message protection is enabled.
     * The default value is `false`.
     */
    @WithDefault("false")
    @WithName("duplicate-protection")
    Boolean duplicateProtection();

    /**
     * Determines whether the URI should be used as FQDN.
     * If it is set to `true`, the stack expects the destination/origin host to be in the
     * format of "aaa://isdn.domain.com:3868" rather than the normal "isdn.domain.com".
     * The default value is `false`.
     */
    @WithDefault("false")
    @WithName("use-uri-as-fqdn")
    Boolean useUriAsFqdn();

    /**
     * Specifies whether the stack should use virtual threads
     * The default value is `false`
     */
    @WithDefault("false")
    @WithName("use-virtual-threads")
    Boolean useVirtualThreads();

    /**
     * Specifies the time each duplicate message is valid for (in extreme cases, it can
     * live up to 2 * DuplicateTimer - 1 milliseconds).
     * The default, minimum value is `240000` (4 minutes in milliseconds).
     */
    @WithDefault("240000")
    @WithName("duplicate-timer")
    Long duplicateTimer();

    /**
     * Specifies the number of requests stored for duplicate protection.
     * The default value is `5000`.
     */
    @WithDefault("5000")
    @WithName("duplicate-size")
    Integer duplicateSize();

    /**
     * Determines how many tasks the peer state machine can have before rejecting the next task.
     * This queue contains FSM events and messaging
     */
    @WithName("queue-size")
    Optional<Integer> queueSize();

    /**
     * Determines the timeout for messages other than protocol FSM messages.
     * The delay is in milliseconds.
     */
    @WithName("message-timeout")
    Optional<Long> messageTimeout();

    /**
     * Determines how long the stack waits for all resources to stop.
     * The delays are in milliseconds.
     */
    @WithName("stop-timeout")
    Optional<Long> stopTimeout();

    /**
     * Determines how long it takes for CER/CEA exchanges to timeout if there is no response.
     * The delays are in milliseconds.
     */
    @WithName("cea-timeout")
    Optional<Long> ceaTimeout();

    /**
     * Determines how long the stack waits to retry the communication with a peer that
     * has stopped answering DWR messages.
     * The delay is in milliseconds.
     */
    @WithName("iac-timeout")
    Optional<Long> iacTimeout();

    /**
     * Determines how long it takes for a DWR/DWA exchange to timeout if there is no response.
     * The delay is in milliseconds.
     */
    @WithName("dwa-timeout")
    Optional<Long> dwaTimeout();

    /**
     * Determines how long it takes for a DPR/DPA exchange to timeout if there is no response.
     * The delay is in milliseconds.
     */
    @WithName("dpa-timeout")
    Optional<Long> dpaTimeout();

    /**
     * Determines how long it takes for the reconnection procedure to timeout.
     * The delay is in milliseconds.
     */
    @WithName("rec-timeout")
    Optional<Long> recTimeout();


    /**
     * Determines how long it takes for the session to timeout
     * The delay is in milliseconds.
     */
    @WithName("session-Timeout")
    Optional<Long> sessionTimeout();

    /**
     * Determines the number of threads for handling events in the Peer FSM.
     */
    @WithName("peer-fsm-thread-count")
    Optional<Integer> peerFSMThreadCount();

    /**
     * Determines a delay before binding.
     * The delay is in milliseconds.
     */
    @WithName("bind-delay")
    Optional<Long> bindDelay();

    /**
     * Controls the thread pool sizes for different aspects of the stack.
     */
    @WithName("concurrent")
    Optional<Concurrent> concurrent();

    /**
     * The caching name to be used if HA datasource is used
     */
    @WithDefault("diameter")
    @WithName("caching-name")
    String cachingName();
}
