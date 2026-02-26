package fr.thinkbit.hytale.structurebuilder.generator;

import fr.thinkbit.hytale.structurebuilder.material.MaterialPresets;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FlatWorldGeneratorTest {

    private final FlatWorldGenerator generator = new FlatWorldGenerator();

    @Test
    void getType() {
        assertEquals("flat_world", generator.getType());
    }

    @Test
    void generateSmallArea() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("z", 0);
        args.put("radius", 2);      // 5x5 area
        args.put("surfaceY", 10);
        args.put("depth", 5);
        args.put("clearHeight", 3);

        StructureResult result = generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        assertEquals("flat_world", result.getStructureType());
        assertTrue(result.getBlocksPlaced() > 0);

        // Area: 5x5 (-2 to 2), depth 5 (y=5..10), clear 3 (y=11..13)
        // Per column: 2 stone + 3 dirt + 1 surface + 3 air = 9 blocks
        // Total: 5 * 5 * 9 = 225
        assertEquals(225, result.getBlocksPlaced());
    }

    @Test
    void terrainLayers() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("z", 0);
        args.put("radius", 0);      // single column
        args.put("surfaceY", 10);
        args.put("depth", 6);
        args.put("clearHeight", 2);

        generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        // Deep stone: y=4,5,6 (depth=6, dirtThickness=3, so stone from 4 to 6)
        assertTrue(placer.hasBlockAt(0, 4, 0, FlatWorldGenerator.DEFAULT_STONE), "Stone at y=4");
        assertTrue(placer.hasBlockAt(0, 5, 0, FlatWorldGenerator.DEFAULT_STONE), "Stone at y=5");
        assertTrue(placer.hasBlockAt(0, 6, 0, FlatWorldGenerator.DEFAULT_STONE), "Stone at y=6");

        // Dirt: y=7,8,9
        assertTrue(placer.hasBlockAt(0, 7, 0, FlatWorldGenerator.DEFAULT_DIRT), "Dirt at y=7");
        assertTrue(placer.hasBlockAt(0, 8, 0, FlatWorldGenerator.DEFAULT_DIRT), "Dirt at y=8");
        assertTrue(placer.hasBlockAt(0, 9, 0, FlatWorldGenerator.DEFAULT_DIRT), "Dirt at y=9");

        // Surface: y=10
        assertTrue(placer.hasBlockAt(0, 10, 0, FlatWorldGenerator.DEFAULT_SURFACE), "Surface at y=10");

        // Air above: y=11,12
        assertTrue(placer.hasBlockAt(0, 11, 0, FlatWorldGenerator.AIR), "Air at y=11");
        assertTrue(placer.hasBlockAt(0, 12, 0, FlatWorldGenerator.AIR), "Air at y=12");
    }

    @Test
    void shallowDepthOnlyDirt() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("z", 0);
        args.put("radius", 0);
        args.put("surfaceY", 10);
        args.put("depth", 2);       // less than dirtThickness (3), so only dirt+surface
        args.put("clearHeight", 1);

        generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        // With depth=2, dirtThickness=min(3,2)=2, dirtStartY=10-2=8
        // Stone: from bottomY(8) to dirtStartY(8) -> nothing
        // Dirt: y=8,9
        assertTrue(placer.hasBlockAt(0, 8, 0, FlatWorldGenerator.DEFAULT_DIRT), "Dirt at y=8");
        assertTrue(placer.hasBlockAt(0, 9, 0, FlatWorldGenerator.DEFAULT_DIRT), "Dirt at y=9");
        // Surface: y=10
        assertTrue(placer.hasBlockAt(0, 10, 0, FlatWorldGenerator.DEFAULT_SURFACE), "Surface at y=10");
        // Air: y=11
        assertTrue(placer.hasBlockAt(0, 11, 0, FlatWorldGenerator.AIR), "Air at y=11");

        // No stone blocks should exist
        assertEquals(0, placer.countBlocksOfType(FlatWorldGenerator.DEFAULT_STONE));
    }

    @Test
    void customBlocks() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("z", 0);
        args.put("radius", 0);
        args.put("surfaceY", 5);
        args.put("depth", 4);
        args.put("clearHeight", 1);
        args.put("stoneBlock", "Rock_Stone_Brick");
        args.put("dirtBlock", "Rock_Sandstone");
        args.put("surfaceBlock", "Rock_Sandstone_Brick");

        generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        // Stone layer: y=1
        assertTrue(placer.hasBlockAt(0, 1, 0, "Rock_Stone_Brick"), "Custom stone");
        // Dirt layer: y=2,3,4
        assertTrue(placer.hasBlockAt(0, 2, 0, "Rock_Sandstone"), "Custom dirt");
        // Surface: y=5
        assertTrue(placer.hasBlockAt(0, 5, 0, "Rock_Sandstone_Brick"), "Custom surface");
    }

    @Test
    void boundingBox() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 50);
        args.put("z", 100);
        args.put("radius", 10);
        args.put("surfaceY", 64);
        args.put("depth", 5);
        args.put("clearHeight", 20);

        StructureResult result = generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        assertEquals(40, result.getMinX());   // 50 - 10
        assertEquals(60, result.getMaxX());   // 50 + 10
        assertEquals(90, result.getMinZ());   // 100 - 10
        assertEquals(110, result.getMaxZ());  // 100 + 10
        assertEquals(59, result.getMinY());   // 64 - 5
        assertEquals(84, result.getMaxY());   // 64 + 20
    }

    @Test
    void defaultValues() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("z", 0);
        args.put("radius", 1); // small to keep test fast

        StructureResult result = generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        // Default surfaceY=64, depth=10, clearHeight=60
        // Area: 3x3, per column: 7 stone + 3 dirt + 1 surface + 60 air = 71
        // Total: 3*3*71 = 639
        assertEquals(639, result.getBlocksPlaced());
        assertEquals(54, result.getMinY());   // 64 - 10
        assertEquals(124, result.getMaxY());  // 64 + 60
    }

    @Test
    void clearsAboveSurface() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("z", 0);
        args.put("radius", 0);
        args.put("surfaceY", 10);
        args.put("depth", 1);
        args.put("clearHeight", 5);

        generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        // All blocks above surface should be air
        for (int y = 11; y <= 15; y++) {
            assertTrue(placer.hasBlockAt(0, y, 0, FlatWorldGenerator.AIR),
                    "Air at y=" + y);
        }
    }

    @Test
    void missingRequiredParameter() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        // missing z

        assertThrows(IllegalArgumentException.class,
                () -> generator.generate(args, MaterialPresets.STONE_CASTLE, placer));
    }
}
