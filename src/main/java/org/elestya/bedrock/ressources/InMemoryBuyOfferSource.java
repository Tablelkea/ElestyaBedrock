package org.elestya.bedrock.ressources;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.elestya.bedrock.api.result.ActionResult;
import org.elestya.bedrock.api.sources.BuyOffer;
import org.elestya.bedrock.api.sources.BuyOfferSource;
import org.elestya.bedrock.api.sources.OfferItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public final class InMemoryBuyOfferSource implements BuyOfferSource {

    private final List<BuyOffer> offers = new ArrayList<>(List.of(
            new BuyOffer("offer-1", "Blé", Material.WHEAT, 64, 64, 5.0, "Alex"),
            new BuyOffer("offer-2", "Lingot de fer", Material.IRON_INGOT, 16, 8, 12.5, "Steve")
    ));
    private final AtomicLong ids = new AtomicLong(3);

    @Override
    public List<BuyOffer> getOffers(Player player) {
        Objects.requireNonNull(player);
        return offers.stream().filter(offer -> !offer.buyer().equalsIgnoreCase(player.getName())).toList();
    }

    @Override
    public List<BuyOffer> getPlayerOffers(Player player) {
        Objects.requireNonNull(player);
        return offers.stream().filter(offer -> offer.buyer().equalsIgnoreCase(player.getName())).toList();
    }

    @Override
    public List<OfferItem> getOfferableItems(Player player) {
        Objects.requireNonNull(player);
        List<OfferItem> result = new ArrayList<>();
        for (int slot = 0; slot < player.getInventory().getStorageContents().length; slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (stack == null || stack.getType().isAir()) continue;
            String name = stack.getItemMeta() != null && stack.getItemMeta().hasDisplayName()
                    ? stack.getItemMeta().getDisplayName() : stack.getType().name();
            result.add(new OfferItem(slot, stack.getType(), name, stack.getAmount()));
        }
        return List.copyOf(result);
    }

    @Override
    public double getDeliveryTotal(Player player, BuyOffer offer, int quantity) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(offer);
        if (quantity <= 0 || quantity > offer.remainingQuantity()) {
            throw new IllegalArgumentException("Delivery quantity is outside the offer's remaining quantity");
        }
        return quantity * offer.unitPrice();
    }

    @Override
    public double getOfferTotal(Player player, OfferItem item, int quantity, double unitPrice) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(item);
        if (quantity <= 0 || quantity > item.available() || !Double.isFinite(unitPrice) || unitPrice <= 0) {
            throw new IllegalArgumentException("Offer quantity and unit price must be valid");
        }
        return quantity * unitPrice;
    }

    @Override
    public ActionResult deliver(Player player, BuyOffer offer, int quantity) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(offer);
        if (!offers.contains(offer) || quantity <= 0 || quantity > offer.remainingQuantity()) {
            return ActionResult.failure("Cette offre ne peut pas recevoir cette quantité.");
        }
        int available = 0;
        for (ItemStack stack : player.getInventory().getStorageContents()) {
            if (stack != null && stack.getType() == offer.material()) available += stack.getAmount();
        }
        if (available < quantity) return ActionResult.failure("Vous ne possédez pas assez d'objets.");

        int remaining = quantity;
        for (int slot = 0; slot < player.getInventory().getStorageContents().length; slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (stack == null || stack.getType() != offer.material()) continue;
            int removed = Math.min(stack.getAmount(), remaining);
            if (stack.getAmount() == removed) player.getInventory().setItem(slot, null);
            else stack.setAmount(stack.getAmount() - removed);
            remaining -= removed;
            if (remaining == 0) break;
        }
        int after = offer.remainingQuantity() - quantity;
        offers.remove(offer);
        if (after > 0) offers.add(new BuyOffer(offer.id(), offer.itemName(), offer.material(), offer.requestedQuantity(),
                after, offer.unitPrice(), offer.buyer()));
        return ActionResult.success("Vous avez livré " + quantity + " × " + offer.itemName()
                + " pour " + quantity * offer.unitPrice() + " (simulation).");
    }

    @Override
    public ActionResult createOffer(Player player, OfferItem item, int quantity, double unitPrice) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(item);
        if (quantity <= 0 || quantity > item.available() || !Double.isFinite(unitPrice) || unitPrice <= 0) {
            return ActionResult.failure("Quantité ou prix invalide.");
        }
        ItemStack stack = player.getInventory().getItem(item.inventorySlot());
        if (stack == null || stack.getType() != item.material() || stack.getAmount() < quantity
                || !displayName(stack).equals(item.name())) {
            return ActionResult.failure("L'objet sélectionné n'est plus disponible.");
        }
        String name = displayName(stack);
        offers.add(new BuyOffer("offer-" + ids.getAndIncrement(), name, item.material(), quantity, quantity,
                unitPrice, player.getName()));
        return ActionResult.success("Offre d'achat créée : " + quantity + " × " + name + " à "
                + unitPrice + " par objet.");
    }

    private String displayName(ItemStack stack) {
        return stack.hasItemMeta() && stack.getItemMeta().hasDisplayName()
                ? stack.getItemMeta().getDisplayName() : stack.getType().name();
    }
}
