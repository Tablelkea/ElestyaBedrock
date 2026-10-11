package org.elestya.bedrock.interfaces.boutique;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.elestya.bedrock.api.sources.BoutiqueSource;
import org.elestya.bedrock.api.sources.ShopCategory;
import org.elestya.bedrock.interfaces.FormService;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;

import java.util.Objects;

public class BoutiqueMainForm {

    private final FormService formService;
    private final BoutiqueSource boutiqueSource;

    public BoutiqueMainForm(FormService formService, BoutiqueSource boutiqueSource){
        this.formService = Objects.requireNonNull(formService);
        this.boutiqueSource = Objects.requireNonNull(boutiqueSource);
    }

    public void open(Player player) {
        Objects.requireNonNull(player);

        SimpleForm.Builder builder = SimpleForm.builder()
                .title("§8§lBoutique");
                for(ShopCategory category : ShopCategory.values()){
                    builder.button(category.text(), FormImage.Type.URL, getURL(category.icon()));
                }
                builder.closedOrInvalidResultHandler(response -> {})
                        .validResultHandler(formService.sync(player, response -> {
                            String title = response.clickedButton().text();
                            ShopCategory categoryName = boutiqueSource.getCategoryByName(title);
                            new BoutiqueCategoryForm(boutiqueSource, formService).open(player, categoryName);
                        }));
                SimpleForm form = builder.build();

                formService.sendFormSync(player, form);
    }

    private String getURL(Material material){
        return "https://blockrender.dev/render/item/"+material.name()+".png?size=128";
    }
}
