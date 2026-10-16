package org.elestya.bedrock.interfaces.cles;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.result.ActionResult;
import org.elestya.bedrock.api.sources.KeyOffer;
import org.elestya.bedrock.api.sources.KeyShopSource;
import org.elestya.bedrock.interfaces.FormImagePaths;
import org.elestya.bedrock.interfaces.FormPages;
import org.elestya.bedrock.interfaces.FormService;
import org.elestya.bedrock.interfaces.FormStyle;
import org.geysermc.cumulus.form.ModalForm;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public final class KeyShopService {

    private final FormService forms;
    private final KeyShopSource source;

    public KeyShopService(FormService forms, KeyShopSource source) {
        this.forms = Objects.requireNonNull(forms);
        this.source = Objects.requireNonNull(source);
    }

    public void open(Player player) {
        open(player, () -> {});
    }

    public void open(Player player, Runnable parent) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(parent);
        forms.runSync(player, () -> openRoot(player, parent, null));
    }

    private void openRoot(Player player, Runnable parent, String result) {
        openRoot(player, parent, result, 0);
    }

    private void openRoot(Player player, Runnable parent, String result, int page) {
        FormPages.Page<KeyOffer> keyPage = FormPages.of(source.getKeys(), page);
        List<KeyOffer> keys = keyPage.values();
        SimpleForm.Builder builder = SimpleForm.builder().title(FormStyle.title("Boutique de clés"))
                .content((result == null ? "" : result + "\n\n")
                        + "Solde : " + FormStyle.PRICE + source.getGemBalance(player) + " gems\n"
                        + FormStyle.TEXT + "Choisissez une clé pour consulter et confirmer son prix."
                        + (keyPage.count() > 1 ? "\nPage " + (keyPage.number() + 1)
                        + " / " + keyPage.count() : ""));
        for (KeyOffer key : keys) {
            builder.button(FormStyle.PRIMARY + key.name() + "\n" + FormStyle.PRICE
                            + key.gemPrice() + " gems · " + FormStyle.TEXT + key.description(),
                    FormImage.Type.PATH, FormImagePaths.forMaterial(key.icon()));
        }
        boolean hasPrevious = keyPage.hasPrevious();
        if (hasPrevious) builder.button(FormStyle.RETURN + "← Page précédente");
        boolean hasNext = keyPage.hasNext();
        if (hasNext) builder.button(FormStyle.PRIMARY + "Page suivante →");
        int previousIndex = hasPrevious ? keys.size() : -1;
        int nextIndex = hasNext ? keys.size() + (hasPrevious ? 1 : 0) : -1;
        int backIndex = keys.size() + (hasPrevious ? 1 : 0) + (hasNext ? 1 : 0);
        builder.button(FormStyle.RETURN + "← Retour");
        builder.closedOrInvalidResultHandler(forms.sync(player, parent));
        builder.validResultHandler(forms.sync(player, once(response -> {
            int index = response.clickedButtonId();
            if (index >= 0 && index < keys.size()) confirm(player, parent, keys.get(index));
            else if (index == previousIndex) openRoot(player, parent, null, keyPage.number() - 1);
            else if (index == nextIndex) openRoot(player, parent, null, keyPage.number() + 1);
            else if (index == backIndex) parent.run();
        })));
        forms.sendFormSync(player, builder.build());
    }

    private void confirm(Player player, Runnable parent, KeyOffer key) {
        ModalForm form = ModalForm.builder().title(FormStyle.title("Confirmer l'achat"))
                .content("Acheter " + key.name() + " pour " + key.gemPrice() + " gems ?\n"
                        + "Solde actuel : " + source.getGemBalance(player) + " gems.")
                .button1(FormStyle.CONFIRM).button2(FormStyle.DANGER + "Annuler")
                .closedOrInvalidResultHandler(forms.sync(player, () -> openRoot(player, parent, null)))
                .validResultHandler(forms.sync(player, once(response -> {
                    if (!response.clickedFirst()) {
                        openRoot(player, parent, null);
                        return;
                    }
                    ActionResult result = source.buy(player, key);
                    openRoot(player, parent, (result.success() ? FormStyle.SUCCESS : FormStyle.DANGER)
                            + result.message());
                }))).build();
        forms.sendFormSync(player, form);
    }

    private <T> Consumer<T> once(Consumer<T> action) {
        AtomicBoolean handled = new AtomicBoolean();
        return value -> {
            if (handled.compareAndSet(false, true)) action.accept(value);
        };
    }
}
