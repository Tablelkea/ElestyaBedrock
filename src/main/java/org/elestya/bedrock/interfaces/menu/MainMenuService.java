package org.elestya.bedrock.interfaces.menu;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.sources.BoutiqueSource;
import org.elestya.bedrock.api.sources.VendreSource;
import org.elestya.bedrock.interfaces.FormService;
import org.elestya.bedrock.ressources.MemoryBoutiqueSource;
import org.elestya.bedrock.ressources.MemoryVendreSource;

import java.util.Objects;

public class MainMenuService {

    private final FormService service;
    private final BoutiqueSource boutiqueSource;
    private final VendreSource vendreSource;

    public MainMenuService(FormService service){
        this.service = Objects.requireNonNull(service);
        this.boutiqueSource = new MemoryBoutiqueSource();
        this.vendreSource = new MemoryVendreSource();
    }

    public void openMainMenu(Player player){

        Objects.requireNonNull(player);

        service.runSync(player, () -> openMenu(player));

    }

    public void openMenu(Player player){

        Objects.requireNonNull(player);

        new MainMenuForm(service, boutiqueSource, vendreSource).open(player);

    }

}
