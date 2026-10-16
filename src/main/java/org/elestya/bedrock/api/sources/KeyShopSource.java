package org.elestya.bedrock.api.sources;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.result.ActionResult;

import java.util.List;

public interface KeyShopSource {

    List<KeyOffer> getKeys();

    int getGemBalance(Player player);

    ActionResult buy(Player player, KeyOffer key);
}
