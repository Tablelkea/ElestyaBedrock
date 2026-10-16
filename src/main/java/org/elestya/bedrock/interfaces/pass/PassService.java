package org.elestya.bedrock.interfaces.pass;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.result.ActionResult;
import org.elestya.bedrock.api.sources.PassSource;
import org.elestya.bedrock.api.sources.PassTier;
import org.elestya.bedrock.interfaces.FormImagePaths;
import org.elestya.bedrock.interfaces.FormPages;
import org.elestya.bedrock.interfaces.FormService;
import org.elestya.bedrock.interfaces.FormStyle;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public final class PassService {

    private final FormService forms;
    private final PassSource source;

    public PassService(FormService forms, PassSource source) {
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
        List<PassTier> availableTiers = source.getTiers(player).stream()
                .filter(PassTier::available)
                .filter(tier -> !tier.claimed())
                .toList();
        FormPages.Page<PassTier> tierPage = FormPages.of(availableTiers, page);
        List<PassTier> available = tierPage.values();
        SimpleForm.Builder builder = SimpleForm.builder()
                .title(FormStyle.title("Pass"))
                .content((result == null ? "" : result + "\n\n")
                        + source.getProgress(player) + "\n"
                        + (availableTiers.isEmpty() ? "Aucun palier disponible."
                        : "Paliers disponibles : " + availableTiers.size() + ".")
                        + (tierPage.count() > 1 ? "\nPage " + (tierPage.number() + 1)
                        + " / " + tierPage.count() : ""));
        builder.button(FormStyle.SUCCESS + "Tout récupérer", FormImage.Type.PATH,
                FormImagePaths.forMaterial(org.bukkit.Material.CHEST));
        int offset = 1;
        for (PassTier tier : available) {
            builder.button(FormStyle.PRIMARY + tier.name() + " · "
                            + (tier.premium() ? "Voie premium" : "Voie gratuite")
                            + "\n" + FormStyle.TEXT + tier.reward(),
                    FormImage.Type.PATH, FormImagePaths.forMaterial(tier.icon()));
        }
        boolean hasPrevious = tierPage.hasPrevious();
        if (hasPrevious) builder.button(FormStyle.RETURN + "← Page précédente");
        boolean hasNext = tierPage.hasNext();
        if (hasNext) builder.button(FormStyle.PRIMARY + "Page suivante →");
        int previousIndex = offset + available.size();
        int nextIndex = offset + available.size() + (hasPrevious ? 1 : 0);
        int backIndex = offset + available.size() + (hasPrevious ? 1 : 0) + (hasNext ? 1 : 0);
        builder.button(FormStyle.RETURN + "← Retour")
                .closedOrInvalidResultHandler(forms.sync(player, parent))
                .validResultHandler(forms.sync(player, once(response -> {
                    int index = response.clickedButtonId();
                    if (index == 0) {
                        ActionResult resultAction = source.claimAll(player);
                        openRoot(player, parent, format(resultAction), tierPage.number());
                    } else if (index >= offset && index < backIndex) {
                        if (index < offset + available.size()) {
                            ActionResult resultAction = source.claim(player, available.get(index - offset));
                            openRoot(player, parent, format(resultAction), tierPage.number());
                        } else if (index == previousIndex && hasPrevious) {
                            openRoot(player, parent, null, tierPage.number() - 1);
                        } else if (index == nextIndex && hasNext) {
                            openRoot(player, parent, null, tierPage.number() + 1);
                        }
                    } else if (index == backIndex) {
                        parent.run();
                    }
                })));
        forms.sendFormSync(player, builder.build());
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
