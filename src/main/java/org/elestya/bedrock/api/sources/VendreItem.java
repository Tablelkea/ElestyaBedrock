package org.elestya.bedrock.api.sources;

import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public record VendreItem(String name, ItemStack item, double price, ShopCategory category) {

    public VendreItem(String name, ItemStack item, double price, ShopCategory category){

        this.name = Objects.requireNonNull(name);
        if(name.isBlank()) throw new IllegalArgumentException("name cannot be blank");

        this.item = Objects.requireNonNull(item);
        this.price = price;
        this.category = Objects.requireNonNull(category);



    }

}
