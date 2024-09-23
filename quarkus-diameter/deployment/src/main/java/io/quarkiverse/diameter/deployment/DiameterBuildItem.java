package io.quarkiverse.diameter.deployment;

import io.quarkus.builder.item.MultiBuildItem;
import io.quarkus.runtime.RuntimeValue;
import org.jdiameter.api.Configuration;
import org.jdiameter.api.Stack;
 
public final class DiameterBuildItem extends MultiBuildItem
{
	private final RuntimeValue<Configuration> configuration;
	private final RuntimeValue<Stack> stack;
	private final String name;

	public DiameterBuildItem(String name,
	                         RuntimeValue<Stack> stack,
	                         RuntimeValue<Configuration> configuration)
	{
		this.stack         = stack;
		this.configuration = configuration;
		this.name          = name;
	}

	public RuntimeValue<Stack> getStack()
	{
		return stack;
	}

	public RuntimeValue<Configuration> getConfiguration()
	{
		return configuration;
	}

	public String getName()
	{
		return name;
	}
}
