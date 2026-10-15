package org.elestya.bedrock.ressources;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.elestya.bedrock.api.result.ActionResult;
import org.elestya.bedrock.api.sources.SellableItem;
import org.elestya.bedrock.api.sources.ShopCategory;
import org.elestya.bedrock.api.sources.SellItem;
import org.elestya.bedrock.api.sources.SellSource;

import java.util.*;

public class InMemorySellSource implements SellSource {

    private final Map<String, SellItem> sellItems = new HashMap<>();
    private final Map<ShopCategory, List<SellItem>> itemsByCategory = new HashMap<>();

    public InMemorySellSource(){
        new InMemorySellSeeder(this).seed();
    }

    @Override
    public List<SellItem> getItems() {
        return List.copyOf(sellItems.values());
    }

    public void addItems(SellItem... items){

        for(SellItem item : items){
            sellItems.put(item.name(), item);
            itemsByCategory.putIfAbsent(item.category(), new ArrayList<>());
            List<SellItem> category = itemsByCategory.get(item.category());
            category.add(item);
        }
    }

    @Override
    public Optional<SellItem> findItemByName(String name) {
        Objects.requireNonNull(name);

        return Optional.ofNullable(sellItems.get(name));
    }

    @Override
    public List<SellItem> getItemsByCategory(ShopCategory category) {

        Objects.requireNonNull(category);

        List<SellItem> items = itemsByCategory.get(category);

        if(items == null) return List.of();

        return List.copyOf(items);
    }

    @Override
    public List<SellableItem> getSellableItems(Player player, ShopCategory category) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(category);

        List<SellableItem> sellableItems = new ArrayList<>();
        for (SellItem item : getItemsByCategory(category)) {
            int quantity = 0;
            for (ItemStack stack : player.getInventory().getStorageContents()) {
                if (stack != null && stack.isSimilar(item.item())) {
                    quantity += stack.getAmount();
                }
            }
            if (quantity > 0) {
                sellableItems.add(new SellableItem(item, quantity));
            }
        }
        return List.copyOf(sellableItems);
    }

    @Override
    public ActionResult sell(Player player, SellItem item, int quantity) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(item);

        if (quantity <= 0) {
            return ActionResult.failure("La quantité doit être supérieure à 0.");
        }

        ItemStack requiredItem = item.item();

        if (!player.getInventory().containsAtLeast(requiredItem, quantity)) {
            return ActionResult.failure("Vous ne possédez pas assez de " + item.name());
        }

        int remaining = quantity;

        for (int slot = 0; slot < player.getInventory().getStorageContents().length; slot++) {
            ItemStack stack = player.getInventory().getItem(slot);

            if (stack == null || !stack.isSimilar(requiredItem)) {
                continue;
            }

            int removed = Math.min(stack.getAmount(), remaining);

            if (stack.getAmount() == removed) {
                player.getInventory().setItem(slot, null);
            } else {
                stack.setAmount(stack.getAmount() - removed);
            }

            remaining -= removed;

            if (remaining == 0) {
                break;
            }
        }

        return ActionResult.success("Vous avez vendu " + quantity + " " + item.name());
    }

    @Override
    public ShopCategory getCategoryByName(String name) {

        for(ShopCategory category : ShopCategory.values()){
            if(category.getDisplayName().equalsIgnoreCase(name)){
                return category;
            }
        }

        return null;

    }
}
