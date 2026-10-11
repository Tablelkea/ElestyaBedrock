package org.elestya.bedrock.interfaces.boutique;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.sources.BoutiqueSource;
import org.elestya.bedrock.interfaces.FormService;

import java.util.Objects;

public class BoutiqueService {
    private final BoutiqueSource boutiqueSource;
    private final FormService formService;

    public BoutiqueService(
            BoutiqueSource boutiqueSource,
            FormService formService
    ) {
        this.boutiqueSource = Objects.requireNonNull(
                boutiqueSource,
                "shopSource cannot be null"
        );

        this.formService = Objects.requireNonNull(
                formService,
                "formService cannot be null"
        );
    }

    public void ouvrirMenuBoutique(Player player){

        Objects.requireNonNull(player, "player cannot be null");

        formService.runSync(player, () -> openMenu(player));

    }

    public void openMenu(Player player){

        Objects.requireNonNull(player, "player cannot be null");

        new BoutiqueMainForm(formService, boutiqueSource).open(player);

    }
}
