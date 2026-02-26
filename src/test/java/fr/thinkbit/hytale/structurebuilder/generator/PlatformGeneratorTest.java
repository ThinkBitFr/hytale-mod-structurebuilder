package fr.thinkbit.hytale.structurebuilder.generator;

import fr.thinkbit.hytale.structurebuilder.material.MaterialPresets;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PlatformGeneratorTest {

    private final PlatformGenerator generator = new PlatformGenerator();

    @Test
    void getType() {
        assertEquals("platform", generator.getType());
    }

    @Test
    void generateDefaultSize() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 64);
        args.put("z", 0);

        StructureResult result = generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        // Default 10x10x1 = 100 blocks
        assertEquals(100, result.getBlocksPlaced());
        assertEquals("platform", result.getStructureType());
        assertEquals(0, result.getMinX());
        assertEquals(64, result.getMinY());
        assertEquals(0, result.getMinZ());
        assertEquals(9, result.getMaxX());
        assertEquals(64, result.getMaxY());
        assertEquals(9, result.getMaxZ());
    }

    @Test
    void generateCustomSize() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 10);
        args.put("y", 50);
        args.put("z", 20);
        args.put("width", 5);
        args.put("depth", 3);
        args.put("thickness", 2);

        StructureResult result = generator.generate(args, MaterialPresets.RUSTIC_WOOD, placer);

        // 5 * 3 * 2 = 30 blocks
        assertEquals(30, result.getBlocksPlaced());
        assertEquals(10, result.getMinX());
        assertEquals(50, result.getMinY());
        assertEquals(20, result.getMinZ());
        assertEquals(14, result.getMaxX());
        assertEquals(51, result.getMaxY());
        assertEquals(22, result.getMaxZ());
    }

    @Test
    void usesFoundationMaterial() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 0);
        args.put("z", 0);
        args.put("width", 2);
        args.put("depth", 2);

        generator.generate(args, MaterialPresets.RUSTIC_WOOD, placer);

        assertTrue(placer.hasBlockAt(0, 0, 0, "Rock_Stone_Cobble"));
        assertTrue(placer.hasBlockAt(1, 0, 1, "Rock_Stone_Cobble"));
        assertEquals(1, placer.getBlockTypesUsed().size());
    }

    @Test
    void missingRequiredParameter() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("z", 0);
        // missing y

        assertThrows(IllegalArgumentException.class,
                () -> generator.generate(args, MaterialPresets.STONE_CASTLE, placer));
    }

    @Test
    void singleBlockPlatform() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 5);
        args.put("y", 10);
        args.put("z", 5);
        args.put("width", 1);
        args.put("depth", 1);
        args.put("thickness", 1);

        StructureResult result = generator.generate(args, MaterialPresets.COBBLESTONE, placer);

        assertEquals(1, result.getBlocksPlaced());
        assertTrue(placer.hasBlockAt(5, 10, 5, "Rock_Stone_Cobble"));
    }

    @Test
    void resultToJson() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 0);
        args.put("z", 0);
        args.put("width", 2);
        args.put("depth", 2);

        StructureResult result = generator.generate(args, MaterialPresets.STONE_CASTLE, placer);
        var json = result.toJson();

        assertEquals("success", json.get("status").getAsString());
        assertEquals("platform", json.get("structureType").getAsString());
        assertEquals(4, json.get("blocksPlaced").getAsInt());
        assertNotNull(json.getAsJsonObject("boundingBox"));
    }
}
