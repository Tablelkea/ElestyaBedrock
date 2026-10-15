package org.elestya.bedrock.interfaces.vendre;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.sources.SellSource;
import org.elestya.bedrock.interfaces.FormService;

import java.util.Objects;

public class SellService {

    private final SellSource source;
    private final FormService service;

    public SellService(SellSource source, FormService service){
        this.source = Objects.requireNonNull(source);
        this.service = Objects.requireNonNull(service);
    }

    public void openSellMenu(Player player){

        Objects.requireNonNull(player, "player cannot be null");

        service.runSync(player, () -> openMenu(player));

    }

    public void openMenu(Player player){

        Objects.requireNonNull(player, "player cannot be null");

        new SellMainForm(service, source).open(player);

    }

}
