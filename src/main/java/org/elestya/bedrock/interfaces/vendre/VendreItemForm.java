package org.elestya.bedrock.interfaces.vendre;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.elestya.bedrock.api.sources.VendreItem;
import org.elestya.bedrock.api.sources.VendreSource;
import org.elestya.bedrock.interfaces.FormService;
import org.geysermc.cumulus.form.CustomForm;

import java.util.Objects;

public class VendreItemForm {

    private final VendreSource vendreSource;
    private final FormService formService;

    public VendreItemForm(FormService formService, VendreSource vendreSource){
        this.formService = Objects.requireNonNull(formService);
        this.vendreSource = Objects.requireNonNull(vendreSource);
    }

    public void open(Player player, VendreItem item) {

        Objects.requireNonNull(player);
        Objects.requireNonNull(item);

        int quantity = 0;

        for (ItemStack itemStack : player.getInventory().getStorageContents()) {
            if (itemStack == null || !itemStack.isSimilar(item.item())) {
                continue;
            }

            quantity += itemStack.getAmount();
        }

        CustomForm form = CustomForm.builder()
                .title("§8§l"+ item.category().text() + " - " + item.name())
                .label("§7Prix: §6" + item.price() + "§7 / unité")
                .slider("§7Quantité§7§l", 1, quantity, 1, 1)
                .closedOrInvalidResultHandler(response -> {
                    new VendreCategoryForm(vendreSource, formService).open(player, item.category());
                })
                .validResultHandler(formService.sync(player, response -> {

                    int sellQuantity = (int) response.asSlider();

                    new VendreConfirmForm(formService, vendreSource).open(player, item, sellQuantity);
                }))
                .build();

        formService.sendFormSync(player, form);
    }

}
