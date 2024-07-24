package io.quarkiverse.diameter.deployment;

import io.quarkiverse.diameter.DiameterConfig;
import io.quarkus.test.QuarkusUnitTest;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jdiameter.api.Stack;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DiameterStackTest
{
	@RegisterExtension
	static final QuarkusUnitTest config = new QuarkusUnitTest().setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)).withConfigurationResource("application.properties");

	@DiameterConfig
	Stack defaultStack;

	@DiameterConfig("test1")
	Stack test1Stack;


	@Test
	public void testConfig()
	{
		assertNotNull(defaultStack);
		assertEquals("aaa://ocsclient:1812", defaultStack.getMetaData().getLocalPeer().getUri().toString());
		assertNotNull(test1Stack);
		assertEquals("aaa://ocsclient:1813", test1Stack.getMetaData().getLocalPeer().getUri().toString());
	}
}
