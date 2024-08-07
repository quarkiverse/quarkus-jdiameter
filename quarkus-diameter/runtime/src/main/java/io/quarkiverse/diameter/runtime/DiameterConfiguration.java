package io.quarkiverse.diameter.runtime;

import io.quarkiverse.diameter.runtime.config.*;
import io.quarkiverse.diameter.runtime.transport.TLSClientConnection;
import io.quarkus.tls.TlsConfiguration;
import io.quarkus.tls.TlsConfigurationRegistry;
import org.jdiameter.api.Configuration;
import org.jdiameter.client.impl.helpers.AppConfiguration;
import org.jdiameter.client.impl.helpers.Ordinal;
import org.jdiameter.server.impl.helpers.EmptyConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.jdiameter.server.impl.helpers.ExtensionPoint.*;
import static org.jdiameter.server.impl.helpers.Parameters.*;

public class DiameterConfiguration extends EmptyConfiguration
{
    private static final Logger LOG = LoggerFactory.getLogger(DiameterConfiguration.class);
    private final TlsConfigurationRegistry tlsRegistry;
    private final List<Configuration> securityItems;

    public DiameterConfiguration(DiameterDetailConfig config, TlsConfigurationRegistry tlsRegistry)
    {
        super(true);

        securityItems    = new ArrayList<>();
        this.tlsRegistry = tlsRegistry;

        addLocalPeer(config.localPeer());
        addParameters(config.parameter());
        addNetwork(config.network());
        if (config.extensions() != null) {
            addExtensions(config.extensions());
        }
        if (!securityItems.isEmpty()) {
            addInternalExtension(InternalTransportFactory, TLSClientConnection.class.getCanonicalName());
            add(Security, securityItems.toArray(EMPTY_ARRAY));
        }//if
    }

    protected void addLocalPeer(LocalPeer peerConfig)
    {
        LOG.warn("Adding local peer '{}'.", peerConfig.uri());
        add(OwnDiameterURI, peerConfig.uri());
        add(OwnRealm, peerConfig.realm());
        add(OwnVendorID, peerConfig.vendorId());
        add(OwnProductName, peerConfig.productName());
        add(OwnFirmwareRevision, peerConfig.firmwareRevision());
        if (!peerConfig.applications().isEmpty()) {
            addApplications(peerConfig.applications().values());
        }
        addIPAddresses(peerConfig.ipAddresses());

        if (!peerConfig.overloadMonitors().isEmpty()) {
            addOverloadMonitor(peerConfig.overloadMonitors().values());
        }

        if (peerConfig.tlsConfigurationName().isPresent()) {
            TlsConfiguration tlsConfig = tlsRegistry.get(peerConfig.tlsConfigurationName().get()).orElse(null);
            if (tlsConfig == null) {
                throw new DiameterSetupException("Tls configuration '" + peerConfig.tlsConfigurationName().get() + "' not found");
            }

            add(SecurityRef, peerConfig.tlsConfigurationName().get());
            securityItems.add(getInstance().add(SDName, peerConfig.tlsConfigurationName().get()).add(SecurityData, tlsConfig));

        }
    }

    protected void addOverloadMonitor(Collection<OverloadMonitor> entries)
    {
        ArrayList<Configuration> items = new ArrayList<>();
        entries.forEach(entry -> items.add(addOverloadMonitorItem(entry)));
        add(OverloadMonitor, items.toArray(EMPTY_ARRAY));
    }

    protected Configuration addOverloadMonitorItem(OverloadMonitor entry)
    {
        return EmptyConfiguration.getInstance().add(OverloadEntryIndex, entry.index()).add(OverloadEntrylowThreshold, entry.lowThreshold()).add(OverloadEntryhighThreshold, entry.highThreshold()).add(ApplicationId, addApplicationID(entry.applicationId()));
    }

    protected void addIPAddresses(Set<String> ipAddresses)
    {
        ArrayList<Configuration> items = new ArrayList<>();
        ipAddresses.forEach(ip -> items.add(EmptyConfiguration.getInstance().add(OwnIPAddress, ip.trim())));
        add(OwnIPAddresses, items.toArray(EMPTY_ARRAY));
    }

    protected Configuration addApplicationID(io.quarkiverse.diameter.runtime.config.ApplicationId applicationId)
    {
        return EmptyConfiguration.getInstance().add(VendorId, applicationId.vendorId()).add(AuthApplId, applicationId.authApplId()).add(AcctApplId, applicationId.acctApplId());
    }

    protected void addApplications(Collection<ApplicationId> applications)
    {
        ArrayList<Configuration> items = new ArrayList<>();
        for (ApplicationId appId : applications) {
            items.add(addApplicationID(appId));
        }
        add(ApplicationId, items.toArray(EMPTY_ARRAY));
    }

    protected void addParameters(Parameter parametersConfig)
    {
        add(AcceptUndefinedPeer, parametersConfig.acceptUndefinedPeer());

        add(DuplicateProtection, parametersConfig.duplicateProtection());

        add(UseUriAsFqdn, parametersConfig.useUriAsFqdn());

        add(DuplicateTimer, parametersConfig.duplicateTimer());

        add(DuplicateSize, parametersConfig.duplicateSize());

        add(UseVirtualThreads, parametersConfig.useVirtualThreads());

        add(CachingName, parametersConfig.cachingName());

        if (parametersConfig.queueSize().isPresent()) {
            add(QueueSize, parametersConfig.queueSize().get());
        }
        if (parametersConfig.messageTimeout().isPresent()) {
            add(MessageTimeOut, parametersConfig.messageTimeout().get());
        }
        if (parametersConfig.stopTimeout().isPresent()) {
            add(StopTimeOut, parametersConfig.stopTimeout().get());
        }
        if (parametersConfig.ceaTimeout().isPresent()) {
            add(CeaTimeOut, parametersConfig.ceaTimeout().get());
        }
        if (parametersConfig.iacTimeout().isPresent()) {
            add(IacTimeOut, parametersConfig.iacTimeout().get());
        }
        if (parametersConfig.dwaTimeout().isPresent()) {
            add(DwaTimeOut, parametersConfig.dwaTimeout().get());
        }
        if (parametersConfig.dpaTimeout().isPresent()) {
            add(DpaTimeOut, parametersConfig.dpaTimeout().get());
        }
        if (parametersConfig.recTimeout().isPresent()) {
            add(RecTimeOut, parametersConfig.recTimeout().get());
        }
        if (parametersConfig.peerFSMThreadCount().isPresent()) {
            add(PeerFSMThreadCount, parametersConfig.peerFSMThreadCount().get());
        }

        if (parametersConfig.sessionTimeout().isPresent()) {
            add(SessionTimeOut, parametersConfig.sessionTimeout().get());
        }

        if (parametersConfig.bindDelay().isPresent()) {
            add(BindDelay, parametersConfig.bindDelay().get());
        }

        if (parametersConfig.concurrent().isPresent()) {
            addConcurrent(parametersConfig.concurrent().get());
        }

    }

    protected void addConcurrent(io.quarkiverse.diameter.runtime.config.Concurrent concurrent)
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

        add(Concurrent, items.toArray(EMPTY_ARRAY));
    }

    protected AppConfiguration createConcurrentItem(String name, int size)
    {
        AppConfiguration cfg = EmptyConfiguration.getInstance();
        cfg.add(ConcurrentEntityName, name);
        cfg.add(ConcurrentEntityPoolSize, size);
        return cfg;
    }


    protected void addNetwork(Network network)
    {
        addPeers(network.peers().values());
        addRealms(network.realms().values());
    }

    protected void addPeers(Collection<Peer> peers)
    {
        ArrayList<Configuration> items = new ArrayList<>();
        peers.forEach(peer -> items.add(addPeer(peer)));
        add(PeerTable, items.toArray(EMPTY_ARRAY));
    }

    protected AppConfiguration addPeer(Peer peer)
    {
        AppConfiguration peerConfig = EmptyConfiguration.getInstance().add(PeerRating, peer.rating()).add(PeerName, peer.peerUri()).add(PeerAttemptConnection, peer.attemptConnect());

        if (peer.ip().isPresent()) {
            peerConfig.add(PeerIp, peer.ip().get());
        }

        if (peer.portRange().isPresent()) {
            peerConfig.add(PeerLocalPortRange, peer.portRange().get());
        }

        if (peer.tlsConfigurationName().isPresent()) {
            TlsConfiguration tlsConfig = tlsRegistry.get(peer.tlsConfigurationName().get()).orElse(null);
            if (tlsConfig == null) {
                throw new DiameterSetupException("Tls configuration '" + peer.tlsConfigurationName().get() + "' not found");
            }
            peerConfig.add(SecurityRef, peer.tlsConfigurationName().get());
            securityItems.add(getInstance().add(SDName, peer.tlsConfigurationName().get()).add(SecurityData, tlsConfig));
        }

        return peerConfig;
    }

    protected void addRealms(Collection<Realm> realms)
    {
        ArrayList<Configuration> items = new ArrayList<>();
        realms.forEach(realm -> items.add(addRealm(realm)));
        add(RealmTable, items.toArray(EMPTY_ARRAY));
    }

    protected AppConfiguration addAgent(Map<String, String> properties)
    {
        AppConfiguration agentConf = getInstance();
        List<Configuration> props = new ArrayList<>();

        for (Map.Entry<String, String> prop : properties.entrySet()) {
            AppConfiguration property = getInstance();
            property.add(PropertyName, prop.getKey());
            property.add(PropertyValue, prop.getValue());
            props.add(property);
        }

        agentConf.add(Properties, props.toArray(EMPTY_ARRAY));
        return agentConf;
    }

    protected AppConfiguration buildRealm(Realm realm)
    {
        AppConfiguration realmEntry = EmptyConfiguration.getInstance()
                                                        .add(RealmName, realm.realmName())
                                                        .add(RealmHosts, realm.peers())
                                                        .add(RealmLocalAction, realm.localAction().name())
                                                        .add(RealmEntryIsDynamic, realm.dynamic())
                                                        .add(RealmEntryExpTime, realm.expTime());
        if (realm.applicationId().isPresent()) {
            realmEntry.add(ApplicationId, addApplicationID(realm.applicationId().get()));
        }

        if (realm.agent().isPresent()) {
            realmEntry.add(Agent, addAgent(realm.agent().get().properties()));
        }

        return realmEntry;
    }

    protected Configuration addRealm(Realm realm)
    {
        return EmptyConfiguration.getInstance().add(RealmEntry, buildRealm(realm));
    }

    protected void addInternalExtension(Ordinal ep, String value)
    {
        Configuration[] extensionConfs = this.getChildren(Extensions.ordinal());
        AppConfiguration internalExtensions = (AppConfiguration) extensionConfs[Internal.id()];
        internalExtensions.add(ep, value);
    }

    protected void addExtensions(Extension extension)
    {
        if (extension.metaData().isPresent()) {
            addInternalExtension(InternalMetaData, extension.metaData().get());
        }

        if (extension.messageParser().isPresent()) {
            addInternalExtension(InternalMessageParser, extension.messageParser().get());
        }

        if (extension.elementParser().isPresent()) {
            addInternalExtension(InternalElementParser, extension.elementParser().get());
        }

        if (extension.transportFactory().isPresent()) {
            addInternalExtension(InternalTransportFactory, extension.transportFactory().get());
        }

        if (extension.connection().isPresent()) {
            addInternalExtension(InternalConnectionClass, extension.connection().get());
        }

        if (extension.peerFsmFactory().isPresent()) {
            addInternalExtension(InternalPeerFsmFactory, extension.peerFsmFactory().get());
        }

        if (extension.sessionFactory().isPresent()) {
            addInternalExtension(InternalSessionFactory, extension.sessionFactory().get());
        }

        if (extension.routerEngine().isPresent()) {
            addInternalExtension(InternalRouterEngine, extension.routerEngine().get());
        }

        if (extension.statisticFactory().isPresent()) {
            addInternalExtension(InternalStatisticFactory, extension.statisticFactory().get());
        }

        if (extension.realmController().isPresent()) {
            addInternalExtension(InternalRealmController, extension.realmController().get());
        }

        if (extension.agentRedirect().isPresent()) {
            addInternalExtension(InternalAgentRedirect, extension.agentRedirect().get());
        }

        if (extension.agentConfiguration().isPresent()) {
            addInternalExtension(InternalAgentConfiguration, extension.agentConfiguration().get());
        }

        if (extension.agentProxy().isPresent()) {
            addInternalExtension(InternalAgentProxy, extension.agentProxy().get());
        }

        if (extension.sessionDatasource().isPresent()) {
            addInternalExtension(InternalSessionDatasource, extension.sessionDatasource().get());
        }

        if (extension.timerFacility().isPresent()) {
            addInternalExtension(InternalTimerFacility, extension.timerFacility().get());
        }

        if (extension.peerController().isPresent()) {
            addInternalExtension(InternalPeerController, extension.peerController().get());
        }

        if (extension.networkGuard().isPresent()) {
            addInternalExtension(InternalNetworkGuard, extension.networkGuard().get());
        }

        if (extension.network().isPresent()) {
            addInternalExtension(InternalNetWork, extension.network().get());
        }

        if (extension.overloadManager().isPresent()) {
            addInternalExtension(InternalOverloadManager, extension.overloadManager().get());
        }

        if (extension.concurrentFactory().isPresent()) {
            addInternalExtension(InternalConcurrentFactory, extension.concurrentFactory().get());
        }

        if (extension.concurrentEntityFactory().isPresent()) {
            addInternalExtension(InternalConcurrentEntityFactory, extension.concurrentEntityFactory().get());
        }

        if (extension.statisticProcessor().isPresent()) {
            addInternalExtension(InternalSessionFactory, extension.statisticProcessor().get());
        }
    }
}
