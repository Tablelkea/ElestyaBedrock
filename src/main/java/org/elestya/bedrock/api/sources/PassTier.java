package org.elestya.bedrock.api.sources;

import org.bukkit.Material;

import java.util.Objects;

public record PassTier(String id, String name, String reward, Material icon, boolean premium,
                       boolean available, boolean claimed) {

    public PassTier {
        Objects.requireNonNull(id);
        Objects.requireNonNull(name);
        Objects.requireNonNull(reward);
        Objects.requireNonNull(icon);
        if (id.isBlank() || name.isBlank() || reward.isBlank()) {
            throw new IllegalArgumentException("Pass tier details must be valid");
        }
    }
}
