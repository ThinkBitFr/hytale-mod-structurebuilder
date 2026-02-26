package fr.thinkbit.hytale.structurebuilder.generator;

import fr.thinkbit.hytale.structurebuilder.material.MaterialPalette;
import fr.thinkbit.hytale.structurebuilder.material.MaterialPresets;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HouseGeneratorTest {

    private final HouseGenerator generator = new HouseGenerator();

    @Test
    void getType() {
        assertEquals("house", generator.getType());
    }

    @Test
    void generateDefaultHouse() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 64);
        args.put("z", 0);

        StructureResult result = generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        assertEquals("house", result.getStructureType());
        assertTrue(result.getBlocksPlaced() > 100, "House should have many blocks");
    }

    @Test
    void hasFoundation() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 0);
        args.put("z", 0);
        args.put("width", 5);
        args.put("depth", 5);

        generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        // Foundation layer at y=0
        for (int dx = 0; dx < 5; dx++) {
            for (int dz = 0; dz < 5; dz++) {
                assertTrue(placer.hasBlockAt(dx, 0, dz, "Rock_Stone_Cobble"),
                        "Foundation at " + dx + ",0," + dz);
            }
        }
    }

    @Test
    void hasWalls() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 0);
        args.put("z", 0);
        args.put("width", 6);
        args.put("depth", 6);
        args.put("floorHeight", 4);
        args.put("windows", false);
        args.put("furniture", false);

        generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        // Walls at edges (y=1 to y=4)
        assertTrue(placer.hasBlockAt(0, 1, 0), "Wall corner at 0,1,0");
        assertTrue(placer.hasBlockAt(5, 1, 0), "Wall corner at 5,1,0");
        assertTrue(placer.hasBlockAt(0, 1, 5), "Wall corner at 0,1,5");
        assertTrue(placer.hasBlockAt(5, 1, 5), "Wall corner at 5,1,5");
        // Interior should be empty (no walls, no furniture)
        assertFalse(placer.hasBlockAt(2, 1, 2), "Interior at 2,1,2 should be empty");
    }

    @Test
    void cornerPillarsUseAccent() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 0);
        args.put("z", 0);
        args.put("width", 6);
        args.put("depth", 6);
        args.put("windows", false);

        generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        // Corners should be accent (Wood_Softwood_Beam for STONE_CASTLE)
        assertTrue(placer.hasBlockAt(0, 1, 0, "Wood_Softwood_Beam"), "Corner accent at 0,1,0");
        assertTrue(placer.hasBlockAt(5, 1, 5, "Wood_Softwood_Beam"), "Corner accent at 5,1,5");
    }

    @Test
    void hasDoor() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 0);
        args.put("z", 0);
        args.put("width", 10);
        args.put("depth", 8);
        args.put("doorSide", "south");

        generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        // Door at south wall center: x=5, z=7 (depth-1), y=1-3
        int doorX = 5; // width/2
        int doorZ = 7; // depth-1
        assertTrue(placer.hasBlockAt(doorX, 1, doorZ, MaterialPalette.AIR), "Door bottom");
        assertTrue(placer.hasBlockAt(doorX, 2, doorZ, MaterialPalette.AIR), "Door middle");
        assertTrue(placer.hasBlockAt(doorX, 3, doorZ, MaterialPalette.AIR), "Door top");
    }

    @Test
    void hasWindowsWhenEnabled() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 0);
        args.put("z", 0);
        args.put("width", 10);
        args.put("depth", 8);
        args.put("windows", true);

        generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        // Windows are AIR blocks at y=2,3 on walls
        long airBlocks = placer.getBlocks().stream()
                .filter(b -> b.blockType().equals(MaterialPalette.AIR))
                .count();
        assertTrue(airBlocks > 3, "Should have window and door openings");
    }

    @Test
    void noWindowsWhenDisabled() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 0);
        args.put("z", 0);
        args.put("width", 10);
        args.put("depth", 8);
        args.put("windows", false);

        generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        // Only door openings (3 air blocks) + door step
        long airBlocks = placer.getBlocks().stream()
                .filter(b -> b.blockType().equals(MaterialPalette.AIR))
                .count();
        assertEquals(3, airBlocks, "Only door opening should be air");
    }

    @Test
    void hasFurnitureWhenEnabled() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 0);
        args.put("z", 0);
        args.put("width", 10);
        args.put("depth", 8);
        args.put("furniture", true);

        generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        assertTrue(placer.hasAnyBlockOfType("Furniture_Lumberjack_Table"), "Should have table");
        assertTrue(placer.hasAnyBlockOfType("Furniture_Lumberjack_Chair"), "Should have chair");
        assertTrue(placer.hasAnyBlockOfType("Furniture_Lumberjack_Bed"), "Should have bed");
        assertTrue(placer.hasAnyBlockOfType("Furniture_Lumberjack_Lantern"), "Should have lantern");
        assertTrue(placer.hasAnyBlockOfType("Furniture_Lumberjack_Chest_Large"), "Should have chest");
    }

    @Test
    void noFurnitureWhenDisabled() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 0);
        args.put("z", 0);
        args.put("width", 10);
        args.put("depth", 8);
        args.put("furniture", false);

        generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        assertFalse(placer.hasAnyBlockOfType("Furniture_Lumberjack_Table"), "No table");
        assertFalse(placer.hasAnyBlockOfType("Furniture_Lumberjack_Bed"), "No bed");
    }

    @Test
    void flatRoof() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 0);
        args.put("z", 0);
        args.put("width", 6);
        args.put("depth", 6);
        args.put("floorHeight", 3);
        args.put("roofStyle", "flat");
        args.put("furniture", false);
        args.put("windows", false);

        StructureResult result = generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        // Flat roof: roofBaseY = 0 + 1 + 3 = 4
        // All blocks at y=4 should exist
        int roofY = 4;
        assertTrue(placer.hasBlockAt(0, roofY, 0), "Flat roof corner");
        assertTrue(placer.hasBlockAt(3, roofY, 3), "Flat roof center");
    }

    @Test
    void gableRoof() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 0);
        args.put("z", 0);
        args.put("width", 8);
        args.put("depth", 8);
        args.put("floorHeight", 3);
        args.put("roofStyle", "gable");
        args.put("furniture", false);
        args.put("windows", false);

        StructureResult result = generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        // Gable roof should extend above walls
        assertTrue(result.getMaxY() > 4, "Gable roof should be higher than walls");
    }

    @Test
    void hipRoof() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 0);
        args.put("z", 0);
        args.put("width", 8);
        args.put("depth", 8);
        args.put("floorHeight", 3);
        args.put("roofStyle", "hip");
        args.put("furniture", false);
        args.put("windows", false);

        StructureResult result = generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        // Hip roof should extend above walls
        assertTrue(result.getMaxY() > 4, "Hip roof should be higher than walls");
    }

    @Test
    void multiFloorHouse() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 0);
        args.put("z", 0);
        args.put("width", 10);
        args.put("depth", 8);
        args.put("floors", 2);
        args.put("floorHeight", 4);
        args.put("roofStyle", "flat");
        args.put("furniture", false);
        args.put("windows", false);

        StructureResult result = generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        // Floor slab between levels at y = 0 + 1 + 4 = 5
        assertTrue(placer.hasBlockAt(2, 5, 2, "Wood_Softwood_Planks"), "Inter-floor slab");

        // Should have ladder blocks for multi-floor
        assertTrue(placer.hasAnyBlockOfType("Furniture_Lumberjack_Ladder"), "Should have ladder");
    }

    @Test
    void doorStep() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 0);
        args.put("z", 0);
        args.put("width", 10);
        args.put("depth", 8);
        args.put("doorSide", "south");
        args.put("furniture", false);

        generator.generate(args, MaterialPresets.STONE_CASTLE, placer);

        // Step at south: x=width/2=5, z=depth=8, y=0
        assertTrue(placer.hasBlockAt(5, 0, 8, "Rock_Stone_Cobble"), "Door step");
    }

    @Test
    void differentMaterials() {
        var placer = new RecordingBlockPlacer();
        Map<String, Object> args = new HashMap<>();
        args.put("x", 0);
        args.put("y", 0);
        args.put("z", 0);
        args.put("width", 6);
        args.put("depth", 6);
        args.put("furniture", false);
        args.put("windows", false);

        generator.generate(args, MaterialPresets.RUSTIC_WOOD, placer);

        // Rustic wood: walls = Wood_Softwood_Planks, foundation = Rock_Stone_Cobble
        assertTrue(placer.hasAnyBlockOfType("Wood_Softwood_Planks"), "Rustic wood walls");
        assertTrue(placer.hasAnyBlockOfType("Rock_Stone_Cobble"), "Rustic wood foundation");
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
