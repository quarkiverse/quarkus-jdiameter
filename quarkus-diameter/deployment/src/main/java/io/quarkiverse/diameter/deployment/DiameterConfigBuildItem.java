package io.quarkiverse.diameter.deployment;

import io.quarkus.builder.item.MultiBuildItem;

public final class DiameterConfigBuildItem extends MultiBuildItem
{

    private final String name;

    public DiameterConfigBuildItem(String name)
    {
        this.name = name;
    }

    public String getProfileName()
    {
        return name;
    }
}
