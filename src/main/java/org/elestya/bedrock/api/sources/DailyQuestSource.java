package org.elestya.bedrock.api.sources;

import org.bukkit.entity.Player;

import java.util.List;

public interface DailyQuestSource {

    List<DailyQuest> getQuests(Player player, DailyQuestCategory category);
}
