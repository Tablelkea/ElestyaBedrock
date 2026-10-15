package org.elestya.bedrock.api.sources;

import java.util.Objects;

public record DailyQuest(String name, String description, int completed, int required) {

    public DailyQuest {
        Objects.requireNonNull(name);
        if (name.isBlank()) {
            throw new IllegalArgumentException("name cannot be blank");
        }
        Objects.requireNonNull(description);
        if (required <= 0) {
            throw new IllegalArgumentException("required must be positive");
        }
        if (completed < 0 || completed > required) {
            throw new IllegalArgumentException("completed must be between 0 and required");
        }
    }
}
