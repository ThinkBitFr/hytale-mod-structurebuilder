package fr.thinkbit.hytale.structurebuilder.feature;

import com.google.gson.JsonObject;
import com.top_serveurs.hytale.plugins.mcp.auth.McpAuthManager;
import com.top_serveurs.hytale.plugins.mcp.features.McpToolSchema;
import com.top_serveurs.hytale.plugins.mcp.models.McpTool;
import com.top_serveurs.hytale.plugins.mcp.models.McpToolCall;
import com.top_serveurs.hytale.plugins.mcp.models.McpToolResponse;
import fr.thinkbit.hytale.structurebuilder.WorldUtil;
import fr.thinkbit.hytale.structurebuilder.generator.FlatWorldGenerator;
import fr.thinkbit.hytale.structurebuilder.generator.StructureResult;
import fr.thinkbit.hytale.structurebuilder.generator.WorldBlockPlacer;
import fr.thinkbit.hytale.structurebuilder.material.MaterialPresets;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CreateFlatWorldFeature extends AbstractWorldFeature {

    private final FlatWorldGenerator generator = new FlatWorldGenerator();

    @Override
    public String getName() {
        return "create_flat_world";
    }

    @Override
    public McpTool getToolDefinition() {
        return new McpTool("create_flat_world",
                "Creates a flat world area with no buildings, structures, trees, or NPCs. " +
                "Generates proper terrain layers (stone, dirt, surface) and clears everything above. " +
                "Use this to prepare a clean canvas for building. " +
                "Warning: this replaces ALL existing terrain and structures in the area.",
                "function");
    }

    @Override
    public String getInputSchema() {
        Map<String, JsonObject> props = new LinkedHashMap<>();
        props.put("x", McpToolSchema.integerProperty("Center X coordinate"));
        props.put("z", McpToolSchema.integerProperty("Center Z coordinate"));
        props.put("radius", McpToolSchema.integerProperty(
                "Half-size of the area (default: 100). Area will be (2*radius+1) x (2*radius+1)"));
        props.put("surfaceY", McpToolSchema.integerProperty(
                "Height of the surface layer (default: 64)"));
        props.put("depth", McpToolSchema.integerProperty(
                "Depth of terrain fill below surface (default: 10)"));
        props.put("clearHeight", McpToolSchema.integerProperty(
                "Height above surface to clear (default: 60)"));
        props.put("surfaceBlock", McpToolSchema.stringProperty(
                "Block type for surface (default: Soil_Dirt)"));
        props.put("dirtBlock", McpToolSchema.stringProperty(
                "Block type for sub-surface dirt layer (default: Soil_Dirt)"));
        props.put("stoneBlock", McpToolSchema.stringProperty(
                "Block type for deep stone layer (default: Rock_Stone_Cobble)"));

        return McpToolSchema.schemaWithProperties(props, List.of("x", "z"));
    }

    @Override
    public McpToolResponse execute(McpToolCall call, McpAuthManager.AuthLevel authLevel) {
        try {
            Map<String, Object> args = call.getArguments();

            var world = WorldUtil.getDefaultWorld();
            var placer = new WorldBlockPlacer(world);

            StructureResult result = WorldUtil.executeOnWorldThread(world,
                    () -> generator.generate(args, MaterialPresets.getDefault(), placer)).get();

            return McpToolResponse.success(GSON.toJson(result.toJson()));
        } catch (Exception e) {
            return McpToolResponse.error("create_flat_world failed: " + e.getMessage());
        }
    }
}
