package org.elestya.bedrock.interfaces.vendre;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.sources.VendreSource;
import org.elestya.bedrock.interfaces.FormService;

import java.util.Objects;

public class VendreService {

    private final VendreSource source;
    private final FormService service;

    public VendreService(VendreSource source, FormService service){
        this.source = Objects.requireNonNull(source);
        this.service = Objects.requireNonNull(service);
    }

    public void ouvrirMenuVendre(Player player){

        Objects.requireNonNull(player, "player cannot be null");

        service.runSync(player, () -> openMenu(player));

    }

    public void openMenu(Player player){

        Objects.requireNonNull(player, "player cannot be null");

        new VendreMainForm(service, source).open(player);

    }

}
