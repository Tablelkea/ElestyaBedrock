package org.elestya.bedrock.interfaces.boutique;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.elestya.bedrock.api.sources.BoutiqueItem;
import org.elestya.bedrock.api.sources.BoutiqueSource;
import org.elestya.bedrock.api.sources.ShopCategory;
import org.elestya.bedrock.interfaces.FormService;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;

import java.util.Objects;
import java.util.Optional;

public class BoutiqueCategoryForm {

    private final BoutiqueSource boutiqueSource;
    private final FormService formService;

    BoutiqueCategoryForm(BoutiqueSource source, FormService formService){
        this.formService = Objects.requireNonNull(formService);
        this.boutiqueSource = Objects.requireNonNull(source);
    }

    public void open(Player player, ShopCategory category) {

        Objects.requireNonNull(player);

        SimpleForm.Builder builder = SimpleForm.builder()
                .title("§8§lBoutique - " + category.text());

        for(BoutiqueItem item : boutiqueSource.getItemFromCategory(category)){
            builder.button( "§l" + item.name() + "\n§r§8Achat: §6" + item.price() + "§8 / unité", FormImage.Type.URL, getURL(item.item().getType()));
        }

        SimpleForm form = builder.button("Retour")
                .closedOrInvalidResultHandler(() -> {
                    new BoutiqueMainForm(formService, boutiqueSource).open(player);
                })
                .validResultHandler(formService.sync(player, response -> {

                    String title = response.clickedButton().text();

                    if(title.equalsIgnoreCase("Retour")){
                        new BoutiqueMainForm(formService, boutiqueSource).open(player);
                        return;
                    }
                    String itemName = title.split("\n", 2)[0];

                    Optional<BoutiqueItem> item = boutiqueSource.findItemByName(itemName.replace("§l", ""));

                    if(item.isEmpty()){
                        return;
                    }

                    new BoutiqueItemForm(formService, boutiqueSource).open(player, item.get());

                }))


                .build();
        formService.sendFormSync(player, form);

    }

    private String getURL(Material material){
        return "https://blockrender.dev/render/item/"+material.name()+".png?size=128";
    }
}
