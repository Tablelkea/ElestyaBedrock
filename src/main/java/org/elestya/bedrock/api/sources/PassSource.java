package org.elestya.bedrock.api.sources;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.result.ActionResult;

import java.util.List;

public interface PassSource {

    String getProgress(Player player);

    List<PassTier> getTiers(Player player);

    ActionResult claim(Player player, PassTier tier);

    ActionResult claimAll(Player player);
}
