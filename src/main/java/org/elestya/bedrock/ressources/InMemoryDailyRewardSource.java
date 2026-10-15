package org.elestya.bedrock.ressources;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.result.ActionResult;
import org.elestya.bedrock.api.sources.DailyRewardDay;
import org.elestya.bedrock.api.sources.DailyRewardSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class InMemoryDailyRewardSource implements DailyRewardSource {

    private final List<String> rewardDescriptions = List.of(
            "250 pièces",
            "500 pièces",
            "1 clé commune",
            "750 pièces",
            "2 clés communes",
            "1 clé rare",
            "2 500 pièces et 1 clé épique"
    );
    private final Map<UUID, Integer> claimedThroughDay = new HashMap<>();

    @Override
    public List<DailyRewardDay> getDays(Player player) {
        Objects.requireNonNull(player);
        int claimedThrough = claimedThroughDay.getOrDefault(player.getUniqueId(), 2);
        List<DailyRewardDay> days = new ArrayList<>(7);
        for (int day = 1; day <= 7; day++) {
            boolean claimed = day <= claimedThrough;
            boolean available = day == claimedThrough + 1 && day <= 7;
            days.add(new DailyRewardDay(day, rewardDescriptions.get(day - 1), claimed, available));
        }
        return List.copyOf(days);
    }

    @Override
    public ActionResult claim(Player player, int day) {
        Objects.requireNonNull(player);
        if (day < 1 || day > 7) {
            return ActionResult.failure("Jour de récompense invalide.");
        }
        int claimedThrough = claimedThroughDay.getOrDefault(player.getUniqueId(), 2);
        if (day != claimedThrough + 1) {
            return ActionResult.failure("Cette récompense n'est pas disponible.");
        }

        claimedThroughDay.put(player.getUniqueId(), day);
        return ActionResult.success("Récompense du jour " + day + " réclamée : "
                + rewardDescriptions.get(day - 1) + ".");
    }

    public void setClaimedThroughDay(UUID playerId, int day) {
        Objects.requireNonNull(playerId);
        if (day < 0 || day > 7) {
            throw new IllegalArgumentException("day must be between 0 and 7");
        }
        claimedThroughDay.put(playerId, day);
    }
}
