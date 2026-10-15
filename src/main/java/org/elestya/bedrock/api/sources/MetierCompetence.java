package org.elestya.bedrock.api.sources;

import java.util.Objects;

public record MetierCompetence(String id, String name, String description, int unlockLevel) {

    public MetierCompetence {
        Objects.requireNonNull(id);
        if (id.isBlank()) throw new IllegalArgumentException("id cannot be blank");

        Objects.requireNonNull(name);
        if (name.isBlank()) throw new IllegalArgumentException("name cannot be blank");

        Objects.requireNonNull(description);
        if (unlockLevel < 1) throw new IllegalArgumentException("unlockLevel must be positive");
    }
}
