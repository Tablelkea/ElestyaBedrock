package org.elestya.bedrock.interfaces.boutiquegems;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.sources.GemShopSource;
import org.elestya.bedrock.interfaces.FormService;

import java.util.Objects;

public final class GemShopService {

    private final FormService forms;
    private final GemShopSource source;

    public GemShopService(FormService forms, GemShopSource source) {
        this.forms = Objects.requireNonNull(forms);
        this.source = Objects.requireNonNull(source);
    }

    public void open(Player player) {
        open(player, () -> {});
    }

    public void open(Player player, Runnable parent) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(parent);
        forms.runSync(player, () -> new GemShopMainForm(forms, source).open(player, parent));
    }
}
