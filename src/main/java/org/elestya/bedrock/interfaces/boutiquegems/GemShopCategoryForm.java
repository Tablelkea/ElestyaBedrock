package org.elestya.bedrock.interfaces.boutiquegems;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.sources.GemShopCategory;
import org.elestya.bedrock.api.sources.GemShopItem;
import org.elestya.bedrock.api.sources.GemShopSource;
import org.elestya.bedrock.interfaces.FormImagePaths;
import org.elestya.bedrock.interfaces.FormPages;
import org.elestya.bedrock.interfaces.FormService;
import org.elestya.bedrock.interfaces.FormStyle;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

final class GemShopCategoryForm {

    private final FormService forms;
    private final GemShopSource source;

    GemShopCategoryForm(FormService forms, GemShopSource source) {
        this.forms = Objects.requireNonNull(forms);
        this.source = Objects.requireNonNull(source);
    }

    void open(Player player, Runnable parent, GemShopCategory category, String result) {
        open(player, parent, category, result, 0);
    }

    void open(Player player, Runnable parent, GemShopCategory category, String result, int page) {
        FormPages.Page<GemShopItem> itemPage = FormPages.of(source.getItems(category), page);
        List<GemShopItem> items = itemPage.values();
        String content = (result == null ? "" : result + "\n\n")
                + (items.isEmpty() ? "Aucun article dans cette catégorie."
                : "Solde : " + source.getGemBalance(player) + " gems.")
                + (itemPage.count() > 1 ? "\nPage " + (itemPage.number() + 1)
                + " / " + itemPage.count() : "");
        SimpleForm.Builder builder = SimpleForm.builder()
                .title(FormStyle.title("Gems · " + category.displayName()))
                .content(content);
        for (GemShopItem item : items) {
            builder.button(FormStyle.PRIMARY + item.name() + "\n"
                            + FormStyle.PRICE + item.gemPrice() + " gems · " + FormStyle.TEXT + item.description(),
                    FormImage.Type.PATH, FormImagePaths.forMaterial(item.icon()));
        }
        boolean hasPrevious = itemPage.hasPrevious();
        if (hasPrevious) builder.button(FormStyle.RETURN + "← Page précédente");
        boolean hasNext = itemPage.hasNext();
        if (hasNext) builder.button(FormStyle.PRIMARY + "Page suivante →");
        int previousIndex = hasPrevious ? items.size() : -1;
        int nextIndex = hasNext ? items.size() + (hasPrevious ? 1 : 0) : -1;
        int returnIndex = items.size() + (hasPrevious ? 1 : 0) + (hasNext ? 1 : 0);
        builder.button(FormStyle.RETURN + "← Retour")
                .closedOrInvalidResultHandler(forms.sync(player,
                        () -> new GemShopMainForm(forms, source).open(player, parent)))
                .validResultHandler(forms.sync(player, once(response -> {
                    int index = response.clickedButtonId();
                    if (index >= 0 && index < items.size()) {
                        new GemShopConfirmForm(forms, source).open(player, parent, category,
                                items.get(index), itemPage.number());
                    } else if (index == previousIndex) {
                        open(player, parent, category, null, itemPage.number() - 1);
                    } else if (index == nextIndex) {
                        open(player, parent, category, null, itemPage.number() + 1);
                    } else if (index == returnIndex) {
                        new GemShopMainForm(forms, source).open(player, parent);
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
