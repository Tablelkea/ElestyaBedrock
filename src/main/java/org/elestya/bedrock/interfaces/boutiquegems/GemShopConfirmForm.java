package org.elestya.bedrock.interfaces.boutiquegems;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.result.ActionResult;
import org.elestya.bedrock.api.sources.GemShopCategory;
import org.elestya.bedrock.api.sources.GemShopItem;
import org.elestya.bedrock.api.sources.GemShopSource;
import org.elestya.bedrock.interfaces.FormService;
import org.elestya.bedrock.interfaces.FormStyle;
import org.geysermc.cumulus.form.ModalForm;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

final class GemShopConfirmForm {

    private final FormService forms;
    private final GemShopSource source;

    GemShopConfirmForm(FormService forms, GemShopSource source) {
        this.forms = Objects.requireNonNull(forms);
        this.source = Objects.requireNonNull(source);
    }

    void open(Player player, Runnable parent, GemShopCategory category, GemShopItem item) {
        open(player, parent, category, item, 0);
    }

    void open(Player player, Runnable parent, GemShopCategory category, GemShopItem item, int page) {
        ModalForm form = ModalForm.builder()
                .title(FormStyle.title("Confirmer l'achat"))
                .content("Acheter " + item.name() + " pour " + item.gemPrice() + " gems ?\n"
                        + "Solde actuel : " + source.getGemBalance(player) + " gems.")
                .button1(FormStyle.CONFIRM)
                .button2(FormStyle.DANGER + "Annuler")
                .closedOrInvalidResultHandler(forms.sync(player,
                        () -> openCategory(player, parent, category, null, page)))
                .validResultHandler(forms.sync(player, once(response -> {
                    if (!response.clickedFirst()) {
                        openCategory(player, parent, category, null, page);
                        return;
                    }
                    ActionResult result = source.buy(player, item);
                    openCategory(player, parent, category,
                            (result.success() ? FormStyle.SUCCESS : FormStyle.DANGER) + result.message(), page);
                })))
                .build();
        forms.sendFormSync(player, form);
    }

    private void openCategory(Player player, Runnable parent, GemShopCategory category, String result, int page) {
        new GemShopCategoryForm(forms, source).open(player, parent, category, result, page);
    }

    private <T> java.util.function.Consumer<T> once(java.util.function.Consumer<T> action) {
        AtomicBoolean handled = new AtomicBoolean();
        return value -> {
            if (handled.compareAndSet(false, true)) action.accept(value);
        };
    }
}
