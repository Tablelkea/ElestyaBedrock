package org.elestya.bedrock.api.sources;

import org.bukkit.entity.Player;
import org.elestya.bedrock.api.result.ActionResult;

import java.util.List;

public interface WorldSource {

    List<BedrockWorld> getWorlds();

    ActionResult teleport(Player player, BedrockWorld world);
}
