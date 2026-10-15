package org.elestya.bedrock.api.sources;

import java.util.Objects;

public record SellableItem(SellItem item, int quantity) {

    public SellableItem {
        Objects.requireNonNull(item);
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be greater than 0");
        }
    }
}
