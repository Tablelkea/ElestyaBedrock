package org.elestya.bedrock.api.sources;

import org.bukkit.Material;

import java.util.Objects;

public record BuyOffer(String id, String itemName, Material material, int requestedQuantity, int remainingQuantity,
                       double unitPrice, String buyer) {

    public BuyOffer {
        Objects.requireNonNull(id);
        Objects.requireNonNull(itemName);
        Objects.requireNonNull(material);
        Objects.requireNonNull(buyer);
        if (id.isBlank() || itemName.isBlank() || buyer.isBlank()
                || requestedQuantity <= 0 || remainingQuantity < 0 || remainingQuantity > requestedQuantity
                || !Double.isFinite(unitPrice) || unitPrice < 0) {
            throw new IllegalArgumentException("Buy offer details must be valid");
        }
    }
}
