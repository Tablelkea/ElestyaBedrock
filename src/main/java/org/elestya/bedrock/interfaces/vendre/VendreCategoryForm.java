package org.elestya.bedrock.interfaces.vendre;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.elestya.bedrock.api.sources.ShopCategory;
import org.elestya.bedrock.api.sources.VendreItem;
import org.elestya.bedrock.api.sources.VendreSource;
import org.elestya.bedrock.interfaces.FormService;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;

import java.util.Objects;
import java.util.Optional;

public class VendreCategoryForm {

    private final FormService formService;
    private final VendreSource vendreSource;

    public VendreCategoryForm(VendreSource vendreSource, FormService formService){
        this.formService = Objects.requireNonNull(formService);
        this.vendreSource = Objects.requireNonNull(vendreSource);
    }

    public void open(Player player, ShopCategory category) {

        Objects.requireNonNull(player);

        SimpleForm.Builder builder = SimpleForm.builder()
                .title("§8§lVente - " + category.text());

        for (VendreItem item : vendreSource.getItemFromCategory(category)) {

            if (!player.getInventory().containsAtLeast(item.item(), 1)) {
                continue;
            }

            builder.button(
                    "§l" + item.name() + "\n§r§8Rachat: §6" + item.price() + "§8 / unité",
                    FormImage.Type.URL,
                    getURL(item.item().getType())
            );
        }

        SimpleForm form = builder.button("Retour")
                .closedOrInvalidResultHandler(() -> new VendreMainForm(formService, vendreSource).open(player))
                .validResultHandler(formService.sync(player, response -> {

                    String title = response.clickedButton().text();

                    if(title.equalsIgnoreCase("Retour")){
                        new VendreMainForm(formService, vendreSource).open(player);
                        return;
                    }
                    String itemName = title.split("\n", 2)[0];

                    Optional<VendreItem> item = vendreSource.findItemByName(itemName.replace("§l", ""));

                    if(item.isEmpty()){
                        return;
                    }

                    new VendreItemForm(formService, vendreSource).open(player, item.get());

                }))


                .build();
        formService.sendFormSync(player, form);

    }

    private String getURL(Material material){
        return "https://blockrender.dev/render/item/"+material.name()+".png?size=128";
    }

}
