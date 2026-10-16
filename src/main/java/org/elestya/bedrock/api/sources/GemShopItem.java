package org.elestya.bedrock.api.sources;

import org.bukkit.Material;

import java.util.Objects;

public record GemShopItem(String id, String name, String description, Material icon,
                          GemShopCategory category, int gemPrice) {

    public GemShopItem {
        Objects.requireNonNull(id);
        Objects.requireNonNull(name);
        Objects.requireNonNull(description);
        Objects.requireNonNull(icon);
        Objects.requireNonNull(category);
        if (id.isBlank() || name.isBlank() || gemPrice < 0) {
            throw new IllegalArgumentException("Gem shop item details must be valid");
        }
    }
}
