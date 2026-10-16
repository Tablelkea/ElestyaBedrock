package org.elestya.bedrock.ressources;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.elestya.bedrock.api.result.ActionResult;
import org.elestya.bedrock.api.sources.AuctionListing;
import org.elestya.bedrock.api.sources.AuctionSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public final class InMemoryAuctionSource implements AuctionSource {

    private final List<AuctionListing> listings = new ArrayList<>(List.of(
            new AuctionListing("hdv-diamonds", "Diamant", Material.DIAMOND, 16, 480.0, "Steve", "Minerais"),
            new AuctionListing("hdv-iron", "Lingot de fer", Material.IRON_INGOT, 32, 256.0, "Alex", "Minerais"),
            new AuctionListing("hdv-bread", "Pain", Material.BREAD, 12, 36.0, "Steve", "Nourriture"),
            new AuctionListing("hdv-oak", "Bûche de chêne", Material.OAK_LOG, 64, 192.0, "Alex", "Blocs")
    ));
    private final AtomicLong ids = new AtomicLong(1);

    @Override
    public synchronized List<String> getCategories() {
        return listings.stream().map(AuctionListing::category).distinct().toList();
    }

    @Override
    public synchronized List<AuctionListing> getListings(String category) {
        Objects.requireNonNull(category);
        return listings.stream().filter(listing -> listing.category().equals(category)).toList();
    }

    @Override
    public List<AuctionListing> getAllListings() {
        return List.copyOf(listings);
    }

    @Override
    public synchronized List<AuctionListing> search(String query) {
        Objects.requireNonNull(query);
        String normalized = query.toLowerCase(Locale.ROOT);
        return listings.stream().filter(listing ->
                listing.itemName().toLowerCase(Locale.ROOT).contains(normalized)
                        || listing.category().toLowerCase(Locale.ROOT).contains(normalized)
                        || listing.seller().toLowerCase(Locale.ROOT).contains(normalized)).toList();
    }

    @Override
    public synchronized List<AuctionListing> getPlayerListings(Player player) {
        Objects.requireNonNull(player);
        return listings.stream().filter(listing -> listing.seller().equalsIgnoreCase(player.getName())).toList();
    }

    @Override
    public synchronized ActionResult buy(Player player, AuctionListing listing) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(listing);
        if (!listings.contains(listing)) {
            return ActionResult.failure("Cette annonce n'est plus disponible.");
        }
        if (!hasSpace(player, listing.icon(), listing.quantity())) {
            return ActionResult.failure("Votre inventaire est plein.");
        }
        player.getInventory().addItem(new org.bukkit.inventory.ItemStack(listing.icon(), listing.quantity()));
        listings.remove(listing);
        return ActionResult.success("Achat simulé : " + listing.quantity() + " × " + listing.itemName()
                + " pour " + listing.price() + ". La monnaie n'est pas branchée dans les données de test.");
    }

    @Override
    public synchronized ActionResult sellHeldItem(Player player, Material expectedMaterial, int expectedQuantity,
                                                   double price) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(expectedMaterial);
        if (!Double.isFinite(price) || price <= 0 || expectedQuantity <= 0) {
            return ActionResult.failure("Le prix doit être supérieur à 0.");
        }
        var hand = player.getInventory().getItemInMainHand();
        if (hand.getType() != expectedMaterial || hand.getAmount() < expectedQuantity) {
            return ActionResult.failure("L'objet tenu a changé ou n'est plus disponible.");
        }
        String itemName = hand.hasItemMeta() && hand.getItemMeta().hasDisplayName()
                ? hand.getItemMeta().getDisplayName() : hand.getType().name();
        hand.setAmount(hand.getAmount() - expectedQuantity);
        if (hand.getAmount() == 0) player.getInventory().setItemInMainHand(null);
        String category = switch (expectedMaterial) {
            case DIAMOND, IRON_INGOT, GOLD_INGOT, COPPER_INGOT -> "Minerais";
            case BREAD, COOKED_BEEF, COOKED_PORKCHOP -> "Nourriture";
            case OAK_LOG, COBBLESTONE, STONE, BRICKS -> "Blocs";
            default -> "Autres";
        };
        listings.add(new AuctionListing("hdv-" + ids.getAndIncrement(), itemName, expectedMaterial,
                expectedQuantity, price, player.getName(), category));
        return ActionResult.success("Mise en vente créée pour " + expectedQuantity + " × " + itemName
                + " à " + price + " (simulation).");
    }

    private boolean hasSpace(Player player, Material material, int quantity) {
        int remaining = quantity;
        for (var stack : player.getInventory().getStorageContents()) {
            if (stack == null || stack.getType().isAir()) {
                remaining -= material.getMaxStackSize();
            } else if (stack.getType() == material) {
                remaining -= Math.max(0, stack.getMaxStackSize() - stack.getAmount());
            }
            if (remaining <= 0) return true;
        }
        return false;
    }
}
