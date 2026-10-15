package org.elestya.bedrock.api.sources;

import java.util.Objects;

public record DailyRewardDay(int day, String rewardDescription, boolean claimed, boolean available) {

    public DailyRewardDay {
        if (day < 1 || day > 7) {
            throw new IllegalArgumentException("day must be between 1 and 7");
        }
        Objects.requireNonNull(rewardDescription);
        if (rewardDescription.isBlank()) {
            throw new IllegalArgumentException("rewardDescription cannot be blank");
        }
        if (claimed && available) {
            throw new IllegalArgumentException("a claimed reward cannot be available");
        }
    }
}
