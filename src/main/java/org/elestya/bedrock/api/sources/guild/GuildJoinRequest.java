package org.elestya.bedrock.api.sources.guild;

import java.util.UUID;

public record GuildJoinRequest(UUID playerId, String playerName) {
}
