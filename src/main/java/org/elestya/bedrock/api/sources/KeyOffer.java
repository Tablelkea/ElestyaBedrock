package org.elestya.bedrock.api.sources;

import org.bukkit.Material;

import java.util.Objects;

public record KeyOffer(String id, String name, String description, Material icon, int gemPrice) {

    public KeyOffer {
        Objects.requireNonNull(id);
        Objects.requireNonNull(name);
        Objects.requireNonNull(description);
        Objects.requireNonNull(icon);
        if (id.isBlank() || name.isBlank() || gemPrice < 0) {
            throw new IllegalArgumentException("Key offer details must be valid");
        }
    }
}
