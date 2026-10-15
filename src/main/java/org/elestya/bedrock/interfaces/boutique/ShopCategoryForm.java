package org.elestya.bedrock.interfaces.boutique;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.sources.ShopItem;
import org.elestya.bedrock.api.sources.ShopSource;
import org.elestya.bedrock.api.sources.ShopCategory;
import org.elestya.bedrock.interfaces.FormImagePaths;
import org.elestya.bedrock.interfaces.FormService;
import org.elestya.bedrock.interfaces.FormStyle;
import org.elestya.bedrock.interfaces.PriceFormatter;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;

import java.util.Objects;
import java.util.ArrayList;
import java.util.List;

public class ShopCategoryForm {

    private final ShopSource shopSource;
    private final FormService formService;

    ShopCategoryForm(ShopSource source, FormService formService){
        this.formService = Objects.requireNonNull(formService);
        this.shopSource = Objects.requireNonNull(source);
    }

    public void open(Player player, ShopCategory category) {

        Objects.requireNonNull(player);

        SimpleForm.Builder builder = SimpleForm.builder()
                .title(FormStyle.title("Boutique · " + category.getDisplayName()));

        List<ShopItem> items = shopSource.getItemsByCategory(category);
        for (ShopItem item : items) {
            builder.button(FormStyle.ITEM_TITLE + item.name()
                            + "\n" + FormStyle.RESET + FormStyle.MUTED + "Achat : "
                            + FormStyle.PRICE + PriceFormatter.format(item.price())
                            + FormStyle.MUTED + " / unité",
                    FormImage.Type.PATH, FormImagePaths.forMaterial(item.item().getType()));
        }

        SimpleForm form = builder.button(FormStyle.RETURN + "← Retour")
                .closedOrInvalidResultHandler(formService.sync(player, () -> {
                    new ShopMainForm(formService, shopSource).open(player);
                }))
                .validResultHandler(formService.sync(player, response -> {

                    int buttonId = response.clickedButtonId();
                    if (buttonId == items.size()) {
                        new ShopMainForm(formService, shopSource).open(player);
                        return;
                    }

                    if (buttonId < 0 || buttonId >= items.size()) {
                        return;
                    }

                    new ShopItemForm(formService, shopSource).open(player, items.get(buttonId));

                }))
                .build();
        formService.sendFormSync(player, form);

    }
}
