package org.elestya.bedrock.interfaces.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.elestya.bedrock.api.sources.BoutiqueSource;
import org.elestya.bedrock.api.sources.VendreSource;
import org.elestya.bedrock.interfaces.FormService;
import org.elestya.bedrock.interfaces.boutique.BoutiqueMainForm;
import org.elestya.bedrock.interfaces.vendre.VendreMainForm;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;

import java.util.Objects;

public class MainMenuForm {

    private final FormService service;
    private final BoutiqueSource boutiqueSource;
    private final VendreSource vendreSource;

    private final String BOUTIQUE = "Boutique";
    private final String VENDRE = "Vendre";
    private final String HDV = "HDV";
    private final String PROFIL = "Profil";
    private final String QUETES = "Quêtes";
    private final String CADEAUX = "Cadeaux";
    private final String GRADE = "Grade";
    private final String GUILDE = "Guilde";
    private final String TOP = "Top";
    private final String GEMS = "GEMS";
    private final String PASS = "Pass";
    private final String STYLE = "Style";
    private final String TROPHEES = "Trophées";
    private final String METIERS = "Metiers";
    private final String LOTERIE = "Loterie";
    private final String MONDES = "Mondes";
    private final String BOOSTS = "Boosts";
    private final String WIKI = "Wiki";

    public MainMenuForm(FormService service, BoutiqueSource boutiqueSource, VendreSource vendreSource){
        this.service = Objects.requireNonNull(service);
        this.boutiqueSource = Objects.requireNonNull(boutiqueSource);
        this.vendreSource = Objects.requireNonNull(vendreSource);
    }

    public void open(Player player) {

        Objects.requireNonNull(player);

        SimpleForm form = SimpleForm.builder()

                .title("§8§lMenu")
                .button(BOUTIQUE, FormImage.Type.URL, getURL(Material.BRICKS))
                .button(VENDRE, FormImage.Type.URL, getURL(Material.HOPPER))
                .button(HDV, FormImage.Type.URL, getURL(Material.EMERALD))
                .button(PROFIL, FormImage.Type.URL, getURL(Material.PLAYER_HEAD))
                .button(QUETES, FormImage.Type.URL, getURL(Material.WRITABLE_BOOK))
                .button(CADEAUX, FormImage.Type.URL, getURL(Material.ENDER_CHEST))
                .button(GRADE, FormImage.Type.URL, getURL(Material.NETHER_STAR))
                .button(GUILDE, FormImage.Type.URL, getURL(Material.WHITE_BANNER))
                .button(TOP, FormImage.Type.URL, getURL(Material.GOLDEN_HELMET))
                .button(GEMS, FormImage.Type.URL, getURL(Material.DIAMOND))
                .button(PASS, FormImage.Type.URL, getURL(Material.NETHER_STAR))
                .button(STYLE, FormImage.Type.URL, getURL(Material.AMETHYST_SHARD))
                .button(TROPHEES, FormImage.Type.URL, getURL(Material.ITEM_FRAME))
                .button(METIERS, FormImage.Type.URL, getURL(Material.IRON_PICKAXE))
                .button(LOTERIE, FormImage.Type.URL, getURL(Material.GOLD_INGOT))
                .button(MONDES, FormImage.Type.URL, getURL(Material.CHERRY_SAPLING))
                .button(BOOSTS, FormImage.Type.URL, getURL(Material.EXPERIENCE_BOTTLE))
                .button(WIKI, FormImage.Type.URL, getURL(Material.KNOWLEDGE_BOOK))

                .closedOrInvalidResultHandler(() -> {}) // ne rien faire
                .validResultHandler(service.sync(player, response -> {

                    String title = response.clickedButton().text();

                    switch (title){

                        case BOUTIQUE -> new BoutiqueMainForm(service, boutiqueSource).open(player);
                        case VENDRE -> new VendreMainForm(service, vendreSource).open(player);

                        default -> {
                        }

                    }
                }))

                .build();

        service.sendFormSync(player, form);


    }

    private String getURL(Material material){
        return "https://blockrender.dev/render/item/"+material.name()+".png?size=128";
    }


}
