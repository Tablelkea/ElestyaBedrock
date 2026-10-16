package org.elestya.bedrock.interfaces.progression;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.result.ActionResult;
import org.elestya.bedrock.api.sources.ProgressionSource;
import org.elestya.bedrock.api.sources.ProgressionStep;
import org.elestya.bedrock.interfaces.FormImagePaths;
import org.elestya.bedrock.interfaces.FormService;
import org.elestya.bedrock.interfaces.FormStyle;
import org.geysermc.cumulus.form.ModalForm;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public final class ProgressionService {

    private final FormService forms;
    private final ProgressionSource source;

    public ProgressionService(FormService forms, ProgressionSource source) {
        this.forms = Objects.requireNonNull(forms);
        this.source = Objects.requireNonNull(source);
    }

    public void openRankup(Player player) {
        openRankup(player, () -> {});
    }

    public void openRankup(Player player, Runnable parent) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(parent);
        forms.runSync(player, () -> showRank(player, parent, null));
    }

    public void openPrestige(Player player) {
        openPrestige(player, () -> {});
    }

    public void openPrestige(Player player, Runnable parent) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(parent);
        forms.runSync(player, () -> showPrestige(player, parent, null));
    }

    private void showRank(Player player, Runnable parent, String result) {
        ProgressionStep step = source.getRankProgress(player);
        boolean kitAvailable = source.canClaimRankKit(player);
        SimpleForm.Builder builder = SimpleForm.builder()
                .title(FormStyle.title("Progression · Grade"))
                .content((result == null ? "" : result + "\n\n")
                        + "Grade actuel : " + FormStyle.VALUE + step.current() + "\n"
                        + FormStyle.TEXT + step.details() + "\n"
                        + "Étape suivante : " + FormStyle.PRIMARY + step.next() + "\n"
                        + "Prix : " + FormStyle.PRICE + step.price());
        int rankIndex = 0;
        builder.button(FormStyle.SUCCESS + "Passer au grade suivant", FormImage.Type.PATH,
                FormImagePaths.forMaterial(org.bukkit.Material.NETHER_STAR));
        if (kitAvailable) {
            builder.button(FormStyle.PRIMARY + "Récupérer mon kit", FormImage.Type.PATH,
                    FormImagePaths.forMaterial(org.bukkit.Material.CHEST));
        }
        int backIndex = kitAvailable ? 2 : 1;
        builder.button(FormStyle.RETURN + "← Retour")
                .closedOrInvalidResultHandler(forms.sync(player, parent))
                .validResultHandler(forms.sync(player, once(response -> {
                    if (response.clickedButtonId() == rankIndex) {
                        confirmRank(player, parent, step);
                    } else if (kitAvailable && response.clickedButtonId() == 1) {
                        ActionResult action = source.claimRankKit(player);
                        showRank(player, parent, format(action));
                    } else if (response.clickedButtonId() == backIndex) {
                        parent.run();
                    }
                })));
        forms.sendFormSync(player, builder.build());
    }

    private void confirmRank(Player player, Runnable parent, ProgressionStep step) {
        confirm(player, "Confirmer le grade", "Passer de " + step.current() + " à " + step.next()
                        + " pour " + step.price() + " ?",
                () -> showRank(player, parent, null),
                () -> showRank(player, parent, format(source.rankUp(player))));
    }

    private void showPrestige(Player player, Runnable parent, String result) {
        ProgressionStep step = source.getPrestigeProgress(player);
        SimpleForm form = SimpleForm.builder().title(FormStyle.title("Progression · Prestige"))
                .content((result == null ? "" : result + "\n\n")
                        + "Prestige actuel : " + FormStyle.VALUE + step.current() + "\n"
                        + FormStyle.TEXT + step.details() + "\n"
                        + "Étape suivante : " + FormStyle.PRIMARY + step.next() + "\n"
                        + "Prix : " + FormStyle.PRICE + step.price())
                .button(FormStyle.SUCCESS + "Passer au prestige suivant", FormImage.Type.PATH,
                        FormImagePaths.forMaterial(org.bukkit.Material.NETHER_STAR))
                .button(FormStyle.RETURN + "← Retour")
                .closedOrInvalidResultHandler(forms.sync(player, parent))
                .validResultHandler(forms.sync(player, once(response -> {
                    if (response.clickedButtonId() == 0) {
                        confirm(player, "Confirmer le prestige",
                                "Passer de " + step.current() + " à " + step.next()
                                        + " pour " + step.price() + " ?",
                                () -> showPrestige(player, parent, null),
                                () -> showPrestige(player, parent, format(source.prestige(player))));
                    } else {
                        parent.run();
                    }
                }))).build();
        forms.sendFormSync(player, form);
    }

    private void confirm(Player player, String title, String content, Runnable cancel, Runnable action) {
        ModalForm form = ModalForm.builder().title(FormStyle.title(title)).content(content)
                .button1(FormStyle.CONFIRM).button2(FormStyle.DANGER + "Annuler")
                .closedOrInvalidResultHandler(forms.sync(player, cancel))
                .validResultHandler(forms.sync(player, once(response -> {
                    if (response.clickedFirst()) action.run();
                    else cancel.run();
                }))).build();
        forms.sendFormSync(player, form);
    }

    private String format(ActionResult result) {
        return (result.success() ? FormStyle.SUCCESS : FormStyle.DANGER) + result.message();
    }

    private <T> Consumer<T> once(Consumer<T> action) {
        AtomicBoolean handled = new AtomicBoolean();
        return value -> {
            if (handled.compareAndSet(false, true)) action.accept(value);
        };
    }
}
