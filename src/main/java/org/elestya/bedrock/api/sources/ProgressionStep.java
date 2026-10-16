package org.elestya.bedrock.api.sources;

import java.util.Objects;

public record ProgressionStep(String current, String next, String details, String price) {

    public ProgressionStep {
        Objects.requireNonNull(current);
        Objects.requireNonNull(next);
        Objects.requireNonNull(details);
        Objects.requireNonNull(price);
        if (current.isBlank() || next.isBlank() || details.isBlank()) {
            throw new IllegalArgumentException("Progression details must be valid");
        }
    }
}
