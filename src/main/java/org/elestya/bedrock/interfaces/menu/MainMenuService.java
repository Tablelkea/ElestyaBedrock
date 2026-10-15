package org.elestya.bedrock.interfaces.menu;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.sources.Metier;
import org.elestya.bedrock.api.sources.MetierSource;
import org.elestya.bedrock.api.sources.DailyQuestSource;
import org.elestya.bedrock.api.sources.DailyRewardSource;
import org.elestya.bedrock.api.sources.WorldSource;
import org.elestya.bedrock.api.sources.ShopSource;
import org.elestya.bedrock.api.sources.SellSource;
import org.elestya.bedrock.api.sources.guild.GuildSource;
import org.elestya.bedrock.api.sources.ShopCategory;
import org.elestya.bedrock.interfaces.FormService;
import org.elestya.bedrock.interfaces.boutique.ShopMainForm;
import org.elestya.bedrock.interfaces.metier.MetierForm;
import org.elestya.bedrock.interfaces.metier.MetierMainForm;
import org.elestya.bedrock.interfaces.metier.MetierPrestigeForm;
import org.elestya.bedrock.interfaces.quetes.DailyQuestForm;
import org.elestya.bedrock.interfaces.recompenses.DailyRewardForm;
import org.elestya.bedrock.interfaces.mondes.WorldForm;
import org.elestya.bedrock.interfaces.vendre.SellCategoryForm;
import org.elestya.bedrock.interfaces.vendre.SellMainForm;
import org.elestya.bedrock.ressources.InMemoryMetierSource;
import org.elestya.bedrock.ressources.InMemoryDailyQuestSource;
import org.elestya.bedrock.ressources.InMemoryDailyRewardSource;
import org.elestya.bedrock.ressources.InMemoryWorldSource;
import org.elestya.bedrock.ressources.InMemoryShopSource;
import org.elestya.bedrock.ressources.InMemorySellSource;
import org.elestya.bedrock.ressources.InMemoryGuildSource;
import org.elestya.bedrock.interfaces.guilde.GuildMenuService;

import java.util.Objects;
import java.util.function.Consumer;

public class MainMenuService {

    private final FormService service;
    private ShopSource shopSource;
    private SellSource sellSource;
    private MetierSource metierSource;
    private DailyQuestSource questSource;
    private DailyRewardSource rewardSource;
    private WorldSource worldSource;
    private GuildSource guildSource;
    private Consumer<Player> guildReserveOpener = player ->
            player.sendMessage("§cL'ouverture de la réserve doit être reliée par Elestya Core.");

    public MainMenuService(FormService service){
        this(service, new InMemoryShopSource(), new InMemorySellSource(),
                new InMemoryMetierSource(), new InMemoryDailyQuestSource(),
                new InMemoryDailyRewardSource(), new InMemoryWorldSource(), new InMemoryGuildSource());
    }

    public MainMenuService(FormService service, MetierSource metierSource){
        this(service, new InMemoryShopSource(), new InMemorySellSource(),
                metierSource, new InMemoryDailyQuestSource(),
                new InMemoryDailyRewardSource(), new InMemoryWorldSource(), new InMemoryGuildSource());
    }

    public MainMenuService(
            FormService service,
            ShopSource shopSource,
            SellSource sellSource,
            MetierSource metierSource,
            DailyQuestSource questSource,
            DailyRewardSource rewardSource,
            WorldSource worldSource
    ){
        this(service, shopSource, sellSource, metierSource, questSource,
                rewardSource, worldSource, new InMemoryGuildSource());
    }

    public MainMenuService(
            FormService service,
            ShopSource shopSource,
            SellSource sellSource,
            MetierSource metierSource,
            DailyQuestSource questSource,
            DailyRewardSource rewardSource,
            WorldSource worldSource,
            GuildSource guildSource
    ){
        this.service = Objects.requireNonNull(service);
        this.shopSource = Objects.requireNonNull(shopSource);
        this.sellSource = Objects.requireNonNull(sellSource);
        this.metierSource = Objects.requireNonNull(metierSource);
        this.questSource = Objects.requireNonNull(questSource);
        this.rewardSource = Objects.requireNonNull(rewardSource);
        this.worldSource = Objects.requireNonNull(worldSource);
        this.guildSource = Objects.requireNonNull(guildSource);
    }

    public void setShopSource(ShopSource shopSource) {
        this.shopSource = Objects.requireNonNull(shopSource);
    }

    public void setSellSource(SellSource sellSource) {
        this.sellSource = Objects.requireNonNull(sellSource);
    }

    public void setMetierSource(MetierSource metierSource) {
        this.metierSource = Objects.requireNonNull(metierSource);
    }

    public void setQuestSource(DailyQuestSource questSource) {
        this.questSource = Objects.requireNonNull(questSource);
    }

    public void setDailyRewardSource(DailyRewardSource rewardSource) {
        this.rewardSource = Objects.requireNonNull(rewardSource);
    }

    public void setWorldSource(WorldSource worldSource) {
        this.worldSource = Objects.requireNonNull(worldSource);
    }

    public void setGuildSource(GuildSource guildSource) {
        this.guildSource = Objects.requireNonNull(guildSource);
    }

    public void setGuildReserveOpener(Consumer<Player> reserveOpener) {
        this.guildReserveOpener = Objects.requireNonNull(reserveOpener);
    }

    public void openMainMenu(Player player){

        Objects.requireNonNull(player);

        service.runSync(player, () -> openMenu(player));

    }

    public void openMenu(Player player){

        Objects.requireNonNull(player);

        new MainMenuForm(service, shopSource, sellSource, metierSource,
                questSource, rewardSource, worldSource, guildSource, guildReserveOpener).open(player);

    }

    public void openShopMenu(Player player) {
        Objects.requireNonNull(player);
        service.runSync(player, () -> new ShopMainForm(service, shopSource).open(player));
    }

    public void openSellMenu(Player player) {
        Objects.requireNonNull(player);
        service.runSync(player, () -> new SellMainForm(service, sellSource).open(player));
    }

    public void openSellMenu(Player player, ShopCategory category) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(category);
        service.runSync(player, () -> new SellCategoryForm(sellSource, service).open(player, category));
    }

    public void openMetierMenu(Player player) {
        Objects.requireNonNull(player);
        service.runSync(player, () -> new MetierMainForm(service, metierSource).open(player));
    }

    public void openMetierMenu(Player player, Metier metier) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(metier);
        service.runSync(player, () -> new MetierForm(service, metierSource).open(player, metier));
    }

    public void openMetierPrestigeMenu(Player player, Metier metier) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(metier);
        service.runSync(player, () -> new MetierPrestigeForm(service, metierSource).open(player, metier));
    }

    public void openQuestMenu(Player player) {
        Objects.requireNonNull(player);
        service.runSync(player, () -> new DailyQuestForm(
                service,
                questSource,
                ignored -> openMainMenu(player)
        ).open(player));
    }

    public void openDailyRewardMenu(Player player) {
        Objects.requireNonNull(player);
        service.runSync(player, () -> new DailyRewardForm(
                service, rewardSource, ignored -> openMainMenu(player)
        ).open(player));
    }

    public void openWorldMenu(Player player) {
        Objects.requireNonNull(player);
        service.runSync(player, () -> new WorldForm(
                service, worldSource, ignored -> openMainMenu(player)
        ).open(player));
    }

    public void openGuildMenu(Player player) {
        Objects.requireNonNull(player);
        service.runSync(player, () -> new GuildMenuService(service, guildSource, guildReserveOpener)
                .open(player, () -> openMainMenu(player)));
    }

}
