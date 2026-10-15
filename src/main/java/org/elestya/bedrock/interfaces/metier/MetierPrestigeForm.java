package org.elestya.bedrock.interfaces.metier;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.sources.Metier;
import org.elestya.bedrock.api.sources.MetierSource;
import org.elestya.bedrock.interfaces.FormService;
import org.elestya.bedrock.interfaces.FormStyle;
import org.geysermc.cumulus.form.SimpleForm;

import java.util.Objects;

public class MetierPrestigeForm {

    private final FormService formService;
    private final MetierSource jobSource;

    public MetierPrestigeForm(FormService formService, MetierSource jobSource) {
        this.formService = Objects.requireNonNull(formService);
        this.jobSource = Objects.requireNonNull(jobSource);
    }

    public void open(Player player, Metier metier) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(metier);

        int unlockedPrestige = jobSource.getPrestige(player, metier);
        boolean prestigeReady = jobSource.isPrestigeReady(player, metier);
        StringBuilder content = new StringBuilder();
        for (int prestige = 1; prestige <= MetierSource.MAX_PRESTIGE; prestige++) {
            if (prestige > 1) {
                content.append('\n');
            }
            content.append(prestige <= unlockedPrestige
                            ? FormStyle.PRIMARY + "✔ Prestige "
                            : FormStyle.MUTED + "· Prestige ")
                    .append(prestige);
        }
        content.append("\n\n");
        content.append(prestigeReady
                ? FormStyle.PRIMARY + "Prestige " + (unlockedPrestige + 1) + " disponible."
                : FormStyle.TEXT + "Atteins le niveau " + FormStyle.VALUE + MetierSource.MAX_LEVEL
                        + FormStyle.TEXT + " pour débloquer le prochain prestige.");

        SimpleForm.Builder builder = SimpleForm.builder()
                .title(FormStyle.title(metier.displayName() + " · Prestiges"))
                .content(content.toString());

        if (prestigeReady) {
            builder.button(FormStyle.PRIMARY + "Passer au Prestige " + (unlockedPrestige + 1));
        }
        builder.button(FormStyle.RETURN + "← Retour")
                .closedOrInvalidResultHandler(response -> {})
                .validResultHandler(formService.sync(player, response -> {
                    if (prestigeReady && response.clickedButtonId() == 0) {
                        if (jobSource.passPrestige(player, metier)) {
                            player.sendMessage(FormStyle.SUCCESS + "Prestige " + (unlockedPrestige + 1)
                                    + " du métier " + metier.displayName() + " débloqué !");
                        }
                        open(player, metier);
                        return;
                    }
                    new MetierForm(formService, jobSource).open(player, metier);
                }));

        formService.sendFormSync(player, builder.build());
    }

}
