package org.elestya.bedrock.api.sources;

import org.bukkit.Material;

import java.util.Objects;

public record OfferItem(int inventorySlot, Material material, String name, int available) {

    public OfferItem {
        Objects.requireNonNull(material);
        Objects.requireNonNull(name);
        if (inventorySlot < 0 || available <= 0 || name.isBlank()) {
            throw new IllegalArgumentException("Offer item details must be valid");
        }
    }
}
