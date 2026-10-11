package org.elestya.bedrock.interfaces.menu;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.sources.BoutiqueSource;
import org.elestya.bedrock.interfaces.FormService;
import org.elestya.bedrock.ressources.MemoryBoutiqueSource;

import java.util.Objects;

public class MainMenuService {

    private final FormService service;
    private final BoutiqueSource boutiqueSource;

    public MainMenuService(FormService service){
        this.service = Objects.requireNonNull(service);
        this.boutiqueSource = new MemoryBoutiqueSource();
    }

    public void openMainMenu(Player player){

        Objects.requireNonNull(player);

        service.runSync(player, () -> openMenu(player));

    }

    public void openMenu(Player player){

        Objects.requireNonNull(player);

        new MainMenuForm(service, boutiqueSource).open(player);

    }

}
