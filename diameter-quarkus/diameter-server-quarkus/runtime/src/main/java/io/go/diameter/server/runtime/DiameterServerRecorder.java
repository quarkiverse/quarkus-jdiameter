package io.go.diameter.server.runtime;

import io.go.diameter.config.DiameterServerConfig;
import io.go.diameter.config.DiameterServerConfiguration;
import io.quarkus.arc.SyntheticCreationalContext;
import io.quarkus.runtime.annotations.Recorder;
import jakarta.inject.Inject;
import org.jdiameter.api.Configuration;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

@Recorder
public class DiameterServerRecorder
{
	@Inject
	DiameterServerConfig config;

	private final ReentrantLock lock = new ReentrantLock();
	private DiameterServerConfiguration serverConfiguration;

	public Function<SyntheticCreationalContext<Configuration>, Configuration> serverConfiguration()
	{
		return context -> {
			lock.lock();
			try {
				if (serverConfiguration == null) {
					serverConfiguration = new DiameterServerConfiguration(config);
				}
				return serverConfiguration;
			}
			finally {
				lock.unlock();
			}
		};
	}
}
