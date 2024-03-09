package com.lowdetailchambers;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.VarPlayer;
import net.runelite.api.Varbits;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.RuneLiteConfig;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.lowmemory.LowMemoryConfig;

@Slf4j
@PluginDescriptor(
	name = "Low Detail Chambers",
	description = "Turn off ground decorations and certain textures only inside of Chambers of Xeric",
	tags = {"memory", "usage", "ground", "decorations", "chambers", "cox"}
)
public class LowDetailChambersPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private PluginManager pluginManager;

	@Inject
	private ConfigManager configManager;

	private boolean lowDetailEnabled = false;

	@Override
	protected void startUp() throws Exception
	{
		clientThread.invoke(() ->
		{
			if (client.getGameState().getState() >= GameState.LOGIN_SCREEN.getState())
			{
				if (insideChambersOfXeric() && lowDetailDisabled() && !lowDetailEnabled)
				{
					client.changeMemoryMode(true);
					lowDetailEnabled = true;
				}
				return true;
			}
			return false;
		});
	}

	@Override
	protected void shutDown() throws Exception
	{
		clientThread.invoke(() ->
		{
			if (lowDetailDisabled() && lowDetailEnabled)
			{
				client.changeMemoryMode(false);
				lowDetailEnabled = false;
			}
		});
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		if (event.getVarbitId() == Varbits.IN_RAID || event.getVarpId() == VarPlayer.IN_RAID_PARTY || event.getVarbitId() == Varbits.RAID_STATE)
		{
			if (!lowDetailDisabled())
			{
				return;
			}

			boolean inRaidChambers = insideChambersOfXeric();
			if (inRaidChambers != lowDetailEnabled)
			{
				client.changeMemoryMode(inRaidChambers);
				lowDetailEnabled = inRaidChambers;
			}
		}
	}

	private boolean insideChambersOfXeric()
	{
		if (client.getVarbitValue(Varbits.IN_RAID) != 1)
		{
			// Not inside the lobby or the raid levels.
			return false;
		}

		int raidPartyID = client.getVarpValue(VarPlayer.IN_RAID_PARTY);
		if (raidPartyID == -1)
		{
			// Raid party ID is -1 when:
			// 1. We're not in a raid party at all (e.g. outside the raid)
			// 2. We were in a party but we're currently reloading the raid from the inside stairs
			// 3. We were in a party but then we started the raid
			//
			// Only #3 is a valid reason to enable low detail mode. The other two cases should NOT result
			// in toggling low detail.

			// The plugin crashes if we check RAID_STATE while not inside Chambers, so only check
			// RAID_STATE here now that we know it's safe.
			int raidState = client.getVarbitValue(Varbits.RAID_STATE);
			return raidState != 0 && raidState != 5;
		}

		// We're in the lobby, we haven't started the raid, and we're not currently reloading the raid.
		return true;
	}

	private boolean lowDetailDisabled()
	{
		final String value = configManager.getConfiguration(RuneLiteConfig.GROUP_NAME, "lowmemoryplugin");

		boolean lowMemoryPluginEnabled = value != null ? Boolean.parseBoolean(value) : false;
		boolean lowMemoryConfigEnabled = configManager.getConfig(LowMemoryConfig.class).lowDetail();

		return !lowMemoryPluginEnabled | !lowMemoryConfigEnabled;
	}

}
