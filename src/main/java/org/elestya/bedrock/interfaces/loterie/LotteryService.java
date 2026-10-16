package org.elestya.bedrock.interfaces.loterie;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.result.ActionResult;
import org.elestya.bedrock.api.sources.LotterySource;
import org.elestya.bedrock.api.sources.LotteryStatus;
import org.elestya.bedrock.interfaces.FormImagePaths;
import org.elestya.bedrock.interfaces.FormService;
import org.elestya.bedrock.interfaces.FormStyle;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public final class LotteryService {

    private final FormService forms;
    private final LotterySource source;

    public LotteryService(FormService forms, LotterySource source) {
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
        LotteryStatus status = source.getStatus(player);
        SimpleForm form = SimpleForm.builder().title(FormStyle.title("Loterie"))
                .content((result == null ? "" : result + "\n\n")
                        + "Cagnotte : " + FormStyle.PRICE + status.prizePool() + "\n"
                        + FormStyle.TEXT + "Inscrits : " + status.participants() + "\n"
                        + FormStyle.TEXT + "Tirage : " + status.drawTime() + "\n"
                        + "Votre inscription : " + (status.participating()
                        ? FormStyle.SUCCESS + "oui" : FormStyle.DANGER + "non"))
                .button((status.participating() ? FormStyle.DANGER + "Se désinscrire"
                                : FormStyle.SUCCESS + "S'inscrire"),
                        FormImage.Type.PATH, FormImagePaths.forMaterial(org.bukkit.Material.GOLD_INGOT))
                .button(FormStyle.RETURN + "← Retour")
                .closedOrInvalidResultHandler(forms.sync(player, parent))
                .validResultHandler(forms.sync(player, once(response -> {
                    if (response.clickedButtonId() == 0) {
                        ActionResult action = source.toggleParticipation(player);
                        show(player, parent, (action.success() ? FormStyle.SUCCESS : FormStyle.DANGER)
                                + action.message());
                    } else {
                        parent.run();
                    }
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
