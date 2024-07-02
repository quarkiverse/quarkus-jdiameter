package io.go.diameter.config;

import io.go.diameter.config.mapping.*;
import org.jdiameter.api.Configuration;
import org.jdiameter.client.impl.helpers.AppConfiguration;
import org.jdiameter.client.impl.helpers.ExtensionPoint;
import org.jdiameter.client.impl.helpers.Ordinal;
import org.jdiameter.client.impl.helpers.Parameters;
import org.jdiameter.server.impl.helpers.EmptyConfiguration;

import java.util.ArrayList;
import java.util.List;

public class DiameterClientConfiguration extends EmptyConfiguration
{
	public DiameterClientConfiguration(DiameterConfig config)
	{
		addLocalPeer(config.localPeer());
		addParameters(config.parameter());
		addNetwork(config.network());
		addExtensions(config.extension());
	}

	protected void addLocalPeer(LocalPeer peerConfig)
	{
		add(Parameters.OwnDiameterURI, peerConfig.uri());
		add(Parameters.OwnRealm, peerConfig.realm());
		add(Parameters.OwnVendorID, peerConfig.vendorId());
		add(Parameters.OwnProductName, peerConfig.productName());
		add(Parameters.OwnFirmwareRevision, peerConfig.firmwareRevision());
		if (peerConfig.applications().isPresent()) {
			addApplications(peerConfig.applications().get());
		}
	}

	protected Configuration addApplicationID(ApplicationId applicationId)
	{
		if (applicationId != null) {
			AppConfiguration e = EmptyConfiguration.getInstance();
			if (applicationId.vendorId().isPresent()) {
				e.add(Parameters.VendorId, applicationId.vendorId().get());
			}
			else {
				e.add(Parameters.VendorId, 0L);
			}
			if (applicationId.authApplId().isPresent()) {
				e.add(Parameters.AuthApplId, applicationId.authApplId().get());
			}
			else {
				e.add(Parameters.AuthApplId, 0L);
			}
			if (applicationId.acctApplId().isPresent()) {
				e.add(Parameters.AcctApplId, applicationId.acctApplId().get());
			}
			else {
				e.add(Parameters.AcctApplId, 0L);
			}
			return e;
		}
		return null;
	}

	protected void addApplications(List<ApplicationId> applications)
	{
		ArrayList<Configuration> items = new ArrayList<>();
		for (ApplicationId appId : applications) {
			items.add(addApplicationID(appId));
		}
		add(Parameters.ApplicationId, items.toArray(EMPTY_ARRAY));
	}

	protected void addParameters(Parameter parametersConfig)
	{
		if (parametersConfig.useUriAsFqdn().isPresent()) {
			add(Parameters.UseUriAsFqdn, parametersConfig.useUriAsFqdn().get());
		}
		else {
			add(Parameters.UseUriAsFqdn, false);
		}
		if (parametersConfig.queueSize().isPresent()) {
			add(Parameters.QueueSize, parametersConfig.queueSize().get());
		}
		if (parametersConfig.messageTimeout().isPresent()) {
			add(Parameters.MessageTimeOut, parametersConfig.messageTimeout().get());
		}
		if (parametersConfig.stopTimeout().isPresent()) {
			add(Parameters.StopTimeOut, parametersConfig.stopTimeout().get());
		}
		if (parametersConfig.ceaTimeout().isPresent()) {
			add(Parameters.CeaTimeOut, parametersConfig.ceaTimeout().get());
		}
		if (parametersConfig.iacTimeout().isPresent()) {
			add(Parameters.IacTimeOut, parametersConfig.iacTimeout().get());
		}
		if (parametersConfig.dwaTimeout().isPresent()) {
			add(Parameters.DwaTimeOut, parametersConfig.dwaTimeout().get());
		}
		if (parametersConfig.dpaTimeout().isPresent()) {
			add(Parameters.DpaTimeOut, parametersConfig.dpaTimeout().get());
		}
		if (parametersConfig.recTimeout().isPresent()) {
			add(Parameters.RecTimeOut, parametersConfig.recTimeout().get());
		}
		if (parametersConfig.peerFSMThreadCount().isPresent()) {
			add(Parameters.PeerFSMThreadCount, parametersConfig.peerFSMThreadCount().get());
		}

		if (parametersConfig.useVirtualThreads().isPresent()) {
			add(Parameters.UseVirtualThreads, parametersConfig.useVirtualThreads().get());
		}

		if (parametersConfig.concurrent().isPresent()) {
			addConcurrent(parametersConfig.concurrent().get());
		}
	}

	protected AppConfiguration createConcurrentItem(String name, int size)
	{
		AppConfiguration cfg = EmptyConfiguration.getInstance();
		cfg.add(Parameters.ConcurrentEntityName, name);
		cfg.add(Parameters.ConcurrentEntityPoolSize, size);
		return cfg;
	}

	protected void addConcurrent(Concurrent concurrent)
	{
		List<Configuration> items = new ArrayList<>();
		if (concurrent.applicationSession().isPresent()) {
			items.add(createConcurrentItem("ApplicationSession", concurrent.applicationSession().get()));
		}

		if (concurrent.duplicationMessageTimer().isPresent()) {
			items.add(createConcurrentItem("DuplicateMessageTimer", concurrent.duplicationMessageTimer().get()));
		}

		if (concurrent.processingMessageTimer().isPresent()) {
			items.add(createConcurrentItem("ProcessingMessageTimer", concurrent.processingMessageTimer().get()));
		}

		if (concurrent.connectionTimer().isPresent()) {
			items.add(createConcurrentItem("ConnectionTimer", concurrent.connectionTimer().get()));
		}

		if (concurrent.peerOverloadTimer().isPresent()) {
			items.add(createConcurrentItem("PeerOverloadTimer", concurrent.peerOverloadTimer().get()));
		}

		if (concurrent.redirectMessageTimer().isPresent()) {
			items.add(createConcurrentItem("RedirectMessageTimer", concurrent.redirectMessageTimer().get()));
		}

		if (concurrent.statisticTimer().isPresent()) {
			items.add(createConcurrentItem("StatisticTimer", concurrent.statisticTimer().get()));
		}

		if (concurrent.threadGroup().isPresent()) {
			items.add(createConcurrentItem("ThreadGroup", concurrent.threadGroup().get()));
		}

		add(Parameters.Concurrent, items.toArray(EMPTY_ARRAY));
	}

	protected void addNetwork(Network network)
	{
		addPeers(network.peers());
		addRealms(network.realms());
	}

	protected void addPeers(List<Peer> peers)
	{
		ArrayList<Configuration> items = new ArrayList<>();
		peers.forEach(peer -> items.add(addPeer(peer)));
		add(Parameters.PeerTable, items.toArray(EMPTY_ARRAY));
	}

	protected AppConfiguration addPeer(Peer peer)
	{
		AppConfiguration peerConfig = EmptyConfiguration.getInstance()
				.add(Parameters.PeerRating, peer.rating())
				.add(Parameters.PeerName, peer.peerUri());

		if (peer.ip().isPresent()) {
			peerConfig.add(Parameters.PeerIp, peer.ip().get());
		}

		if (peer.portRange().isPresent()) {
			peerConfig.add(Parameters.PeerLocalPortRange, peer.portRange().get());
		}

		return peerConfig;
	}

	protected void addRealms(List<Realm> realms)
	{
		ArrayList<Configuration> items = new ArrayList<>();
		realms.forEach(realm -> items.add(addRealm(realm)));
		add(Parameters.RealmTable, items.toArray(EMPTY_ARRAY));
	}

	protected AppConfiguration buildRealm(Realm realm)
	{
		return EmptyConfiguration.getInstance().
				add(Parameters.ApplicationId, addApplicationID(realm.applicationId()));
	}

	protected Configuration addRealm(Realm realm)
	{
		return EmptyConfiguration.getInstance().add(Parameters.RealmEntry, buildRealm(realm));
	}

	protected void addInternalExtension(Ordinal ep, String value)
	{
		Configuration[] extensionConfs = this.getChildren(org.jdiameter.client.impl.helpers.Parameters.Extensions.ordinal());
		AppConfiguration internalExtensions = (AppConfiguration) extensionConfs[org.jdiameter.client.impl.helpers.ExtensionPoint.Internal.id()];
		internalExtensions.add(ep, value);
	}

	protected void addExtensions(Extension extension)
	{
		if (extension.metaData().isPresent()) {
			addInternalExtension(ExtensionPoint.InternalMetaData, extension.metaData().get());
		}

		if (extension.messageParser().isPresent()) {
			addInternalExtension(ExtensionPoint.InternalMessageParser, extension.messageParser().get());
		}

		if (extension.elementParser().isPresent()) {
			addInternalExtension(ExtensionPoint.InternalElementParser, extension.elementParser().get());
		}

		if (extension.transportFactory().isPresent()) {
			addInternalExtension(ExtensionPoint.InternalTransportFactory, extension.transportFactory().get());
		}

		if (extension.connection().isPresent()) {
			addInternalExtension(ExtensionPoint.InternalConnectionClass, extension.connection().get());
		}

		if (extension.peerFsmFactory().isPresent()) {
			addInternalExtension(ExtensionPoint.InternalPeerFsmFactory, extension.peerFsmFactory().get());
		}

		if (extension.sessionFactory().isPresent()) {
			addInternalExtension(ExtensionPoint.InternalSessionFactory, extension.sessionFactory().get());
		}

		if (extension.routerEngine().isPresent()) {
			addInternalExtension(ExtensionPoint.InternalRouterEngine, extension.routerEngine().get());
		}

		if (extension.statisticFactory().isPresent()) {
			addInternalExtension(ExtensionPoint.InternalStatisticFactory, extension.statisticFactory().get());
		}

		if (extension.realmController().isPresent()) {
			addInternalExtension(ExtensionPoint.InternalRealmController, extension.realmController().get());
		}

		if (extension.agentRedirect().isPresent()) {
			addInternalExtension(ExtensionPoint.InternalAgentRedirect, extension.agentRedirect().get());
		}

		if (extension.agentConfiguration().isPresent()) {
			addInternalExtension(ExtensionPoint.InternalAgentConfiguration, extension.agentConfiguration().get());
		}
		if (extension.agentProxy().isPresent()) {
			addInternalExtension(ExtensionPoint.InternalAgentProxy, extension.agentProxy().get());
		}
		if (extension.sessionDatasource().isPresent()) {
			addInternalExtension(ExtensionPoint.InternalSessionDatasource, extension.agentRedirect().get());
		}
		if (extension.timerFacility().isPresent()) {
			addInternalExtension(ExtensionPoint.InternalTimerFacility, extension.timerFacility().get());
		}
		if (extension.peerController().isPresent()) {
			addInternalExtension(ExtensionPoint.InternalPeerController, extension.peerController().get());
		}
	}
}
