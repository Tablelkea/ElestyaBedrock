package org.elestya.bedrock.api.sources;

import org.bukkit.Material;

public enum GemShopCategory {
    RANKS("Grades", Material.NETHER_STAR),
    PASSES("Pass", Material.PAPER),
    OTHER("Autres", Material.DIAMOND);

    private final String displayName;
    private final Material icon;

    GemShopCategory(String displayName, Material icon) {
        this.displayName = displayName;
        this.icon = icon;
    }

    public String displayName() {
        return displayName;
    }

    public Material icon() {
        return icon;
    }
}
