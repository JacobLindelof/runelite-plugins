package com.energyinfo;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Skill;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

@Slf4j
@PluginDescriptor(
	name = "Energy Info"
)
public class EnergyInfoPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private InfoBoxManager infoBoxManager;

	@Inject
	private SkillIconManager skillIconManager;

	@Inject
	private EnergyInfoConfig config;

	@Override
	protected void startUp() throws Exception
	{
		infoBoxManager.addInfoBox(new EnergyInfoBox(skillIconManager.getSkillImage(Skill.AGILITY), this, client, config));
	}

	@Override
	protected void shutDown() throws Exception
	{
		infoBoxManager.removeIf(t -> t instanceof EnergyInfoBox);
	}


	@Provides
	EnergyInfoConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(EnergyInfoConfig.class);
	}
}
