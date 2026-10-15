package org.elestya.bedrock.ressources;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.elestya.bedrock.api.sources.ShopCategory;
import org.elestya.bedrock.api.sources.SellItem;

import java.util.Objects;

public class InMemorySellSeeder {

    public InMemorySellSource source;

    public InMemorySellSeeder(InMemorySellSource source) {
        this.source = Objects.requireNonNull(source);
    }

    public void seed() {
        // ==================== BLOCK ====================

        source.addItems(new SellItem("Bûche de Chêne", ItemStack.of(Material.OAK_LOG), 1, ShopCategory.BLOCK), new SellItem("Planches de Chêne", ItemStack.of(Material.OAK_PLANKS), 1, ShopCategory.BLOCK), new SellItem("Pierre", ItemStack.of(Material.STONE), 1, ShopCategory.BLOCK), new SellItem("Pierre Taillée", ItemStack.of(Material.COBBLESTONE), 1, ShopCategory.BLOCK), new SellItem("Terre", ItemStack.of(Material.DIRT), 1, ShopCategory.BLOCK), new SellItem("Sable", ItemStack.of(Material.SAND), 1, ShopCategory.BLOCK), new SellItem("Gravier", ItemStack.of(Material.GRAVEL), 1, ShopCategory.BLOCK), new SellItem("Verre", ItemStack.of(Material.GLASS), 1, ShopCategory.BLOCK), new SellItem("Briques de Pierre", ItemStack.of(Material.STONE_BRICKS), 1, ShopCategory.BLOCK), new SellItem("Ardoise des Abîmes", ItemStack.of(Material.DEEPSLATE), 1, ShopCategory.BLOCK));

// ==================== FOOD ====================

        source.addItems(new SellItem("Pomme", ItemStack.of(Material.APPLE), 1, ShopCategory.FOOD), new SellItem("Pain", ItemStack.of(Material.BREAD), 1, ShopCategory.FOOD), new SellItem("Carotte", ItemStack.of(Material.CARROT), 1, ShopCategory.FOOD), new SellItem("Pomme de Terre", ItemStack.of(Material.POTATO), 1, ShopCategory.FOOD));

// ==================== DROPS ====================

        source.addItems(new SellItem("Os", ItemStack.of(Material.BONE), 1, ShopCategory.DROPS), new SellItem("Fil", ItemStack.of(Material.STRING), 1, ShopCategory.DROPS), new SellItem("Poudre à Canon", ItemStack.of(Material.GUNPOWDER), 1, ShopCategory.DROPS), new SellItem("Chair Putréfiée", ItemStack.of(Material.ROTTEN_FLESH), 1, ShopCategory.DROPS));

// ==================== PLANTS ====================

        source.addItems(new SellItem("Graines de Blé", ItemStack.of(Material.WHEAT_SEEDS), 1, ShopCategory.PLANTS), new SellItem("Graines de Citrouille", ItemStack.of(Material.PUMPKIN_SEEDS), 1, ShopCategory.PLANTS), new SellItem("Graines de Pastèque", ItemStack.of(Material.MELON_SEEDS), 1, ShopCategory.PLANTS), new SellItem("Canne à Sucre", ItemStack.of(Material.SUGAR_CANE), 1, ShopCategory.PLANTS));

// ==================== ORE ====================

        source.addItems(new SellItem("Charbon", ItemStack.of(Material.COAL), 1, ShopCategory.ORE), new SellItem("Fer Brut", ItemStack.of(Material.RAW_IRON), 1, ShopCategory.ORE), new SellItem("Or Brut", ItemStack.of(Material.RAW_GOLD), 1, ShopCategory.ORE), new SellItem("Cuivre Brut", ItemStack.of(Material.RAW_COPPER), 1, ShopCategory.ORE));

// ==================== FISHING ====================

        source.addItems(new SellItem("Morue", ItemStack.of(Material.COD), 1, ShopCategory.FISHING), new SellItem("Saumon", ItemStack.of(Material.SALMON), 1, ShopCategory.FISHING), new SellItem("Poisson tropical", ItemStack.of(Material.TROPICAL_FISH), 1, ShopCategory.FISHING), new SellItem("Poisson-globe", ItemStack.of(Material.PUFFERFISH), 1, ShopCategory.FISHING));
    }

}
