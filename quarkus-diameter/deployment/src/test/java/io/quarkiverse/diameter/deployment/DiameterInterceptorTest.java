package io.quarkiverse.diameter.deployment;

import io.quarkiverse.diameter.DiameterService;
import io.quarkiverse.diameter.DiameterServiceOptions;
import io.quarkus.test.QuarkusUnitTest;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.cca.ClientCCASession;
import org.jdiameter.api.cca.ClientCCASessionListener;
import org.jdiameter.api.cca.events.JCreditControlAnswer;
import org.jdiameter.api.cca.events.JCreditControlRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class DiameterInterceptorTest
{
	@RegisterExtension
	static final QuarkusUnitTest config = new QuarkusUnitTest().setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class).addClasses(DiameterServiceTest.class)).withConfigurationResource("application.properties");


	@Test
	public void testInterceptor()
	{
		//Dummy test to trigger the test case
	}

	@DiameterServiceOptions
	@DiameterService
	static class DiameterServiceTest implements ClientCCASessionListener
	{
		@Override
		public void doCreditControlAnswer(ClientCCASession session, JCreditControlRequest request, JCreditControlAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException
		{

		}
	}
}
