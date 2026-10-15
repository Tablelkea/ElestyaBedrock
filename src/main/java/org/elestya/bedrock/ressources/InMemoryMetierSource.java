package org.elestya.bedrock.ressources;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.elestya.bedrock.api.result.ActionResult;
import org.elestya.bedrock.api.sources.Metier;
import org.elestya.bedrock.api.sources.MetierCompetence;
import org.elestya.bedrock.api.sources.MetierSource;

import java.util.*;

public class InMemoryMetierSource implements MetierSource {

    private final Map<String, Metier> metiers = new LinkedHashMap<>();
    private final Map<UUID, Map<String, Integer>> levels = new HashMap<>();
    private final Map<UUID, Map<String, Integer>> prestiges = new HashMap<>();
    private final Map<UUID, Map<String, Set<Integer>>> openedChests = new HashMap<>();
    private final Map<UUID, Map<String, Set<String>>> enabledCompetences = new HashMap<>();

    public InMemoryMetierSource() {
        new InMemoryMetierSeeder(this).seed();
    }

    public void addMetier(Metier metier) {
        Objects.requireNonNull(metier);
        metiers.put(metier.id(), metier);
    }

    @Override
    public List<Metier> getMetiers() {
        return List.copyOf(metiers.values());
    }

    @Override
    public int getLevel(Player player, Metier metier) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(metier);

        return getLevel(player.getUniqueId(), metier);
    }

    private int getLevel(UUID playerId, Metier metier) {
        return levels.getOrDefault(playerId, Map.of()).getOrDefault(metier.id(), 0);
    }

    @Override
    public int getPrestige(Player player, Metier metier) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(metier);

        return prestiges.getOrDefault(player.getUniqueId(), Map.of()).getOrDefault(metier.id(), 0);
    }

    @Override
    public void setPrestige(Player player, Metier metier, int prestige) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(metier);
        if (prestige < 0 || prestige > MetierSource.MAX_PRESTIGE) {
            throw new IllegalArgumentException("prestige must be between 0 and " + MetierSource.MAX_PRESTIGE);
        }

        prestiges.computeIfAbsent(player.getUniqueId(), ignored -> new HashMap<>())
                .put(metier.id(), prestige);
    }

    @Override
    public boolean isCompetenceEnabled(Player player, Metier metier, MetierCompetence competence) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(metier);
        Objects.requireNonNull(competence);

        return enabledCompetences.getOrDefault(player.getUniqueId(), Map.of())
                .getOrDefault(metier.id(), Set.of())
                .contains(competence.id());
    }

    @Override
    public void setCompetenceEnabled(
            Player player,
            Metier metier,
            MetierCompetence competence,
            boolean enabled
    ) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(metier);
        Objects.requireNonNull(competence);
        if (!metier.competences().contains(competence)) {
            throw new IllegalArgumentException("competence does not belong to metier");
        }
        if (enabled && getLevel(player.getUniqueId(), metier) < competence.unlockLevel()) {
            throw new IllegalArgumentException("competence is not unlocked");
        }

        Set<String> playerCompetences = enabledCompetences
                .computeIfAbsent(player.getUniqueId(), ignored -> new HashMap<>())
                .computeIfAbsent(metier.id(), ignored -> new HashSet<>());
        if (enabled) {
            playerCompetences.add(competence.id());
        } else {
            playerCompetences.remove(competence.id());
        }
    }

    public void setLevel(Player player, Metier metier, int level) {
        Objects.requireNonNull(player);
        setLevel(player.getUniqueId(), metier, level);
    }

    public void setLevel(UUID playerId, Metier metier, int level) {
        Objects.requireNonNull(playerId);
        Objects.requireNonNull(metier);
        if (level < 0) throw new IllegalArgumentException("level cannot be negative");

        int previousLevel = getLevel(playerId, metier);
        levels.computeIfAbsent(playerId, ignored -> new HashMap<>())
                .put(metier.id(), level);
        int currentPrestige = prestiges.getOrDefault(playerId, Map.of()).getOrDefault(metier.id(), 0);
        if (previousLevel < MetierSource.MAX_LEVEL && level >= MetierSource.MAX_LEVEL
                && currentPrestige < MetierSource.MAX_PRESTIGE) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                player.sendMessage("§aTu peux maintenant passer le Prestige "
                        + (currentPrestige + 1) + " du métier " + metier.displayName()
                        + " depuis le menu des métiers.");
            }
        }
    }

    @Override
    public boolean passPrestige(Player player, Metier metier) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(metier);
        if (!isPrestigeReady(player, metier)) {
            return false;
        }

        setPrestige(player, metier, getPrestige(player, metier) + 1);
        levels.computeIfAbsent(player.getUniqueId(), ignored -> new HashMap<>()).put(metier.id(), 0);
        return true;
    }

    @Override
    public int getOpenedChestCount(Player player, Metier metier) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(metier);

        return openedChests.getOrDefault(player.getUniqueId(), Map.of())
                .getOrDefault(metier.id(), Set.of()).size();
    }

    @Override
    public int getAvailableChestCount(Player player, Metier metier) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(metier);

        return getPrestige(player, metier) * MetierSource.MAX_LEVEL
                + Math.min(getLevel(player, metier), MetierSource.MAX_LEVEL);
    }

    @Override
    public boolean isChestOpened(Player player, Metier metier, int chestNumber) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(metier);
        if (chestNumber < 1) {
            throw new IllegalArgumentException("chestNumber must be positive");
        }

        return openedChests.getOrDefault(player.getUniqueId(), Map.of())
                .getOrDefault(metier.id(), Set.of()).contains(chestNumber);
    }

    @Override
    public ActionResult openChest(Player player, Metier metier, int chestNumber) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(metier);
        if (chestNumber < 1 || chestNumber > getAvailableChestCount(player, metier)) {
            return ActionResult.failure("Ce coffre n'est pas disponible.");
        }
        if (isChestOpened(player, metier, chestNumber)) {
            return ActionResult.failure("Ce coffre a déjà été ouvert.");
        }

        openedChests.computeIfAbsent(player.getUniqueId(), ignored -> new HashMap<>())
                .computeIfAbsent(metier.id(), ignored -> new HashSet<>())
                .add(chestNumber);
        return ActionResult.success("Le coffre " + chestNumber + " du métier "
                + metier.displayName() + " a été ouvert.");
    }

    @Override
    public ActionResult openAllAvailableChests(Player player, Metier metier) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(metier);

        Set<Integer> opened = openedChests.computeIfAbsent(player.getUniqueId(), ignored -> new HashMap<>())
                .computeIfAbsent(metier.id(), ignored -> new HashSet<>());
        int newlyOpened = 0;
        int available = getAvailableChestCount(player, metier);
        for (int chestNumber = 1; chestNumber <= available; chestNumber++) {
            if (opened.add(chestNumber)) {
                newlyOpened++;
            }
        }
        if (newlyOpened == 0) {
            return ActionResult.failure("Aucun coffre disponible à ouvrir.");
        }
        return ActionResult.success(newlyOpened + " coffre(s) du métier "
                + metier.displayName() + " ouvert(s).");
    }
}
