package org.elestya.bedrock.interfaces.metier;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.result.ActionResult;
import org.elestya.bedrock.api.sources.Metier;
import org.elestya.bedrock.api.sources.MetierSource;
import org.elestya.bedrock.interfaces.FormService;
import org.elestya.bedrock.interfaces.FormStyle;
import org.geysermc.cumulus.form.SimpleForm;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class MetierCoffresForm {

    private static final int VISIBLE_CHESTS = 10;

    private final FormService formService;
    private final MetierSource metierSource;

    public MetierCoffresForm(FormService formService, MetierSource metierSource) {
        this.formService = Objects.requireNonNull(formService);
        this.metierSource = Objects.requireNonNull(metierSource);
    }

    public void open(Player player, Metier metier) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(metier);

        int availableChests = metierSource.getAvailableChestCount(player, metier);
        int openedChests = metierSource.getOpenedChestCount(player, metier);
        List<Integer> unopenedChestNumbers = IntStream.rangeClosed(1, availableChests)
                .filter(chestNumber -> !metierSource.isChestOpened(player, metier, chestNumber))
                .limit(VISIBLE_CHESTS)
                .boxed()
                .toList();

        SimpleForm.Builder builder = SimpleForm.builder()
                .title(FormStyle.title(metier.displayName() + " · Coffres"))
                .content(unopenedChestNumbers.isEmpty()
                        ? FormStyle.TEXT + "Aucun coffre à ouvrir."
                        : FormStyle.TEXT + "Coffres ouverts : " + FormStyle.VALUE + openedChests
                                + FormStyle.TEXT + " / " + FormStyle.VALUE + availableChests
                                + "\n" + FormStyle.MUTED + "Choisis un coffre ou ouvre-les tous.");

        if (!unopenedChestNumbers.isEmpty()) {
            builder.button(FormStyle.PRIMARY + "Tout ouvrir");
        }
        unopenedChestNumbers.forEach(chestNumber ->
                builder.button(FormStyle.PRIMARY + "Ouvrir le coffre " + chestNumber));

        int returnButtonId = unopenedChestNumbers.size() + (unopenedChestNumbers.isEmpty() ? 0 : 1);
        builder.button(FormStyle.RETURN + "← Retour")
                .closedOrInvalidResultHandler(response -> {})
                .validResultHandler(formService.sync(player, response -> {
                    int buttonId = response.clickedButtonId();
                    if (buttonId == returnButtonId) {
                        new MetierForm(formService, metierSource).open(player, metier);
                        return;
                    }
                    if (buttonId < 0 || buttonId >= returnButtonId) {
                        return;
                    }

                    ActionResult result;
                    if (!unopenedChestNumbers.isEmpty() && buttonId == 0) {
                        result = metierSource.openAllAvailableChests(player, metier);
                    } else {
                        int chestIndex = buttonId - (unopenedChestNumbers.isEmpty() ? 0 : 1);
                        result = metierSource.openChest(player, metier, unopenedChestNumbers.get(chestIndex));
                    }
                    player.sendMessage((result.success() ? FormStyle.SUCCESS : FormStyle.DANGER) + result.message());
                    open(player, metier);
                }));

        formService.sendFormSync(player, builder.build());
    }
}
