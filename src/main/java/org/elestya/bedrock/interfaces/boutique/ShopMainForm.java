package org.elestya.bedrock.interfaces.boutique;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.sources.ShopSource;
import org.elestya.bedrock.api.sources.ShopCategory;
import org.elestya.bedrock.interfaces.FormService;
import org.elestya.bedrock.interfaces.FormImagePaths;
import org.elestya.bedrock.interfaces.FormStyle;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;

import java.util.Objects;
import java.util.List;

public class ShopMainForm {

    private final FormService formService;
    private final ShopSource shopSource;

    public ShopMainForm(FormService formService, ShopSource shopSource){
        this.formService = Objects.requireNonNull(formService);
        this.shopSource = Objects.requireNonNull(shopSource);
    }

    public void open(Player player) {
        Objects.requireNonNull(player);

        List<ShopCategory> categories = java.util.Arrays.stream(ShopCategory.values())
                .filter(category -> category != ShopCategory.FISHING)
                .toList();
        SimpleForm.Builder builder = SimpleForm.builder()
                .title(FormStyle.title("Boutique"));
        for (ShopCategory category : categories) {
            builder.button(FormStyle.PRIMARY + category.getDisplayName(),
                    FormImage.Type.PATH, FormImagePaths.forMaterial(category.getIcon()));
        }
        builder.closedOrInvalidResultHandler(response -> {})
                .validResultHandler(formService.sync(player, response -> {
                    int buttonId = response.clickedButtonId();
                    if (buttonId < 0 || buttonId >= categories.size()) {
                        return;
                    }
                    new ShopCategoryForm(shopSource, formService).open(player, categories.get(buttonId));
                }));
        SimpleForm form = builder.build();

        formService.sendFormSync(player, form);
    }
}
