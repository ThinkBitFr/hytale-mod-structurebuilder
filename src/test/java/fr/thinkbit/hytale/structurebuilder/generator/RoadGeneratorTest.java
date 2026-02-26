package fr.thinkbit.hytale.structurebuilder.generator;

import fr.thinkbit.hytale.structurebuilder.material.MaterialPresets;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RoadGeneratorTest {

    private final RoadGenerator generator = new RoadGenerator();

    @Test
    void getType() {
        assertEquals("road", generator.getType());
    }

    @Test
    void basicRoadNoBorders() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 0);
        args.put("z", 0);
        args.put("length", 10);
        args.put("width", 3);
        args.put("borders", false);
        args.put("lanterns", false);

        StructureResult result = generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        assertEquals("road", result.getStructureType());
        assertEquals(30, result.getBlocksPlaced()); // 10 * 3
    }

    @Test
    void withBorders() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 0);
        args.put("z", 0);
        args.put("length", 5);
        args.put("width", 3);
        args.put("borders", true);
        args.put("lanterns", false);

        StructureResult result = generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        // Road: 5*3=15, Borders: 5*2=10
        assertEquals(25, result.getBlocksPlaced());

        // Border at z=-1 and z=3 (direction x, width 3)
        assertTrue(placer.hasBlockAt(0, 0, -1), "Left border");
        assertTrue(placer.hasBlockAt(0, 0, 3), "Right border");
    }

    @Test
    void withLanterns() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 0);
        args.put("z", 0);
        args.put("length", 20);
        args.put("width", 3);
        args.put("borders", false);
        args.put("lanterns", true);
        args.put("lanternSpacing", 8);

        generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        // Lanterns at l=0, 8, 16
        assertTrue(placer.hasAnyBlockOfType(MaterialPresets.STONE_CASTLE.getLantern()),
                "Should have lanterns");
    }

    @Test
    void directionZ() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 0);
        args.put("z", 0);
        args.put("length", 5);
        args.put("width", 2);
        args.put("direction", "z");
        args.put("borders", false);
        args.put("lanterns", false);

        StructureResult result = generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        assertEquals(10, result.getBlocksPlaced());
        // Road extends along Z, width along X
        assertTrue(placer.hasBlockAt(0, 0, 0));
        assertTrue(placer.hasBlockAt(1, 0, 4));
    }

    @Test
    void bordersAlternateMaterials() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 0);
        args.put("z", 0);
        args.put("length", 4);
        args.put("width", 3);
        args.put("borders", true);
        args.put("lanterns", false);

        generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        // Even positions use foundation, odd use accent
        assertTrue(placer.hasBlockAt(0, 0, -1, MaterialPresets.STONE_CASTLE.getFoundation()),
                "Even border = foundation");
        assertTrue(placer.hasBlockAt(1, 0, -1, MaterialPresets.STONE_CASTLE.getWallAccent()),
                "Odd border = accent");
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
