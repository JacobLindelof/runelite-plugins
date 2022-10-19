package com.energyinfo;

import java.awt.Color;
import java.awt.image.BufferedImage;

import net.runelite.api.Client;
import net.runelite.api.Varbits;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.ui.overlay.infobox.InfoBoxPriority;

class EnergyInfoBox extends InfoBox
{
    private static final Color STAMINA_ACTIVE = new Color(0, 255, 0, 255);
    private static final Color STAMINA_INACTIVE = new Color(255, 255, 255, 220);
    private static final Color BELOW_THRESHOLD = new Color(255, 0, 0, 220);

    private final EnergyInfoPlugin plugin;
    private final EnergyInfoConfig config;
    private final Client client;


    EnergyInfoBox(BufferedImage image, EnergyInfoPlugin plugin, Client client, EnergyInfoConfig config)
    {
        super(image, plugin);
        this.plugin = plugin;
        this.config = config;
        this.client = client;
        setTooltip("Run energy");
        setPriority(InfoBoxPriority.HIGH);
    }

    @Override
    public String getText()
    {
        return String.valueOf(client.getEnergy()) + '%';
    }

    @Override
    public Color getTextColor()
    {
        if (client.getVarbitValue(Varbits.RUN_SLOWED_DEPLETION_ACTIVE) != 0)
        {
            return STAMINA_ACTIVE;
        }
        if (config.lowEnergyThreshold() > 0 & client.getEnergy() < config.lowEnergyThreshold())
        {
            return BELOW_THRESHOLD;
        }
        else
        {
            return STAMINA_INACTIVE;
        }
    }

    @Override
    public boolean render()
    {
        return config.displayInfobox();
    }

}