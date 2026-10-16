package org.elestya.bedrock.api.sources;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.result.ActionResult;

import java.util.List;

public interface BuyOfferSource {

    List<BuyOffer> getOffers(Player player);

    List<BuyOffer> getPlayerOffers(Player player);

    List<OfferItem> getOfferableItems(Player player);

    double getDeliveryTotal(Player player, BuyOffer offer, int quantity);

    double getOfferTotal(Player player, OfferItem item, int quantity, double unitPrice);

    ActionResult deliver(Player player, BuyOffer offer, int quantity);

    ActionResult createOffer(Player player, OfferItem item, int quantity, double unitPrice);
}
