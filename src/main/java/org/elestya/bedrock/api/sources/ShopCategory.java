package org.elestya.bedrock.api.sources;

import org.bukkit.Material;

import java.util.Objects;

public enum ShopCategory {

    BLOCK("Blocs", Material.BRICKS),
    FOOD("Nourritures", Material.COOKED_BEEF),
    DROPS("Butins", Material.BONE),
    PLANTS("Plantes", Material.OAK_SAPLING),
    ORE("Minerais", Material.DIAMOND);

    private final String name;
    private final Material icon;

    ShopCategory(String name, Material icon){
        this.name = Objects.requireNonNull(name);
        this.icon = Objects.requireNonNull(icon);
    }

    public Material icon() {
        return icon;
    }

    public String text() {
        return name;
    }
}
