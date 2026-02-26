package fr.thinkbit.hytale.structurebuilder.generator;

import fr.thinkbit.hytale.structurebuilder.material.MaterialPresets;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BridgeGeneratorTest {

    private final BridgeGenerator generator = new BridgeGenerator();

    @Test
    void getType() {
        assertEquals("bridge", generator.getType());
    }

    @Test
    void basicBridge() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 10);
        args.put("z", 0);
        args.put("length", 10);
        args.put("width", 3);
        args.put("railings", false);
        args.put("supports", false);

        StructureResult result = generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        assertEquals("bridge", result.getStructureType());
        // 10 * 3 = 30 deck blocks
        assertEquals(30, result.getBlocksPlaced());
    }

    @Test
    void withRailings() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 10);
        args.put("z", 0);
        args.put("length", 5);
        args.put("width", 3);
        args.put("railings", true);
        args.put("supports", false);

        StructureResult result = generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        // Deck: 5*3=15, Railings: 5*2=10
        assertEquals(25, result.getBlocksPlaced());
        // Railings should be 1 above deck
        assertTrue(placer.hasBlockAt(0, 11, 0));
        assertTrue(placer.hasBlockAt(0, 11, 2));
    }

    @Test
    void withSupports() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 10);
        args.put("z", 0);
        args.put("length", 10);
        args.put("width", 3);
        args.put("railings", false);
        args.put("supports", true);
        args.put("supportSpacing", 5);
        args.put("supportDepth", 3);

        StructureResult result = generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        // Deck: 10*3=30
        // Supports: at l=0,5 -> 2 positions * 2 edges * 3 depth = 12
        assertEquals(42, result.getBlocksPlaced());
        // Support below deck at y=9,8,7
        assertTrue(placer.hasBlockAt(0, 9, 0));
        assertTrue(placer.hasBlockAt(0, 8, 0));
        assertTrue(placer.hasBlockAt(0, 7, 0));
    }

    @Test
    void directionZ() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 10);
        args.put("z", 0);
        args.put("length", 5);
        args.put("width", 2);
        args.put("direction", "z");
        args.put("railings", false);
        args.put("supports", false);

        StructureResult result = generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        assertEquals(10, result.getBlocksPlaced());
        // Bridge extends along Z, width along X
        assertTrue(placer.hasBlockAt(0, 10, 0));
        assertTrue(placer.hasBlockAt(1, 10, 4));
    }

    @Test
    void boundingBox() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 5);
        args.put("y", 20);
        args.put("z", 10);
        args.put("length", 10);
        args.put("width", 3);
        args.put("railings", true);
        args.put("supports", true);
        args.put("supportDepth", 4);

        StructureResult result = generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        assertEquals(5, result.getMinX());
        assertEquals(14, result.getMaxX());
        assertEquals(10, result.getMinZ());
        assertEquals(12, result.getMaxZ());
        assertEquals(16, result.getMinY()); // 20 - 4 support depth
        assertEquals(21, result.getMaxY()); // 20 + 1 railing
    }

    @Test
    void missingRequiredParameter() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        // missing y and z

        assertThrows(IllegalArgumentException.class,
                () -> generator.generate(args, MaterialPresets.STONE_CASTLE, placer));
    }
}
