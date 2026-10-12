package org.elestya.bedrock.interfaces.vendre;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.elestya.bedrock.api.sources.ShopCategory;
import org.elestya.bedrock.api.sources.VendreSource;
import org.elestya.bedrock.interfaces.FormService;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;

import java.util.Objects;

public class VendreMainForm {

    private final FormService formService;
    private final VendreSource vendreSource;

    public VendreMainForm(FormService formService, VendreSource vendreSource){
        this.formService = Objects.requireNonNull(formService);
        this.vendreSource = Objects.requireNonNull(vendreSource);
    }

    public void open(Player player){

        Objects.requireNonNull(player);

        SimpleForm.Builder builder = SimpleForm.builder()
                .title("§8§lVendre");

        for(ShopCategory category : ShopCategory.values()){
            builder.button(category.text(), FormImage.Type.URL, getURL(category.icon()));
        }
        builder.closedOrInvalidResultHandler(response -> {})
                .validResultHandler(formService.sync(player, response -> {
                    String title = response.clickedButton().text();
                    ShopCategory categoryName = vendreSource.getCategoryByName(title);
                    new VendreCategoryForm(vendreSource, formService).open(player, categoryName);
                }));
        SimpleForm form = builder.build();

        formService.sendFormSync(player, form);

    }

    private String getURL(Material material){
        return "https://blockrender.dev/render/item/"+material.name()+".png?size=128";
    }

}
