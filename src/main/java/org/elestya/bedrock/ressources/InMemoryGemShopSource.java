package org.elestya.bedrock.ressources;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.elestya.bedrock.api.result.ActionResult;
import org.elestya.bedrock.api.sources.GemShopCategory;
import org.elestya.bedrock.api.sources.GemShopItem;
import org.elestya.bedrock.api.sources.GemShopSource;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class InMemoryGemShopSource implements GemShopSource {

    private final List<GemShopItem> items = List.of(
            new GemShopItem("rank-vip", "Grade VIP", "Accès au grade VIP.", Material.GOLD_INGOT,
                    GemShopCategory.RANKS, 500),
            new GemShopItem("rank-elite", "Grade Élite", "Accès au grade Élite.", Material.DIAMOND,
                    GemShopCategory.RANKS, 1_200),
            new GemShopItem("pass-premium", "Pass Premium", "Débloque la voie premium.", Material.PAPER,
                    GemShopCategory.PASSES, 750)
    );
    private final ConcurrentMap<UUID, Integer> balances = new ConcurrentHashMap<>();

    @Override
    public int getGemBalance(Player player) {
        Objects.requireNonNull(player);
        return balances.computeIfAbsent(player.getUniqueId(), ignored -> 1_000);
    }

    @Override
    public List<GemShopItem> getItems() {
        return items;
    }

    @Override
    public List<GemShopItem> getItems(GemShopCategory category) {
        Objects.requireNonNull(category);
        return items.stream().filter(item -> item.category() == category).toList();
    }

    @Override
    public ActionResult buy(Player player, GemShopItem item) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(item);
        if (!items.contains(item)) return ActionResult.failure("Cet article n'est plus disponible.");
        int balance = getGemBalance(player);
        if (balance < item.gemPrice()) return ActionResult.failure("Vous n'avez pas assez de gems.");
        balances.put(player.getUniqueId(), balance - item.gemPrice());
        return ActionResult.success("Achat de " + item.name() + " effectué (données de test).");
    }
}
