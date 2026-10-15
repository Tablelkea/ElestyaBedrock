package org.elestya.bedrock;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.elestya.bedrock.api.sources.Metier;
import org.elestya.bedrock.api.sources.DailyQuestSource;
import org.elestya.bedrock.api.sources.DailyRewardSource;
import org.elestya.bedrock.api.sources.MetierSource;
import org.elestya.bedrock.api.sources.SellSource;
import org.elestya.bedrock.api.sources.ShopCategory;
import org.elestya.bedrock.api.sources.ShopSource;
import org.elestya.bedrock.api.sources.WorldSource;
import org.elestya.bedrock.api.sources.guild.GuildSource;
import org.elestya.bedrock.commands.BedrockFormCommand;
import org.elestya.bedrock.commands.ColorRenderCommand;
import org.elestya.bedrock.commands.MetierDebugCommand;
import org.elestya.bedrock.commands.UpdateCommand;
import org.elestya.bedrock.interfaces.FormService;
import org.elestya.bedrock.interfaces.menu.MainMenuService;
import org.elestya.bedrock.ressources.InMemoryMetierSource;
import org.elestya.bedrock.ressources.InMemoryDailyQuestSource;
import org.elestya.bedrock.ressources.InMemoryDailyRewardSource;
import org.elestya.bedrock.ressources.InMemorySellSource;
import org.elestya.bedrock.ressources.InMemoryShopSource;
import org.elestya.bedrock.ressources.InMemoryWorldSource;
import org.elestya.bedrock.ressources.InMemoryGuildSource;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.Objects;
import java.util.function.Consumer;

public final class ElestyaBedrock extends JavaPlugin {

    private MainMenuService mainMenu;
    private InMemoryMetierSource memoryMetierSource;

    @Override
    public void onEnable() {

        FormService formService = new FormService(this, FloodgateApi.getInstance());

        this.memoryMetierSource = new InMemoryMetierSource();
        this.mainMenu = new MainMenuService(
                formService,
                new InMemoryShopSource(),
                new InMemorySellSource(),
                memoryMetierSource,
                new InMemoryDailyQuestSource(),
                new InMemoryDailyRewardSource(),
                new InMemoryWorldSource(),
                new InMemoryGuildSource()
        );

        Objects.requireNonNull(getCommand("bedrock"), "/bedrock is not register").setExecutor(new BedrockFormCommand(this));
        Objects.requireNonNull(getCommand("color"), "/color is not register").setExecutor(new ColorRenderCommand());
        Objects.requireNonNull(getCommand("metierdebug"), "/metierdebug is not registered")
                .setExecutor(new MetierDebugCommand(memoryMetierSource));
        Objects.requireNonNull(getCommand("update"), "/update is not registered")
                .setExecutor(new UpdateCommand());

    }

    public void openMainMenu(Player player){
        Objects.requireNonNull(player);

        mainMenu.openMainMenu(player);
    }

    public void setShopSource(ShopSource shopSource) {
        mainMenu.setShopSource(shopSource);
    }

    public void setSellSource(SellSource sellSource) {
        mainMenu.setSellSource(sellSource);
    }

    public void setMetierSource(MetierSource metierSource) {
        mainMenu.setMetierSource(metierSource);
    }

    public void setQuestSource(DailyQuestSource questSource) {
        mainMenu.setQuestSource(questSource);
    }

    public void setDailyRewardSource(DailyRewardSource rewardSource) {
        mainMenu.setDailyRewardSource(rewardSource);
    }

    public void setWorldSource(WorldSource worldSource) {
        mainMenu.setWorldSource(worldSource);
    }

    public void setGuildSource(GuildSource guildSource) {
        mainMenu.setGuildSource(guildSource);
    }

    public void setGuildReserveOpener(Consumer<Player> reserveOpener) {
        mainMenu.setGuildReserveOpener(reserveOpener);
    }

    public void openGuildMenu(Player player) {
        Objects.requireNonNull(player);
        mainMenu.openGuildMenu(player);
    }

    public void openShopMenu(Player player) {
        Objects.requireNonNull(player);
        mainMenu.openShopMenu(player);
    }

    public void openSellMenu(Player player) {
        Objects.requireNonNull(player);
        mainMenu.openSellMenu(player);
    }

    public void openSellMenu(Player player, ShopCategory category) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(category);
        mainMenu.openSellMenu(player, category);
    }

    public void openMetierMenu(Player player) {
        Objects.requireNonNull(player);
        mainMenu.openMetierMenu(player);
    }

    public void openMetierMenu(Player player, Metier metier) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(metier);
        mainMenu.openMetierMenu(player, metier);
    }

    public void openMetierPrestigeMenu(Player player, Metier metier) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(metier);
        mainMenu.openMetierPrestigeMenu(player, metier);
    }

    public void openQuestMenu(Player player) {
        Objects.requireNonNull(player);
        mainMenu.openQuestMenu(player);
    }

    public void openDailyRewardMenu(Player player) {
        Objects.requireNonNull(player);
        mainMenu.openDailyRewardMenu(player);
    }

    public void openWorldMenu(Player player) {
        Objects.requireNonNull(player);
        mainMenu.openWorldMenu(player);
    }
}
