package fr.thinkbit.hytale.structurebuilder.feature;

import com.google.gson.JsonObject;
import com.top_serveurs.hytale.plugins.mcp.auth.McpAuthManager;
import com.top_serveurs.hytale.plugins.mcp.features.McpToolSchema;
import com.top_serveurs.hytale.plugins.mcp.models.McpTool;
import com.top_serveurs.hytale.plugins.mcp.models.McpToolCall;
import com.top_serveurs.hytale.plugins.mcp.models.McpToolResponse;
import fr.thinkbit.hytale.structurebuilder.StructureBuilderPlugin;
import fr.thinkbit.hytale.structurebuilder.WorldUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * MCP tool to configure the world for a fresh start:
 * - Disables NPC spawning
 * - Disables quest/objective markers
 * - Disables spawn markers
 * - Freezes all NPCs
 * - Controls prefab (structure) blocking during world generation
 *
 * The PrefabBlockerSystem cancels PrefabPasteEvent to prevent
 * world-gen structures while keeping natural terrain.
 */
public class CreateWorldFeature extends AbstractWorldFeature {

    @Override
    public String getName() {
        return "create_world";
    }

    @Override
    public McpTool getToolDefinition() {
        return new McpTool("create_world",
                "Configures the world for a clean building environment. " +
                "Disables NPC spawning, quests, and objective markers. " +
                "When blockStructures is true (default), prevents world-gen buildings " +
                "from being placed in newly generated chunks — natural terrain " +
                "(hills, caves, biomes) is preserved. " +
                "Call this once at server start to prepare a fresh canvas for building.",
                "function");
    }

    @Override
    public String getInputSchema() {
        Map<String, JsonObject> props = new LinkedHashMap<>();
        props.put("disableNPC", McpToolSchema.stringProperty(
                "Disable NPC spawning: true/false (default: true)"));
        props.put("disableQuests", McpToolSchema.stringProperty(
                "Disable quest/objective markers: true/false (default: true)"));
        props.put("freezeNPC", McpToolSchema.stringProperty(
                "Freeze all existing NPCs: true/false (default: true)"));
        props.put("blockStructures", McpToolSchema.stringProperty(
                "Block world-gen structures (buildings, camps): true/false (default: true). " +
                "Natural terrain is preserved."));

        return McpToolSchema.schemaWithProperties(props, List.of());
    }

    @Override
    public McpToolResponse execute(McpToolCall call, McpAuthManager.AuthLevel authLevel) {
        try {
            boolean disableNPC = getBool(call, "disableNPC", true);
            boolean disableQuests = getBool(call, "disableQuests", true);
            boolean freezeNPC = getBool(call, "freezeNPC", true);
            boolean blockStructures = getBool(call, "blockStructures", true);

            var world = WorldUtil.getDefaultWorld();
            var config = world.getWorldConfig();

            // Apply world config changes
            config.setSpawningNPC(!disableNPC);
            config.setObjectiveMarkersEnabled(!disableQuests);
            config.setIsSpawnMarkersEnabled(!disableQuests);
            config.setIsAllNPCFrozen(freezeNPC);
            config.markChanged();

            // Update prefab blocker
            var plugin = StructureBuilderPlugin.get();
            plugin.getPrefabBlocker().setBlocking(blockStructures);

            // Build response
            JsonObject result = new JsonObject();
            result.addProperty("status", "success");
            result.addProperty("spawningNPC", config.isSpawningNPC());
            result.addProperty("objectiveMarkersEnabled", config.isObjectiveMarkersEnabled());
            result.addProperty("spawnMarkersEnabled", config.isSpawnMarkersEnabled());
            result.addProperty("allNPCFrozen", config.isAllNPCFrozen());
            result.addProperty("blockingStructures", plugin.getPrefabBlocker().isBlocking());
            result.addProperty("seed", config.getSeed());

            return McpToolResponse.success(GSON.toJson(result));
        } catch (Exception e) {
            return McpToolResponse.error("create_world failed: " + e.getMessage());
        }
    }
}
