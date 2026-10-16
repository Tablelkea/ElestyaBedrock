package org.elestya.bedrock.api.sources;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.result.ActionResult;

public interface ProgressionSource {

    ProgressionStep getRankProgress(Player player);

    ActionResult rankUp(Player player);

    boolean canClaimRankKit(Player player);

    ActionResult claimRankKit(Player player);

    ProgressionStep getPrestigeProgress(Player player);

    ActionResult prestige(Player player);
}
