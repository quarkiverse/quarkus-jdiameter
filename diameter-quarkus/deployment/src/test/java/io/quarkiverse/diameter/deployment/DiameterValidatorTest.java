package io.quarkiverse.diameter.deployment;

import io.quarkiverse.diameter.DiameterService;
import io.quarkiverse.diameter.runtime.DiameterSetupException;
import io.quarkus.test.QuarkusUnitTest;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class DiameterValidatorTest
{
	@RegisterExtension
	static final QuarkusUnitTest config = new QuarkusUnitTest().setExpectedException(DiameterSetupException.class).setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class).addClasses(TestClass.class)).withConfigurationResource("application.properties");


	@Test
	public void testValidation()
	{
		//Dummy test to trigger the test case
	}

	@DiameterService
	static class TestClass
	{

	}
}
