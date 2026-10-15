package org.elestya.bedrock.api.sources;

import org.bukkit.Material;

import java.util.Objects;

public record BedrockWorld(String id, String name, String description, Material icon, WorldCategory category) {

    public BedrockWorld {
        Objects.requireNonNull(id);
        if (id.isBlank()) {
            throw new IllegalArgumentException("id cannot be blank");
        }
        Objects.requireNonNull(name);
        if (name.isBlank()) {
            throw new IllegalArgumentException("name cannot be blank");
        }
        Objects.requireNonNull(description);
        Objects.requireNonNull(icon);
        Objects.requireNonNull(category);
    }
}
