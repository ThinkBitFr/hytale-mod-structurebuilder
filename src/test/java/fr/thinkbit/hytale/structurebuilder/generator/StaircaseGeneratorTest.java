package fr.thinkbit.hytale.structurebuilder.generator;

import fr.thinkbit.hytale.structurebuilder.material.MaterialPresets;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class StaircaseGeneratorTest {

    private final StaircaseGenerator generator = new StaircaseGenerator();

    @Test
    void getType() {
        assertEquals("staircase", generator.getType());
    }

    @Test
    void straightStaircaseNoRailings() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 0);
        args.put("z", 0);
        args.put("height", 4);
        args.put("width", 2);
        args.put("style", "straight");
        args.put("railings", false);

        StructureResult result = generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        assertEquals("staircase", result.getStructureType());
        assertTrue(result.getBlocksPlaced() > 0);

        // Step 0: 2 blocks at y=0 (no fill below)
        assertTrue(placer.hasBlockAt(0, 0, 0));
        assertTrue(placer.hasBlockAt(0, 0, 1));

        // Step 3: at x=3, y=3
        assertTrue(placer.hasBlockAt(3, 3, 0));
        assertTrue(placer.hasBlockAt(3, 3, 1));
    }

    @Test
    void straightStaircaseFillBelow() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 0);
        args.put("z", 0);
        args.put("height", 3);
        args.put("width", 1);
        args.put("style", "straight");
        args.put("railings", false);

        generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        // Step 2 at x=2, y=2: should fill y=0 and y=1 below it
        assertTrue(placer.hasBlockAt(2, 0, 0), "Fill at y=0 below step 2");
        assertTrue(placer.hasBlockAt(2, 1, 0), "Fill at y=1 below step 2");
        assertTrue(placer.hasBlockAt(2, 2, 0), "Step surface at y=2");
    }

    @Test
    void straightWithRailings() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 0);
        args.put("z", 0);
        args.put("height", 3);
        args.put("width", 3);
        args.put("style", "straight");
        args.put("railings", true);

        generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        // Railings are 1 above each step, on the edges (z=0 and z=2)
        assertTrue(placer.hasBlockAt(0, 1, 0), "Railing at step 0, left");
        assertTrue(placer.hasBlockAt(0, 1, 2), "Railing at step 0, right");
        assertTrue(placer.hasBlockAt(2, 3, 0), "Railing at step 2, left");
        assertTrue(placer.hasBlockAt(2, 3, 2), "Railing at step 2, right");
    }

    @Test
    void spiralStaircase() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 0);
        args.put("z", 0);
        args.put("height", 8);
        args.put("width", 3);
        args.put("style", "spiral");

        StructureResult result = generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        assertEquals("staircase", result.getStructureType());
        assertTrue(result.getBlocksPlaced() > 0);

        // Central column at (2, 2) should exist
        assertTrue(placer.hasBlockAt(2, 0, 2), "Central column at y=0");
        assertTrue(placer.hasBlockAt(2, 8, 2), "Central column at top");

        // Landing platform at top (y=8)
        assertTrue(placer.hasBlockAt(1, 8, 1), "Landing block");
    }

    @Test
    void directionZ() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 0);
        args.put("z", 0);
        args.put("height", 3);
        args.put("width", 2);
        args.put("style", "straight");
        args.put("direction", "z");
        args.put("railings", false);

        generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        // Steps should extend along Z axis
        assertTrue(placer.hasBlockAt(0, 0, 0));
        assertTrue(placer.hasBlockAt(0, 2, 2));
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
