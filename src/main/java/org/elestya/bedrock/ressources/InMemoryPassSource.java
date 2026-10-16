package org.elestya.bedrock.ressources;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.elestya.bedrock.api.result.ActionResult;
import org.elestya.bedrock.api.sources.PassSource;
import org.elestya.bedrock.api.sources.PassTier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryPassSource implements PassSource {

    private final List<PassTier> tiers = List.of(
            new PassTier("tier-1-free", "Palier 1", "16 pains", Material.BREAD, false, true, false),
            new PassTier("tier-1-premium", "Palier 1", "2 clés rares", Material.TRIPWIRE_HOOK, true, true, false),
            new PassTier("tier-2-free", "Palier 2", "8 lingots de fer", Material.IRON_INGOT, false, false, false),
            new PassTier("tier-2-premium", "Palier 2", "1 clé épique", Material.TRIPWIRE_HOOK, true, false, false)
    );
    private final Set<String> claimed = ConcurrentHashMap.newKeySet();

    @Override
    public String getProgress(Player player) {
        Objects.requireNonNull(player);
        return "Niveau 2 · 45 / 100 points";
    }

    @Override
    public List<PassTier> getTiers(Player player) {
        Objects.requireNonNull(player);
        return tiers.stream().map(tier -> new PassTier(tier.id(), tier.name(), tier.reward(), tier.icon(),
                tier.premium(), tier.available(), claimed.contains(key(player, tier.id())))).toList();
    }

    @Override
    public ActionResult claim(Player player, PassTier tier) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(tier);
        PassTier current = tiers.stream().filter(value -> value.id().equals(tier.id())).findFirst().orElse(null);
        if (current == null || !current.available()) {
            return ActionResult.failure("Ce palier n'est pas disponible.");
        }
        if (!claimed.add(key(player, tier.id()))) {
            return ActionResult.failure("Cette récompense a déjà été récupérée.");
        }
        return ActionResult.success("Récompense récupérée : " + current.reward() + " (données de test).");
    }

    @Override
    public ActionResult claimAll(Player player) {
        Objects.requireNonNull(player);
        List<PassTier> available = getTiers(player).stream()
                .filter(PassTier::available)
                .filter(tier -> !tier.claimed())
                .toList();
        if (available.isEmpty()) return ActionResult.failure("Aucune récompense disponible.");
        List<String> claimedNames = new ArrayList<>();
        for (PassTier tier : available) {
            ActionResult result = claim(player, tier);
            if (result.success()) claimedNames.add(tier.name() + (tier.premium() ? " (premium)" : " (gratuite)"));
        }
        return claimedNames.isEmpty()
                ? ActionResult.failure("Aucune récompense n'a pu être récupérée.")
                : ActionResult.success("Paliers récupérés : " + String.join(", ", claimedNames) + ".");
    }

    private String key(Player player, String id) {
        return player.getUniqueId() + ":" + id;
    }
}
