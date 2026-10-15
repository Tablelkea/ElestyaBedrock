package org.elestya.bedrock.api.sources;

import org.bukkit.Material;

import java.util.List;
import java.util.Objects;

public record Metier(String id, String displayName, Material icon, List<MetierCompetence> competences) {

    public Metier {
        Objects.requireNonNull(id);
        if (id.isBlank()) throw new IllegalArgumentException("id cannot be blank");

        Objects.requireNonNull(displayName);
        if (displayName.isBlank()) throw new IllegalArgumentException("displayName cannot be blank");

        Objects.requireNonNull(icon);
        competences = List.copyOf(Objects.requireNonNull(competences));
    }

    public Metier(String id, String displayName, Material icon) {
        this(id, displayName, icon, List.of());
    }
}
