package fr.thinkbit.hytale.structurebuilder.material;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MaterialPresetsTest {

    @Test
    void rusticWoodPreset() {
        MaterialPalette p = MaterialPresets.RUSTIC_WOOD;
        assertNotNull(p);
        assertEquals("rustic_wood", p.getName());
        assertEquals("Rock_Stone_Cobble", p.getFoundation());
        assertEquals("Wood_Softwood_Planks", p.getWall());
        assertEquals("Wood_Softwood_Beam", p.getWallAccent());
        assertEquals("Wood_Softwood_Planks", p.getFloor());
        assertEquals("Wood_Softwood_Planks", p.getRoof());
        assertEquals("Wood_Softwood_Beam", p.getRoofTrim());
    }

    @Test
    void stoneCastlePreset() {
        MaterialPalette p = MaterialPresets.STONE_CASTLE;
        assertNotNull(p);
        assertEquals("stone_castle", p.getName());
        assertEquals("Rock_Stone_Cobble", p.getFoundation());
        assertEquals("Rock_Stone_Brick", p.getWall());
        assertEquals("Wood_Softwood_Beam", p.getWallAccent());
    }

    @Test
    void cobblestonePreset() {
        MaterialPalette p = MaterialPresets.COBBLESTONE;
        assertNotNull(p);
        assertEquals("cobblestone", p.getName());
        assertEquals("Rock_Stone_Cobble", p.getFoundation());
        assertEquals("Rock_Stone_Cobble", p.getWall());
        assertEquals("Rock_Stone_Brick", p.getWallAccent());
    }

    @Test
    void getByName() {
        assertEquals(MaterialPresets.RUSTIC_WOOD, MaterialPresets.getByName("rustic_wood"));
        assertEquals(MaterialPresets.STONE_CASTLE, MaterialPresets.getByName("stone_castle"));
        assertEquals(MaterialPresets.COBBLESTONE, MaterialPresets.getByName("cobblestone"));
        assertNull(MaterialPresets.getByName("nonexistent"));
    }

    @Test
    void getDefault() {
        assertEquals(MaterialPresets.STONE_CASTLE, MaterialPresets.getDefault());
    }

    @Test
    void getAvailablePresets() {
        List<String> presets = MaterialPresets.getAvailablePresets();
        assertEquals(3, presets.size());
        assertTrue(presets.contains("rustic_wood"));
        assertTrue(presets.contains("stone_castle"));
        assertTrue(presets.contains("cobblestone"));
    }

    @Test
    void paletteHasFurnitureDefaults() {
        MaterialPalette p = MaterialPresets.STONE_CASTLE;
        assertEquals("Furniture_Lumberjack_Table", p.getTable());
        assertEquals("Furniture_Lumberjack_Chair", p.getChair());
        assertEquals("Furniture_Lumberjack_Bed", p.getBed());
        assertEquals("Furniture_Lumberjack_Lantern", p.getLantern());
        assertEquals("Furniture_Lumberjack_Chest_Large", p.getChest());
        assertEquals("Furniture_Lumberjack_Wardrobe", p.getWardrobe());
        assertEquals("Furniture_Lumberjack_Ladder", p.getLadder());
    }

    @Test
    void airConstant() {
        assertEquals("Empty", MaterialPalette.AIR);
    }
}
