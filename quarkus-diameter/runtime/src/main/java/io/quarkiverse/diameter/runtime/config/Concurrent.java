package io.quarkiverse.diameter.runtime.config;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithName;

import java.util.Optional;

@ConfigGroup
public interface Concurrent
{
    /**
     * Determines the maximum thread count in other entities.
     */
    @WithName("thread-group")
    Optional<Integer> threadGroup();

    /**
     * Determines the thread count for message processing tasks.
     */
    @WithName("processing-message-timer")
    Optional<Integer> processingMessageTimer();

    /**
     * Specifies the thread pool for identifying duplicate messages.
     */
    @WithName("duplication-message-timer")
    Optional<Integer> duplicationMessageTimer();

    /**
     * Specifies the thread pool for redirecting messages that do not need any further processing.
     */
    @WithName("redirect-message-timer")
    Optional<Integer> redirectMessageTimer();

    /**
     * Determines the thread pool for managing the overload monitor.
     */
    @WithName("peer-overload-timer")
    Optional<Integer> peerOverloadTimer();

    /**
     * Determines the thread pool for managing tasks regarding peer connection FSM.
     */
    @WithName("connection-timer")
    Optional<Integer> connectionTimer();

    /**
     * Determines the thread pool for statistic gathering tasks.
     */
    @WithName("statistic-timer")
    Optional<Integer> statisticTimer();

    /**
     * Determines the thread pool for managing the invocation of application session FSMs,
     * which will invoke listeners.
     */
    @WithName("application-session")
    Optional<Integer> applicationSession();
}
