package org.elestya.bedrock.api.sources;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.result.ActionResult;

import java.util.List;
import java.util.Optional;

public interface ShopSource {

    List<ShopItem> getItems();

    Optional<ShopItem> findItemByName(String name);

    List<ShopItem> getItemsByCategory(ShopCategory category);

    ActionResult buy(Player player, ShopItem item, int quantity);

    ShopCategory getCategoryByName(String name);

}
