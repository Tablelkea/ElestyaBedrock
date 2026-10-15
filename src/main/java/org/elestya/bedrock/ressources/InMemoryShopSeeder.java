package org.elestya.bedrock.ressources;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.elestya.bedrock.api.sources.ShopItem;
import org.elestya.bedrock.api.sources.ShopCategory;

import java.util.Objects;

public class InMemoryShopSeeder {

    private final InMemoryShopSource source;

    public InMemoryShopSeeder(InMemoryShopSource source){
        this.source = Objects.requireNonNull(source, "source cannot be null");
    }

    public void seed(){
        // ==================== BLOCK ====================

        source.addItems(
                new ShopItem("Bûche de Chêne", ItemStack.of(Material.OAK_LOG), 1, ShopCategory.BLOCK),
                new ShopItem("Planches de Chêne", ItemStack.of(Material.OAK_PLANKS), 1, ShopCategory.BLOCK),
                new ShopItem("Pierre", ItemStack.of(Material.STONE), 1, ShopCategory.BLOCK),
                new ShopItem("Pierre Taillée", ItemStack.of(Material.COBBLESTONE), 1, ShopCategory.BLOCK),
                new ShopItem("Terre", ItemStack.of(Material.DIRT), 1, ShopCategory.BLOCK),
                new ShopItem("Sable", ItemStack.of(Material.SAND), 1, ShopCategory.BLOCK),
                new ShopItem("Gravier", ItemStack.of(Material.GRAVEL), 1, ShopCategory.BLOCK),
                new ShopItem("Verre", ItemStack.of(Material.GLASS), 1, ShopCategory.BLOCK),
                new ShopItem("Briques de Pierre", ItemStack.of(Material.STONE_BRICKS), 1, ShopCategory.BLOCK),
                new ShopItem("Ardoise des Abîmes", ItemStack.of(Material.DEEPSLATE), 1, ShopCategory.BLOCK)
        );

// ==================== FOOD ====================

        source.addItems(
                new ShopItem("Pomme", ItemStack.of(Material.APPLE), 1, ShopCategory.FOOD),
                new ShopItem("Pain", ItemStack.of(Material.BREAD), 1, ShopCategory.FOOD),
                new ShopItem("Carotte", ItemStack.of(Material.CARROT), 1, ShopCategory.FOOD),
                new ShopItem("Pomme de Terre", ItemStack.of(Material.POTATO), 1, ShopCategory.FOOD)
        );

// ==================== DROPS ====================

        source.addItems(
                new ShopItem("Os", ItemStack.of(Material.BONE), 1, ShopCategory.DROPS),
                new ShopItem("Fil", ItemStack.of(Material.STRING), 1, ShopCategory.DROPS),
                new ShopItem("Poudre à Canon", ItemStack.of(Material.GUNPOWDER), 1, ShopCategory.DROPS),
                new ShopItem("Chair Putréfiée", ItemStack.of(Material.ROTTEN_FLESH), 1, ShopCategory.DROPS)
        );

// ==================== PLANTS ====================

        source.addItems(
                new ShopItem("Graines de Blé", ItemStack.of(Material.WHEAT_SEEDS), 1, ShopCategory.PLANTS),
                new ShopItem("Graines de Citrouille", ItemStack.of(Material.PUMPKIN_SEEDS), 1, ShopCategory.PLANTS),
                new ShopItem("Graines de Pastèque", ItemStack.of(Material.MELON_SEEDS), 1, ShopCategory.PLANTS),
                new ShopItem("Canne à Sucre", ItemStack.of(Material.SUGAR_CANE), 1, ShopCategory.PLANTS)
        );

// ==================== ORE ====================

        source.addItems(
                new ShopItem("Charbon", ItemStack.of(Material.COAL), 1, ShopCategory.ORE),
                new ShopItem("Fer Brut", ItemStack.of(Material.RAW_IRON), 1, ShopCategory.ORE),
                new ShopItem("Or Brut", ItemStack.of(Material.RAW_GOLD), 1, ShopCategory.ORE),
                new ShopItem("Cuivre Brut", ItemStack.of(Material.RAW_COPPER), 1, ShopCategory.ORE)
        );
    }

}
