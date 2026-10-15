package org.elestya.bedrock.interfaces.boutique;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.sources.ShopItem;
import org.elestya.bedrock.api.sources.ShopSource;
import org.elestya.bedrock.interfaces.FormService;
import org.elestya.bedrock.interfaces.FormStyle;
import org.elestya.bedrock.interfaces.PriceFormatter;
import org.geysermc.cumulus.form.CustomForm;

import java.util.Objects;

public class ShopItemForm {

    private final FormService formService;
    private final ShopSource shopSource;

    public ShopItemForm(FormService formService, ShopSource shopSource){

        this.formService = Objects.requireNonNull(formService);
        this.shopSource = Objects.requireNonNull(shopSource);

    }

    public void open(Player player, ShopItem item) {

        Objects.requireNonNull(player);
        Objects.requireNonNull(item);

        CustomForm form = CustomForm.builder()
                .title(FormStyle.title(item.category().getDisplayName() + " · " + item.name()))
                .label(FormStyle.TEXT + "Prix unitaire : " + FormStyle.PRICE
                        + PriceFormatter.format(item.price()))
                .slider(FormStyle.TEXT + "Quantité à acheter", 1, 64, 1, 1)
                .closedOrInvalidResultHandler(formService.sync(player, response -> {
                    new ShopCategoryForm(shopSource, formService).open(player, item.category());
                }))
                .validResultHandler(formService.sync(player, response -> {

                    int quantity = (int) response.asSlider();

                    new ShopConfirmForm(formService, shopSource).open(player, item, quantity);
                }))
                .build();

        formService.sendFormSync(player, form);
    }
}
