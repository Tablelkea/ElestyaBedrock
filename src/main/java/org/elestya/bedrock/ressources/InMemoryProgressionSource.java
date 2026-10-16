package org.elestya.bedrock.ressources;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.result.ActionResult;
import org.elestya.bedrock.api.sources.ProgressionSource;
import org.elestya.bedrock.api.sources.ProgressionStep;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryProgressionSource implements ProgressionSource {

    private final Set<UUID> kitsClaimed = ConcurrentHashMap.newKeySet();

    @Override
    public ProgressionStep getRankProgress(Player player) {
        Objects.requireNonNull(player);
        return new ProgressionStep("Voyageur", "Aventurier", "Progression : 8 / 10 objectifs", "2 500");
    }

    @Override
    public ActionResult rankUp(Player player) {
        Objects.requireNonNull(player);
        return ActionResult.success("Passage au grade Aventurier effectué (données de test).");
    }

    @Override
    public boolean canClaimRankKit(Player player) {
        Objects.requireNonNull(player);
        return !kitsClaimed.contains(player.getUniqueId());
    }

    @Override
    public ActionResult claimRankKit(Player player) {
        Objects.requireNonNull(player);
        return kitsClaimed.add(player.getUniqueId())
                ? ActionResult.success("Kit du grade récupéré (données de test).")
                : ActionResult.failure("Le kit a déjà été récupéré.");
    }

    @Override
    public ProgressionStep getPrestigeProgress(Player player) {
        Objects.requireNonNull(player);
        return new ProgressionStep("Prestige 1", "Prestige 2", "Niveau requis : 50 · Niveau actuel : 50", "10 000");
    }

    @Override
    public ActionResult prestige(Player player) {
        Objects.requireNonNull(player);
        return ActionResult.success("Prestige 2 obtenu (données de test).");
    }
}
