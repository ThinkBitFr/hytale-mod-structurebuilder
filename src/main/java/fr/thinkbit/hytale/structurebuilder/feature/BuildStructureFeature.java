package fr.thinkbit.hytale.structurebuilder.feature;

import com.google.gson.JsonObject;
import com.top_serveurs.hytale.plugins.mcp.auth.McpAuthManager;
import com.top_serveurs.hytale.plugins.mcp.features.McpToolSchema;
import com.top_serveurs.hytale.plugins.mcp.models.McpTool;
import com.top_serveurs.hytale.plugins.mcp.models.McpToolCall;
import com.top_serveurs.hytale.plugins.mcp.models.McpToolResponse;
import fr.thinkbit.hytale.structurebuilder.WorldUtil;
import fr.thinkbit.hytale.structurebuilder.generator.*;
import fr.thinkbit.hytale.structurebuilder.material.MaterialPalette;
import fr.thinkbit.hytale.structurebuilder.material.MaterialPresets;

import java.util.*;

public class BuildStructureFeature extends AbstractWorldFeature {

    private final Map<String, StructureGenerator> generators = new LinkedHashMap<>();

    public BuildStructureFeature() {
        register(new PlatformGenerator());
        register(new WallGenerator());
        register(new TowerGenerator());
        register(new HouseGenerator());
    }

    private void register(StructureGenerator gen) {
        generators.put(gen.getType(), gen);
    }

    @Override
    public String getName() {
        return "build_structure";
    }

    @Override
    public McpTool getToolDefinition() {
        return new McpTool("build_structure",
                "Builds a complete structure (house, tower, wall, platform) at the given position. " +
                "Replaces dozens of individual block-placement calls with a single server-side generation. " +
                "Types: house (multi-floor with roof, windows, furniture), tower (round/square with battlements), " +
                "wall (with optional battlements), platform (flat surface). " +
                "Materials: rustic_wood, stone_castle, cobblestone.",
                "function");
    }

    @Override
    public String getInputSchema() {
        Map<String, JsonObject> props = new LinkedHashMap<>();
        props.put("type", McpToolSchema.stringProperty(
                "Structure type: house, tower, wall, platform"));
        props.put("x", McpToolSchema.integerProperty("X coordinate"));
        props.put("y", McpToolSchema.integerProperty("Y coordinate"));
        props.put("z", McpToolSchema.integerProperty("Z coordinate"));
        props.put("material", McpToolSchema.stringProperty(
                "Material preset: rustic_wood, stone_castle, cobblestone (default: stone_castle)"));
        // House params
        props.put("width", McpToolSchema.integerProperty("Width in X (default: 10)"));
        props.put("depth", McpToolSchema.integerProperty("Depth in Z (default: 8)"));
        props.put("floors", McpToolSchema.integerProperty("Number of floors (house, default: 1)"));
        props.put("floorHeight", McpToolSchema.integerProperty("Wall height per floor (house, default: 4)"));
        props.put("roofStyle", McpToolSchema.stringProperty(
                "Roof style: flat, gable, hip (house, default: gable)"));
        props.put("furniture", McpToolSchema.stringProperty("Add furniture: true/false (house, default: true)"));
        props.put("windows", McpToolSchema.stringProperty("Add windows: true/false (house, default: true)"));
        props.put("doorSide", McpToolSchema.stringProperty(
                "Door side: north, south, east, west (house, default: south)"));
        // Tower params
        props.put("radius", McpToolSchema.integerProperty("Radius (tower, default: 4)"));
        props.put("shape", McpToolSchema.stringProperty("Shape: round, square (tower, default: round)"));
        // Wall/Tower params
        props.put("height", McpToolSchema.integerProperty("Height (wall/tower, default: varies)"));
        props.put("thickness", McpToolSchema.integerProperty("Thickness (wall/platform, default: 1)"));
        props.put("length", McpToolSchema.integerProperty("Length (wall, default: 10)"));
        props.put("direction", McpToolSchema.stringProperty("Direction: x or z (wall, default: x)"));
        props.put("battlements", McpToolSchema.stringProperty(
                "Add battlements: true/false (wall/tower, default: true)"));

        return McpToolSchema.schemaWithProperties(props, List.of("type", "x", "y", "z"));
    }

    @Override
    public McpToolResponse execute(McpToolCall call, McpAuthManager.AuthLevel authLevel) {
        try {
            String type = getString(call, "type");
            if (type == null) {
                return McpToolResponse.error("Missing required parameter: type");
            }

            StructureGenerator generator = generators.get(type);
            if (generator == null) {
                return McpToolResponse.error("Unknown structure type: " + type +
                        ". Available: " + String.join(", ", generators.keySet()));
            }

            // Resolve material palette
            String materialName = getString(call, "material", "stone_castle");
            MaterialPalette palette = MaterialPresets.getByName(materialName);
            if (palette == null) {
                return McpToolResponse.error("Unknown material preset: " + materialName +
                        ". Available: " + String.join(", ", MaterialPresets.getAvailablePresets()));
            }

            // Pass all arguments through to the generator
            Map<String, Object> args = call.getArguments();

            var world = WorldUtil.getDefaultWorld();
            var placer = new WorldBlockPlacer(world);

            StructureResult result = WorldUtil.executeOnWorldThread(world,
                    () -> generator.generate(args, palette, placer)).get();

            return McpToolResponse.success(GSON.toJson(result.toJson()));
        } catch (Exception e) {
            return McpToolResponse.error("build_structure failed: " + e.getMessage());
        }
    }

    // Visible for testing
    public StructureResult generateStructure(String type, Map<String, Object> args,
                                              MaterialPalette palette, BlockPlacer placer) {
        StructureGenerator generator = generators.get(type);
        if (generator == null) {
            throw new IllegalArgumentException("Unknown structure type: " + type);
        }
        return generator.generate(args, palette, placer);
    }
}
