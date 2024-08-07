package io.quarkiverse.diameter.runtime.config;

import io.quarkus.runtime.annotations.ConfigDocSection;
import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;
import io.smallrye.config.WithUnnamedKey;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@ConfigGroup
public interface LocalPeer
{
    /**
     * Specifies the URI for the local peer. The URI has the following format: "aaa://FQDN:port".
     */
    @WithDefault("aaa://localhost:1812")
    @WithName("uri")
    String uri();

    /**
     * The name of the TLS configuration to use.
     * <p>
     * If not set and the default TLS configuration is configured ({@code quarkus.tls.*}) then that will be used.
     * If a name is configured, it uses the configuration from {@code quarkus.tls.<name>.*}
     * If a name is configured, but no TLS configuration is found with that name then an error will be thrown.
     * <p>
     * If no TLS configuration is set, and {@code quarkus.tls.*} is not configured, then, no security will be used
     */
    @WithName("tls-configuration-name")
    Optional<String> tlsConfigurationName();

    /**
     * Contains one or more valid IP address for the local peer.`
     */
    @WithName("ip-addresses")
    @WithDefault("127.0.0.1")
    Set<String> ipAddresses();

    /**
     * Specifies the realm of the local peer.
     */
    @WithName("realm")
    @WithDefault("io.quarkiverse.diameter")
    String realm();

    /**
     * Specifies the name of the local peer product
     */
    @WithDefault("Go Diameter")
    @WithName("product-name")
    String productName();

    /**
     * Specifies the version of the firmware.
     */
    @WithDefault("3")
    @WithName("firmware-revision")
    long firmwareRevision();

    /**
     * Specifies a numeric identifier that corresponds to the vendor ID allocated by IANA.
     */
    @WithDefault("0")
    @WithName("vendor-id")
    long vendorId();

    /**
     * Contains a list of default supported applications.
     */
    @WithName("applications")
    @ConfigDocSection
    @WithUnnamedKey
    Map<String, ApplicationId> applications();

    /**
     * Optional parent element containing child elements that specify settings
     * relating to the Overload Monitor. The map key is the index of this overload monitor,
     * so priorities/orders can be specified.
     */
    @WithName("overload-monitors")
    @ConfigDocSection
    Map<String, OverloadMonitor> overloadMonitors();
}
