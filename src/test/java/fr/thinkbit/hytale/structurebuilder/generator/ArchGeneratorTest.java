package fr.thinkbit.hytale.structurebuilder.generator;

import fr.thinkbit.hytale.structurebuilder.material.MaterialPresets;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ArchGeneratorTest {

    private final ArchGenerator generator = new ArchGenerator();

    @Test
    void getType() {
        assertEquals("arch", generator.getType());
    }

    @Test
    void basicArch() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 0);
        args.put("z", 0);
        args.put("width", 5);
        args.put("height", 7);
        args.put("depth", 1);
        args.put("lanterns", false);

        StructureResult result = generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        assertEquals("arch", result.getStructureType());
        assertTrue(result.getBlocksPlaced() > 0);
    }

    @Test
    void hasPillars() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 0);
        args.put("z", 0);
        args.put("width", 5);
        args.put("height", 7);
        args.put("depth", 1);
        args.put("direction", "z");
        args.put("lanterns", false);

        generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        // Left pillar at x=0 should have accent blocks
        assertTrue(placer.hasBlockAt(0, 0, 0, MaterialPresets.STONE_CASTLE.getWallAccent()),
                "Left pillar base");
        // Right pillar at x=4
        assertTrue(placer.hasBlockAt(4, 0, 0, MaterialPresets.STONE_CASTLE.getWallAccent()),
                "Right pillar base");
    }

    @Test
    void hasArchCurve() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 0);
        args.put("z", 0);
        args.put("width", 5);
        args.put("height", 7);
        args.put("depth", 1);
        args.put("direction", "z");
        args.put("lanterns", false);

        generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        // Center of arch (x=2) should have a wall block at the highest point
        assertTrue(placer.hasAnyBlockOfType(MaterialPresets.STONE_CASTLE.getWall()),
                "Arch should contain wall blocks");
    }

    @Test
    void withLanterns() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 0);
        args.put("z", 0);
        args.put("width", 5);
        args.put("height", 7);
        args.put("depth", 1);
        args.put("direction", "z");
        args.put("lanterns", true);

        generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        assertTrue(placer.hasAnyBlockOfType(MaterialPresets.STONE_CASTLE.getLantern()),
                "Should have lanterns");
    }

    @Test
    void directionX() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 0);
        args.put("z", 0);
        args.put("width", 5);
        args.put("height", 7);
        args.put("depth", 2);
        args.put("direction", "x");
        args.put("lanterns", false);

        StructureResult result = generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        assertEquals("arch", result.getStructureType());
        assertTrue(result.getBlocksPlaced() > 0);
        // Pillars should be along Z axis
        assertTrue(placer.hasBlockAt(0, 0, 0, MaterialPresets.STONE_CASTLE.getWallAccent()));
        assertTrue(placer.hasBlockAt(0, 0, 4, MaterialPresets.STONE_CASTLE.getWallAccent()));
    }

    @Test
    void hasTrimOnTop() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 0);
        args.put("z", 0);
        args.put("width", 5);
        args.put("height", 7);
        args.put("depth", 1);
        args.put("lanterns", false);

        generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        assertTrue(placer.hasAnyBlockOfType(MaterialPresets.STONE_CASTLE.getRoofTrim()),
                "Should have decorative trim on top");
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
