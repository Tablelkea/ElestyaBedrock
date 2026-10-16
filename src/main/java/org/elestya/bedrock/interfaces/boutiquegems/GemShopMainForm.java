package org.elestya.bedrock.interfaces.boutiquegems;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.sources.GemShopCategory;
import org.elestya.bedrock.api.sources.GemShopSource;
import org.elestya.bedrock.interfaces.FormImagePaths;
import org.elestya.bedrock.interfaces.FormService;
import org.elestya.bedrock.interfaces.FormStyle;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

final class GemShopMainForm {

    private final FormService forms;
    private final GemShopSource source;

    GemShopMainForm(FormService forms, GemShopSource source) {
        this.forms = Objects.requireNonNull(forms);
        this.source = Objects.requireNonNull(source);
    }

    void open(Player player, Runnable parent) {
        GemShopCategory[] categories = GemShopCategory.values();
        SimpleForm.Builder builder = SimpleForm.builder()
                .title(FormStyle.title("Boutique Gems"))
                .content("Solde : " + FormStyle.PRICE + source.getGemBalance(player) + " gems\n"
                        + FormStyle.TEXT + "Choisissez une catégorie.");
        for (GemShopCategory category : categories) {
            builder.button(FormStyle.PRIMARY + category.displayName(), FormImage.Type.PATH,
                    FormImagePaths.forMaterial(category.icon()));
        }
        int backIndex = categories.length;
        builder.button(FormStyle.RETURN + "← Retour")
                .closedOrInvalidResultHandler(forms.sync(player, parent))
                .validResultHandler(forms.sync(player, once(response -> {
                    int index = response.clickedButtonId();
                    if (index >= 0 && index < categories.length) {
                        new GemShopCategoryForm(forms, source).open(player, parent, categories[index], null);
                    } else if (index == backIndex) {
                        parent.run();
                    }
                })));
        forms.sendFormSync(player, builder.build());
    }

    private <T> java.util.function.Consumer<T> once(java.util.function.Consumer<T> action) {
        AtomicBoolean handled = new AtomicBoolean();
        return value -> {
            if (handled.compareAndSet(false, true)) action.accept(value);
        };
    }
}
