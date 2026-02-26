package fr.thinkbit.hytale.structurebuilder.generator;

import fr.thinkbit.hytale.structurebuilder.material.MaterialPresets;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WallGeneratorTest {

    private final WallGenerator generator = new WallGenerator();

    @Test
    void getType() {
        assertEquals("wall", generator.getType());
    }

    @Test
    void generateDefaultWall() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 64);
        args.put("z", 0);

        StructureResult result = generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        // Default: length=10, height=5, thickness=1 = 50 wall blocks + battlements
        // Battlements: every other on length 10 = 5 merlons (positions 0,2,4,6,8)
        assertEquals(55, result.getBlocksPlaced());
        assertEquals("wall", result.getStructureType());
    }

    @Test
    void wallDirectionX() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 0);
        args.put("z", 0);
        args.put("length", 5);
        args.put("height", 3);
        args.put("direction", "x");
        args.put("battlements", false);

        StructureResult result = generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        assertEquals(15, result.getBlocksPlaced()); // 5 * 3 * 1
        // Check wall extends along X
        assertTrue(placer.hasBlockAt(0, 0, 0));
        assertTrue(placer.hasBlockAt(4, 0, 0));
        assertTrue(placer.hasBlockAt(0, 2, 0));
        assertEquals(4, result.getMaxX());
        assertEquals(0, result.getMaxZ());
    }

    @Test
    void wallDirectionZ() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 0);
        args.put("z", 0);
        args.put("length", 5);
        args.put("height", 3);
        args.put("direction", "z");
        args.put("battlements", false);

        StructureResult result = generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        assertEquals(15, result.getBlocksPlaced());
        // Check wall extends along Z
        assertTrue(placer.hasBlockAt(0, 0, 0));
        assertTrue(placer.hasBlockAt(0, 0, 4));
        assertEquals(0, result.getMaxX());
        assertEquals(4, result.getMaxZ());
    }

    @Test
    void cornerPillarsUseAccent() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 0);
        args.put("z", 0);
        args.put("length", 5);
        args.put("height", 2);
        args.put("battlements", false);

        generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        // Corners (l=0 and l=4) should be accent
        assertTrue(placer.hasBlockAt(0, 0, 0, "Wood_Softwood_Beam")); // accent at start
        assertTrue(placer.hasBlockAt(4, 0, 0, "Wood_Softwood_Beam")); // accent at end
        // Middle should be wall
        assertTrue(placer.hasBlockAt(2, 0, 0, "Rock_Stone_Brick")); // wall block
    }

    @Test
    void battlementsPlaced() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 0);
        args.put("z", 0);
        args.put("length", 6);
        args.put("height", 3);
        args.put("battlements", true);

        generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        // Battlements at y=3 (height), every other block: l=0,2,4
        assertTrue(placer.hasBlockAt(0, 3, 0));
        assertTrue(placer.hasBlockAt(2, 3, 0));
        assertTrue(placer.hasBlockAt(4, 3, 0));
        // Gaps at l=1,3,5
        assertFalse(placer.hasBlockAt(1, 3, 0));
        assertFalse(placer.hasBlockAt(3, 3, 0));
        assertFalse(placer.hasBlockAt(5, 3, 0));
    }

    @Test
    void thickWall() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 0);
        args.put("z", 0);
        args.put("length", 3);
        args.put("height", 2);
        args.put("thickness", 2);
        args.put("battlements", false);

        StructureResult result = generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        assertEquals(12, result.getBlocksPlaced()); // 3 * 2 * 2
        assertTrue(placer.hasBlockAt(0, 0, 0));
        assertTrue(placer.hasBlockAt(0, 0, 1)); // thickness
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
