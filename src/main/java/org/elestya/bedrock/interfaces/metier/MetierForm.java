package org.elestya.bedrock.interfaces.metier;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.sources.Metier;
import org.elestya.bedrock.api.sources.MetierSource;
import org.elestya.bedrock.interfaces.FormService;
import org.elestya.bedrock.interfaces.FormStyle;
import org.geysermc.cumulus.form.SimpleForm;

import java.util.Objects;

public class MetierForm {

    private final FormService formService;
    private final MetierSource metierSource;

    public MetierForm(FormService formService, MetierSource metierSource) {
        this.formService = Objects.requireNonNull(formService);
        this.metierSource = Objects.requireNonNull(metierSource);
    }

    public void open(Player player, Metier metier) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(metier);

        SimpleForm form = SimpleForm.builder()
                .title(FormStyle.title(metier.displayName()))
                .content(FormStyle.TEXT + "Niveau actuel : " + FormStyle.VALUE
                        + metierSource.getLevel(player, metier))
                .button(FormStyle.PRIMARY + "Ouvrir les coffres")
                .button(FormStyle.PRIMARY + "Mes compétences")
                .button(FormStyle.PRIMARY + "Recevoir l'outil")
                .button(FormStyle.PRIMARY + "Prestige")
                .button(FormStyle.RETURN + "← Retour")
                .closedOrInvalidResultHandler(response -> {})
                .validResultHandler(formService.sync(player, response -> {
                    switch (response.clickedButtonId()) {
                        case 0 -> new MetierCoffresForm(formService, metierSource).open(player, metier);
                        case 1 -> new MetierCompetencesForm(formService, metierSource).open(player, metier);
                        case 3 -> new MetierPrestigeForm(formService, metierSource).open(player, metier);
                        case 4 -> new MetierMainForm(formService, metierSource).open(player);
                        default -> {
                        }
                    }
                }))
                .build();

        formService.sendFormSync(player, form);
    }
}
