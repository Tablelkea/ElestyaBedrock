package org.elestya.bedrock.interfaces.vendre;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.sources.SellableItem;
import org.elestya.bedrock.api.sources.ShopCategory;
import org.elestya.bedrock.api.sources.SellSource;
import org.elestya.bedrock.interfaces.FormService;
import org.elestya.bedrock.interfaces.FormImagePaths;
import org.elestya.bedrock.interfaces.PriceFormatter;
import org.elestya.bedrock.interfaces.FormStyle;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;

import java.util.List;
import java.util.Objects;

public class SellMainForm {

    private final FormService formService;
    private final SellSource sellSource;

    public SellMainForm(FormService formService, SellSource sellSource){
        this.formService = Objects.requireNonNull(formService);
        this.sellSource = Objects.requireNonNull(sellSource);
    }

    public void open(Player player){

        Objects.requireNonNull(player);

        formService.runSync(player, () -> openSync(player));
    }

    private void openSync(Player player) {
        List<ShopCategory> categories = java.util.Arrays.asList(ShopCategory.values());
        List<SellableItem> sellableItems = categories.stream()
                .flatMap(category -> sellSource.getSellableItems(player, category).stream())
                .toList();
        double total = sellableItems.stream()
                .mapToDouble(sellable -> sellable.item().price() * sellable.quantity())
                .sum();
        SimpleForm.Builder builder = SimpleForm.builder()
                .title(FormStyle.title("Vente"))
                .button(sellableItems.isEmpty()
                        ? FormStyle.PRIMARY + "Tout vendre\n" + FormStyle.RESET
                                + FormStyle.MUTED + "Aucun objet à vendre"
                        : FormStyle.PRIMARY + "Tout vendre\n" + FormStyle.RESET
                                + FormStyle.MUTED + "Total : " + FormStyle.PRICE + PriceFormatter.format(total));

        for (ShopCategory category : categories) {
            builder.button(FormStyle.PRIMARY + category.getDisplayName(),
                    FormImage.Type.PATH, FormImagePaths.forMaterial(category.getIcon()));
        }
        builder.closedOrInvalidResultHandler(response -> {})
                .validResultHandler(formService.sync(player, response -> {
                    int buttonId = response.clickedButtonId();
            if (buttonId == 0) {
                if (!sellableItems.isEmpty()) {
                    new SellConfirmForm(formService, sellSource).openAll(player, sellableItems);
                } else {
                    player.sendMessage(FormStyle.DANGER + "Tu n'as rien à vendre.");
                }
                return;
            }
            if (buttonId < 1 || buttonId > categories.size()) {
                return;
            }
            new SellCategoryForm(sellSource, formService).open(player, categories.get(buttonId - 1));
        }));
        SimpleForm form = builder.build();

        formService.sendFormSync(player, form);

    }
}
