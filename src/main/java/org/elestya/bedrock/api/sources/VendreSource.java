package org.elestya.bedrock.api.sources;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.result.ActionResult;

import java.util.List;
import java.util.Optional;

public interface VendreSource {

        List<VendreItem> getVendreItems();

        Optional<VendreItem> findItemByName(String name);

        ActionResult sell(Player player, VendreItem item, int quantity);

        ShopCategory getCategoryByName(String name);

        List<VendreItem> getItemFromCategory(ShopCategory category);

}
