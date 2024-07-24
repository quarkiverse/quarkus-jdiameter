package io.quarkiverse.diameter.runtime.config;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithName;

import java.util.Optional;

@ConfigGroup
public interface Extension
{
	/**
	 * The MetaData extension
	 */
	@WithName("metadata")
	Optional<String> metaData();

	/**
	 * The MetaData extension
	 */
	@WithName("message-parser")
	Optional<String> messageParser();

	/**
	 * The MetaData extension
	 */
	@WithName("element-parser")
	Optional<String> elementParser();

	/**
	 * The MetaData extension
	 */
	@WithName("router-engine")
	Optional<String> routerEngine();

	/**
	 * The MetaData extension
	 */
	@WithName("peer-controller")
	Optional<String> peerController();

	/**
	 * The Realm Controller extension
	 */
	@WithName("realm-controller")
	Optional<String> realmController();

	/**
	 * The Session Factory extension
	 */
	@WithName("session-factory")
	Optional<String> sessionFactory();

	/**
	 * The Transport Factory extension
	 */
	@WithName("transport-factory")
	Optional<String> transportFactory();

	/**
	 * The Connection extension
	 */
	@WithName("connection")
	Optional<String> connection();

	/**
	 * The Network Guard extension
	 */
	@WithName("network-guard")
	Optional<String> networkGuard();

	/**
	 * The Peer Fsm Factory extension
	 */
	@WithName("peer-fsm-factory")
	Optional<String> peerFsmFactory();

	/**
	 * The Statistic Factory extension
	 */
	@WithName("statistic-factory")
	Optional<String> statisticFactory();

	/**
	 * The Concurrent Factory extension
	 */
	@WithName("concurrent-factory")
	Optional<String> concurrentFactory();

	/**
	 * The Concurrent Entity Factory extension
	 */
	@WithName("concurrent-entity-factory")
	Optional<String> concurrentEntityFactory();

	/**
	 * The Statistic Processor extension
	 */
	@WithName("statistic-processor")
	Optional<String> statisticProcessor();

	/**
	 * The Network extension
	 */
	@WithName("network")
	Optional<String> network();

	/**
	 * The Session Datasource extension
	 */
	@WithName("session-datasource")
	Optional<String> sessionDatasource();

	/**
	 * The Timer Facility extension
	 */
	@WithName("timer-facility")
	Optional<String> timerFacility();

	/**
	 * The Agent Redirect extension
	 */
	@WithName("agent-redirect")
	Optional<String> agentRedirect();

	/**
	 * The Agent Configuration extension
	 */
	@WithName("agent-configuration")
	Optional<String> agentConfiguration();

	/**
	 * The Agent Proxy extension
	 */
	@WithName("agent-proxy")
	Optional<String> agentProxy();

	/**
	 * The Overload Manager extension
	 */
	@WithName("overload-manager")
	Optional<String> overloadManager();
}
