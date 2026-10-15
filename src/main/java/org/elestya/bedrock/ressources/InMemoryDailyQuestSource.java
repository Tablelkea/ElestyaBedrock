package org.elestya.bedrock.ressources;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.sources.DailyQuest;
import org.elestya.bedrock.api.sources.DailyQuestCategory;
import org.elestya.bedrock.api.sources.DailyQuestSource;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class InMemoryDailyQuestSource implements DailyQuestSource {

    private final Map<DailyQuestCategory, List<DailyQuest>> quests = new EnumMap<>(DailyQuestCategory.class);

    public InMemoryDailyQuestSource() {
        quests.put(DailyQuestCategory.EASY, List.of(
                new DailyQuest("Première récolte", "Récupère du blé dans les champs.", 8, 16),
                new DailyQuest("Petit chantier", "Casse des blocs de pierre.", 24, 32)
        ));
        quests.put(DailyQuestCategory.NORMAL, List.of(
                new DailyQuest("Tour du marché", "Achète des objets à la boutique.", 3, 8),
                new DailyQuest("Récolte variée", "Récolte des ressources de trois types.", 1, 3)
        ));
        quests.put(DailyQuestCategory.HARD, List.of(
                new DailyQuest("Exploration dangereuse", "Élimine des créatures hostiles.", 6, 20),
                new DailyQuest("Grand bâtisseur", "Place des blocs de construction.", 42, 128)
        ));
        quests.put(DailyQuestCategory.WEEKLY, List.of(
                new DailyQuest("Régularité", "Termine des quêtes quotidiennes.", 3, 7),
                new DailyQuest("Semaine productive", "Récolte des ressources au fil de la semaine.", 126, 500)
        ));
    }

    @Override
    public List<DailyQuest> getQuests(Player player, DailyQuestCategory category) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(category);
        return List.copyOf(Objects.requireNonNull(quests.get(category), "No quests configured for " + category));
    }

    public void setQuests(DailyQuestCategory category, List<DailyQuest> dailyQuests) {
        quests.put(Objects.requireNonNull(category), List.copyOf(Objects.requireNonNull(dailyQuests)));
    }
}
