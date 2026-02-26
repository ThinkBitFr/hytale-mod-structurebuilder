package fr.thinkbit.hytale.structurebuilder.generator;

import fr.thinkbit.hytale.structurebuilder.material.MaterialPalette;

import java.util.Map;

public class BridgeGenerator implements StructureGenerator {

    @Override
    public String getType() {
        return "bridge";
    }

    @Override
    public StructureResult generate(Map<String, Object> args, MaterialPalette palette, BlockPlacer placer) {
        long start = System.currentTimeMillis();

        int x = getInt(args, "x");
        int y = getInt(args, "y");
        int z = getInt(args, "z");
        int length = getInt(args, "length", 15);
        int width = getInt(args, "width", 3);
        String direction = getString(args, "direction", "x");
        boolean railings = getBool(args, "railings", true);
        boolean supports = getBool(args, "supports", true);
        int supportSpacing = getInt(args, "supportSpacing", 5);
        int supportDepth = getInt(args, "supportDepth", 5);

        String floorBlock = palette.getFloor();
        String accentBlock = palette.getWallAccent();
        String foundationBlock = palette.getFoundation();

        int minX = x, minY = y, minZ = z;
        int maxX = x, maxY = y, maxZ = z;

        // Build deck
        for (int l = 0; l < length; l++) {
            for (int w = 0; w < width; w++) {
                int bx, bz;
                if ("x".equals(direction)) {
                    bx = x + l;
                    bz = z + w;
                } else {
                    bx = x + w;
                    bz = z + l;
                }
                placer.setBlock(bx, y, bz, floorBlock);
                minX = Math.min(minX, bx); maxX = Math.max(maxX, bx);
                minZ = Math.min(minZ, bz); maxZ = Math.max(maxZ, bz);
            }
        }

        // Railings on edges
        if (railings) {
            for (int l = 0; l < length; l++) {
                int bx1, bz1, bx2, bz2;
                if ("x".equals(direction)) {
                    bx1 = x + l; bz1 = z;
                    bx2 = x + l; bz2 = z + width - 1;
                } else {
                    bx1 = x; bz1 = z + l;
                    bx2 = x + width - 1; bz2 = z + l;
                }
                placer.setBlock(bx1, y + 1, bz1, accentBlock);
                placer.setBlock(bx2, y + 1, bz2, accentBlock);
                maxY = Math.max(maxY, y + 1);
            }
        }

        // Support pillars
        if (supports) {
            for (int l = 0; l < length; l += supportSpacing) {
                // Place supports at both edges
                for (int d = 1; d <= supportDepth; d++) {
                    int bx1, bz1, bx2, bz2;
                    if ("x".equals(direction)) {
                        bx1 = x + l; bz1 = z;
                        bx2 = x + l; bz2 = z + width - 1;
                    } else {
                        bx1 = x; bz1 = z + l;
                        bx2 = x + width - 1; bz2 = z + l;
                    }
                    placer.setBlock(bx1, y - d, bz1, foundationBlock);
                    placer.setBlock(bx2, y - d, bz2, foundationBlock);
                    minY = Math.min(minY, y - d);
                }
            }
        }

        long elapsed = System.currentTimeMillis() - start;
        return new StructureResult(
                placer.getBlockCount(),
                minX, minY, minZ,
                maxX, maxY, maxZ,
                elapsed, "bridge"
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

    private boolean getBool(Map<String, Object> args, String key, boolean defaultValue) {
        Object val = args.get(key);
        if (val == null) return defaultValue;
        if (val instanceof Boolean b) return b;
        return Boolean.parseBoolean(val.toString());
    }
}
