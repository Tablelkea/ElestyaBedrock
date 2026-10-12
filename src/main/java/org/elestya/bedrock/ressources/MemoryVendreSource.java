package org.elestya.bedrock.ressources;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.elestya.bedrock.api.result.ActionResult;
import org.elestya.bedrock.api.sources.ShopCategory;
import org.elestya.bedrock.api.sources.VendreItem;
import org.elestya.bedrock.api.sources.VendreSource;

import java.util.*;

public class MemoryVendreSource implements VendreSource {

    private final Map<String, VendreItem> vendreItems = new HashMap<>();
    private final Map<ShopCategory, List<VendreItem>> shopCategory = new HashMap<>();

    public MemoryVendreSource(){
        new MemoryVendreSeeder(this).seed();
    }

    @Override
    public List<VendreItem> getVendreItems() {
        return List.copyOf(vendreItems.values());
    }

    public void addItems(VendreItem... items){

        for(VendreItem item : items){
            vendreItems.put(item.name(), item);
            shopCategory.putIfAbsent(item.category(), new ArrayList<>());
            List<VendreItem> category = shopCategory.get(item.category());
            category.add(item);
        }
    }

    @Override
    public Optional<VendreItem> findItemByName(String name) {
        Objects.requireNonNull(name);

        return Optional.ofNullable(vendreItems.get(name));
    }

    @Override
    public List<VendreItem> getItemFromCategory(ShopCategory category) {

        Objects.requireNonNull(category);

        List<VendreItem> items = shopCategory.get(category);

        if(items == null) return List.of();

        return List.copyOf(items);
    }

    @Override
    public ActionResult sell(Player player, VendreItem item, int quantity) {
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
            if(category.text().equalsIgnoreCase(name)){
                return category;
            }
        }

        return null;

    }
}
