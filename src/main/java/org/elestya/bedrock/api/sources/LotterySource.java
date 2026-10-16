package org.elestya.bedrock.api.sources;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.result.ActionResult;

public interface LotterySource {

    LotteryStatus getStatus(Player player);

    ActionResult toggleParticipation(Player player);
}
