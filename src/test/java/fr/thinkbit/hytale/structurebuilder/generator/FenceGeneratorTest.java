package fr.thinkbit.hytale.structurebuilder.generator;

import fr.thinkbit.hytale.structurebuilder.material.MaterialPresets;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FenceGeneratorTest {

    private final FenceGenerator generator = new FenceGenerator();

    @Test
    void getType() {
        assertEquals("fence", generator.getType());
    }

    @Test
    void basicFenceNoGate() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 0);
        args.put("z", 0);
        args.put("width", 5);
        args.put("depth", 5);
        args.put("height", 1);
        args.put("gate", false);
        args.put("posts", false);

        StructureResult result = generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        assertEquals("fence", result.getStructureType());
        assertTrue(result.getBlocksPlaced() > 0);

        // Perimeter: 4 sides of 5, minus 4 corners (counted once each)
        // North: 5, South: 5, West: 3 (inner), East: 3 (inner) = 16
        assertEquals(16, result.getBlocksPlaced());
    }

    @Test
    void fenceWithCornerPosts() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 0);
        args.put("z", 0);
        args.put("width", 5);
        args.put("depth", 5);
        args.put("height", 2);
        args.put("gate", false);
        args.put("posts", true);

        StructureResult result = generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        // Corner posts are taller (height+1)
        assertTrue(placer.hasBlockAt(0, 2, 0), "Corner post at (0,0) extends above");
        assertTrue(placer.hasBlockAt(4, 2, 0), "Corner post at (4,0) extends above");
        assertTrue(placer.hasBlockAt(0, 2, 4), "Corner post at (0,4) extends above");
        assertTrue(placer.hasBlockAt(4, 2, 4), "Corner post at (4,4) extends above");
    }

    @Test
    void fenceWithSouthGate() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 0);
        args.put("z", 0);
        args.put("width", 10);
        args.put("depth", 10);
        args.put("height", 2);
        args.put("gate", true);
        args.put("gateSide", "south");
        args.put("gateWidth", 3);
        args.put("posts", false);

        generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        // Gate opening: south wall (z=9), centered at x=4,5,6 (width=10, gateWidth=3)
        // Gate blocks at height 1 should be missing (opening)
        int gateStartX = 10 / 2 - 3 / 2; // 4
        // The wall blocks at gate position height 1 should NOT be there
        assertFalse(placer.hasBlockAt(gateStartX, 1, 9,
                MaterialPresets.STONE_CASTLE.getWall()), "Gate opening should be empty");
    }

    @Test
    void gateHasThreshold() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 0);
        args.put("z", 0);
        args.put("width", 10);
        args.put("depth", 10);
        args.put("height", 2);
        args.put("gate", true);
        args.put("gateSide", "south");
        args.put("gateWidth", 3);
        args.put("posts", false);

        generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        // Gate threshold at y=0 on south wall
        int gateStartX = 10 / 2 - 3 / 2; // 4
        assertTrue(placer.hasBlockAt(gateStartX, 0, 9,
                MaterialPresets.STONE_CASTLE.getFoundation()), "Foundation threshold at gate");
    }

    @Test
    void boundingBox() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 10);
        args.put("y", 5);
        args.put("z", 20);
        args.put("width", 15);
        args.put("depth", 15);
        args.put("height", 2);
        args.put("gate", false);
        args.put("posts", false);

        StructureResult result = generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        assertEquals(10, result.getMinX());
        assertEquals(24, result.getMaxX());  // 10 + 15 - 1
        assertEquals(20, result.getMinZ());
        assertEquals(34, result.getMaxZ());  // 20 + 15 - 1
    }

    @Test
    void missingRequiredParameter() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);

        assertThrows(IllegalArgumentException.class,
                () -> generator.generate(args, MaterialPresets.STONE_CASTLE, placer));
    }
}
