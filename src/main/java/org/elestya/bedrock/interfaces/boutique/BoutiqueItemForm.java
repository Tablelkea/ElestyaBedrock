package org.elestya.bedrock.interfaces.boutique;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.sources.BoutiqueItem;
import org.elestya.bedrock.api.sources.BoutiqueSource;
import org.elestya.bedrock.interfaces.FormService;
import org.geysermc.cumulus.form.CustomForm;

import java.util.Objects;

public class BoutiqueItemForm {

    private final FormService formService;
    private final BoutiqueSource boutiqueSource;

    public BoutiqueItemForm(FormService formService, BoutiqueSource boutiqueSource){

        this.formService = Objects.requireNonNull(formService);
        this.boutiqueSource = Objects.requireNonNull(boutiqueSource);

    }

    public void open(Player player, BoutiqueItem item) {

        Objects.requireNonNull(player);
        Objects.requireNonNull(item);

        CustomForm form = CustomForm.builder()
                .title("§8§l"+ item.category().text() + " - " + item.name())
                .label("§7Prix: §6" + item.price() + "§7 / unité")
                .slider("§7Quantité§7§l", 1, 64, 1, 1)
                .closedOrInvalidResultHandler(response -> {
                    new BoutiqueCategoryForm(boutiqueSource, formService).open(player, item.category());
                })
                .validResultHandler(formService.sync(player, response -> {

                    int quantity = (int) response.asSlider();

                    new BoutiqueConfirmForm(formService, boutiqueSource).open(player, item, quantity);
                }))
                .build();

        formService.sendFormSync(player, form);
    }
}
