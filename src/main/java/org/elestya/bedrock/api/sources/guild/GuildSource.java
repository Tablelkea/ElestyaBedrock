package org.elestya.bedrock.api.sources.guild;

import org.elestya.bedrock.api.result.ActionResult;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface GuildSource {
    List<Guild> getGuilds();
    Optional<Guild> findGuildByPlayer(UUID playerId);
    Optional<Guild> findGuildById(String guildId);
    List<GuildJoinRequest> getJoinRequests(String guildId);
    List<Guild> getPendingJoinRequestGuilds(UUID playerId);
    List<Guild> getReceivedInvitations(UUID playerId);
    Set<GuildPermission> getPermissions(String guildId, GuildRank rank);
    boolean hasPermission(UUID playerId, GuildPermission permission);

    ActionResult createGuild(UUID playerId, String playerName, String name, String entryMessage);
    ActionResult requestToJoin(UUID playerId, String playerName, String guildId);
    ActionResult cancelJoinRequest(UUID playerId, String guildId);
    ActionResult acceptJoinRequest(UUID actorId, UUID playerId);
    ActionResult rejectJoinRequest(UUID actorId, UUID playerId);
    ActionResult invitePlayer(UUID actorId, UUID targetId, String playerName);
    ActionResult acceptInvitation(UUID playerId, String guildId);
    ActionResult rejectInvitation(UUID playerId, String guildId);
    ActionResult leaveGuild(UUID playerId);
    ActionResult deposit(UUID playerId, double amount);
    ActionResult withdraw(UUID playerId, double amount);
    ActionResult promoteMember(UUID actorId, UUID targetId);
    ActionResult demoteMember(UUID actorId, UUID targetId);
    ActionResult kickMember(UUID actorId, UUID targetId);
    ActionResult transferOwnership(UUID actorId, UUID targetId);
    ActionResult setPermission(UUID actorId, GuildRank rank, GuildPermission permission, boolean enabled);
    ActionResult depositAllChestItems(UUID playerId);
}
