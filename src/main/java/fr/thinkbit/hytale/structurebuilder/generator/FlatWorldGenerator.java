package fr.thinkbit.hytale.structurebuilder.generator;

import fr.thinkbit.hytale.structurebuilder.material.MaterialPalette;

import java.util.Map;

/**
 * Generates a flat world area with proper terrain layers:
 * - Deep layer: stone
 * - Middle layer: dirt
 * - Surface: dirt/grass
 * - Above: air (clears any existing structures)
 */
public class FlatWorldGenerator implements StructureGenerator {

    // Default terrain blocks
    public static final String DEFAULT_STONE = "Rock_Stone_Cobble";
    public static final String DEFAULT_DIRT = "Soil_Dirt";
    public static final String DEFAULT_SURFACE = "Soil_Dirt";
    public static final String AIR = "Empty";

    @Override
    public String getType() {
        return "flat_world";
    }

    @Override
    public StructureResult generate(Map<String, Object> args, MaterialPalette palette, BlockPlacer placer) {
        long start = System.currentTimeMillis();

        int centerX = getInt(args, "x");
        int centerZ = getInt(args, "z");
        int radius = getInt(args, "radius", 100);
        int surfaceY = getInt(args, "surfaceY", 64);
        int depth = getInt(args, "depth", 10);        // how deep below surface to fill
        int clearHeight = getInt(args, "clearHeight", 60); // how high above surface to clear
        String stoneBlock = getString(args, "stoneBlock", DEFAULT_STONE);
        String dirtBlock = getString(args, "dirtBlock", DEFAULT_DIRT);
        String surfaceBlock = getString(args, "surfaceBlock", DEFAULT_SURFACE);

        int x1 = centerX - radius;
        int x2 = centerX + radius;
        int z1 = centerZ - radius;
        int z2 = centerZ + radius;

        int bottomY = surfaceY - depth;
        int topY = surfaceY + clearHeight;

        // Dirt layer thickness (top 3 blocks below surface)
        int dirtThickness = Math.min(3, depth);
        int dirtStartY = surfaceY - dirtThickness;

        for (int x = x1; x <= x2; x++) {
            for (int z = z1; z <= z2; z++) {
                // Stone layer (deep)
                for (int y = bottomY; y < dirtStartY; y++) {
                    placer.setBlock(x, y, z, stoneBlock);
                }

                // Dirt layer (sub-surface)
                for (int y = dirtStartY; y < surfaceY; y++) {
                    placer.setBlock(x, y, z, dirtBlock);
                }

                // Surface layer
                placer.setBlock(x, surfaceY, z, surfaceBlock);

                // Clear above (remove structures, trees, etc.)
                for (int y = surfaceY + 1; y <= topY; y++) {
                    placer.setBlock(x, y, z, AIR);
                }
            }
        }

        long elapsed = System.currentTimeMillis() - start;
        return new StructureResult(
                placer.getBlockCount(),
                x1, bottomY, z1,
                x2, topY, z2,
                elapsed, "flat_world"
        );
    }

    private int getInt(Map<String, Object> args, String key) {
        Object val = args.get(key);
        if (val == null) throw new IllegalArgumentException("Missing required parameter: " + key);
        return ((Number) val).intValue();
    }

    private int getInt(Map<String, Object> args, String key, int defaultValue) {
        Object val = args.get(key);
        if (val == null) return defaultValue;
        return ((Number) val).intValue();
    }

    private String getString(Map<String, Object> args, String key, String defaultValue) {
        Object val = args.get(key);
        if (val == null) return defaultValue;
        return val.toString();
    }
}
