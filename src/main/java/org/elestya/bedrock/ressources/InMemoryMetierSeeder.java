package org.elestya.bedrock.ressources;

import org.bukkit.Material;
import org.elestya.bedrock.api.sources.Metier;
import org.elestya.bedrock.api.sources.MetierCompetence;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class InMemoryMetierSeeder {

    private static final UUID DEBUG_PLAYER_ID = UUID.fromString("00000000-0000-0000-0009-01fcdd0a3e38");

    private final InMemoryMetierSource source;

    public InMemoryMetierSeeder(InMemoryMetierSource source) {
        this.source = Objects.requireNonNull(source);
    }

    public void seed() {
        Metier mineur = new Metier("mineur", "Mineur", Material.IRON_PICKAXE, List.of(
                competence("veine_de_charbon", "Veine de charbon", "Augmente les gains de charbon.", 5),
                competence("oeil_du_mineur", "Œil du mineur", "Améliore les chances de trouver du minerai.", 10),
                competence("fer_efficace", "Fer efficace", "Améliore les gains de fer.", 20),
                competence("prospection", "Prospection", "Augmente les chances de découverte.", 30),
                competence("cuivre_abondant", "Cuivre abondant", "Améliore les gains de cuivre.", 35),
                competence("coup_rapide", "Coup rapide", "Augmente la vitesse de minage.", 45),
                competence("or_brut", "Or brut", "Améliore les gains d'or.", 50),
                competence("veine_etendue", "Veine étendue", "Augmente les trouvailles par veine.", 60),
                competence("pierre_precieuse", "Pierre précieuse", "Augmente les chances de trouver des gemmes.", 70),
                competence("expert_des_profondeurs", "Expert des profondeurs", "Améliore les gains en profondeur.", 80),
                competence("fortune_souterraine", "Fortune souterraine", "Augmente la valeur des minerais trouvés.", 90),
                competence("maitre_mineur", "Maître mineur", "Débloque le meilleur bonus de minage.", 100)
        ));
        Metier bucheron = new Metier("bucheron", "Bûcheron", Material.IRON_AXE, List.of(
                competence("bois_abondant", "Bois abondant", "Augmente les gains de bois.", 5),
                competence("hache_affutee", "Hache affûtée", "Augmente la vitesse de coupe.", 10),
                competence("sens_des_arbres", "Sens des arbres", "Améliore les trouvailles en forêt.", 20),
                competence("bois_de_qualite", "Bois de qualité", "Améliore la valeur du bois récolté.", 30),
                competence("coupe_efficace", "Coupe efficace", "Réduit les efforts de coupe.", 35),
                competence("recolte_feuillage", "Récolte du feuillage", "Améliore les gains de ressources végétales.", 45),
                competence("abattage_maitrise", "Abattage maîtrisé", "Augmente les gains lors d'un abattage.", 50),
                competence("instinct_forestier", "Instinct forestier", "Augmente les chances de trouvailles rares.", 60),
                competence("bois_precieux", "Bois précieux", "Améliore les gains de bois rare.", 70),
                competence("force_du_bucheron", "Force du bûcheron", "Augmente la vitesse d'abattage.", 80),
                competence("gardien_de_la_foret", "Gardien de la forêt", "Améliore les gains de récolte forestière.", 90),
                competence("maitre_bucheron", "Maître bûcheron", "Débloque le meilleur bonus de bûcheronnage.", 100)
        ));
        Metier fermier = new Metier("fermier", "Fermier", Material.WHEAT, List.of(
                competence("recolte_genereuse", "Récolte généreuse", "Augmente les gains de récolte.", 5),
                competence("pousse_rapide", "Pousse rapide", "Améliore la croissance des cultures récoltées.", 10),
                competence("graines_abondantes", "Graines abondantes", "Augmente les gains de graines.", 20),
                competence("main_verte", "Main verte", "Améliore les trouvailles agricoles.", 30),
                competence("recolte_de_qualite", "Récolte de qualité", "Améliore la valeur des récoltes.", 35),
                competence("culture_maitrisee", "Culture maîtrisée", "Augmente les gains sur les cultures mûres.", 45),
                competence("verger_productif", "Verger productif", "Améliore les gains de fruits.", 50),
                competence("elevage_expert", "Élevage expert", "Améliore les gains liés aux animaux.", 60),
                competence("recolte_rare", "Récolte rare", "Augmente les chances de récoltes rares.", 70),
                competence("saison_faste", "Saison faste", "Augmente les gains de toutes les cultures.", 80),
                competence("abondance_agricole", "Abondance agricole", "Améliore la valeur des productions.", 90),
                competence("maitre_fermier", "Maître fermier", "Débloque le meilleur bonus agricole.", 100)
        ));
        Metier chasseur = new Metier("chasseur", "Chasseur", Material.IRON_SWORD, List.of(
                competence("pisteur", "Pisteur", "Augmente les gains sur les créatures chassées.", 5),
                competence("coup_precis", "Coup précis", "Améliore les chances de butin supplémentaire.", 10),
                competence("chasseur_experimente", "Chasseur expérimenté", "Améliore les gains de chasse.", 20),
                competence("instinct_du_chasseur", "Instinct du chasseur", "Augmente les chances de butin rare.", 30),
                competence("peau_resistante", "Peau résistante", "Améliore les gains de peaux et de fourrures.", 35),
                competence("traque_rapide", "Traque rapide", "Augmente les gains lors des traques.", 45),
                competence("chasse_nocturne", "Chasse nocturne", "Améliore les trouvailles pendant la nuit.", 50),
                competence("trophee_precieux", "Trophée précieux", "Augmente la valeur des trophées.", 60),
                competence("maitre_des_pistes", "Maître des pistes", "Augmente les chances de butin rare.", 70),
                competence("frappe_du_predateur", "Frappe du prédateur", "Améliore les gains sur les grandes créatures.", 80),
                competence("legende_de_la_chasse", "Légende de la chasse", "Augmente la valeur du butin de chasse.", 90),
                competence("maitre_chasseur", "Maître chasseur", "Débloque le meilleur bonus de chasse.", 100)
        ));
        Metier pecheur = new Metier("pecheur", "Pêcheur", Material.FISHING_ROD, List.of(
                competence("prise_facile", "Prise facile", "Augmente les chances de réussir une prise.", 5),
                competence("filet_rempli", "Filet rempli", "Améliore les gains de poissons.", 10),
                competence("oeil_marin", "Œil marin", "Augmente les chances de trouver des objets.", 20),
                competence("peche_patiente", "Pêche patiente", "Améliore les chances de prises rares.", 30),
                competence("prise_de_qualite", "Prise de qualité", "Améliore la valeur des poissons.", 35),
                competence("courant_favorable", "Courant favorable", "Augmente les gains de pêche.", 45),
                competence("tresor_flottant", "Trésor flottant", "Améliore les trouvailles au trésor.", 50),
                competence("peche_en_eaux_profondes", "Pêche en eaux profondes", "Améliore les prises en profondeur.", 60),
                competence("maitre_des_appats", "Maître des appâts", "Augmente les chances de prise rare.", 70),
                competence("instinct_marin", "Instinct marin", "Augmente la valeur des trouvailles.", 80),
                competence("tresor_des_mers", "Trésor des mers", "Améliore les chances de trésors précieux.", 90),
                competence("maitre_pecheur", "Maître pêcheur", "Débloque le meilleur bonus de pêche.", 100)
        ));
        Metier excavateur = new Metier("excavateur", "Excavateur", Material.IRON_SHOVEL, List.of(
                competence("terre_abondante", "Terre abondante", "Augmente les gains de terre.", 5),
                competence("pelle_rapide", "Pelle rapide", "Augmente la vitesse de creusage.", 10),
                competence("oeil_du_fouilleur", "Œil du fouilleur", "Améliore les trouvailles en creusant.", 20),
                competence("sable_precieux", "Sable précieux", "Améliore les gains de sable.", 30),
                competence("gravier_abondant", "Gravier abondant", "Augmente les gains de gravier.", 35),
                competence("fouille_efficace", "Fouille efficace", "Améliore les gains de fouille.", 45),
                competence("artefact_enseveli", "Artefact enseveli", "Augmente les chances de trouvailles rares.", 50),
                competence("creusage_maitrise", "Creusage maîtrisé", "Augmente la vitesse de creusage.", 60),
                competence("relique_ancienne", "Relique ancienne", "Améliore les chances de trouver des reliques.", 70),
                competence("fouille_profonde", "Fouille profonde", "Augmente la valeur des trouvailles.", 80),
                competence("tresor_enseveli", "Trésor enseveli", "Améliore les chances de trésors précieux.", 90),
                competence("maitre_excavateur", "Maître excavateur", "Débloque le meilleur bonus de fouille.", 100)
        ));

        source.addMetier(mineur);
        source.addMetier(bucheron);
        source.addMetier(fermier);
        source.addMetier(chasseur);
        source.addMetier(pecheur);
        source.addMetier(excavateur);

        source.setLevel(DEBUG_PLAYER_ID, mineur, 25);
        source.setLevel(DEBUG_PLAYER_ID, bucheron, 10);
        source.setLevel(DEBUG_PLAYER_ID, fermier, 40);
        source.setLevel(DEBUG_PLAYER_ID, chasseur, 5);
        source.setLevel(DEBUG_PLAYER_ID, pecheur, 55);
        source.setLevel(DEBUG_PLAYER_ID, excavateur, 0);
    }

    private static MetierCompetence competence(String id, String name, String description, int unlockLevel) {
        return new MetierCompetence(id, name, description, unlockLevel);
    }
}
