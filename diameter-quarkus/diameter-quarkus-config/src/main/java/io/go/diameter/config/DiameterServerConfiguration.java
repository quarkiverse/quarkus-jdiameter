package io.go.diameter.config;

import io.go.diameter.config.mapping.*;
import org.jdiameter.api.Configuration;
import org.jdiameter.client.impl.helpers.AppConfiguration;
import org.jdiameter.server.impl.helpers.EmptyConfiguration;
import org.jdiameter.server.impl.helpers.ExtensionPoint;
import org.jdiameter.server.impl.helpers.Parameters;

import java.util.ArrayList;
import java.util.List;

public class DiameterServerConfiguration extends DiameterClientConfiguration
{
	public DiameterServerConfiguration(DiameterConfig config)
	{
		super(config);
	}

	@Override
	protected void addLocalPeer(LocalPeer peerConfig)
	{
		super.addLocalPeer(peerConfig);

		addIPAddresses(peerConfig.ipAddresses());

		if (peerConfig.overloadMonitors().isPresent()) {
			addOverloadMonitor(peerConfig.overloadMonitors().get());
		}
	}

	protected void addOverloadMonitor(List<OverloadMonitor> entries)
	{
		ArrayList<Configuration> items = new ArrayList<>();
		entries.forEach(entry -> items.add(addOverloadMonitorItem(entry)));
		add(Parameters.OverloadMonitor, items.toArray(EMPTY_ARRAY));
	}

	protected Configuration addOverloadMonitorItem(OverloadMonitor entry)
	{
		return EmptyConfiguration.getInstance()
				.add(Parameters.OverloadEntryIndex, entry.index())
				.add(Parameters.OverloadEntrylowThreshold, entry.lowThreshold())
				.add(Parameters.OverloadEntryhighThreshold, entry.highThreshold())
				.add(Parameters.ApplicationId, addApplicationID(entry.applicationId()));
	}

	protected void addIPAddresses(List<String> ipAddresses)
	{
		ArrayList<Configuration> items = new ArrayList<>();
		ipAddresses.forEach(ip -> items.add(EmptyConfiguration.getInstance().add(Parameters.OwnIPAddress, ip)));
		add(Parameters.OwnIPAddresses, items.toArray(EMPTY_ARRAY));
	}

	@Override
	protected AppConfiguration addPeer(Peer peer)
	{
		AppConfiguration peerConfig = super.addPeer(peer);

		peerConfig.add(Parameters.PeerAttemptConnection, peer.attemptConnect());

		return peerConfig;
	}

	@Override
	protected AppConfiguration buildRealm(Realm realm)
	{
		AppConfiguration realmEntry = super.buildRealm(realm);
		realmEntry.add(Parameters.RealmName, realm.realmName())
				.add(Parameters.RealmHosts, realm.peers())
				.add(Parameters.RealmLocalAction, realm.localAction().name())
				.add(Parameters.RealmEntryIsDynamic, realm.dynamic())
				.add(Parameters.RealmEntryExpTime, realm.expTime());
		return realmEntry;
	}

	@Override
	protected void addParameters(Parameter parametersConfig)
	{
		super.addParameters(parametersConfig);

		if (parametersConfig.acceptUndefinedPeer().isPresent()) {
			add(Parameters.AcceptUndefinedPeer, parametersConfig.acceptUndefinedPeer().get());
		}
		else {
			add(Parameters.AcceptUndefinedPeer, false);
		}

		if (parametersConfig.duplicateTimer().isPresent()) {
			add(Parameters.DuplicateTimer, parametersConfig.duplicateTimer().get());
		}
		else {
			add(Parameters.DuplicateTimer, 240000L);
		}

		if (parametersConfig.duplicateProtection().isPresent()) {
			add(Parameters.DuplicateProtection, parametersConfig.duplicateProtection().get());
		}
		else {
			add(Parameters.DuplicateProtection, false);
		}

		if (parametersConfig.duplicateSize().isPresent()) {
			add(Parameters.DuplicateSize, parametersConfig.duplicateSize().get());
		}
		else {
			add(Parameters.DuplicateSize, 5000L);
		}

		if (parametersConfig.bindDelay().isPresent()) {
			add(Parameters.BindDelay, parametersConfig.bindDelay().get());
		}
	}

	@Override
	protected void addExtensions(Extension extension)
	{
		super.addExtensions(extension);

		if (extension.networkGuard().isPresent()) {
			addInternalExtension(ExtensionPoint.InternalNetworkGuard, extension.networkGuard().get());
		}

		if (extension.network().isPresent()) {
			addInternalExtension(ExtensionPoint.InternalNetWork, extension.network().get());
		}

		if (extension.overloadManager().isPresent()) {
			addInternalExtension(ExtensionPoint.InternalOverloadManager, extension.overloadManager().get());
		}
	}
}
