package org.elestya.bedrock.interfaces.metier;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.sources.Metier;
import org.elestya.bedrock.api.sources.MetierCompetence;
import org.elestya.bedrock.api.sources.MetierSource;
import org.elestya.bedrock.interfaces.FormService;
import org.elestya.bedrock.interfaces.FormStyle;
import org.geysermc.cumulus.form.CustomForm;

import java.util.List;
import java.util.Objects;

public class MetierCompetencesForm {

    private final FormService formService;
    private final MetierSource metierSource;

    public MetierCompetencesForm(FormService formService, MetierSource metierSource) {
        this.formService = Objects.requireNonNull(formService);
        this.metierSource = Objects.requireNonNull(metierSource);
    }

    public void open(Player player, Metier metier) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(metier);

        List<MetierCompetence> competences = metier.competences();
        List<MetierCompetence> unlockedCompetences = metierSource.getUnlockedCompetences(player, metier);
        CustomForm.Builder builder = CustomForm.builder()
                .title(FormStyle.title(metier.displayName() + " · Compétences"));

        if (competences.isEmpty()) {
            builder.label(FormStyle.TEXT + "Aucune compétence disponible.");
        } else {
            for (MetierCompetence competence : competences) {
                String label = FormStyle.PRIMARY + competence.name()
                        + "\n" + FormStyle.RESET + FormStyle.TEXT + competence.description()
                        + "\n" + FormStyle.MUTED + "Débloquée au niveau " + competence.unlockLevel();
                if (unlockedCompetences.contains(competence)) {
                    builder.toggle(label, metierSource.isCompetenceEnabled(player, metier, competence));
                } else {
                    builder.label(FormStyle.MUTED + competence.name() + " · Verrouillée\n"
                            + FormStyle.TEXT + competence.description()
                            + "\n" + FormStyle.MUTED + "Débloquée au niveau " + competence.unlockLevel());
                }
            }
        }

        builder.closedOrInvalidResultHandler(formService.sync(player, response ->
                        new MetierForm(formService, metierSource).open(player, metier)))
                .validResultHandler(formService.sync(player, response -> {
                    for (int index = 0; index < unlockedCompetences.size(); index++) {
                        metierSource.setCompetenceEnabled(
                                player,
                                metier,
                                unlockedCompetences.get(index),
                                response.asToggle(index)
                        );
                    }
                    new MetierForm(formService, metierSource).open(player, metier);
                }));

        formService.sendFormSync(player, builder.build());
    }
}
