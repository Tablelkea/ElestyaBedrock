package org.elestya.bedrock.interfaces;

import org.bukkit.Material;

import java.util.Map;

public final class FormImagePaths {

    private static final Map<Material, String> TEXTURE_OVERRIDES = Map.ofEntries(
            Map.entry(Material.BRICKS, "textures/blocks/brick"),
            Map.entry(Material.BRICK, "textures/items/brick"),
            Map.entry(Material.HOPPER, "textures/blocks/hopper_side"),
            Map.entry(Material.PLAYER_HEAD, "textures/items/head"),
            Map.entry(Material.WHITE_BANNER, "textures/items/banner"),
            Map.entry(Material.ENDER_CHEST, "textures/blocks/ender_chest_front"),
            Map.entry(Material.WRITABLE_BOOK, "textures/items/book_writable"),
            Map.entry(Material.CHERRY_SAPLING, "textures/blocks/sapling_cherry"),
            Map.entry(Material.COD, "textures/items/raw_fish"),
            Map.entry(Material.SALMON, "textures/items/raw_salmon"),
            Map.entry(Material.TROPICAL_FISH, "textures/items/clownfish"),
            Map.entry(Material.PUFFERFISH, "textures/items/fish_pufferfish"),
            Map.entry(Material.OAK_LOG, "textures/blocks/log_oak"),
            Map.entry(Material.OAK_PLANKS, "textures/blocks/planks_oak"),
            Map.entry(Material.COBBLESTONE, "textures/blocks/cobblestone"),
            Map.entry(Material.STONE_BRICKS, "textures/blocks/stonebrick"),
            Map.entry(Material.DEEPSLATE, "textures/blocks/deepslate"),
            Map.entry(Material.RAW_IRON, "textures/items/raw_iron"),
            Map.entry(Material.RAW_GOLD, "textures/items/raw_gold"),
            Map.entry(Material.RAW_COPPER, "textures/items/raw_copper")
    );

    private FormImagePaths() {
    }

    public static String forMaterial(Material material) {
        String override = TEXTURE_OVERRIDES.get(material);
        if (override != null) {
            return override;
        }

        String namespace = material.isItem() && !material.isBlock() ? "items" : "blocks";
        return "textures/" + namespace + "/" + material.name().toLowerCase(java.util.Locale.ROOT);
    }
}
