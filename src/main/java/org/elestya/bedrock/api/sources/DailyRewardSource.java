package org.elestya.bedrock.api.sources;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.result.ActionResult;

import java.util.List;

public interface DailyRewardSource {

    List<DailyRewardDay> getDays(Player player);

    ActionResult claim(Player player, int day);
}
