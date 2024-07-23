package io.quarkiverse.diameter.runtime.transport;

import io.quarkus.tls.TlsConfiguration;
import org.jdiameter.api.Configuration;
import org.jdiameter.client.api.io.IConnectionListener;
import org.jdiameter.client.api.parser.IMessageParser;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.net.InetAddress;
import java.net.Socket;

import static org.jdiameter.client.impl.helpers.Parameters.SecurityData;

public class TLSClientConnection extends org.jdiameter.client.impl.transport.tls.TLSClientConnection
{

	public TLSClientConnection(Configuration config, IConcurrentFactory concurrentFactory, InetAddress remoteAddress, int remotePort, InetAddress localAddress, int localPort, IMessageParser parser, String ref)
	{
		super(config, concurrentFactory, remoteAddress, remotePort, localAddress, localPort, parser, ref);
	}

	public TLSClientConnection(Configuration config, IConcurrentFactory concurrentFactory, InetAddress remoteAddress, int remotePort, InetAddress localAddress, int localPort, IConnectionListener listener, IMessageParser parser, String ref)
	{
		super(config, concurrentFactory, remoteAddress, remotePort, localAddress, localPort, listener, parser, ref);
	}

	public TLSClientConnection(Configuration config, Configuration localPeerSSLConfig, IConcurrentFactory concurrentFactory, Socket socket, IMessageParser parser) throws Exception
	{
		super(config, localPeerSSLConfig, concurrentFactory, socket, parser);
	}


	@Override
	protected SSLSocketFactory fillSecurityData(Configuration sslConfig) throws Exception
	{
		Object secData = sslConfig.getValue(SecurityData.ordinal());
		if (secData instanceof TlsConfiguration tlsConfiguration) {
			SSLContext context = tlsConfiguration.createSSLContext();
			return context.getSocketFactory();
		}

		return super.fillSecurityData(sslConfig);
	}

	@Override
	protected SSLSocketFactory fillSecurityData(Configuration config, String ref) throws Exception
	{
		Object secData = config.getValue(SecurityData.ordinal());
		if (secData instanceof TlsConfiguration tlsConfiguration) {
			SSLContext context = tlsConfiguration.createSSLContext();
			return context.getSocketFactory();
		}

		return super.fillSecurityData(config, ref);
	}
}
