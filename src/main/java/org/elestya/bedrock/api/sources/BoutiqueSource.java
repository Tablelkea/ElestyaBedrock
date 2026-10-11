package org.elestya.bedrock.api.sources;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.result.ActionResult;

import java.util.List;
import java.util.Optional;

public interface BoutiqueSource {

    List<BoutiqueItem> getShopItems();

    Optional<BoutiqueItem> findItemByName(String name);

    List<BoutiqueItem> getItemFromCategory(ShopCategory category);

    ActionResult buy(Player player, BoutiqueItem item, int quantity);

    ShopCategory getCategoryByName(String name);

}
