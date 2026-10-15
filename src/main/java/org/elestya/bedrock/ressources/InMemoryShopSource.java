package org.elestya.bedrock.ressources;

import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ItemStack;
import org.elestya.bedrock.api.result.ActionResult;
import org.elestya.bedrock.api.sources.ShopItem;
import org.elestya.bedrock.api.sources.ShopCategory;
import org.elestya.bedrock.api.sources.ShopSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class InMemoryShopSource implements ShopSource {

    private final Map<String, ShopItem> shopItems = new HashMap<>();
    private final Map<ShopCategory, List<ShopItem>> itemsByCategory = new HashMap<>();

    public InMemoryShopSource(){
        new InMemoryShopSeeder(this).seed();
    }

    public void addItems(ShopItem... items){

        for(ShopItem item : items){
            shopItems.put(item.name(), item);
            itemsByCategory.putIfAbsent(item.category(), new ArrayList<>());
            List<ShopItem> category = itemsByCategory.get(item.category());
            category.add(item);
        }
    }

    @Override
    public List<ShopItem> getItems() {
        return List.copyOf(shopItems.values());
    }

    @Override
    public Optional<ShopItem> findItemByName(String name) {
        Objects.requireNonNull(name);

        return Optional.ofNullable(shopItems.get(name));
    }

    @Override
    public List<ShopItem> getItemsByCategory(ShopCategory category) {

        Objects.requireNonNull(category);

        List<ShopItem> items = itemsByCategory.get(category);

        if(items == null) return List.of();

        return List.copyOf(items);
    }

    @Override
    public ActionResult buy(Player player, ShopItem item, int quantity) {

        Objects.requireNonNull(player);
        Objects.requireNonNull(item);

        if (quantity <= 0) {
            return ActionResult.failure("La quantité doit être supérieure à zéro.");
        }

        ItemStack cloned = item.item().clone();
        cloned.setAmount(quantity);
        PlayerInventory inventory = player.getInventory();
        if (!hasSpace(inventory, cloned)) {
            return ActionResult.failure("Inventaire plein : achat annulé.");
        }

        ItemStack[] previousContents = inventory.getStorageContents();
        if (!inventory.addItem(cloned).isEmpty()) {
            inventory.setStorageContents(previousContents);
            return ActionResult.failure("Impossible de placer l'achat dans votre inventaire.");
        }
        return ActionResult.success("Vous avez acheté " + quantity + " " + item.name());
    }

    private boolean hasSpace(PlayerInventory inventory, ItemStack item) {
        long available = 0;
        int maxStackSize = item.getMaxStackSize();
        for (ItemStack content : inventory.getStorageContents()) {
            if (content == null || content.getType().isAir()) {
                available += maxStackSize;
            } else if (content.isSimilar(item)) {
                available += Math.max(0, maxStackSize - content.getAmount());
            }
            if (available >= item.getAmount()) {
                return true;
            }
        }
        return false;
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
