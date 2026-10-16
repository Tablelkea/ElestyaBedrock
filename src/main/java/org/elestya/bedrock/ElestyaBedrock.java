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
import org.elestya.bedrock.api.sources.AuctionSource;
import org.elestya.bedrock.api.sources.BuyOfferSource;
import org.elestya.bedrock.api.sources.GemShopSource;
import org.elestya.bedrock.api.sources.KeyShopSource;
import org.elestya.bedrock.api.sources.PassSource;
import org.elestya.bedrock.api.sources.ProgressionSource;
import org.elestya.bedrock.api.sources.BoostSource;
import org.elestya.bedrock.api.sources.LotterySource;
import org.elestya.bedrock.api.sources.guild.GuildSource;
import org.elestya.bedrock.commands.BedrockFormCommand;
import org.elestya.bedrock.commands.ColorRenderCommand;
import org.elestya.bedrock.commands.MetierDebugCommand;
import org.elestya.bedrock.commands.UpdateCommand;
import org.elestya.bedrock.interfaces.FormService;
import org.elestya.bedrock.interfaces.hdv.AuctionMainForm;
import org.elestya.bedrock.interfaces.offres.BuyOfferMenuService;
import org.elestya.bedrock.interfaces.cles.KeyShopService;
import org.elestya.bedrock.interfaces.boutiquegems.GemShopService;
import org.elestya.bedrock.interfaces.pass.PassService;
import org.elestya.bedrock.interfaces.progression.ProgressionService;
import org.elestya.bedrock.interfaces.boost.BoostService;
import org.elestya.bedrock.interfaces.loterie.LotteryService;
import org.elestya.bedrock.interfaces.menu.MainMenuService;
import org.elestya.bedrock.ressources.InMemoryAuctionSource;
import org.elestya.bedrock.ressources.InMemoryBuyOfferSource;
import org.elestya.bedrock.ressources.InMemoryGemShopSource;
import org.elestya.bedrock.ressources.InMemoryKeyShopSource;
import org.elestya.bedrock.ressources.InMemoryPassSource;
import org.elestya.bedrock.ressources.InMemoryProgressionSource;
import org.elestya.bedrock.ressources.InMemoryBoostSource;
import org.elestya.bedrock.ressources.InMemoryLotterySource;
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

    private FormService formService;
    private MainMenuService mainMenu;
    private InMemoryMetierSource memoryMetierSource;
    private AuctionMainForm auctionMenu;
    private BuyOfferMenuService buyOfferMenu;
    private KeyShopService keyShopMenu;
    private GemShopService gemShopMenu;
    private PassService passMenu;
    private ProgressionService progressionMenu;
    private BoostService boostMenu;
    private LotteryService lotteryMenu;

    @Override
    public void onEnable() {

        this.formService = new FormService(this, FloodgateApi.getInstance());
        InMemoryAuctionSource auctionSource = new InMemoryAuctionSource();
        InMemoryBuyOfferSource buyOfferSource = new InMemoryBuyOfferSource();
        InMemoryKeyShopSource keyShopSource = new InMemoryKeyShopSource();
        InMemoryGemShopSource gemShopSource = new InMemoryGemShopSource();
        InMemoryPassSource passSource = new InMemoryPassSource();
        InMemoryProgressionSource progressionSource = new InMemoryProgressionSource();
        InMemoryBoostSource boostSource = new InMemoryBoostSource();
        InMemoryLotterySource lotterySource = new InMemoryLotterySource();

        this.auctionMenu = new AuctionMainForm(formService, auctionSource);
        this.buyOfferMenu = new BuyOfferMenuService(formService, buyOfferSource);
        this.keyShopMenu = new KeyShopService(formService, keyShopSource);
        this.gemShopMenu = new GemShopService(formService, gemShopSource);
        this.passMenu = new PassService(formService, passSource);
        this.progressionMenu = new ProgressionService(formService, progressionSource);
        this.boostMenu = new BoostService(formService, boostSource);
        this.lotteryMenu = new LotteryService(formService, lotterySource);

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
        mainMenu.setAuctionSource(auctionSource);
        mainMenu.setGemShopSource(gemShopSource);
        mainMenu.setPassSource(passSource);
        mainMenu.setProgressionSource(progressionSource);
        mainMenu.setBoostSource(boostSource);
        mainMenu.setLotterySource(lotterySource);

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

    public void setAuctionSource(AuctionSource source) {
        source = Objects.requireNonNull(source);
        auctionMenu = new AuctionMainForm(Objects.requireNonNull(formService), source);
        mainMenu.setAuctionSource(source);
    }

    public void setBuyOfferSource(BuyOfferSource source) {
        buyOfferMenu = new BuyOfferMenuService(Objects.requireNonNull(formService), Objects.requireNonNull(source));
    }

    public void setKeyShopSource(KeyShopSource source) {
        keyShopMenu = new KeyShopService(Objects.requireNonNull(formService), Objects.requireNonNull(source));
    }

    public void setGemShopSource(GemShopSource source) {
        gemShopMenu = new GemShopService(Objects.requireNonNull(formService), Objects.requireNonNull(source));
        mainMenu.setGemShopSource(source);
    }

    public void setPassSource(PassSource source) {
        passMenu = new PassService(Objects.requireNonNull(formService), Objects.requireNonNull(source));
        mainMenu.setPassSource(source);
    }

    public void setProgressionSource(ProgressionSource source) {
        progressionMenu = new ProgressionService(Objects.requireNonNull(formService), Objects.requireNonNull(source));
        mainMenu.setProgressionSource(source);
    }

    public void setBoostSource(BoostSource source) {
        boostMenu = new BoostService(Objects.requireNonNull(formService), Objects.requireNonNull(source));
        mainMenu.setBoostSource(source);
    }

    public void setLotterySource(LotterySource source) {
        lotteryMenu = new LotteryService(Objects.requireNonNull(formService), Objects.requireNonNull(source));
        mainMenu.setLotterySource(source);
    }

    public void openAuctionMenu(Player player) {
        Objects.requireNonNull(player);
        auctionMenu.open(player);
    }

    public void openBuyOfferMenu(Player player) {
        Objects.requireNonNull(player);
        buyOfferMenu.open(player);
    }

    public void openKeyShopMenu(Player player) {
        Objects.requireNonNull(player);
        keyShopMenu.open(player);
    }

    public void openGemShopMenu(Player player) {
        Objects.requireNonNull(player);
        gemShopMenu.open(player);
    }

    public void openPassMenu(Player player) {
        Objects.requireNonNull(player);
        passMenu.open(player);
    }

    public void openRankupMenu(Player player) {
        Objects.requireNonNull(player);
        progressionMenu.openRankup(player);
    }

    public void openPrestigeMenu(Player player) {
        Objects.requireNonNull(player);
        progressionMenu.openPrestige(player);
    }

    public void openBoostMenu(Player player) {
        Objects.requireNonNull(player);
        boostMenu.open(player);
    }

    public void openLotteryMenu(Player player) {
        Objects.requireNonNull(player);
        lotteryMenu.open(player);
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
