package org.elestya.bedrock.api.sources;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.result.ActionResult;

import java.util.List;
import java.util.Optional;

public interface SellSource {

        List<SellItem> getItems();

        Optional<SellItem> findItemByName(String name);

        ActionResult sell(Player player, SellItem item, int quantity);

        ShopCategory getCategoryByName(String name);

        List<SellItem> getItemsByCategory(ShopCategory category);

        List<SellableItem> getSellableItems(Player player, ShopCategory category);

}
