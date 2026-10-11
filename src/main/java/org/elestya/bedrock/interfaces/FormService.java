package org.elestya.bedrock.interfaces;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.geysermc.cumulus.form.Form;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.Objects;
import java.util.function.Consumer;

public class FormService {

    private final JavaPlugin plugin;
    private final FloodgateApi api;

    public FormService(JavaPlugin plugin, FloodgateApi api) {
        this.plugin = Objects.requireNonNull(plugin);
        this.api = Objects.requireNonNull(api);
    }

    public boolean isBedrockPlayer(Player player) {

        Objects.requireNonNull(player);

        return api.isFloodgatePlayer(player.getUniqueId());
    }

    public boolean sendFormSync(Player player, Form form) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(form);


        if (!isBedrockPlayer(player)) return false;


        plugin.getServer().getScheduler().runTask(plugin, () -> {

            if (!player.isOnline()) return;

            api.sendForm(player.getUniqueId(), form);
        });
        return true;
    }

    public void runSync(Player player, Runnable runnable) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(runnable);

        Runnable guardAction = () -> {
            if (!player.isOnline()) return;

            runnable.run();
        };

        if (Bukkit.isPrimaryThread()) {
            guardAction.run();
            return;
        }

        Bukkit.getScheduler().runTask(plugin, guardAction);

    }

    public Runnable sync(Player player, Runnable runnable) {
        return () -> runSync(player, runnable);
    }

    public <T> Consumer<T> sync(Player player, Consumer<T> consumer) {
        Objects.requireNonNull(player);
        return value -> runSync(player, () -> consumer.accept(value));

    }
}
