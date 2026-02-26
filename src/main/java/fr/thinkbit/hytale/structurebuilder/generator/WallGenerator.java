package fr.thinkbit.hytale.structurebuilder.generator;

import fr.thinkbit.hytale.structurebuilder.material.MaterialPalette;

import java.util.Map;

public class WallGenerator implements StructureGenerator {

    @Override
    public String getType() {
        return "wall";
    }

    @Override
    public StructureResult generate(Map<String, Object> args, MaterialPalette palette, BlockPlacer placer) {
        long start = System.currentTimeMillis();

        int x = getInt(args, "x");
        int y = getInt(args, "y");
        int z = getInt(args, "z");
        int length = getInt(args, "length", 10);
        int height = getInt(args, "height", 5);
        int thickness = getInt(args, "thickness", 1);
        String direction = getString(args, "direction", "x"); // "x" or "z"
        boolean battlements = getBool(args, "battlements", true);

        String wallBlock = palette.getWall();
        String accentBlock = palette.getWallAccent();

        int maxX = x, maxY = y, maxZ = z;

        // Build the main wall
        for (int h = 0; h < height; h++) {
            for (int l = 0; l < length; l++) {
                for (int t = 0; t < thickness; t++) {
                    int bx, by, bz;
                    if ("x".equals(direction)) {
                        bx = x + l;
                        by = y + h;
                        bz = z + t;
                    } else {
                        bx = x + t;
                        by = y + h;
                        bz = z + l;
                    }
                    // Corner pillars use accent
                    boolean isCorner = (l == 0 || l == length - 1);
                    placer.setBlock(bx, by, bz, isCorner ? accentBlock : wallBlock);
                    maxX = Math.max(maxX, bx);
                    maxY = Math.max(maxY, by);
                    maxZ = Math.max(maxZ, bz);
                }
            }
        }

        // Battlements (merlons) on top
        if (battlements) {
            int battlementY = y + height;
            for (int l = 0; l < length; l++) {
                // Merlon every other block
                if (l % 2 == 0) {
                    for (int t = 0; t < thickness; t++) {
                        int bx, bz;
                        if ("x".equals(direction)) {
                            bx = x + l;
                            bz = z + t;
                        } else {
                            bx = x + t;
                            bz = z + l;
                        }
                        placer.setBlock(bx, battlementY, bz, wallBlock);
                        maxX = Math.max(maxX, bx);
                        maxY = Math.max(maxY, battlementY);
                        maxZ = Math.max(maxZ, bz);
                    }
                }
            }
        }

        long elapsed = System.currentTimeMillis() - start;
        return new StructureResult(
                placer.getBlockCount(),
                x, y, z,
                maxX, maxY, maxZ,
                elapsed, "wall"
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
