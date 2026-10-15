package org.elestya.bedrock.interfaces.boutique;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.result.ActionResult;
import org.elestya.bedrock.api.sources.ShopItem;
import org.elestya.bedrock.api.sources.ShopSource;
import org.elestya.bedrock.interfaces.FormService;
import org.elestya.bedrock.interfaces.FormStyle;
import org.elestya.bedrock.interfaces.PriceFormatter;
import org.geysermc.cumulus.form.ModalForm;

import java.util.Objects;

public class ShopConfirmForm {

    private final FormService formService;
    private final ShopSource shopSource;

    public ShopConfirmForm(FormService formService, ShopSource shopSource){

        this.formService = Objects.requireNonNull(formService);
        this.shopSource = Objects.requireNonNull(shopSource);

    }

    public void open(Player player, ShopItem item, int quantity){

        Objects.requireNonNull(player);
        Objects.requireNonNull(item);

        ModalForm form = ModalForm.builder()
                .title(FormStyle.title("Confirmer l'achat"))
                .content(FormStyle.TEXT + "Acheter " + FormStyle.VALUE + quantity + " × "
                        + FormStyle.PRIMARY + item.name() + "\n"
                        + FormStyle.TEXT + "Total : " + FormStyle.PRICE
                        + PriceFormatter.format(item.price() * quantity))
                .button1(FormStyle.DANGER + "Annuler")
                .button2(FormStyle.CONFIRM)
                .closedOrInvalidResultHandler(formService.sync(player, response -> {
                    new ShopItemForm(formService, shopSource).open(player, item);
                }))
                .validResultHandler(formService.sync(player, response -> {

                    if(response.clickedFirst()){
                        new ShopItemForm(formService, shopSource).open(player, item);
                        return;
                    }

                    ActionResult result = shopSource.buy(player, item, quantity);
                    if(result.success()){
                        player.sendMessage(FormStyle.SUCCESS + result.message());
                        openResultForm(player, item, quantity);
                    }else{
                        player.sendMessage(FormStyle.DANGER + result.message());
                    }
                }))
                .build();

        formService.sendFormSync(player, form);

    }

    private void openResultForm(Player player, ShopItem item, int quantity){

        Objects.requireNonNull(player);
        Objects.requireNonNull(item);

        ModalForm form = ModalForm.builder()
                .title(FormStyle.title("Achat effectué"))
                .content(FormStyle.TEXT + "Acheté : " + FormStyle.VALUE + quantity + " × "
                        + FormStyle.PRIMARY + item.name() + "\n" + FormStyle.TEXT + "Total : "
                        + FormStyle.PRICE + PriceFormatter.format(item.price() * quantity))
                .button1(FormStyle.SECONDARY + "Fermer")
                .button2(FormStyle.PRIMARY + "Acheter à nouveau")
                .closedOrInvalidResultHandler(formService.sync(player, () -> {
                    new ShopCategoryForm(shopSource, formService).open(player, item.category());
                }))
                .validResultHandler(formService.sync(player, response -> {
                    if(response.clickedFirst()){
                        new ShopItemForm(formService, shopSource).open(player, item);
                    }else{
                        new ShopCategoryForm(shopSource, formService).open(player, item.category());
                    }
                }))
                .build();

        formService.sendFormSync(player, form);

    }

}
