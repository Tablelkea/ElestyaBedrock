package org.elestya.bedrock.api.sources;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.result.ActionResult;

import java.util.List;

public interface GemShopSource {

    int getGemBalance(Player player);

    List<GemShopItem> getItems();

    List<GemShopItem> getItems(GemShopCategory category);

    ActionResult buy(Player player, GemShopItem item);
}
