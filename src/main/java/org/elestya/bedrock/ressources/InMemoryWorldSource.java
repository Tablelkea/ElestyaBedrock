package org.elestya.bedrock.ressources;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.elestya.bedrock.api.result.ActionResult;
import org.elestya.bedrock.api.sources.BedrockWorld;
import org.elestya.bedrock.api.sources.WorldCategory;
import org.elestya.bedrock.api.sources.WorldSource;

import java.util.List;
import java.util.Objects;

public class InMemoryWorldSource implements WorldSource {

    private final List<BedrockWorld> worlds = List.of(
            new BedrockWorld("overworld", "Overworld", "Monde de ressources principal", Material.GRASS_BLOCK,
                    WorldCategory.RESOURCES),
            new BedrockWorld("nether", "Nether", "Récolte les ressources du Nether", Material.NETHERRACK,
                    WorldCategory.RESOURCES),
            new BedrockWorld("end", "End", "Explore les îles de l'End", Material.END_STONE,
                    WorldCategory.RESOURCES),
            new BedrockWorld("survie", "Monde survie", "Survie et aventure", Material.OAK_SAPLING,
                    WorldCategory.SURVIVAL),
            new BedrockWorld("exploration", "Exploration", "Explore les terres sauvages", Material.COMPASS,
                    WorldCategory.COMBAT),
            new BedrockWorld("arene", "Arène", "Affronte les autres combattants", Material.IRON_SWORD,
                    WorldCategory.COMBAT),
            new BedrockWorld("halloween", "Halloween", "Explore le monde d'Halloween", Material.CARVED_PUMPKIN,
                    WorldCategory.COMBAT)
    );

    @Override
    public List<BedrockWorld> getWorlds() {
        return worlds;
    }

    @Override
    public ActionResult teleport(Player player, BedrockWorld world) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(world);
        if (!worlds.contains(world)) {
            return ActionResult.failure("Ce monde n'est pas disponible.");
        }
        return ActionResult.success("Téléportation vers " + world.name() + " simulée.");
    }
}
