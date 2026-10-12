package org.elestya.bedrock.ressources;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.elestya.bedrock.api.sources.ShopCategory;
import org.elestya.bedrock.api.sources.VendreItem;

import java.util.Objects;

public class MemoryVendreSeeder {

    public MemoryVendreSource source;

    public MemoryVendreSeeder(MemoryVendreSource source) {
        this.source = Objects.requireNonNull(source);
    }

    public void seed() {
        // ==================== BLOCK ====================

        source.addItems(new VendreItem("Bûche de Chêne", ItemStack.of(Material.OAK_LOG), 1, ShopCategory.BLOCK), new VendreItem("Planches de Chêne", ItemStack.of(Material.OAK_PLANKS), 1, ShopCategory.BLOCK), new VendreItem("Pierre", ItemStack.of(Material.STONE), 1, ShopCategory.BLOCK), new VendreItem("Pierre Taillée", ItemStack.of(Material.COBBLESTONE), 1, ShopCategory.BLOCK), new VendreItem("Terre", ItemStack.of(Material.DIRT), 1, ShopCategory.BLOCK), new VendreItem("Sable", ItemStack.of(Material.SAND), 1, ShopCategory.BLOCK), new VendreItem("Gravier", ItemStack.of(Material.GRAVEL), 1, ShopCategory.BLOCK), new VendreItem("Verre", ItemStack.of(Material.GLASS), 1, ShopCategory.BLOCK), new VendreItem("Briques de Pierre", ItemStack.of(Material.STONE_BRICKS), 1, ShopCategory.BLOCK), new VendreItem("Ardoise des Abîmes", ItemStack.of(Material.DEEPSLATE), 1, ShopCategory.BLOCK));

// ==================== FOOD ====================

        source.addItems(new VendreItem("Pomme", ItemStack.of(Material.APPLE), 1, ShopCategory.FOOD), new VendreItem("Pain", ItemStack.of(Material.BREAD), 1, ShopCategory.FOOD), new VendreItem("Carotte", ItemStack.of(Material.CARROT), 1, ShopCategory.FOOD), new VendreItem("Pomme de Terre", ItemStack.of(Material.POTATO), 1, ShopCategory.FOOD));

// ==================== DROPS ====================

        source.addItems(new VendreItem("Os", ItemStack.of(Material.BONE), 1, ShopCategory.DROPS), new VendreItem("Fil", ItemStack.of(Material.STRING), 1, ShopCategory.DROPS), new VendreItem("Poudre à Canon", ItemStack.of(Material.GUNPOWDER), 1, ShopCategory.DROPS), new VendreItem("Chair Putréfiée", ItemStack.of(Material.ROTTEN_FLESH), 1, ShopCategory.DROPS));

// ==================== PLANTS ====================

        source.addItems(new VendreItem("Graines de Blé", ItemStack.of(Material.WHEAT_SEEDS), 1, ShopCategory.PLANTS), new VendreItem("Graines de Citrouille", ItemStack.of(Material.PUMPKIN_SEEDS), 1, ShopCategory.PLANTS), new VendreItem("Graines de Pastèque", ItemStack.of(Material.MELON_SEEDS), 1, ShopCategory.PLANTS), new VendreItem("Canne à Sucre", ItemStack.of(Material.SUGAR_CANE), 1, ShopCategory.PLANTS));

// ==================== ORE ====================

        source.addItems(new VendreItem("Charbon", ItemStack.of(Material.COAL), 1, ShopCategory.ORE), new VendreItem("Fer Brut", ItemStack.of(Material.RAW_IRON), 1, ShopCategory.ORE), new VendreItem("Or Brut", ItemStack.of(Material.RAW_GOLD), 1, ShopCategory.ORE), new VendreItem("Cuivre Brut", ItemStack.of(Material.RAW_COPPER), 1, ShopCategory.ORE));
    }

}
