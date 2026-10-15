package org.elestya.bedrock.api.sources.guild;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record Guild(
        String id,
        String name,
        UUID ownerId,
        double balance,
        int points,
        String entryMessage,
        List<GuildMember> members
) {
    public Guild {
        members = List.copyOf(members);
    }

    public Optional<GuildMember> findMember(UUID playerId) {
        return members.stream().filter(member -> member.playerId().equals(playerId)).findFirst();
    }
}
