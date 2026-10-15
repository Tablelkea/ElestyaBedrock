package org.elestya.bedrock.api.sources;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.result.ActionResult;

import java.util.List;

public interface MetierSource {

    int MAX_LEVEL = 100;
    int MAX_PRESTIGE = 10;

    List<Metier> getMetiers();

    int getLevel(Player player, Metier metier);

    int getPrestige(Player player, Metier metier);

    void setPrestige(Player player, Metier metier, int prestige);

    default boolean isPrestigeReady(Player player, Metier metier) {
        return getLevel(player, metier) >= MAX_LEVEL && getPrestige(player, metier) < MAX_PRESTIGE;
    }

    boolean passPrestige(Player player, Metier metier);

    int getOpenedChestCount(Player player, Metier metier);

    int getAvailableChestCount(Player player, Metier metier);

    boolean isChestOpened(Player player, Metier metier, int chestNumber);

    ActionResult openChest(Player player, Metier metier, int chestNumber);

    ActionResult openAllAvailableChests(Player player, Metier metier);

    boolean isCompetenceEnabled(Player player, Metier metier, MetierCompetence competence);

    void setCompetenceEnabled(Player player, Metier metier, MetierCompetence competence, boolean enabled);

    default List<MetierCompetence> getUnlockedCompetences(Player player, Metier metier) {
        int level = getLevel(player, metier);
        return metier.competences().stream()
                .filter(competence -> competence.unlockLevel() <= level)
                .toList();
    }
}
