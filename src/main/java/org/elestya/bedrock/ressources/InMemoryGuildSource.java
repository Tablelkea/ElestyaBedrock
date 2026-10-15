package org.elestya.bedrock.ressources;

import org.elestya.bedrock.api.result.ActionResult;
import org.elestya.bedrock.api.sources.guild.Guild;
import org.elestya.bedrock.api.sources.guild.GuildJoinRequest;
import org.elestya.bedrock.api.sources.guild.GuildMember;
import org.elestya.bedrock.api.sources.guild.GuildPermission;
import org.elestya.bedrock.api.sources.guild.GuildRank;
import org.elestya.bedrock.api.sources.guild.GuildSource;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class InMemoryGuildSource implements GuildSource {
    private final Map<String, State> guilds = new HashMap<>();
    private final Map<UUID, String> memberships = new HashMap<>();
    private final Map<UUID, Set<String>> invitations = new HashMap<>();
    private final Map<UUID, Set<GuildPermission>> permissions = new HashMap<>();
    private final Map<UUID, String> playerNames = new HashMap<>();

    public InMemoryGuildSource() {
        UUID owner = UUID.fromString("0dc414fc-a56c-493e-a96f-5eb70d850001");
        UUID deputy = UUID.fromString("0dc414fc-a56c-493e-a96f-5eb70d850002");
        State state = new State("aurora", "Aurora", owner, 12500, 1840, "Bienvenue chez Aurora !");
        state.members.add(new GuildMember(owner, "AuroraChef", GuildRank.OWNER));
        state.members.add(new GuildMember(deputy, "Nova", GuildRank.DEPUTY));
        guilds.put(state.id, state);
        memberships.put(owner, state.id);
        memberships.put(deputy, state.id);
        remember(owner, "AuroraChef");
        remember(deputy, "Nova");
        state.permissionSets.put(GuildRank.DEPUTY, EnumSet.of(
                GuildPermission.RECRUIT, GuildPermission.KICK, GuildPermission.OPEN_CONTAINERS
        ));
        state.permissionSets.put(GuildRank.MEMBER, EnumSet.of(GuildPermission.OPEN_CONTAINERS));
        state.permissionSets.put(GuildRank.RECRUIT, EnumSet.noneOf(GuildPermission.class));

        State second = new State("nebula", "Nébula", UUID.randomUUID(), 7200, 1120, "");
        second.members.add(new GuildMember(second.ownerId, "NebulaChef", GuildRank.OWNER));
        guilds.put(second.id, second);
    }

    @Override
    public List<Guild> getGuilds() {
        return guilds.values().stream().map(this::snapshot).toList();
    }

    @Override
    public Optional<Guild> findGuildByPlayer(UUID playerId) {
        return Optional.ofNullable(memberships.get(playerId)).map(guilds::get).map(this::snapshot);
    }

    @Override
    public Optional<Guild> findGuildById(String guildId) {
        return Optional.ofNullable(guilds.get(guildId)).map(this::snapshot);
    }

    @Override
    public List<GuildJoinRequest> getJoinRequests(String guildId) {
        State guild = guilds.get(guildId);
        return guild == null ? List.of() : List.copyOf(guild.requests.values());
    }

    @Override
    public List<Guild> getPendingJoinRequestGuilds(UUID playerId) {
        return guilds.values().stream()
                .filter(guild -> guild.requests.containsKey(playerId))
                .map(this::snapshot)
                .toList();
    }

    @Override
    public List<Guild> getReceivedInvitations(UUID playerId) {
        return invitations.getOrDefault(playerId, Set.of()).stream()
                .map(guilds::get).filter(java.util.Objects::nonNull).map(this::snapshot).toList();
    }

    @Override
    public Set<GuildPermission> getPermissions(String guildId, GuildRank rank) {
        State guild = guilds.get(guildId);
        if (guild == null) return Set.of();
        if (rank == GuildRank.OWNER) return Set.copyOf(EnumSet.allOf(GuildPermission.class));
        return Set.copyOf(guild.permissionSets.getOrDefault(rank, EnumSet.noneOf(GuildPermission.class)));
    }

    @Override
    public boolean hasPermission(UUID playerId, GuildPermission permission) {
        State guild = guilds.get(memberships.get(playerId));
        if (guild == null) return false;
        GuildMember member = member(guild, playerId);
        return member != null && (member.rank() == GuildRank.OWNER
                || guild.permissionSets.getOrDefault(member.rank(), EnumSet.noneOf(GuildPermission.class)).contains(permission));
    }

    @Override
    public ActionResult createGuild(UUID playerId, String playerName, String name, String entryMessage) {
        if (memberships.containsKey(playerId)) return fail("Vous êtes déjà dans une guilde.");
        String cleanName = name == null ? "" : name.trim();
        if (!cleanName.matches("[\\p{L}0-9_-]{3,16}")) return fail("Nom invalide : 3 à 16 lettres, chiffres, tirets ou tirets bas.");
        if (guilds.values().stream().anyMatch(g -> g.name.equalsIgnoreCase(cleanName))) return fail("Ce nom de guilde est déjà utilisé.");
        String id = cleanName.toLowerCase(java.util.Locale.ROOT);
        State guild = new State(id, cleanName, playerId, 0, 0, entryMessage == null ? "" : entryMessage.trim());
        guild.members.add(new GuildMember(playerId, safeName(playerName), GuildRank.OWNER));
        guild.permissionSets.put(GuildRank.DEPUTY, EnumSet.noneOf(GuildPermission.class));
        guild.permissionSets.put(GuildRank.MEMBER, EnumSet.noneOf(GuildPermission.class));
        guild.permissionSets.put(GuildRank.RECRUIT, EnumSet.noneOf(GuildPermission.class));
        guilds.put(id, guild);
        memberships.put(playerId, id);
        remember(playerId, playerName);
        return ActionResult.success("Guilde « " + cleanName + " » créée.");
    }

    @Override
    public ActionResult requestToJoin(UUID playerId, String playerName, String guildId) {
        State guild = guilds.get(guildId);
        if (guild == null) return fail("Cette guilde n'existe plus.");
        if (memberships.containsKey(playerId)) return fail("Vous êtes déjà dans une guilde.");
        if (guild.requests.containsKey(playerId)) return fail("Votre demande est déjà en attente.");
        long count = guilds.values().stream().filter(g -> g.requests.containsKey(playerId)).count();
        if (count >= 5) return fail("Vous avez atteint la limite de cinq demandes simultanées.");
        guild.requests.put(playerId, new GuildJoinRequest(playerId, safeName(playerName)));
        remember(playerId, playerName);
        return ActionResult.success("Demande envoyée à " + guild.name + ".");
    }

    @Override
    public ActionResult cancelJoinRequest(UUID playerId, String guildId) {
        State guild = guilds.get(guildId);
        return guild != null && guild.requests.remove(playerId) != null
                ? ActionResult.success("Demande annulée.") : fail("Demande introuvable.");
    }

    @Override
    public ActionResult acceptJoinRequest(UUID actorId, UUID playerId) {
        State guild = guildFor(actorId);
        if (guild == null || !hasPermission(actorId, GuildPermission.RECRUIT)) return fail("Vous ne pouvez pas gérer le recrutement.");
        GuildJoinRequest request = guild.requests.remove(playerId);
        if (request == null) return fail("Cette demande n'est plus disponible.");
        if (memberships.containsKey(playerId)) return fail("Ce joueur a déjà rejoint une guilde.");
        addMember(guild, playerId, request.playerName());
        return ActionResult.success(request.playerName() + " a rejoint la guilde.");
    }

    @Override
    public ActionResult rejectJoinRequest(UUID actorId, UUID playerId) {
        State guild = guildFor(actorId);
        if (guild == null || !hasPermission(actorId, GuildPermission.RECRUIT)) return fail("Vous ne pouvez pas gérer le recrutement.");
        return guild.requests.remove(playerId) != null ? ActionResult.success("Demande refusée.") : fail("Cette demande n'est plus disponible.");
    }

    @Override
    public ActionResult invitePlayer(UUID actorId, UUID targetId, String playerName) {
        State guild = guildFor(actorId);
        if (guild == null || !hasPermission(actorId, GuildPermission.RECRUIT)) return fail("Vous ne pouvez pas inviter de joueurs.");
        if (targetId.equals(actorId) || memberships.containsKey(targetId)) return fail("Ce joueur ne peut pas rejoindre la guilde.");
        invitations.computeIfAbsent(targetId, ignored -> new HashSet<>()).add(guild.id);
        remember(targetId, playerName);
        return ActionResult.success("Invitation envoyée à " + safeName(playerName) + ".");
    }

    @Override
    public ActionResult acceptInvitation(UUID playerId, String guildId) {
        State guild = guilds.get(guildId);
        if (guild == null || !invitations.getOrDefault(playerId, Set.of()).contains(guildId)) return fail("Cette invitation n'est plus disponible.");
        if (memberships.containsKey(playerId)) return fail("Vous êtes déjà dans une guilde.");
        invitations.get(playerId).remove(guildId);
        addMember(guild, playerId, playerNames.getOrDefault(playerId, "Joueur"));
        return ActionResult.success("Vous avez rejoint " + guild.name + ".");
    }

    @Override
    public ActionResult rejectInvitation(UUID playerId, String guildId) {
        Set<String> playerInvites = invitations.get(playerId);
        return playerInvites != null && playerInvites.remove(guildId)
                ? ActionResult.success("Invitation refusée.") : fail("Cette invitation n'est plus disponible.");
    }

    @Override
    public ActionResult leaveGuild(UUID playerId) {
        State guild = guildFor(playerId);
        if (guild == null) return fail("Vous n'êtes dans aucune guilde.");
        GuildMember member = member(guild, playerId);
        if (member.rank() == GuildRank.OWNER && guild.members.size() > 1) return fail("Transférez d'abord la guilde à un autre membre.");
        guild.members.remove(member);
        memberships.remove(playerId);
        if (guild.members.isEmpty()) guilds.remove(guild.id);
        return ActionResult.success("Vous avez quitté la guilde.");
    }

    @Override
    public ActionResult deposit(UUID playerId, double amount) {
        State guild = guildFor(playerId);
        if (guild == null || !validAmount(amount)) return fail("Montant invalide.");
        guild.balance += amount;
        return ActionResult.success("Dépôt de " + format(amount) + " effectué dans le trésor.");
    }

    @Override
    public ActionResult withdraw(UUID playerId, double amount) {
        State guild = guildFor(playerId);
        if (guild == null || !hasPermission(playerId, GuildPermission.BANK_AND_UPGRADES)) return fail("Vous ne pouvez pas retirer du trésor.");
        if (!validAmount(amount) || amount > guild.balance) return fail("Montant invalide ou solde insuffisant.");
        guild.balance -= amount;
        return ActionResult.success("Retrait de " + format(amount) + " effectué.");
    }

    @Override
    public ActionResult promoteMember(UUID actorId, UUID targetId) {
        State guild = guildFor(actorId);
        GuildMember target = guild == null ? null : member(guild, targetId);
        if (guild == null || !guild.ownerId.equals(actorId) || target == null || target.rank() == GuildRank.OWNER || target.rank() == GuildRank.DEPUTY)
            return fail("Promotion impossible.");
        replaceRank(guild, target, target.rank() == GuildRank.RECRUIT ? GuildRank.MEMBER : GuildRank.DEPUTY);
        return ActionResult.success(target.name() + " a été promu.");
    }

    @Override
    public ActionResult demoteMember(UUID actorId, UUID targetId) {
        State guild = guildFor(actorId);
        GuildMember target = guild == null ? null : member(guild, targetId);
        if (guild == null || !guild.ownerId.equals(actorId) || target == null || target.rank() == GuildRank.OWNER || target.rank() == GuildRank.RECRUIT)
            return fail("Rétrogradation impossible.");
        replaceRank(guild, target, target.rank() == GuildRank.DEPUTY ? GuildRank.MEMBER : GuildRank.RECRUIT);
        return ActionResult.success(target.name() + " a été rétrogradé.");
    }

    @Override
    public ActionResult kickMember(UUID actorId, UUID targetId) {
        State guild = guildFor(actorId);
        GuildMember target = guild == null ? null : member(guild, targetId);
        if (guild == null || !hasPermission(actorId, GuildPermission.KICK) || target == null
                || target.rank() == GuildRank.OWNER || actorId.equals(targetId)) return fail("Expulsion impossible.");
        guild.members.remove(target);
        memberships.remove(targetId);
        return ActionResult.success(target.name() + " a été expulsé.");
    }

    @Override
    public ActionResult transferOwnership(UUID actorId, UUID targetId) {
        State guild = guildFor(actorId);
        GuildMember actor = guild == null ? null : member(guild, actorId);
        GuildMember target = guild == null ? null : member(guild, targetId);
        if (guild == null || !guild.ownerId.equals(actorId) || target == null || targetId.equals(actorId)) return fail("Cession impossible.");
        replaceRank(guild, actor, GuildRank.DEPUTY);
        replaceRank(guild, target, GuildRank.OWNER);
        guild.ownerId = targetId;
        return ActionResult.success("La direction de la guilde a été transférée à " + target.name() + ".");
    }

    @Override
    public ActionResult setPermission(UUID actorId, GuildRank rank, GuildPermission permission, boolean enabled) {
        State guild = guildFor(actorId);
        if (guild == null || !guild.ownerId.equals(actorId) || rank == GuildRank.OWNER) return fail("Seul le chef peut modifier ces permissions.");
        Set<GuildPermission> values = guild.permissionSets.computeIfAbsent(rank, ignored -> EnumSet.noneOf(GuildPermission.class));
        if (enabled) values.add(permission); else values.remove(permission);
        return ActionResult.success("Permission mise à jour.");
    }

    @Override
    public ActionResult depositAllChestItems(UUID playerId) {
        return hasPermission(playerId, GuildPermission.RESERVE)
                ? ActionResult.success("Tous les objets éligibles ont été déposés dans la réserve.")
                : fail("Vous ne pouvez pas déposer dans la réserve.");
    }

    private void addMember(State guild, UUID playerId, String name) {
        guild.members.add(new GuildMember(playerId, safeName(name), GuildRank.RECRUIT));
        memberships.put(playerId, guild.id);
        remember(playerId, name);
    }

    private void replaceRank(State guild, GuildMember old, GuildRank rank) {
        int index = guild.members.indexOf(old);
        guild.members.set(index, new GuildMember(old.playerId(), old.name(), rank));
    }

    private GuildMember member(State guild, UUID playerId) {
        return guild.members.stream().filter(value -> value.playerId().equals(playerId)).findFirst().orElse(null);
    }

    private State guildFor(UUID playerId) {
        return guilds.get(memberships.get(playerId));
    }

    private Guild snapshot(State state) {
        return new Guild(state.id, state.name, state.ownerId, state.balance, state.points, state.entryMessage, state.members);
    }

    private void remember(UUID id, String name) {
        String safe = safeName(name);
        playerNames.put(id, safe);
    }

    private String safeName(String name) {
        return name == null || name.isBlank() ? "Joueur" : name.trim();
    }

    private boolean validAmount(double amount) {
        return Double.isFinite(amount) && amount > 0;
    }

    private String format(double amount) {
        return String.format(java.util.Locale.FRANCE, "%,.2f", amount);
    }

    private ActionResult fail(String message) {
        return ActionResult.failure(message);
    }

    private static final class State {
        private final String id;
        private final String name;
        private UUID ownerId;
        private double balance;
        private final int points;
        private final String entryMessage;
        private final List<GuildMember> members = new ArrayList<>();
        private final Map<UUID, GuildJoinRequest> requests = new HashMap<>();
        private final Map<GuildRank, Set<GuildPermission>> permissionSets = new EnumMap<>(GuildRank.class);

        private State(String id, String name, UUID ownerId, double balance, int points, String entryMessage) {
            this.id = id;
            this.name = name;
            this.ownerId = ownerId;
            this.balance = balance;
            this.points = points;
            this.entryMessage = entryMessage;
        }
    }
}
