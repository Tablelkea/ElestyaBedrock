package org.elestya.bedrock.api.sources;

import org.bukkit.Material;

import java.util.Objects;

public enum ShopCategory {

    BLOCK("Blocs", Material.BRICKS),
    FOOD("Nourritures", Material.COOKED_BEEF),
    DROPS("Butins", Material.BONE),
    PLANTS("Plantes", Material.OAK_SAPLING),
    ORE("Minerais", Material.DIAMOND),
    FISHING("Prises de pêche", Material.COD);

    private final String displayName;
    private final Material icon;

    ShopCategory(String displayName, Material icon){
        this.displayName = Objects.requireNonNull(displayName);
        this.icon = Objects.requireNonNull(icon);
    }

    public Material getIcon() {
        return icon;
    }

    public String getDisplayName() {
        return displayName;
    }
}
