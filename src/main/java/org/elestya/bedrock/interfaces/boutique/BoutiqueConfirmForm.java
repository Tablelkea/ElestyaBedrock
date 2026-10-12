package org.elestya.bedrock.interfaces.boutique;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.sources.BoutiqueItem;
import org.elestya.bedrock.api.sources.BoutiqueSource;
import org.elestya.bedrock.interfaces.FormService;
import org.geysermc.cumulus.form.ModalForm;

import java.util.Objects;

public class BoutiqueConfirmForm {

    private final FormService formService;
    private final BoutiqueSource boutiqueSource;

    public BoutiqueConfirmForm(FormService formService, BoutiqueSource boutiqueSource){

        this.formService = Objects.requireNonNull(formService);
        this.boutiqueSource = Objects.requireNonNull(boutiqueSource);

    }

    public void open(Player player, BoutiqueItem item, int quantity){

        Objects.requireNonNull(player);
        Objects.requireNonNull(item);

        ModalForm form = ModalForm.builder()
                .title("§8§lConfirmation d'achat")
                .content("Vous êtes sur le point d'acheter " + quantity + " x §d§l " + item.name() + "\n§7§la un prix total de: §a§l" + item.price() * quantity)
                .button1("§c§lAnnuler")
                .button2("§2§lValider")
                .closedOrInvalidResultHandler(formService.sync(player, response -> {
                    new BoutiqueItemForm(formService, boutiqueSource).open(player, item);
                }))
                .validResultHandler(formService.sync(player, response -> {

                    if(response.clickedButtonText().equalsIgnoreCase("§c§lAnnuler")){
                        new BoutiqueItemForm(formService, boutiqueSource).open(player, item);
                        return;
                    }

                    openResultForm(player, item, quantity);
                    boutiqueSource.buy(player, item, quantity);
                }))
                .build();

        formService.sendFormSync(player, form);

    }

    private void openResultForm(Player player, BoutiqueItem item, int quantity){

        Objects.requireNonNull(player);
        Objects.requireNonNull(item);

        ModalForm form = ModalForm.builder()
                .title("§2§lAchat")
                .content("§7§lVous avez acheté "+ quantity +" x §d" + item.name() + " §7§lpour §6§l" + item.price() * quantity)
                .button1("§c§lFermer")
                .button2("§2§lAcheter a nouveau")
                .closedOrInvalidResultHandler(() -> {
                    new BoutiqueCategoryForm(boutiqueSource, formService).open(player, item.category());
                })
                .validResultHandler(formService.sync(player, response -> {
                    String title = response.clickedButtonText();

                    if(title.equalsIgnoreCase("Acheter a nouveau")){
                        new BoutiqueItemForm(formService, boutiqueSource).open(player, item);
                    }else{
                        new BoutiqueCategoryForm(boutiqueSource, formService).open(player, item.category());
                    }
                }))
                .build();

        formService.sendFormSync(player, form);

    }

}
