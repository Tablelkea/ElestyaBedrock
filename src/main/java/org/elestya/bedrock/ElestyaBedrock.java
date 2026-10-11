package org.elestya.bedrock;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.elestya.bedrock.commands.BedrockFormCommand;
import org.elestya.bedrock.commands.ColorRenderCommand;
import org.elestya.bedrock.interfaces.FormService;
import org.elestya.bedrock.interfaces.menu.MainMenuService;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.Objects;

public final class ElestyaBedrock extends JavaPlugin {

    private FormService formService;
    private FloodgateApi api;

    private MainMenuService mainMenu;

    @Override
    public void onEnable() {

        this.api = FloodgateApi.getInstance();
        this.formService = new FormService(this, api);

        this.mainMenu = new MainMenuService(formService);

        Objects.requireNonNull(getCommand("bedrock"), "/bedrock is not register").setExecutor(new BedrockFormCommand(this));
        Objects.requireNonNull(getCommand("color"), "/color is not register").setExecutor(new ColorRenderCommand());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void openMainMenu(Player player){
        Objects.requireNonNull(player);

        mainMenu.openMainMenu(player);
    }
}
