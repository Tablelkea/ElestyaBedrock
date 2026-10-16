package org.elestya.bedrock.api.sources;

import org.bukkit.Material;

import java.util.Objects;

public record AuctionListing(String id, String itemName, Material icon, int quantity, double price,
                             String seller, String category) {

    public AuctionListing {
        Objects.requireNonNull(id);
        Objects.requireNonNull(itemName);
        Objects.requireNonNull(icon);
        Objects.requireNonNull(seller);
        Objects.requireNonNull(category);
        if (id.isBlank() || itemName.isBlank() || seller.isBlank() || category.isBlank()) {
            throw new IllegalArgumentException("Listing text fields cannot be blank");
        }
        if (quantity <= 0 || !Double.isFinite(price) || price < 0) {
            throw new IllegalArgumentException("Listing quantity and price must be valid");
        }
    }
}
