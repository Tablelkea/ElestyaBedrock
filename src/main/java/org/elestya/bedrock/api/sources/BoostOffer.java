package org.elestya.bedrock.api.sources;

import org.bukkit.Material;

import java.util.Objects;

public record BoostOffer(String id, String name, String description, String duration, int available,
                         Material icon) {

    public BoostOffer {
        Objects.requireNonNull(id);
        Objects.requireNonNull(name);
        Objects.requireNonNull(description);
        Objects.requireNonNull(duration);
        Objects.requireNonNull(icon);
        if (id.isBlank() || name.isBlank() || description.isBlank() || duration.isBlank() || available < 0) {
            throw new IllegalArgumentException("Boost details must be valid");
        }
    }
}
