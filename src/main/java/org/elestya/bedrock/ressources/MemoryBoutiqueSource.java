package org.elestya.bedrock.ressources;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.elestya.bedrock.api.result.ActionResult;
import org.elestya.bedrock.api.sources.BoutiqueItem;
import org.elestya.bedrock.api.sources.BoutiqueSource;
import org.elestya.bedrock.api.sources.ShopCategory;

import java.util.*;

public class MemoryBoutiqueSource implements BoutiqueSource {

    private final Map<String, BoutiqueItem> boutiqueItems = new HashMap<>();
    private final Map<ShopCategory, List<BoutiqueItem>> shopCategory = new HashMap<>();

    public MemoryBoutiqueSource(){
        new MemoryBoutiqueSeeder(this).seed();
    }

    public void addItems(BoutiqueItem... items){

        for(BoutiqueItem item : items){
            boutiqueItems.put(item.name(), item);
            shopCategory.putIfAbsent(item.category(), new ArrayList<>());
            List<BoutiqueItem> category = shopCategory.get(item.category());
            category.add(item);
        }
    }

    @Override
    public List<BoutiqueItem> getBoutiqueItems() {
        return List.copyOf(boutiqueItems.values());
    }

    @Override
    public Optional<BoutiqueItem> findItemByName(String name) {
        Objects.requireNonNull(name);

        return Optional.ofNullable(boutiqueItems.get(name));
    }

    @Override
    public List<BoutiqueItem> getItemFromCategory(ShopCategory category) {

        Objects.requireNonNull(category);

        List<BoutiqueItem> items = shopCategory.get(category);

        if(items == null) return List.of();

        return List.copyOf(items);
    }

    @Override
    public ActionResult buy(Player player, BoutiqueItem item, int quantity) {

        Objects.requireNonNull(player);
        Objects.requireNonNull(item);

        ItemStack cloned = item.item().clone();

        cloned.setAmount(quantity);

        player.give(cloned);
        return ActionResult.success("Vous avez acheté " + quantity + " " + item.name());
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
