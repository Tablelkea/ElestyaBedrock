package org.elestya.bedrock.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.elestya.bedrock.ElestyaBedrock;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BedrockFormCommand implements CommandExecutor {

    private final ElestyaBedrock plugin;

    public BedrockFormCommand(ElestyaBedrock plugin){
        this.plugin = Objects.requireNonNull(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if(!(sender instanceof Player player)){
            sender.sendMessage("§cCette commande doit être exécutée par un joueur.");
            return false;
        }

        plugin.openMainMenu(player);
        return true;
    }
}
