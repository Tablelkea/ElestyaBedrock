package org.elestya.bedrock.interfaces.boutique;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.sources.ShopSource;
import org.elestya.bedrock.interfaces.FormService;

import java.util.Objects;

public class ShopService {
    private final ShopSource shopSource;
    private final FormService formService;

    public ShopService(
            ShopSource shopSource,
            FormService formService
    ) {
        this.shopSource = Objects.requireNonNull(
                shopSource,
                "shopSource cannot be null"
        );

        this.formService = Objects.requireNonNull(
                formService,
                "formService cannot be null"
        );
    }

    public void openShopMenu(Player player){

        Objects.requireNonNull(player, "player cannot be null");

        formService.runSync(player, () -> openMenu(player));

    }

    public void openMenu(Player player){

        Objects.requireNonNull(player, "player cannot be null");

        new ShopMainForm(formService, shopSource).open(player);

    }
}
