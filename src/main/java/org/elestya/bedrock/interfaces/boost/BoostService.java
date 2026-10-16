package org.elestya.bedrock.interfaces.boost;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.result.ActionResult;
import org.elestya.bedrock.api.sources.BoostOffer;
import org.elestya.bedrock.api.sources.BoostSource;
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

public final class BoostService {

    private final FormService forms;
    private final BoostSource source;

    public BoostService(FormService forms, BoostSource source) {
        this.forms = Objects.requireNonNull(forms);
        this.source = Objects.requireNonNull(source);
    }

    public void open(Player player) {
        open(player, () -> {});
    }

    public void open(Player player, Runnable parent) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(parent);
        forms.runSync(player, () -> show(player, parent, null));
    }

    private void show(Player player, Runnable parent, String result) {
        show(player, parent, result, 0);
    }

    private void show(Player player, Runnable parent, String result, int page) {
        FormPages.Page<BoostOffer> boostPage = FormPages.of(source.getBoosts(player), page);
        List<BoostOffer> boosts = boostPage.values();
        SimpleForm.Builder builder = SimpleForm.builder().title(FormStyle.title("Boosts"))
                .content((result == null ? "" : result + "\n\n") + "Boosts disponibles dans votre réserve :"
                        + (boostPage.count() > 1 ? "\nPage " + (boostPage.number() + 1)
                        + " / " + boostPage.count() : ""));
        for (BoostOffer boost : boosts) {
            builder.button(FormStyle.PRIMARY + boost.name() + " · x" + boost.available()
                            + "\n" + FormStyle.TEXT + boost.duration() + " · " + boost.description(),
                    FormImage.Type.PATH, FormImagePaths.forMaterial(boost.icon()));
        }
        boolean hasPrevious = boostPage.hasPrevious();
        if (hasPrevious) builder.button(FormStyle.RETURN + "← Page précédente");
        boolean hasNext = boostPage.hasNext();
        if (hasNext) builder.button(FormStyle.PRIMARY + "Page suivante →");
        int previous = hasPrevious ? boosts.size() : -1;
        int next = hasNext ? boosts.size() + (hasPrevious ? 1 : 0) : -1;
        int back = boosts.size() + (hasPrevious ? 1 : 0) + (hasNext ? 1 : 0);
        builder.button(FormStyle.RETURN + "← Retour")
                .closedOrInvalidResultHandler(forms.sync(player, parent))
                .validResultHandler(forms.sync(player, once(response -> {
                    int index = response.clickedButtonId();
                    if (index >= 0 && index < boosts.size()) {
                        confirm(player, parent, boosts.get(index), boostPage.number());
                    } else if (index == previous) {
                        show(player, parent, null, boostPage.number() - 1);
                    } else if (index == next) {
                        show(player, parent, null, boostPage.number() + 1);
                    } else if (index == back) {
                        parent.run();
                    }
                })));
        forms.sendFormSync(player, builder.build());
    }

    private void confirm(Player player, Runnable parent, BoostOffer boost, int page) {
        ModalForm form = ModalForm.builder().title(FormStyle.title("Confirmer le boost"))
                .content("Lancer " + boost.name() + " pour " + boost.duration() + " ?\n"
                        + boost.description() + "\nRéserve affichée : " + boost.available() + ".")
                .button1(FormStyle.CONFIRM).button2(FormStyle.DANGER + "Annuler")
                .closedOrInvalidResultHandler(forms.sync(player, () -> show(player, parent, null, page)))
                .validResultHandler(forms.sync(player, once(response -> {
                    if (!response.clickedFirst()) {
                        show(player, parent, null, page);
                        return;
                    }
                    ActionResult result = source.activate(player, boost);
                    show(player, parent, (result.success() ? FormStyle.SUCCESS : FormStyle.DANGER)
                            + result.message(), page);
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
