package org.elestya.bedrock.interfaces.vendre;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.sources.VendreItem;
import org.elestya.bedrock.api.sources.VendreSource;
import org.elestya.bedrock.interfaces.FormService;
import org.geysermc.cumulus.form.ModalForm;

import java.util.Objects;

public class VendreConfirmForm {

    private final FormService formService;
    private final VendreSource vendreSource;

    public VendreConfirmForm(FormService formService, VendreSource vendreSource){
        this.formService = Objects.requireNonNull(formService);
        this.vendreSource = Objects.requireNonNull(vendreSource);
    }

    public void open(Player player, VendreItem item, int quantity){

        Objects.requireNonNull(player);
        Objects.requireNonNull(item);

        ModalForm form = ModalForm.builder()
                .title("§8§lConfirmation de vente")
                .content("Vous êtes sur le point de vendre " + quantity + " x §d§l " + item.name() + "\n§7§la un prix total de: §a§l" + item.price() * quantity)
                .button1("§c§lAnnuler")
                .button2("§2§lValider")
                .closedOrInvalidResultHandler(formService.sync(player, response -> {
                    new VendreItemForm(formService, vendreSource).open(player, item);
                }))
                .validResultHandler(formService.sync(player, response -> {

                    System.out.println(response.clickedButtonText());

                    if(response.clickedButtonText().equalsIgnoreCase("§c§lAnnuler")){
                        new VendreItemForm(formService, vendreSource).open(player, item);
                        return;
                    }

                    openResultForm(player, item, quantity);
                    vendreSource.sell(player, item, quantity);

                }))
                .build();

        formService.sendFormSync(player, form);

    }

    private void openResultForm(Player player, VendreItem item, int quantity){

        Objects.requireNonNull(player);
        Objects.requireNonNull(item);

        ModalForm form = ModalForm.builder()
                .title("§2§lAchat")
                .content("§7§lVous avez acheté "+ quantity +" x §d" + item.name() + "§7§l pour §6§l" + item.price() * quantity)
                .button1("§c§lFermer")
                .button2("§2§lAcheter a nouveau")
                .closedOrInvalidResultHandler(() -> {
                    new VendreCategoryForm(vendreSource, formService).open(player, item.category());
                })
                .validResultHandler(formService.sync(player, response -> {
                    String title = response.clickedButtonText();

                    if(title.equalsIgnoreCase("Acheter a nouveau")){
                        new VendreItemForm(formService, vendreSource).open(player, item);
                    }else{
                        new VendreCategoryForm(vendreSource, formService).open(player, item.category());
                    }
                }))
                .build();

        formService.sendFormSync(player, form);

    }

}
