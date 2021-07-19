package com.redspeaks.mcores.lib;

import org.bukkit.Material;

public class BlockComposition {

    private final Material type;
    private int percentage;
    public BlockComposition(Material type, int percentage) {
        this.type = type;
        this.percentage = percentage;
    }

    public Material getType() {
        return type;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }
}
