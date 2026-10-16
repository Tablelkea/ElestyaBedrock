package org.elestya.bedrock.api.sources;

import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.elestya.bedrock.api.result.ActionResult;

import java.util.List;

public interface AuctionSource {

    List<String> getCategories();

    List<AuctionListing> getListings(String category);

    List<AuctionListing> getAllListings();

    List<AuctionListing> search(String query);

    List<AuctionListing> getPlayerListings(Player player);

    ActionResult buy(Player player, AuctionListing listing);

    ActionResult sellHeldItem(Player player, Material expectedMaterial, int expectedQuantity, double price);
}
