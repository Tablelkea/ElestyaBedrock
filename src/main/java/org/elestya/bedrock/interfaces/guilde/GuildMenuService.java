package org.elestya.bedrock.interfaces.guilde;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.elestya.bedrock.api.result.ActionResult;
import org.elestya.bedrock.api.sources.guild.Guild;
import org.elestya.bedrock.api.sources.guild.GuildJoinRequest;
import org.elestya.bedrock.api.sources.guild.GuildMember;
import org.elestya.bedrock.api.sources.guild.GuildPermission;
import org.elestya.bedrock.api.sources.guild.GuildRank;
import org.elestya.bedrock.api.sources.guild.GuildSource;
import org.elestya.bedrock.interfaces.FormService;
import org.elestya.bedrock.interfaces.FormStyle;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.form.ModalForm;
import org.geysermc.cumulus.form.SimpleForm;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public final class GuildMenuService {
    private static final String BACK = "← Retour";
    private static final int GUILDS_PER_PAGE = 24;
    private static final GuildPermission[] EDITABLE_PERMISSIONS = {
            GuildPermission.BUILD,
            GuildPermission.OPEN_CONTAINERS,
            GuildPermission.RECRUIT,
            GuildPermission.CLAIM_CHUNKS,
            GuildPermission.KICK,
            GuildPermission.MANAGE_WARP,
            GuildPermission.BANK_AND_UPGRADES,
            GuildPermission.RESERVE
    };

    private final FormService forms;
    private final GuildSource source;
    private final Consumer<Player> reserveOpener;

    public GuildMenuService(FormService forms, GuildSource source) {
        this(forms, source, player ->
                player.sendMessage("§cL'ouverture de la réserve doit être reliée par Elestya Core."));
    }

    public GuildMenuService(FormService forms, GuildSource source, Consumer<Player> reserveOpener) {
        this.forms = Objects.requireNonNull(forms);
        this.source = Objects.requireNonNull(source);
        this.reserveOpener = Objects.requireNonNull(reserveOpener);
    }

    public void open(Player player, Runnable parent) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(parent);
        forms.runSync(player, () -> {
            if (!forms.isBedrockPlayer(player)) return;
            openRoot(player, parent, null);
        });
    }

    private void openRoot(Player player, Runnable parent, String result) {
        openRoot(player, parent, result, 0);
    }

    private void openRoot(Player player, Runnable parent, String result, int requestedPage) {
        Guild guild = source.findGuildByPlayer(player.getUniqueId()).orElse(null);
        if (guild != null) {
            openGuildHome(player, parent, result);
            return;
        }

        List<Guild> guilds = source.getGuilds().stream()
                .sorted(Comparator.comparing(Guild::name, String.CASE_INSENSITIVE_ORDER)).toList();
        int pageCount = Math.max(1, (guilds.size() + GUILDS_PER_PAGE - 1) / GUILDS_PER_PAGE);
        int page = Math.max(0, Math.min(requestedPage, pageCount - 1));
        int from = page * GUILDS_PER_PAGE;
        List<Guild> visibleGuilds = guilds.subList(from, Math.min(from + GUILDS_PER_PAGE, guilds.size()));
        List<Guild> pending = source.getPendingJoinRequestGuilds(player.getUniqueId());
        List<Guild> invites = source.getReceivedInvitations(player.getUniqueId());
        SimpleForm.Builder builder = SimpleForm.builder()
                .title(FormStyle.title("Guildes"))
                .content((result == null ? "" : FormStyle.VALUE + result + "\n\n")
                        + FormStyle.TEXT + "Page " + (page + 1) + " / " + pageCount
                        + "\nChoisissez une guilde, consultez vos demandes ou créez la vôtre.");
        visibleGuilds.forEach(g -> builder.button(FormStyle.PRIMARY + g.name()
                + "\n" + FormStyle.TEXT + g.members().size() + " membre(s) · " + g.points() + " points"));

        int previousIndex = -1;
        if (page > 0) {
            previousIndex = visibleGuilds.size();
            builder.button(FormStyle.SECONDARY + "← Page précédente");
        }
        int nextIndex = -1;
        if (page < pageCount - 1) {
            nextIndex = visibleGuilds.size() + (page > 0 ? 1 : 0);
            builder.button(FormStyle.SECONDARY + "Page suivante →");
        }
        int actionsStart = visibleGuilds.size() + (page > 0 ? 1 : 0) + (page < pageCount - 1 ? 1 : 0);
        int rankingIndex = actionsStart;
        builder.button(FormStyle.SECONDARY + "Classement");
        int invitationIndex = -1;
        if (!invites.isEmpty()) {
            invitationIndex = rankingIndex + 1;
            builder.button(FormStyle.PRIMARY + "Invitations reçues\n" + invites.size() + " en attente");
        }
        int requestsIndex = -1;
        if (!pending.isEmpty()) {
            requestsIndex = builderButtonCount(actionsStart, invites.isEmpty());
            builder.button(FormStyle.PRIMARY + "Demandes envoyées\n" + pending.size() + " en attente");
        }
        int createIndex = builderButtonCount(actionsStart, invites.isEmpty()) + (pending.isEmpty() ? 0 : 1);
        builder.button(FormStyle.SUCCESS + "Créer une guilde");
        int backIndex = createIndex + 1;
        builder.button(FormStyle.RETURN + BACK);
        setClose(builder, player, parent);
        int finalPreviousIndex = previousIndex;
        int finalNextIndex = nextIndex;
        int finalInvitationIndex = invitationIndex;
        int finalRequestsIndex = requestsIndex;
        builder.validResultHandler(forms.sync(player, once(response -> {
            int index = response.clickedButtonId();
            if (index < visibleGuilds.size()) {
                openGuildDetails(player, parent, visibleGuilds.get(index).id(), null, page);
            } else if (index == finalPreviousIndex) {
                openRoot(player, parent, null, page - 1);
            } else if (index == finalNextIndex) {
                openRoot(player, parent, null, page + 1);
            } else if (index == rankingIndex) {
                openRanking(player, parent, page);
            } else if (index == finalInvitationIndex) {
                openInvitations(player, parent, null);
            } else if (index == finalRequestsIndex) {
                openSentRequests(player, parent, null);
            } else if (index == createIndex) {
                openCreateGuild(player, parent, "", "", null);
            } else if (index == backIndex) {
                parent.run();
            }
        })));
        forms.sendFormSync(player, builder.build());
    }

    private int builderButtonCount(int guildCount, boolean noInvitations) {
        return guildCount + 1 + (noInvitations ? 0 : 1);
    }

    private void openGuildDetails(Player player, Runnable parent, String id, String result, int page) {
        Guild guild = source.findGuildById(id).orElse(null);
        if (guild == null) {
            openRoot(player, parent, "Cette guilde n'existe plus.", page);
            return;
        }
        SimpleForm form = SimpleForm.builder()
                .title(FormStyle.title(guild.name()))
                .content((result == null ? "" : result + "\n\n")
                        + "Membres : " + guild.members().size()
                        + "\nPoints : " + guild.points()
                        + "\nTrésor : " + money(guild.balance()))
                .button(FormStyle.CONFIRM)
                .button(FormStyle.RETURN + BACK)
                .closedOrInvalidResultHandler(forms.sync(player, () -> openRoot(player, parent, null, page)))
                .validResultHandler(forms.sync(player, once(response -> {
                    if (response.clickedButtonId() == 0) {
                        ActionResult action = source.requestToJoin(player.getUniqueId(), player.getName(), id);
                        openGuildDetails(player, parent, id, action.message(), page);
                    } else {
                        openRoot(player, parent, null, page);
                    }
                }))).build();
        forms.sendFormSync(player, form);
    }

    private void openCreateGuild(Player player, Runnable parent, String oldName, String oldMessage, String error) {
        CustomForm.Builder builder = CustomForm.builder().title(FormStyle.title("Créer une guilde"));
        if (error != null) builder.label(FormStyle.DANGER + error);
        builder.label(FormStyle.TEXT + "Nom : 3 à 16 caractères (lettres, chiffres, - ou _).");
        builder.input("Nom de la guilde", "Exemple : Aurora", oldName);
        builder.input("Message d'entrée", "Message affiché aux nouveaux membres", oldMessage);
        builder.closedOrInvalidResultHandler(forms.sync(player, () -> openRoot(player, parent, null)));
        builder.validResultHandler(forms.sync(player, once(response -> {
            int base = error == null ? 1 : 2;
            String name = clean(response.asInput(base));
            String welcome = clean(response.asInput(base + 1));
            ActionResult action = source.createGuild(player.getUniqueId(), player.getName(), name, welcome);
            if (!action.success()) {
                openCreateGuild(player, parent, name, welcome, action.message());
            } else {
                openGuildHome(player, parent, action.message());
            }
        })));
        forms.sendFormSync(player, builder.build());
    }

    private void openGuildHome(Player player, Runnable parent, String result) {
        Guild guild = source.findGuildByPlayer(player.getUniqueId()).orElse(null);
        if (guild == null) {
            openRoot(player, parent, result);
            return;
        }
        GuildMember member = guild.findMember(player.getUniqueId()).orElse(null);
        if (member == null) {
            openRoot(player, parent, result);
            return;
        }
        SimpleForm.Builder builder = SimpleForm.builder()
                .title(FormStyle.title("Guilde · " + guild.name()))
                .content((result == null ? "" : FormStyle.VALUE + result + "\n\n")
                        + FormStyle.TEXT + "Rang : " + rankName(member.rank())
                        + "\nMembres : " + guild.members().size()
                        + "\nTrésor : " + money(guild.balance())
                        + "\nPoints : " + guild.points());
        builder.button(FormStyle.PRIMARY + "Membres");
        builder.button(FormStyle.PRIMARY + "Trésor et banque");
        builder.button(FormStyle.PRIMARY + "Ouvrir la réserve");
        builder.button(FormStyle.PRIMARY + "Recrutement");
        if (member.rank() == GuildRank.OWNER) builder.button(FormStyle.PRIMARY + "Permissions");
        builder.button(FormStyle.DANGER + "Quitter la guilde");
        builder.button(FormStyle.RETURN + BACK);
        setClose(builder, player, parent);
        builder.validResultHandler(forms.sync(player, once(response -> {
            int index = response.clickedButtonId();
            if (index == 0) openMembers(player, parent, null);
            else if (index == 1) openTreasury(player, parent, null);
            else if (index == 2) reserveOpener.accept(player);
            else if (index == 3) openRecruitment(player, parent, null);
            else if (member.rank() == GuildRank.OWNER && index == 4) openPermissionRanks(player, parent);
            else if (index == (member.rank() == GuildRank.OWNER ? 5 : 4)) confirmLeave(player, parent);
            else if (index == (member.rank() == GuildRank.OWNER ? 6 : 5)) parent.run();
        })));
        forms.sendFormSync(player, builder.build());
    }

    private void openMembers(Player player, Runnable parent, String result) {
        Guild guild = source.findGuildByPlayer(player.getUniqueId()).orElse(null);
        if (guild == null) { openRoot(player, parent, result); return; }
        List<GuildMember> members = guild.members();
        SimpleForm.Builder builder = SimpleForm.builder().title(FormStyle.title("Membres · " + guild.name()))
                .content(result == null ? "Sélectionnez un membre pour consulter ses actions." : result);
        for (GuildMember member : members) builder.button(FormStyle.PRIMARY + member.name()
                + "\n" + FormStyle.TEXT + rankName(member.rank()));
        builder.button(FormStyle.RETURN + BACK);
        setClose(builder, player, () -> openGuildHome(player, parent, null));
        builder.validResultHandler(forms.sync(player, once(response -> {
            int index = response.clickedButtonId();
            if (index < members.size()) openMember(player, parent, members.get(index).playerId(), null);
            else openGuildHome(player, parent, null);
        })));
        forms.sendFormSync(player, builder.build());
    }

    private void openMember(Player player, Runnable parent, UUID targetId, String result) {
        Guild guild = source.findGuildByPlayer(player.getUniqueId()).orElse(null);
        GuildMember target = guild == null ? null : guild.findMember(targetId).orElse(null);
        if (target == null) { openMembers(player, parent, "Ce membre n'est plus dans la guilde."); return; }
        SimpleForm.Builder builder = SimpleForm.builder().title(FormStyle.title(target.name()))
                .content((result == null ? "" : result + "\n\n") + "Rang : " + rankName(target.rank()));
        boolean owner = guild.ownerId().equals(player.getUniqueId());
        if (owner && target.rank() != GuildRank.OWNER) {
            if (target.rank() != GuildRank.RECRUIT) builder.button(FormStyle.PRIMARY + "Rétrograder");
            builder.button(FormStyle.PRIMARY + (target.rank() == GuildRank.DEPUTY ? "Céder la guilde" : "Promouvoir"));
        }
        if (source.hasPermission(player.getUniqueId(), GuildPermission.KICK)
                && target.rank() != GuildRank.OWNER && !targetId.equals(player.getUniqueId())) {
            builder.button(FormStyle.DANGER + "Expulser");
        }
        builder.button(FormStyle.RETURN + BACK);
        setClose(builder, player, () -> openMembers(player, parent, null));
        builder.validResultHandler(forms.sync(player, once(response -> {
            int index = response.clickedButtonId();
            int cursor = 0;
            if (owner && target.rank() != GuildRank.OWNER) {
                if (target.rank() != GuildRank.RECRUIT) {
                    if (index == cursor++) {
                        confirmAction(player, "Rétrograder " + target.name() + " ?",
                                () -> runMemberAction(player, parent, targetId, source.demoteMember(player.getUniqueId(), targetId)),
                                () -> openMember(player, parent, targetId, null));
                        return;
                    }
                }
                if (index == cursor++) {
                    if (target.rank() == GuildRank.DEPUTY) {
                        confirmAction(player, "Céder la guilde à " + target.name() + " ?",
                                () -> runMemberAction(player, parent, targetId, source.transferOwnership(player.getUniqueId(), targetId)),
                                () -> openMember(player, parent, targetId, null));
                    } else {
                        runMemberAction(player, parent, targetId, source.promoteMember(player.getUniqueId(), targetId));
                    }
                    return;
                }
            }
            if (source.hasPermission(player.getUniqueId(), GuildPermission.KICK)
                    && target.rank() != GuildRank.OWNER && !targetId.equals(player.getUniqueId())) {
                if (index == cursor) {
                    confirmAction(player, "Expulser " + target.name() + " de la guilde ?",
                            () -> runMemberAction(player, parent, targetId, source.kickMember(player.getUniqueId(), targetId)),
                            () -> openMember(player, parent, targetId, null));
                    return;
                }
                cursor++;
            }
            if (index == cursor) openMembers(player, parent, null);
        })));
        forms.sendFormSync(player, builder.build());
    }

    private void runMemberAction(Player player, Runnable parent, UUID targetId, ActionResult action) {
        if (source.findGuildByPlayer(player.getUniqueId()).flatMap(guild -> guild.findMember(targetId)).isEmpty()) {
            openMembers(player, parent, action.message());
            return;
        }
        openMember(player, parent, targetId, action.message());
    }

    private void openTreasury(Player player, Runnable parent, String result) {
        Guild guild = source.findGuildByPlayer(player.getUniqueId()).orElse(null);
        if (guild == null) { openRoot(player, parent, result); return; }
        SimpleForm form = SimpleForm.builder().title(FormStyle.title("Trésor · " + guild.name()))
                .content((result == null ? "" : result + "\n\n") + "Solde : " + money(guild.balance()))
                .button(FormStyle.PRIMARY + "Déposer")
                .button(FormStyle.PRIMARY + "Retirer")
                .button(FormStyle.PRIMARY + "Tout déposer dans la réserve")
                .button(FormStyle.RETURN + BACK)
                .closedOrInvalidResultHandler(forms.sync(player, () -> openGuildHome(player, parent, null)))
                .validResultHandler(forms.sync(player, once(response -> {
                    switch (response.clickedButtonId()) {
                        case 0 -> amountForm(player, parent, true);
                        case 1 -> amountForm(player, parent, false);
                        case 2 -> {
                            ActionResult action = source.depositAllChestItems(player.getUniqueId());
                            openTreasury(player, parent, action.message());
                        }
                        default -> openGuildHome(player, parent, null);
                    }
                }))).build();
        forms.sendFormSync(player, form);
    }

    private void amountForm(Player player, Runnable parent, boolean deposit) {
        String actionName = deposit ? "Déposer" : "Retirer";
        CustomForm form = CustomForm.builder().title(FormStyle.title(actionName + " · trésor"))
                .label("Solde actuel : " + money(source.findGuildByPlayer(player.getUniqueId()).map(Guild::balance).orElse(0.0)))
                .input("Montant", "Exemple : 500", "")
                .closedOrInvalidResultHandler(forms.sync(player, () -> openTreasury(player, parent, null)))
                .validResultHandler(forms.sync(player, once(response -> {
                    Double amount = parseAmount(response.asInput(1));
                    if (amount == null) {
                        openTreasury(player, parent, "Saisissez un montant positif valide.");
                        return;
                    }
                    ActionResult result = deposit
                            ? source.deposit(player.getUniqueId(), amount)
                            : source.withdraw(player.getUniqueId(), amount);
                    openTreasury(player, parent, result.message());
                }))).build();
        forms.sendFormSync(player, form);
    }

    private void openRecruitment(Player player, Runnable parent, String result) {
        if (!source.hasPermission(player.getUniqueId(), GuildPermission.RECRUIT)) {
            openGuildHome(player, parent, "Vous n'avez pas la permission de gérer le recrutement.");
            return;
        }
        SimpleForm form = SimpleForm.builder().title(FormStyle.title("Recrutement"))
                .content(result == null ? "Invitez un joueur ou examinez les demandes d'adhésion." : result)
                .button(FormStyle.PRIMARY + "Inviter un joueur")
                .button(FormStyle.PRIMARY + "Demandes d'adhésion")
                .button(FormStyle.RETURN + BACK)
                .closedOrInvalidResultHandler(forms.sync(player, () -> openGuildHome(player, parent, null)))
                .validResultHandler(forms.sync(player, once(response -> {
                    if (response.clickedButtonId() == 0) inviteForm(player, parent, "", null);
                    else if (response.clickedButtonId() == 1) openJoinRequests(player, parent, null);
                    else openGuildHome(player, parent, null);
                }))).build();
        forms.sendFormSync(player, form);
    }

    private void inviteForm(Player player, Runnable parent, String previous, String error) {
        CustomForm.Builder builder = CustomForm.builder().title(FormStyle.title("Inviter un joueur"));
        if (error != null) builder.label(FormStyle.DANGER + error);
        builder.input("Pseudo du joueur en ligne", "Pseudo", previous);
        builder.closedOrInvalidResultHandler(forms.sync(player, () -> openRecruitment(player, parent, null)));
        builder.validResultHandler(forms.sync(player, once(response -> {
            String name = clean(response.asInput(error == null ? 0 : 1));
            Player target = Bukkit.getPlayerExact(name);
            if (target == null) {
                inviteForm(player, parent, name, "Joueur introuvable ou hors ligne.");
                return;
            }
            ActionResult result = source.invitePlayer(player.getUniqueId(), target.getUniqueId(), target.getName());
            openRecruitment(player, parent, result.message());
        })));
        forms.sendFormSync(player, builder.build());
    }

    private void openJoinRequests(Player player, Runnable parent, String result) {
        Guild guild = source.findGuildByPlayer(player.getUniqueId()).orElse(null);
        if (guild == null || !source.hasPermission(player.getUniqueId(), GuildPermission.RECRUIT)) {
            openGuildHome(player, parent, "Vous ne pouvez pas consulter ces demandes.");
            return;
        }
        List<GuildJoinRequest> requests = source.getJoinRequests(guild.id());
        SimpleForm.Builder builder = SimpleForm.builder().title(FormStyle.title("Demandes d'adhésion"))
                .content((result == null ? "" : result + "\n\n") + (requests.isEmpty() ? "Aucune demande en attente." : "Choisissez une demande."));
        requests.forEach(request -> builder.button(FormStyle.PRIMARY + request.playerName()));
        builder.button(FormStyle.RETURN + BACK);
        setClose(builder, player, () -> openRecruitment(player, parent, null));
        builder.validResultHandler(forms.sync(player, once(response -> {
            int index = response.clickedButtonId();
            if (index < requests.size()) openJoinRequest(player, parent, requests.get(index));
            else openRecruitment(player, parent, null);
        })));
        forms.sendFormSync(player, builder.build());
    }

    private void openJoinRequest(Player player, Runnable parent, GuildJoinRequest request) {
        SimpleForm form = SimpleForm.builder().title(FormStyle.title(request.playerName()))
                .content(request.playerName() + " souhaite rejoindre votre guilde.")
                .button(FormStyle.CONFIRM)
                .button(FormStyle.DANGER + "Refuser")
                .button(FormStyle.RETURN + BACK)
                .closedOrInvalidResultHandler(forms.sync(player, () -> openJoinRequests(player, parent, null)))
                .validResultHandler(forms.sync(player, once(response -> {
                    if (response.clickedButtonId() == 0) {
                        ActionResult result = source.acceptJoinRequest(player.getUniqueId(), request.playerId());
                        notifyOnline(request.playerId(), result.message());
                        openJoinRequests(player, parent, result.message());
                    } else if (response.clickedButtonId() == 1) {
                        ActionResult result = source.rejectJoinRequest(player.getUniqueId(), request.playerId());
                        openJoinRequests(player, parent, result.message());
                    } else openJoinRequests(player, parent, null);
                }))).build();
        forms.sendFormSync(player, form);
    }

    private void openInvitations(Player player, Runnable parent, String result) {
        List<Guild> invites = source.getReceivedInvitations(player.getUniqueId());
        SimpleForm.Builder builder = SimpleForm.builder().title(FormStyle.title("Invitations reçues"))
                .content((result == null ? "" : result + "\n\n") + (invites.isEmpty() ? "Aucune invitation en attente." : "Choisissez une guilde."));
        invites.forEach(guild -> builder.button(FormStyle.PRIMARY + guild.name()));
        builder.button(FormStyle.RETURN + BACK);
        setClose(builder, player, () -> openRoot(player, parent, null));
        builder.validResultHandler(forms.sync(player, once(response -> {
            int index = response.clickedButtonId();
            if (index >= invites.size()) { openRoot(player, parent, null); return; }
            Guild guild = invites.get(index);
            SimpleForm detail = SimpleForm.builder().title(FormStyle.title("Invitation · " + guild.name()))
                    .content("Cette guilde vous invite à la rejoindre.")
                    .button(FormStyle.CONFIRM)
                    .button(FormStyle.DANGER + "Refuser")
                    .button(FormStyle.RETURN + BACK)
                    .closedOrInvalidResultHandler(forms.sync(player, () -> openInvitations(player, parent, null)))
                    .validResultHandler(forms.sync(player, once(answer -> {
                        ActionResult action = answer.clickedButtonId() == 0
                                ? source.acceptInvitation(player.getUniqueId(), guild.id())
                                : answer.clickedButtonId() == 1
                                ? source.rejectInvitation(player.getUniqueId(), guild.id()) : null;
                        if (action != null && action.success() && answer.clickedButtonId() == 0) {
                            openGuildHome(player, parent, action.message());
                        } else openInvitations(player, parent, action == null ? null : action.message());
                    }))).build();
            forms.sendFormSync(player, detail);
        })));
        forms.sendFormSync(player, builder.build());
    }

    private void openSentRequests(Player player, Runnable parent, String result) {
        List<Guild> requests = source.getPendingJoinRequestGuilds(player.getUniqueId());
        SimpleForm.Builder builder = SimpleForm.builder().title(FormStyle.title("Demandes envoyées"))
                .content((result == null ? "" : result + "\n\n") + (requests.isEmpty() ? "Aucune demande en attente." : "Sélectionnez une demande pour l'annuler."));
        requests.forEach(guild -> builder.button(FormStyle.PRIMARY + guild.name() + "\nEn attente"));
        builder.button(FormStyle.RETURN + BACK);
        setClose(builder, player, () -> openRoot(player, parent, null));
        builder.validResultHandler(forms.sync(player, once(response -> {
            int index = response.clickedButtonId();
            if (index >= requests.size()) { openRoot(player, parent, null); return; }
            Guild guild = requests.get(index);
            confirmAction(player, "Annuler votre demande pour " + guild.name() + " ?",
                    () -> openSentRequests(player, parent,
                            source.cancelJoinRequest(player.getUniqueId(), guild.id()).message()),
                    () -> openSentRequests(player, parent, null));
        })));
        forms.sendFormSync(player, builder.build());
    }

    private void openPermissionRanks(Player player, Runnable parent) {
        openPermissionRanks(player, parent, null);
    }

    private void openPermissionRanks(Player player, Runnable parent, String result) {
        Guild guild = source.findGuildByPlayer(player.getUniqueId()).orElse(null);
        if (guild == null || !guild.ownerId().equals(player.getUniqueId())) {
            openGuildHome(player, parent, "Seul le chef peut modifier les permissions.");
            return;
        }
        SimpleForm form = SimpleForm.builder().title(FormStyle.title("Permissions"))
                .content((result == null ? "" : result + "\n\n") + "Choisissez le rang à configurer.")
                .button(FormStyle.PRIMARY + "Adjoint")
                .button(FormStyle.PRIMARY + "Membre")
                .button(FormStyle.PRIMARY + "Recrue")
                .button(FormStyle.RETURN + BACK)
                .closedOrInvalidResultHandler(forms.sync(player, () -> openGuildHome(player, parent, null)))
                .validResultHandler(forms.sync(player, once(response -> {
                    GuildRank[] ranks = {GuildRank.DEPUTY, GuildRank.MEMBER, GuildRank.RECRUIT};
                    int index = response.clickedButtonId();
                    if (index < ranks.length) openPermissionForm(player, parent, ranks[index], null);
                    else openGuildHome(player, parent, null);
                }))).build();
        forms.sendFormSync(player, form);
    }

    private void openPermissionForm(Player player, Runnable parent, GuildRank rank, String result) {
        Guild guild = source.findGuildByPlayer(player.getUniqueId()).orElse(null);
        if (guild == null || !guild.ownerId().equals(player.getUniqueId())) {
            openGuildHome(player, parent, "Seul le chef peut modifier les permissions.");
            return;
        }
        Set<GuildPermission> current = source.getPermissions(guild.id(), rank);
        CustomForm.Builder builder = CustomForm.builder().title(FormStyle.title("Permissions · " + rankName(rank)));
        if (result != null) builder.label(result);
        for (GuildPermission permission : EDITABLE_PERMISSIONS) {
            builder.toggle(permissionName(permission), current.contains(permission));
        }
        builder.closedOrInvalidResultHandler(forms.sync(player, () -> openPermissionRanks(player, parent)));
        builder.validResultHandler(forms.sync(player, once(response -> {
            int offset = result == null ? 0 : 1;
            String failure = null;
            for (int i = 0; i < EDITABLE_PERMISSIONS.length; i++) {
                GuildPermission permission = EDITABLE_PERMISSIONS[i];
                boolean enabled = response.asToggle(i + offset);
                if (current.contains(permission) == enabled) continue;
                ActionResult action = source.setPermission(player.getUniqueId(), rank, permission, enabled);
                if (!action.success()) { failure = action.message(); break; }
            }
            if (failure != null) openPermissionRanks(player, parent, failure);
            else openPermissionRanks(player, parent, "Permissions de " + rankName(rank) + " mises à jour.");
        })));
        forms.sendFormSync(player, builder.build());
    }

    private void confirmLeave(Player player, Runnable parent) {
        confirmAction(player, "Quitter définitivement votre guilde ? Cette action est irréversible.",
                () -> {
                    ActionResult result = source.leaveGuild(player.getUniqueId());
                    openRoot(player, parent, result.message());
                }, () -> openGuildHome(player, parent, null));
    }

    private void confirmAction(Player player, String text, Runnable confirm, Runnable cancel) {
        ModalForm form = ModalForm.builder().title(FormStyle.title("Confirmation"))
                .content(text)
                .button1(FormStyle.CONFIRM)
                .button2("Annuler")
                .closedOrInvalidResultHandler(forms.sync(player, cancel))
                .validResultHandler(forms.sync(player, once(response -> {
                    if (response.clickedButtonId() == 0) confirm.run();
                    else cancel.run();
                }))).build();
        forms.sendFormSync(player, form);
    }

    private void openRanking(Player player, Runnable parent, int returnPage) {
        List<Guild> guilds = source.getGuilds().stream()
                .sorted(Comparator.comparingInt(Guild::points).reversed()).toList();
        SimpleForm.Builder builder = SimpleForm.builder().title(FormStyle.title("Classement des guildes"))
                .content(guilds.isEmpty() ? "Aucune guilde classée." : "Classement par points.");
        for (int i = 0; i < guilds.size(); i++) {
            Guild guild = guilds.get(i);
            builder.button(FormStyle.PRIMARY + "#" + (i + 1) + " · " + guild.name()
                    + "\n" + guild.points() + " points · " + guild.members().size() + " membres");
        }
        builder.button(FormStyle.RETURN + BACK);
        setClose(builder, player, () -> openRoot(player, parent, null, returnPage));
        builder.validResultHandler(forms.sync(player, once(response -> openRoot(player, parent, null, returnPage))));
        forms.sendFormSync(player, builder.build());
    }

    private <T> Consumer<T> once(Consumer<T> action) {
        AtomicBoolean handled = new AtomicBoolean();
        return value -> {
            if (handled.compareAndSet(false, true)) action.accept(value);
        };
    }

    private void setClose(SimpleForm.Builder builder, Player player, Runnable action) {
        builder.closedOrInvalidResultHandler(forms.sync(player, action));
    }

    private void notifyOnline(UUID playerId, String message) {
        Player target = Bukkit.getPlayer(playerId);
        if (target != null && target.isOnline()) target.sendMessage(message);
    }

    private String clean(String text) {
        return text == null ? "" : text.trim();
    }

    private Double parseAmount(String input) {
        if (input == null || input.isBlank()) return null;
        try {
            double amount = Double.parseDouble(input.trim().replace(',', '.'));
            return Double.isFinite(amount) && amount > 0 ? amount : null;
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private String money(double amount) {
        return String.format(Locale.FRANCE, "%,.2f", amount);
    }

    private String rankName(GuildRank rank) {
        return switch (rank) {
            case OWNER -> "Chef";
            case DEPUTY -> "Adjoint";
            case MEMBER -> "Membre";
            case RECRUIT -> "Recrue";
        };
    }

    private String permissionName(GuildPermission permission) {
        return switch (permission) {
            case BUILD -> "Construire";
            case OPEN_CONTAINERS -> "Ouvrir les conteneurs";
            case RECRUIT -> "Inviter et recruter";
            case CLAIM_CHUNKS -> "Revendiquer des zones";
            case KICK -> "Expulser des membres";
            case MANAGE_WARP -> "Gérer le warp";
            case BANK_AND_UPGRADES -> "Banque et améliorations";
            case RESERVE -> "Utiliser la réserve";
        };
    }
}
