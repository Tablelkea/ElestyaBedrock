package org.elestya.bedrock.interfaces.vendre;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.sources.SellableItem;
import org.elestya.bedrock.api.sources.SellItem;
import org.elestya.bedrock.api.sources.SellSource;
import org.elestya.bedrock.interfaces.FormService;
import org.elestya.bedrock.interfaces.PriceFormatter;
import org.elestya.bedrock.interfaces.FormStyle;
import org.geysermc.cumulus.form.CustomForm;

import java.util.Objects;

public class SellItemForm {

    private final SellSource sellSource;
    private final FormService formService;

    public SellItemForm(FormService formService, SellSource sellSource){
        this.formService = Objects.requireNonNull(formService);
        this.sellSource = Objects.requireNonNull(sellSource);
    }

    public void open(Player player, SellItem item) {

        Objects.requireNonNull(player);
        Objects.requireNonNull(item);

        formService.runSync(player, () -> openSync(player, item));
    }

    private void openSync(Player player, SellItem item) {
        int quantity = sellSource.getSellableItems(player, item.category()).stream()
                .filter(sellable -> sellable.item().name().equals(item.name()))
                .mapToInt(SellableItem::quantity)
                .findFirst()
                .orElse(0);

        if (quantity == 0) {
            player.sendMessage(FormStyle.DANGER + "Tu n'en as plus.");
            new SellCategoryForm(sellSource, formService).open(player, item.category());
            return;
        }

        CustomForm form = CustomForm.builder()
                .title(FormStyle.title(item.category().getDisplayName() + " · " + item.name()))
                .label(FormStyle.TEXT + "Prix unitaire : " + FormStyle.PRICE + PriceFormatter.format(item.price())
                        + "\n" + FormStyle.TEXT + "Disponible dans le sac : " + FormStyle.VALUE + quantity)
                .slider(FormStyle.TEXT + "Quantité à vendre", 1, quantity, 1, 1)
                .closedOrInvalidResultHandler(formService.sync(player, response -> {
                    new SellCategoryForm(sellSource, formService).open(player, item.category());
                }))
                .validResultHandler(formService.sync(player, response -> {

                    int sellQuantity = (int) response.asSlider();

                    new SellConfirmForm(formService, sellSource).open(player, item, sellQuantity);
                }))
                .build();

        formService.sendFormSync(player, form);
    }

}
