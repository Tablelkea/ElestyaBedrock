package org.elestya.bedrock.api.sources;

import java.util.Objects;

public record LotteryStatus(String prizePool, int participants, String drawTime, boolean participating) {

    public LotteryStatus {
        Objects.requireNonNull(prizePool);
        Objects.requireNonNull(drawTime);
        if (prizePool.isBlank() || drawTime.isBlank() || participants < 0) {
            throw new IllegalArgumentException("Lottery details must be valid");
        }
    }
}
