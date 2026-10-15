package org.elestya.bedrock.interfaces.metier;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.sources.Metier;
import org.elestya.bedrock.api.sources.MetierSource;
import org.elestya.bedrock.interfaces.FormImagePaths;
import org.elestya.bedrock.interfaces.FormService;
import org.elestya.bedrock.interfaces.FormStyle;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;

import java.util.List;
import java.util.Objects;

public class MetierMainForm {

    private final FormService formService;
    private final MetierSource metierSource;

    public MetierMainForm(FormService formService, MetierSource metierSource) {
        this.formService = Objects.requireNonNull(formService);
        this.metierSource = Objects.requireNonNull(metierSource);
    }

    public void open(Player player) {
        Objects.requireNonNull(player);

        List<Metier> metiers = metierSource.getMetiers();
        SimpleForm.Builder builder = SimpleForm.builder()
                .title(FormStyle.title("Métiers"));

        for (Metier metier : metiers) {
            builder.button(
                    FormStyle.PRIMARY + metier.displayName() + "\n" + FormStyle.RESET
                            + FormStyle.MUTED + "Niveau " + FormStyle.VALUE + metierSource.getLevel(player, metier),
                    FormImage.Type.PATH,
                    FormImagePaths.forMaterial(metier.icon())
            );
        }

        builder.closedOrInvalidResultHandler(response -> {})
                .validResultHandler(formService.sync(player, response -> {
                    int buttonId = response.clickedButtonId();
                    if (buttonId < 0 || buttonId >= metiers.size()) {
                        return;
                    }

                    new MetierForm(formService, metierSource).open(player, metiers.get(buttonId));
                }));

        formService.sendFormSync(player, builder.build());
    }
}
