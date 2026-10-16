package org.elestya.bedrock.ressources;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.elestya.bedrock.api.result.ActionResult;
import org.elestya.bedrock.api.sources.BoostOffer;
import org.elestya.bedrock.api.sources.BoostSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class InMemoryBoostSource implements BoostSource {

    private final List<BoostOffer> boosts = new ArrayList<>(List.of(
            new BoostOffer("xp", "Expérience", "Double l'expérience gagnée.", "30 minutes", 2,
                    Material.EXPERIENCE_BOTTLE),
            new BoostOffer("money", "Monnaie", "Double les gains de monnaie.", "15 minutes", 1,
                    Material.GOLD_INGOT),
            new BoostOffer("empty", "Réserve épuisée", "Aucun boost disponible.", "—", 0,
                    Material.BARRIER)
    ));

    @Override
    public synchronized List<BoostOffer> getBoosts(Player player) {
        Objects.requireNonNull(player);
        return List.copyOf(boosts);
    }

    @Override
    public synchronized ActionResult activate(Player player, BoostOffer boost) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(boost);
        int index = boosts.indexOf(boost);
        if (index < 0) return ActionResult.failure("Ce boost n'est plus disponible.");
        if (boost.available() == 0) return ActionResult.failure("Ce boost n'est plus en réserve.");
        boosts.set(index, new BoostOffer(boost.id(), boost.name(), boost.description(), boost.duration(),
                boost.available() - 1, boost.icon()));
        return ActionResult.success("Boost " + boost.name() + " activé pour " + boost.duration()
                + " (données de test).");
    }
}
