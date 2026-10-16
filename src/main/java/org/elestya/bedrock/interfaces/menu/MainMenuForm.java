package org.elestya.bedrock.interfaces.menu;

import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.elestya.bedrock.api.sources.*;
import org.elestya.bedrock.api.sources.guild.GuildSource;
import org.elestya.bedrock.interfaces.FormService;
import org.elestya.bedrock.interfaces.FormImagePaths;
import org.elestya.bedrock.interfaces.FormStyle;
import org.elestya.bedrock.interfaces.boutique.ShopMainForm;
import org.elestya.bedrock.interfaces.boutiquegems.GemShopService;
import org.elestya.bedrock.interfaces.hdv.AuctionMainForm;
import org.elestya.bedrock.interfaces.pass.PassService;
import org.elestya.bedrock.interfaces.progression.ProgressionService;
import org.elestya.bedrock.interfaces.boost.BoostService;
import org.elestya.bedrock.interfaces.loterie.LotteryService;
import org.elestya.bedrock.interfaces.metier.MetierMainForm;
import org.elestya.bedrock.interfaces.quetes.DailyQuestForm;
import org.elestya.bedrock.interfaces.recompenses.DailyRewardForm;
import org.elestya.bedrock.interfaces.vendre.SellMainForm;
import org.elestya.bedrock.interfaces.mondes.WorldForm;
import org.elestya.bedrock.interfaces.guilde.GuildMenuService;
import org.elestya.bedrock.ressources.InMemoryGuildSource;
import org.elestya.bedrock.ressources.InMemoryAuctionSource;
import org.elestya.bedrock.ressources.InMemoryBoostSource;
import org.elestya.bedrock.ressources.InMemoryLotterySource;
import org.elestya.bedrock.ressources.InMemoryPassSource;
import org.elestya.bedrock.ressources.InMemoryProgressionSource;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;

import java.util.Objects;
import java.util.function.Consumer;

public class MainMenuForm {

    private final FormService service;
    private final ShopSource shopSource;
    private final SellSource sellSource;
    private final MetierSource metierSource;
    private final DailyQuestSource questSource;
    private final DailyRewardSource rewardSource;
    private final WorldSource worldSource;
    private final GuildSource guildSource;
    private final Consumer<Player> guildReserveOpener;
    private final GemShopSource gemSource;
    private final AuctionSource auctionSource;
    private final PassSource passSource;
    private final ProgressionSource progressionSource;
    private final BoostSource boostSource;
    private final LotterySource lotterySource;

    private static final String SHOP = "Boutique";
    private static final String SELL = "Vendre";
    private static final String AUCTION = "HDV";
    private static final String PROFILE = "Profil";
    private static final String QUESTS = "Quêtes";
    private static final String GIFTS = "Récompenses";
    private static final String RANK = "Grade";
    private static final String GUILD = "Guilde";
    private static final String LEADERBOARD = "Top";
    private static final String GEMS = "Gems";
    private static final String PASS = "Pass";
    private static final String STYLE = "Style";
    private static final String TROPHIES = "Trophées";
    private static final String PROFESSIONS = "Métiers";
    private static final String LOTTERY = "Loterie";
    private static final String WORLDS = "Mondes";
    private static final String BOOSTS = "Boosts";
    private static final String WIKI = "Wiki";

    public MainMenuForm(
            FormService service,
            ShopSource shopSource,
            SellSource sellSource,
            MetierSource metierSource,
            DailyQuestSource questSource,
            DailyRewardSource rewardSource,
            WorldSource worldSource,
            GemShopSource gemSource
    ) {
        this(service, shopSource, sellSource, metierSource, questSource, rewardSource,
                worldSource, gemSource, new InMemoryGuildSource(), MainMenuForm::reserveNotConfigured,
                new InMemoryAuctionSource(), new InMemoryPassSource(), new InMemoryProgressionSource(),
                new InMemoryBoostSource(), new InMemoryLotterySource());
    }

    public MainMenuForm(
            FormService service,
            ShopSource shopSource,
            SellSource sellSource,
            MetierSource metierSource,
            DailyQuestSource questSource,
            DailyRewardSource rewardSource,
            WorldSource worldSource,
            GemShopSource gemSource,
            GuildSource guildSource
    ) {
        this(service, shopSource, sellSource, metierSource, questSource, rewardSource,
                worldSource, gemSource, guildSource, MainMenuForm::reserveNotConfigured,
                new InMemoryAuctionSource(), new InMemoryPassSource(), new InMemoryProgressionSource(),
                new InMemoryBoostSource(), new InMemoryLotterySource());
    }

    public MainMenuForm(
            FormService service,
            ShopSource shopSource,
            SellSource sellSource,
            MetierSource metierSource,
            DailyQuestSource questSource,
            DailyRewardSource rewardSource,
            WorldSource worldSource,
            GemShopSource gemSource,
            GuildSource guildSource,
            Consumer<Player> guildReserveOpener
    ) {
        this(service, shopSource, sellSource, metierSource, questSource, rewardSource, worldSource, gemSource,
                guildSource, guildReserveOpener, new InMemoryAuctionSource(), new InMemoryPassSource(),
                new InMemoryProgressionSource(), new InMemoryBoostSource(), new InMemoryLotterySource());
    }

    public MainMenuForm(
            FormService service,
            ShopSource shopSource,
            SellSource sellSource,
            MetierSource metierSource,
            DailyQuestSource questSource,
            DailyRewardSource rewardSource,
            WorldSource worldSource,
            GemShopSource gemSource,
            GuildSource guildSource,
            Consumer<Player> guildReserveOpener,
            AuctionSource auctionSource,
            PassSource passSource,
            ProgressionSource progressionSource,
            BoostSource boostSource,
            LotterySource lotterySource
    ) {
        this.service = Objects.requireNonNull(service);
        this.shopSource = Objects.requireNonNull(shopSource);
        this.sellSource = Objects.requireNonNull(sellSource);
        this.metierSource = Objects.requireNonNull(metierSource);
        this.questSource = Objects.requireNonNull(questSource);
        this.rewardSource = Objects.requireNonNull(rewardSource);
        this.worldSource = Objects.requireNonNull(worldSource);
        this.gemSource = Objects.requireNonNull(gemSource);
        this.guildSource = Objects.requireNonNull(guildSource);
        this.guildReserveOpener = Objects.requireNonNull(guildReserveOpener);
        this.auctionSource = Objects.requireNonNull(auctionSource);
        this.passSource = Objects.requireNonNull(passSource);
        this.progressionSource = Objects.requireNonNull(progressionSource);
        this.boostSource = Objects.requireNonNull(boostSource);
        this.lotterySource = Objects.requireNonNull(lotterySource);
    }

    public void open(Player player) {

        Objects.requireNonNull(player);

        SimpleForm form = SimpleForm.builder()

                .title(FormStyle.title("Menu"))
                .button(FormStyle.PRIMARY + SHOP, FormImage.Type.PATH, FormImagePaths.forMaterial(Material.BRICKS)) // 0
                .button(FormStyle.PRIMARY + SELL, FormImage.Type.PATH, FormImagePaths.forMaterial(Material.HOPPER)) // 1
                .button(FormStyle.PRIMARY + AUCTION, FormImage.Type.PATH, FormImagePaths.forMaterial(Material.EMERALD)) // 2
                .button(FormStyle.PRIMARY + PROFILE, FormImage.Type.PATH, FormImagePaths.forMaterial(Material.PLAYER_HEAD)) // 3
                .button(FormStyle.PRIMARY + QUESTS, FormImage.Type.PATH, FormImagePaths.forMaterial(Material.WRITABLE_BOOK)) // 4
                .button(FormStyle.PRIMARY + GIFTS, FormImage.Type.PATH, FormImagePaths.forMaterial(Material.ENDER_CHEST)) // 5
                .button(FormStyle.PRIMARY + RANK, FormImage.Type.PATH, FormImagePaths.forMaterial(Material.NETHER_STAR)) // 6
                .button(FormStyle.PRIMARY + GUILD, FormImage.Type.PATH, FormImagePaths.forMaterial(Material.WHITE_BANNER)) // 7
                .button(FormStyle.PRIMARY + LEADERBOARD, FormImage.Type.PATH, FormImagePaths.forMaterial(Material.GOLDEN_HELMET)) // 8
                .button(FormStyle.PRIMARY + GEMS, FormImage.Type.PATH, FormImagePaths.forMaterial(Material.DIAMOND))// 9
                .button(FormStyle.PRIMARY + PASS, FormImage.Type.PATH, FormImagePaths.forMaterial(Material.NETHER_STAR))// 10
                .button(FormStyle.PRIMARY + STYLE, FormImage.Type.PATH, FormImagePaths.forMaterial(Material.AMETHYST_SHARD))// 11
                .button(FormStyle.PRIMARY + TROPHIES, FormImage.Type.PATH, FormImagePaths.forMaterial(Material.ITEM_FRAME))// 12
                .button(FormStyle.PRIMARY + PROFESSIONS, FormImage.Type.PATH, FormImagePaths.forMaterial(Material.IRON_PICKAXE))// 13
                .button(FormStyle.PRIMARY + LOTTERY, FormImage.Type.PATH, FormImagePaths.forMaterial(Material.GOLD_INGOT))// 14
                .button(FormStyle.PRIMARY + WORLDS, FormImage.Type.PATH, FormImagePaths.forMaterial(Material.CHERRY_SAPLING))// 15
                .button(FormStyle.PRIMARY + BOOSTS, FormImage.Type.PATH, FormImagePaths.forMaterial(Material.EXPERIENCE_BOTTLE))// 16
                .button(FormStyle.PRIMARY + WIKI, FormImage.Type.PATH, FormImagePaths.forMaterial(Material.KNOWLEDGE_BOOK))// 17

                .closedOrInvalidResultHandler(() -> {}) // ne rien faire
                .validResultHandler(service.sync(player, response -> {

                    switch (response.clickedButtonId()) {
                        case 0 -> new ShopMainForm(service, shopSource).open(player);
                        case 1 -> new SellMainForm(service, sellSource).open(player);
                        case 2 -> new AuctionMainForm(service, auctionSource).open(player, () -> openRoot(player));
                        case 4 -> new DailyQuestForm(
                                service,
                                questSource,
                                ignored -> openRoot(player)
                        ).open(player);
                        case 5 -> new DailyRewardForm(
                                service, rewardSource, ignored -> openRoot(player)
                        ).open(player);
                        case 6 -> new ProgressionService(service, progressionSource)
                                .openRankup(player, () -> openRoot(player));
                        case 7 -> new GuildMenuService(service, guildSource, guildReserveOpener)
                                .open(player, () -> openRoot(player));
                        case 9 -> new GemShopService(service, gemSource).open(player, () -> openRoot(player));
                        case 10 -> new PassService(service, passSource).open(player, () -> openRoot(player));
                        case 13 -> new MetierMainForm(service, metierSource).open(player);
                        case 14 -> new LotteryService(service, lotterySource).open(player, () -> openRoot(player));
                        case 15 -> new WorldForm(
                                service, worldSource, ignored -> openRoot(player)
                        ).open(player);
                        case 16 -> new BoostService(service, boostSource).open(player, () -> openRoot(player));

                        default -> {
                        }

                    }
                }))

                .build();

        service.sendFormSync(player, form);


    }

    private void openRoot(Player player) {
        new MainMenuForm(service, shopSource, sellSource, metierSource,
                questSource, rewardSource, worldSource, gemSource, guildSource, guildReserveOpener,
                auctionSource, passSource, progressionSource, boostSource, lotterySource).open(player);
    }

    private static void reserveNotConfigured(Player player) {
        player.sendMessage("§cL'ouverture de la réserve doit être reliée par Elestya Core.");
    }
}
