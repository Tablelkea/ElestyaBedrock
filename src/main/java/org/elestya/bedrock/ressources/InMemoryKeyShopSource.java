package org.elestya.bedrock.ressources;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.elestya.bedrock.api.result.ActionResult;
import org.elestya.bedrock.api.sources.KeyOffer;
import org.elestya.bedrock.api.sources.KeyShopSource;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class InMemoryKeyShopSource implements KeyShopSource {

    private final List<KeyOffer> keys = List.of(
            new KeyOffer("common", "Clé commune", "Ouvre une caisse commune.", Material.TRIPWIRE_HOOK, 100),
            new KeyOffer("rare", "Clé rare", "Ouvre une caisse rare.", Material.TRIPWIRE_HOOK, 350),
            new KeyOffer("epic", "Clé épique", "Ouvre une caisse épique.", Material.TRIPWIRE_HOOK, 800)
    );
    private final ConcurrentMap<UUID, Integer> balances = new ConcurrentHashMap<>();

    @Override
    public List<KeyOffer> getKeys() {
        return keys;
    }

    @Override
    public int getGemBalance(Player player) {
        Objects.requireNonNull(player);
        return balances.computeIfAbsent(player.getUniqueId(), ignored -> 1_000);
    }

    @Override
    public ActionResult buy(Player player, KeyOffer key) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(key);
        if (!keys.contains(key)) return ActionResult.failure("Cette clé n'est plus disponible.");
        int balance = getGemBalance(player);
        if (balance < key.gemPrice()) return ActionResult.failure("Vous n'avez pas assez de gems.");
        if (!hasSpace(player, key.icon())) return ActionResult.failure("Votre inventaire est plein.");
        player.getInventory().addItem(new org.bukkit.inventory.ItemStack(key.icon()));
        balances.put(player.getUniqueId(), balance - key.gemPrice());
        return ActionResult.success("Vous avez acheté " + key.name() + " (objet de test remis).");
    }

    private boolean hasSpace(Player player, Material material) {
        for (var stack : player.getInventory().getStorageContents()) {
            if (stack == null || stack.getType().isAir()
                    || (stack.getType() == material && stack.getAmount() < stack.getMaxStackSize())) return true;
        }
        return false;
    }
}
