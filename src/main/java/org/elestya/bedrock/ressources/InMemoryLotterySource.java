package org.elestya.bedrock.ressources;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.result.ActionResult;
import org.elestya.bedrock.api.sources.LotterySource;
import org.elestya.bedrock.api.sources.LotteryStatus;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class InMemoryLotterySource implements LotterySource {

    private final Set<UUID> participants = ConcurrentHashMap.newKeySet();
    private final AtomicInteger count = new AtomicInteger(12);

    @Override
    public LotteryStatus getStatus(Player player) {
        Objects.requireNonNull(player);
        return new LotteryStatus("25 000", count.get(), "dans 18 minutes",
                participants.contains(player.getUniqueId()));
    }

    @Override
    public ActionResult toggleParticipation(Player player) {
        Objects.requireNonNull(player);
        if (participants.add(player.getUniqueId())) {
            count.incrementAndGet();
            return ActionResult.success("Vous êtes inscrit au prochain tirage (données de test).");
        }
        if (participants.remove(player.getUniqueId())) {
            count.decrementAndGet();
            return ActionResult.success("Vous êtes désinscrit du prochain tirage (données de test).");
        }
        return ActionResult.failure("Impossible de modifier votre inscription.");
    }
}
