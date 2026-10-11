package org.elestya.bedrock.ressources;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.elestya.bedrock.api.sources.BoutiqueItem;
import org.elestya.bedrock.api.sources.ShopCategory;

import java.util.Objects;

public class MemoryBoutiqueSeeder {

    private final MemoryBoutiqueSource source;

    public MemoryBoutiqueSeeder(MemoryBoutiqueSource source){
        this.source = Objects.requireNonNull(source, "source cannot be null");
    }

    public void seed(){
        // ==================== BLOCK ====================

        source.addItems(
                new BoutiqueItem("Bûche de Chêne", ItemStack.of(Material.OAK_LOG), 1, ShopCategory.BLOCK),
                new BoutiqueItem("Planches de Chêne", ItemStack.of(Material.OAK_PLANKS), 1, ShopCategory.BLOCK),
                new BoutiqueItem("Pierre", ItemStack.of(Material.STONE), 1, ShopCategory.BLOCK),
                new BoutiqueItem("Pierre Taillée", ItemStack.of(Material.COBBLESTONE), 1, ShopCategory.BLOCK),
                new BoutiqueItem("Terre", ItemStack.of(Material.DIRT), 1, ShopCategory.BLOCK),
                new BoutiqueItem("Sable", ItemStack.of(Material.SAND), 1, ShopCategory.BLOCK),
                new BoutiqueItem("Gravier", ItemStack.of(Material.GRAVEL), 1, ShopCategory.BLOCK),
                new BoutiqueItem("Verre", ItemStack.of(Material.GLASS), 1, ShopCategory.BLOCK),
                new BoutiqueItem("Briques de Pierre", ItemStack.of(Material.STONE_BRICKS), 1, ShopCategory.BLOCK),
                new BoutiqueItem("Ardoise des Abîmes", ItemStack.of(Material.DEEPSLATE), 1, ShopCategory.BLOCK)
        );

// ==================== FOOD ====================

        source.addItems(
                new BoutiqueItem("Pomme", ItemStack.of(Material.APPLE), 1, ShopCategory.FOOD),
                new BoutiqueItem("Pain", ItemStack.of(Material.BREAD), 1, ShopCategory.FOOD),
                new BoutiqueItem("Carotte", ItemStack.of(Material.CARROT), 1, ShopCategory.FOOD),
                new BoutiqueItem("Pomme de Terre", ItemStack.of(Material.POTATO), 1, ShopCategory.FOOD)
        );

// ==================== DROPS ====================

        source.addItems(
                new BoutiqueItem("Os", ItemStack.of(Material.BONE), 1, ShopCategory.DROPS),
                new BoutiqueItem("Fil", ItemStack.of(Material.STRING), 1, ShopCategory.DROPS),
                new BoutiqueItem("Poudre à Canon", ItemStack.of(Material.GUNPOWDER), 1, ShopCategory.DROPS),
                new BoutiqueItem("Chair Putréfiée", ItemStack.of(Material.ROTTEN_FLESH), 1, ShopCategory.DROPS)
        );

// ==================== PLANTS ====================

        source.addItems(
                new BoutiqueItem("Graines de Blé", ItemStack.of(Material.WHEAT_SEEDS), 1, ShopCategory.PLANTS),
                new BoutiqueItem("Graines de Citrouille", ItemStack.of(Material.PUMPKIN_SEEDS), 1, ShopCategory.PLANTS),
                new BoutiqueItem("Graines de Pastèque", ItemStack.of(Material.MELON_SEEDS), 1, ShopCategory.PLANTS),
                new BoutiqueItem("Canne à Sucre", ItemStack.of(Material.SUGAR_CANE), 1, ShopCategory.PLANTS)
        );

// ==================== ORE ====================

        source.addItems(
                new BoutiqueItem("Charbon", ItemStack.of(Material.COAL), 1, ShopCategory.ORE),
                new BoutiqueItem("Fer Brut", ItemStack.of(Material.RAW_IRON), 1, ShopCategory.ORE),
                new BoutiqueItem("Or Brut", ItemStack.of(Material.RAW_GOLD), 1, ShopCategory.ORE),
                new BoutiqueItem("Cuivre Brut", ItemStack.of(Material.RAW_COPPER), 1, ShopCategory.ORE)
        );
    }

}
