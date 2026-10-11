package org.elestya.bedrock.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.floodgate.api.FloodgateApi;
import org.jetbrains.annotations.NotNull;

public class ColorRenderCommand implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if(!(sender instanceof Player player)){
            return false;
        }

        SimpleForm form = SimpleForm.builder()
                .title("§8§lCouleurs")

                .button("§0§lNoir §f(&0)")
                .button("§1§lBleu foncé §f(&1)")
                .button("§2§lVert foncé §f(&2)")
                .button("§3§lCyan foncé §f(&3)")
                .button("§4§lRouge foncé §f(&4)")
                .button("§5§lViolet §f(&5)")
                .button("§6§lOr §f(&6)")
                .button("§7§lGris clair §f(&7)")
                .button("§8§lGris foncé §f(&8)")
                .button("§9§lBleu §f(&9)")
                .button("§a§lVert §f(&a)")
                .button("§b§lCyan §f(&b)")
                .button("§c§lRouge §f(&c)")
                .button("§d§lRose §f(&d)")
                .button("§e§lJaune §f(&e)")
                .button("§f§lBlanc §7(&f)")

                .build();

        FloodgateApi.getInstance().sendForm(player.getUniqueId(), form);

        return false;
    }
}
