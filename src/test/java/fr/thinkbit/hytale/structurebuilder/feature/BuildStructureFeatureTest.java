package fr.thinkbit.hytale.structurebuilder.feature;

import fr.thinkbit.hytale.structurebuilder.generator.RecordingBlockPlacer;
import fr.thinkbit.hytale.structurebuilder.generator.StructureResult;
import fr.thinkbit.hytale.structurebuilder.material.MaterialPresets;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BuildStructureFeatureTest {

    private final BuildStructureFeature feature = new BuildStructureFeature();

    @Test
    void getName() {
        assertEquals("build_structure", feature.getName());
    }

    @Test
    void getToolDefinition() {
        var tool = feature.getToolDefinition();
        assertNotNull(tool);
        assertEquals("build_structure", tool.getName());
    }

    @Test
    void getInputSchema() {
        String schema = feature.getInputSchema();
        assertNotNull(schema);
        assertTrue(schema.contains("type"));
        assertTrue(schema.contains("\"x\""));
        assertTrue(schema.contains("\"y\""));
        assertTrue(schema.contains("\"z\""));
    }

    @Test
    void generatePlatform() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 64);
        args.put("z", 0);
        args.put("width", 5);
        args.put("depth", 5);

        StructureResult result = feature.generateStructure("platform", args,
                MaterialPresets.STONE_CASTLE, placer);

        assertEquals("platform", result.getStructureType());
        assertEquals(25, result.getBlocksPlaced());
    }

    @Test
    void generateHouse() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 64);
        args.put("z", 0);

        StructureResult result = feature.generateStructure("house", args,
                MaterialPresets.RUSTIC_WOOD, placer);

        assertEquals("house", result.getStructureType());
        assertTrue(result.getBlocksPlaced() > 0);
    }

    @Test
    void generateWall() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 64);
        args.put("z", 0);
        args.put("length", 8);
        args.put("height", 4);

        StructureResult result = feature.generateStructure("wall", args,
                MaterialPresets.COBBLESTONE, placer);

        assertEquals("wall", result.getStructureType());
        assertTrue(result.getBlocksPlaced() > 0);
    }

    @Test
    void generateTower() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 64);
        args.put("z", 0);
        args.put("radius", 3);

        StructureResult result = feature.generateStructure("tower", args,
                MaterialPresets.STONE_CASTLE, placer);

        assertEquals("tower", result.getStructureType());
        assertTrue(result.getBlocksPlaced() > 0);
    }

    @Test
    void unknownTypeThrows() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 64);
        args.put("z", 0);

        assertThrows(IllegalArgumentException.class,
                () -> feature.generateStructure("castle", args, MaterialPresets.STONE_CASTLE, placer));
    }

    @Test
    void allGeneratorsRegistered() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> baseArgs = new HashMap<>();
        baseArgs.put("x", 0);
        baseArgs.put("y", 0);
        baseArgs.put("z", 0);

        // All four types should work
        for (String type : new String[]{"platform", "wall", "tower", "house"}) {
            assertDoesNotThrow(
                    () -> feature.generateStructure(type, new HashMap<>(baseArgs),
                            MaterialPresets.STONE_CASTLE, new RecordingBlockPlacer()),
                    "Generator for type '" + type + "' should be registered");
        }
    }
}
