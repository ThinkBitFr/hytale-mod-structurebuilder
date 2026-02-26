package fr.thinkbit.hytale.structurebuilder.generator;

import fr.thinkbit.hytale.structurebuilder.material.MaterialPalette;
import fr.thinkbit.hytale.structurebuilder.material.MaterialPresets;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TowerGeneratorTest {

    private final TowerGenerator generator = new TowerGenerator();

    @Test
    void getType() {
        assertEquals("tower", generator.getType());
    }

    @Test
    void generateSquareTower() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 50);
        args.put("y", 64);
        args.put("z", 50);
        args.put("radius", 3);
        args.put("height", 8);
        args.put("shape", "square");

        StructureResult result = generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        assertEquals("tower", result.getStructureType());
        assertTrue(result.getBlocksPlaced() > 0);
        assertEquals(47, result.getMinX()); // cx - radius
        assertEquals(53, result.getMaxX()); // cx + radius
        assertEquals(47, result.getMinZ());
        assertEquals(53, result.getMaxZ());
    }

    @Test
    void generateRoundTower() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 50);
        args.put("y", 64);
        args.put("z", 50);
        args.put("radius", 4);
        args.put("height", 10);
        args.put("shape", "round");

        StructureResult result = generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        assertEquals("tower", result.getStructureType());
        assertTrue(result.getBlocksPlaced() > 0);
        // Round tower should not fill corners
        assertFalse(placer.hasBlockAt(46, 65, 46)); // far corner outside radius
    }

    @Test
    void hasDoorOpening() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 50);
        args.put("y", 64);
        args.put("z", 50);
        args.put("radius", 3);
        args.put("height", 6);
        args.put("shape", "square");

        generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        // Door is at south side (cx, y+1..y+2, cz+radius)
        assertTrue(placer.hasBlockAt(50, 65, 53, MaterialPalette.AIR));
        assertTrue(placer.hasBlockAt(50, 66, 53, MaterialPalette.AIR));
    }

    @Test
    void hasBattlements() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 50);
        args.put("y", 64);
        args.put("z", 50);
        args.put("radius", 3);
        args.put("height", 5);
        args.put("shape", "square");
        args.put("battlements", true);

        StructureResult result = generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        // Roof at y=64+5+1=70, battlements at y=71
        int batY = 64 + 5 + 1 + 1;
        // Some battlement blocks should exist at that height
        boolean anyBattlement = placer.getBlocks().stream()
                .anyMatch(b -> b.y() == batY && !b.blockType().equals(MaterialPalette.AIR));
        assertTrue(anyBattlement, "Should have battlement blocks at y=" + batY);
    }

    @Test
    void noBattlements() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 50);
        args.put("y", 64);
        args.put("z", 50);
        args.put("radius", 3);
        args.put("height", 5);
        args.put("shape", "square");
        args.put("battlements", false);

        StructureResult result = generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        // Without battlements, max Y should be roof (y + height + 1)
        assertEquals(64 + 5 + 1, result.getMaxY());
    }

    @Test
    void usesCorrectMaterials() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 0);
        args.put("z", 0);
        args.put("radius", 2);
        args.put("height", 3);
        args.put("shape", "square");
        args.put("battlements", false);

        generator.generate(args, MaterialPresets.RUSTIC_WOOD, placer);

        var types = placer.getBlockTypesUsed();
        // Should use foundation, wall, floor blocks + air for door
        assertTrue(types.contains("Rock_Stone_Cobble"), "Should contain foundation block");
        assertTrue(types.contains("Wood_Softwood_Planks"), "Should contain wall/floor block");
    }

    @Test
    void hasFoundationLayer() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 10);
        args.put("y", 0);
        args.put("z", 10);
        args.put("radius", 2);
        args.put("height", 3);
        args.put("shape", "square");
        args.put("battlements", false);

        generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        // Foundation at y=0 should be filled
        assertTrue(placer.hasBlockAt(10, 0, 10, "Rock_Stone_Cobble"));
    }

    @Test
    void missingRequiredParameter() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        // missing y, z

        assertThrows(IllegalArgumentException.class,
                () -> generator.generate(args, MaterialPresets.STONE_CASTLE, placer));
    }
}
