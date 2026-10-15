package org.elestya.bedrock.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.elestya.bedrock.api.sources.Metier;
import org.elestya.bedrock.ressources.InMemoryMetierSource;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class MetierDebugCommand implements CommandExecutor {

    private static final UUID DEBUG_PLAYER_ID = UUID.fromString("00000000-0000-0000-0009-01fcdd0a3e38");
    private final InMemoryMetierSource metierSource;

    public MetierDebugCommand(InMemoryMetierSource metierSource) {
        this.metierSource = Objects.requireNonNull(metierSource);
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String @NotNull [] args
    ) {
        if (!(sender instanceof Player player) || !DEBUG_PLAYER_ID.equals(player.getUniqueId())) {
            sender.sendMessage("§cCette commande de debug est réservée au joueur autorisé.");
            return true;
        }

        if (args.length != 2) {
            player.sendMessage("§cUsage : /metierdebug <metier> <niveau>");
            player.sendMessage("§7Métiers : " + metierSource.getMetiers().stream()
                    .map(Metier::id)
                    .toList());
            return true;
        }

        String requestedId = args[0].toLowerCase(Locale.ROOT);
        Metier metier = metierSource.getMetiers().stream()
                .filter(candidate -> candidate.id().equalsIgnoreCase(requestedId))
                .findFirst()
                .orElse(null);
        if (metier == null) {
            player.sendMessage("§cMétier inconnu : " + args[0]);
            return true;
        }

        int level;
        try {
            level = Integer.parseInt(args[1]);
        } catch (NumberFormatException exception) {
            player.sendMessage("§cLe niveau doit être un nombre entier positif.");
            return true;
        }

        if (level < 0) {
            player.sendMessage("§cLe niveau ne peut pas être négatif.");
            return true;
        }

        metierSource.setLevel(player, metier, level);
        player.sendMessage("§aNiveau de " + metier.displayName() + " défini sur " + level + ".");
        return true;
    }
}
