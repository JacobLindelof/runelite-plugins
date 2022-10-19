package com.energyinfo;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("energyinfo")
public interface EnergyInfoConfig extends Config
{
	@ConfigItem(
		keyName = "displayInfobox",
		name = "Display Infobox",
		description = "Display run energy information as an infobox."
	)
	default boolean displayInfobox()
	{
		return true;
	}

	@ConfigItem(
			keyName = "lowEnergyThreshold",
			name = "Low Energy Threshold",
			description = "Threshold for low run energy."
	)
	default int lowEnergyThreshold()
	{
		return 0;
	}
}
