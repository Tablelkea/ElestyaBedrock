package org.elestya.bedrock.api.sources;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.result.ActionResult;

import java.util.List;

public interface BoostSource {

    List<BoostOffer> getBoosts(Player player);

    ActionResult activate(Player player, BoostOffer boost);
}
