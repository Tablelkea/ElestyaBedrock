package org.elestya.bedrock.interfaces.vendre;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.result.ActionResult;
import org.elestya.bedrock.api.sources.ShopCategory;
import org.elestya.bedrock.api.sources.SellableItem;
import org.elestya.bedrock.api.sources.SellItem;
import org.elestya.bedrock.api.sources.SellSource;
import org.elestya.bedrock.interfaces.FormService;
import org.elestya.bedrock.interfaces.PriceFormatter;
import org.elestya.bedrock.interfaces.FormStyle;
import org.geysermc.cumulus.form.ModalForm;

import java.util.List;
import java.util.Objects;

public class SellConfirmForm {

    private final FormService formService;
    private final SellSource sellSource;

    public SellConfirmForm(FormService formService, SellSource sellSource) {
        this.formService = Objects.requireNonNull(formService);
        this.sellSource = Objects.requireNonNull(sellSource);
    }

    public void open(Player player, SellItem item, int quantity) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(item);

        String total = PriceFormatter.format(item.price() * quantity);
        ModalForm form = ModalForm.builder()
                .title(FormStyle.title("Confirmer la vente"))
                .content(FormStyle.TEXT + "Vendre " + FormStyle.VALUE + quantity + " × "
                        + FormStyle.PRIMARY + item.name() + "\n"
                        + FormStyle.TEXT + "Total estimé : " + FormStyle.PRICE + total)
                .button1(FormStyle.DANGER + "Annuler")
                .button2(FormStyle.CONFIRM)
                .closedOrInvalidResultHandler(formService.sync(player,
                        () -> new SellItemForm(formService, sellSource).open(player, item)))
                .validResultHandler(formService.sync(player, response -> {
                    if (response.clickedFirst()) {
                        new SellItemForm(formService, sellSource).open(player, item);
                        return;
                    }

                    ActionResult result = sellSource.sell(player, item, quantity);
                    player.sendMessage((result.success() ? FormStyle.SUCCESS : FormStyle.DANGER) + result.message());
                    if (result.success()) {
                        openResultForm(player, item, quantity);
                    }
                }))
                .build();

        formService.sendFormSync(player, form);
    }

    public void openAll(Player player, ShopCategory category, List<SellableItem> items) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(category);
        openAllInternal(player, category, items);
    }

    public void openAll(Player player, List<SellableItem> items) {
        Objects.requireNonNull(player);
        openAllInternal(player, null, items);
    }

    private void openAllInternal(Player player, ShopCategory category, List<SellableItem> items) {
        List<SellableItem> sellableItems = List.copyOf(items);
        double total = sellableItems.stream()
                .mapToDouble(sellable -> sellable.item().price() * sellable.quantity())
                .sum();
        int totalQuantity = sellableItems.stream().mapToInt(SellableItem::quantity).sum();
        Runnable returnToMenu = category == null
                ? () -> new SellMainForm(formService, sellSource).open(player)
                : () -> new SellCategoryForm(sellSource, formService).open(player, category);

        ModalForm form = ModalForm.builder()
                .title(FormStyle.title("Confirmer la vente"))
                .content(FormStyle.TEXT + "Vendre tous les objets éligibles : "
                        + FormStyle.VALUE + totalQuantity + "\n"
                        + FormStyle.TEXT + "Total estimé : " + FormStyle.PRICE + PriceFormatter.format(total))
                .button1(FormStyle.DANGER + "Annuler")
                .button2(FormStyle.CONFIRM)
                .closedOrInvalidResultHandler(formService.sync(player, returnToMenu))
                .validResultHandler(formService.sync(player, response -> {
                    if (response.clickedFirst()) {
                        returnToMenu.run();
                        return;
                    }

                    int soldQuantity = 0;
                    double soldTotal = 0;
                    for (SellableItem sellable : sellableItems) {
                        SellItem item = sellable.item();
                        ActionResult result = sellSource.sell(player, item, sellable.quantity());
                        player.sendMessage((result.success() ? FormStyle.SUCCESS : FormStyle.DANGER) + result.message());
                        if (result.success()) {
                            soldQuantity += sellable.quantity();
                            soldTotal += item.price() * sellable.quantity();
                        }
                    }
                    if (soldQuantity > 0) {
                        openResultForm(player, category, soldQuantity, soldTotal);
                    }
                }))
                .build();

        formService.sendFormSync(player, form);
    }

    private void openResultForm(Player player, SellItem item, int quantity) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(item);

        ModalForm form = ModalForm.builder()
                .title(FormStyle.title("Vente effectuée"))
                .content(FormStyle.TEXT + "Vendu : " + FormStyle.VALUE + quantity + " × "
                        + FormStyle.PRIMARY + item.name() + "\n" + FormStyle.TEXT + "Total : "
                        + FormStyle.PRICE + PriceFormatter.format(item.price() * quantity))
                .button1(FormStyle.SECONDARY + "Fermer")
                .button2(FormStyle.PRIMARY + "Vendre à nouveau")
                .closedOrInvalidResultHandler(formService.sync(player,
                        () -> new SellCategoryForm(sellSource, formService).open(player, item.category())))
                .validResultHandler(formService.sync(player, response -> {
                    if (response.clickedFirst()) {
                        new SellItemForm(formService, sellSource).open(player, item);
                    } else {
                        new SellCategoryForm(sellSource, formService).open(player, item.category());
                    }
                }))
                .build();

        formService.sendFormSync(player, form);
    }

    private void openResultForm(Player player, ShopCategory category, int soldQuantity, double total) {
        Runnable returnToMenu = category == null
                ? () -> new SellMainForm(formService, sellSource).open(player)
                : () -> new SellCategoryForm(sellSource, formService).open(player, category);
        ModalForm form = ModalForm.builder()
                .title(FormStyle.title("Vente effectuée"))
                .content(FormStyle.TEXT + "Objets vendus : " + FormStyle.VALUE + soldQuantity + "\n"
                        + FormStyle.TEXT + "Total : " + FormStyle.PRICE + PriceFormatter.format(total))
                .button1(FormStyle.SECONDARY + "Fermer")
                .button2(FormStyle.PRIMARY + "Vendre à nouveau")
                .closedOrInvalidResultHandler(formService.sync(player, returnToMenu))
                .validResultHandler(formService.sync(player, response -> returnToMenu.run()))
                .build();

        formService.sendFormSync(player, form);
    }
}
