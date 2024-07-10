package io.go.diameter.deployment;

import io.go.diameter.DiameterConfig;
import io.quarkus.test.QuarkusUnitTest;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jdiameter.api.Configuration;
import org.jdiameter.server.impl.helpers.Parameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DiameterConfigTest
{
	@RegisterExtension
	static final QuarkusUnitTest config = new QuarkusUnitTest()
			.setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class))
			.withConfigurationResource("application.properties");

	@DiameterConfig
	Configuration defaultConfiguration;

	@DiameterConfig("test1")
	Configuration test1Configuration;


	@Test
	public void testConfig()
	{
		assertNotNull(defaultConfiguration);
		assertEquals("aaa://ocsclient:1812", defaultConfiguration.getStringValue(Parameters.OwnDiameterURI.ordinal(), null));
		assertNotNull(test1Configuration);
		assertEquals("aaa://ocsclient:1813", test1Configuration.getStringValue(Parameters.OwnDiameterURI.ordinal(), null));
	}
}
