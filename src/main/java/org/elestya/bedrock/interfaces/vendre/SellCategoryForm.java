package org.elestya.bedrock.interfaces.vendre;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.sources.ShopCategory;
import org.elestya.bedrock.api.sources.SellableItem;
import org.elestya.bedrock.api.sources.SellItem;
import org.elestya.bedrock.api.sources.SellSource;
import org.elestya.bedrock.interfaces.FormService;
import org.elestya.bedrock.interfaces.FormImagePaths;
import org.elestya.bedrock.interfaces.PriceFormatter;
import org.elestya.bedrock.interfaces.FormStyle;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;

import java.util.Objects;
import java.util.List;

public class SellCategoryForm {

    private final FormService formService;
    private final SellSource sellSource;

    public SellCategoryForm(SellSource sellSource, FormService formService){
        this.formService = Objects.requireNonNull(formService);
        this.sellSource = Objects.requireNonNull(sellSource);
    }

    public void open(Player player, ShopCategory category) {

        Objects.requireNonNull(player);
        Objects.requireNonNull(category);

        formService.runSync(player, () -> openSync(player, category));
    }

    private void openSync(Player player, ShopCategory category) {
        SimpleForm.Builder builder = SimpleForm.builder()
                .title(FormStyle.title("Vente · " + category.getDisplayName()));

        List<SellableItem> items = sellSource.getSellableItems(player, category);
        if (items.isEmpty()) {
            builder.content(FormStyle.TEXT + "Aucun objet vendable dans ce rayon.");
        } else {
            double total = items.stream().mapToDouble(sellable -> sellable.item().price() * sellable.quantity()).sum();
            builder.button(FormStyle.PRIMARY + "Tout vendre\n" + FormStyle.RESET
                    + FormStyle.MUTED + "Total : " + FormStyle.PRICE + PriceFormatter.format(total));
            for (SellableItem sellable : items) {
                SellItem item = sellable.item();
                builder.button(FormStyle.ITEM_TITLE + item.name() + "\n" + FormStyle.RESET
                                + FormStyle.MUTED + "En sac : " + FormStyle.VALUE + sellable.quantity()
                                + "\n" + FormStyle.MUTED + "Prix unitaire : " + FormStyle.PRICE
                                + PriceFormatter.format(item.price()),
                        FormImage.Type.PATH, FormImagePaths.forMaterial(item.item().getType()));
            }
        }

        SimpleForm form = builder.button(FormStyle.RETURN + "← Retour")
                .closedOrInvalidResultHandler(formService.sync(player, () -> new SellMainForm(formService, sellSource).open(player)))
                .validResultHandler(formService.sync(player, response -> {

                    int buttonId = response.clickedButtonId();
                    if (buttonId == items.size() + (items.isEmpty() ? 0 : 1)) {
                        new SellMainForm(formService, sellSource).open(player);
                        return;
                    }
                    if (buttonId < 0 || buttonId >= items.size() + (items.isEmpty() ? 0 : 1)) {
                        return;
                    }

                    if (!items.isEmpty() && buttonId == 0) {
                        new SellConfirmForm(formService, sellSource).openAll(player, category, items);
                        return;
                    }
                    int itemIndex = buttonId - (items.isEmpty() ? 0 : 1);
                    new SellItemForm(formService, sellSource).open(player, items.get(itemIndex).item());
                }))
                .build();
        formService.sendFormSync(player, form);

    }
}
