package org.elestya.bedrock.interfaces.mondes;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.result.ActionResult;
import org.elestya.bedrock.api.sources.BedrockWorld;
import org.elestya.bedrock.api.sources.WorldCategory;
import org.elestya.bedrock.api.sources.WorldSource;
import org.elestya.bedrock.interfaces.FormImagePaths;
import org.elestya.bedrock.interfaces.FormService;
import org.elestya.bedrock.interfaces.FormStyle;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

public class WorldForm {

    private final FormService formService;
    private final WorldSource worldSource;
    private final Consumer<Player> parentOpener;

    public WorldForm(FormService formService, WorldSource worldSource, Consumer<Player> parentOpener) {
        this.formService = Objects.requireNonNull(formService);
        this.worldSource = Objects.requireNonNull(worldSource);
        this.parentOpener = Objects.requireNonNull(parentOpener);
    }

    public void open(Player player) {
        openRoot(player, null);
    }

    private void openRoot(Player player, String resultMessage) {
        Objects.requireNonNull(player);
        List<BedrockWorld> worlds = List.copyOf(Objects.requireNonNull(
                worldSource.getWorlds(),
                "world list cannot be null"
        ));
        String content = resultMessage == null
                ? FormStyle.TEXT + "Choisis une destination."
                : resultMessage + "\n\n" + FormStyle.TEXT + "Choisis une destination.";

        SimpleForm.Builder builder = SimpleForm.builder()
                .title(FormStyle.title("Mondes"))
                .content(content)
                .button(FormStyle.PRIMARY + "Ressource\n"
                        + FormStyle.RESET + FormStyle.MUTED + "Overworld · Nether · End")
                .button(FormStyle.PRIMARY + "Monde survie\n"
                        + FormStyle.RESET + FormStyle.MUTED + "Téléportation directe")
                .button(FormStyle.PRIMARY + "Combat\n"
                        + FormStyle.RESET + FormStyle.MUTED + "Exploration · Arène · Halloween")
                .button(FormStyle.RETURN + "← Retour au menu");

        SimpleForm form = builder
                .closedOrInvalidResultHandler(formService.sync(player, () -> parentOpener.accept(player)))
                .validResultHandler(formService.sync(player, response -> {
                    switch (response.clickedButtonId()) {
                        case 0 -> openCategory(player, WorldCategory.RESOURCES, null);
                        case 1 -> teleportToSurvival(player, worlds);
                        case 2 -> openCategory(player, WorldCategory.COMBAT, null);
                        case 3 -> parentOpener.accept(player);
                        default -> {
                        }
                    }
                }))
                .build();

        formService.sendFormSync(player, form);
    }

    private void teleportToSurvival(Player player, List<BedrockWorld> worlds) {
        List<BedrockWorld> survivalWorlds = worlds.stream()
                .filter(world -> world.category() == WorldCategory.SURVIVAL)
                .toList();
        if (survivalWorlds.size() != 1) {
            openRoot(player, FormStyle.DANGER + (survivalWorlds.isEmpty()
                    ? "Le monde survie n'est pas disponible."
                    : "La destination du monde survie est mal configurée."));
            return;
        }

        ActionResult result = worldSource.teleport(player, survivalWorlds.getFirst());
        openRoot(player, (result.success() ? FormStyle.SUCCESS : FormStyle.DANGER) + result.message());
    }

    private void openCategory(Player player, WorldCategory category, String resultMessage) {
        List<BedrockWorld> worlds = List.copyOf(Objects.requireNonNull(
                worldSource.getWorlds(),
                "world list cannot be null"
        ));
        List<BedrockWorld> categoryWorlds = worlds.stream()
                .filter(world -> world.category() == category)
                .toList();
        String content = (resultMessage == null ? "" : resultMessage + "\n\n")
                + FormStyle.TEXT + "Choisis un monde de "
                + category.displayName().toLowerCase(Locale.ROOT) + ".";
        SimpleForm.Builder builder = SimpleForm.builder()
                .title(FormStyle.title(category.displayName()));
        if (categoryWorlds.isEmpty()) {
            builder.content((resultMessage == null ? "" : resultMessage + "\n\n")
                    + FormStyle.MUTED + "Aucun monde disponible dans cette catégorie.");
        } else {
            builder.content(content);
        }
        for (BedrockWorld world : categoryWorlds) {
            builder.button(FormStyle.PRIMARY + world.name() + "\n"
                            + FormStyle.RESET + FormStyle.MUTED + world.description(),
                    FormImage.Type.PATH, FormImagePaths.forMaterial(world.icon()));
        }
        int backButtonId = categoryWorlds.size();
        SimpleForm form = builder.button(FormStyle.RETURN + "← Retour aux mondes")
                .closedOrInvalidResultHandler(formService.sync(player, () -> openRoot(player, null)))
                .validResultHandler(formService.sync(player, response -> {
                    int buttonId = response.clickedButtonId();
                    if (buttonId == backButtonId) {
                        openRoot(player, null);
                        return;
                    }
                    if (buttonId < 0 || buttonId >= categoryWorlds.size()) {
                        return;
                    }

                    BedrockWorld selectedWorld = categoryWorlds.get(buttonId);
                    ActionResult result = worldSource.teleport(player, selectedWorld);
                    openCategory(player, category,
                            (result.success() ? FormStyle.SUCCESS : FormStyle.DANGER) + result.message());
                }))
                .build();

        formService.sendFormSync(player, form);
    }
}
