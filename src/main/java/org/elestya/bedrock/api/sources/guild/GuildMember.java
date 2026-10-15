package org.elestya.bedrock.api.sources.guild;

import java.util.UUID;

public record GuildMember(UUID playerId, String name, GuildRank rank) {
}
