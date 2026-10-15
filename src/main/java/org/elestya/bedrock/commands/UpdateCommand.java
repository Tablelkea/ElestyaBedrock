package org.elestya.bedrock.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class UpdateCommand implements CommandExecutor {

    private static final String[] UPDATE_MESSAGES = {
            "§8§m----------------------------------------",
            "§5§lElestyaBedrock §7- §fDernières modifications",
            "§8• §7Interfaces harmonisées : palette grise/violette, confirmations vert foncé et boutons Retour gris.",
            "§8• §7Métiers : formulaires de compétences et prestige, ouverture individuelle ou groupée des coffres.",
            "§8• §7Quêtes : données de démonstration, écrans par difficulté et progression globale en pourcentage.",
            "§8• §7Récompenses journalières : affichage des 7 jours et réclamation quand elle est disponible.",
            "§8• §7Mondes : Ressource (Overworld, Nether, End), Survie directe et Combat (Exploration, Arène, Halloween).",
            "§8• §7Guildes : liste, création, demandes, invitations, membres, rangs, permissions et trésor.",
            "§8• §7Guilde : confirmations pour quitter, expulser ou céder la guilde ; résultats visibles dans les formulaires.",
            "§8• §7Intégration Core : sources Guilde remplaçables et point d'entrée public pour ouvrir le menu.",
            "§8• §7Réserve de guilde : bouton « Ouvrir la réserve » prêt à être relié par le Core, sans logique embarquée.",
            "§8• §7Boutique de test : vérification de la place dans l'inventaire avant de remettre un achat.",
            "§8§m----------------------------------------"
    };

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String @NotNull [] args
    ) {
        sender.sendMessage(UPDATE_MESSAGES);
        return true;
    }
}
