package fr.thinkbit.hytale.structurebuilder.generator;

import fr.thinkbit.hytale.structurebuilder.material.MaterialPalette;
import fr.thinkbit.hytale.structurebuilder.material.MaterialPresets;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WellGeneratorTest {

    private final WellGenerator generator = new WellGenerator();

    @Test
    void getType() {
        assertEquals("well", generator.getType());
    }

    @Test
    void basicWellNoRoof() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 10);
        args.put("z", 0);
        args.put("radius", 2);
        args.put("wallHeight", 2);
        args.put("roof", false);
        args.put("depth", 3);

        StructureResult result = generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        assertEquals("well", result.getStructureType());
        assertTrue(result.getBlocksPlaced() > 0);
    }

    @Test
    void hasShaftFloor() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 10);
        args.put("z", 0);
        args.put("radius", 2);
        args.put("wallHeight", 2);
        args.put("roof", false);
        args.put("depth", 5);

        generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        // Shaft floor at y = 10 - 5 = 5
        assertTrue(placer.hasBlockAt(0, 5, 0, MaterialPresets.STONE_CASTLE.getFoundation()),
                "Shaft floor at center");
    }

    @Test
    void shaftInteriorIsAir() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 10);
        args.put("z", 0);
        args.put("radius", 2);
        args.put("wallHeight", 2);
        args.put("roof", false);
        args.put("depth", 3);

        generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        // Interior at y=8 (underground) should be air
        assertTrue(placer.hasBlockAt(0, 8, 0, MaterialPalette.AIR),
                "Shaft interior should be cleared");
    }

    @Test
    void aboveGroundWall() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 10);
        args.put("z", 0);
        args.put("radius", 2);
        args.put("wallHeight", 3);
        args.put("roof", false);
        args.put("depth", 1);

        generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        // Wall ring at y=10 (ground level)
        assertTrue(placer.hasBlockAt(2, 10, 0, MaterialPresets.STONE_CASTLE.getWall()),
                "Above-ground wall at edge");
        // y=12 should be last wall layer
        assertTrue(placer.hasBlockAt(2, 12, 0, MaterialPresets.STONE_CASTLE.getWall()),
                "Wall at top");
    }

    @Test
    void withRoof() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 10);
        args.put("z", 0);
        args.put("radius", 2);
        args.put("wallHeight", 3);
        args.put("roof", true);
        args.put("roofHeight", 3);
        args.put("depth", 3);

        StructureResult result = generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        // Roof should add support posts and pyramid
        assertTrue(placer.hasAnyBlockOfType(MaterialPresets.STONE_CASTLE.getRoof()),
                "Should have roof blocks");
        assertTrue(placer.hasAnyBlockOfType(MaterialPresets.STONE_CASTLE.getWallAccent()),
                "Should have support post accent blocks");
    }

    @Test
    void roofHasLantern() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 10);
        args.put("z", 0);
        args.put("radius", 2);
        args.put("wallHeight", 3);
        args.put("roof", true);
        args.put("roofHeight", 3);
        args.put("depth", 3);

        generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        assertTrue(placer.hasAnyBlockOfType(MaterialPresets.STONE_CASTLE.getLantern()),
                "Should have hanging lantern");
    }

    @Test
    void boundingBox() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 50);
        args.put("y", 20);
        args.put("z", 50);
        args.put("radius", 3);
        args.put("wallHeight", 2);
        args.put("roof", false);
        args.put("depth", 4);

        StructureResult result = generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        assertEquals(47, result.getMinX());  // 50 - 3
        assertEquals(53, result.getMaxX());  // 50 + 3
        assertEquals(47, result.getMinZ());  // 50 - 3
        assertEquals(53, result.getMaxZ());  // 50 + 3
        assertEquals(16, result.getMinY());  // 20 - 4
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
