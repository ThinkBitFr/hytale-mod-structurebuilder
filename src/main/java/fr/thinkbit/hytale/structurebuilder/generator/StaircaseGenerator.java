package fr.thinkbit.hytale.structurebuilder.generator;

import fr.thinkbit.hytale.structurebuilder.material.MaterialPalette;

import java.util.Map;

public class StaircaseGenerator implements StructureGenerator {

    @Override
    public String getType() {
        return "staircase";
    }

    @Override
    public StructureResult generate(Map<String, Object> args, MaterialPalette palette, BlockPlacer placer) {
        long start = System.currentTimeMillis();

        int x = getInt(args, "x");
        int y = getInt(args, "y");
        int z = getInt(args, "z");
        int height = getInt(args, "height", 8);
        int width = getInt(args, "width", 3);
        String style = getString(args, "style", "straight");
        String direction = getString(args, "direction", "x");
        boolean railings = getBool(args, "railings", true);

        String floorBlock = palette.getFloor();
        String wallBlock = palette.getWall();
        String accentBlock = palette.getWallAccent();

        int minX = x, minY = y, minZ = z;
        int maxX = x, maxY = y, maxZ = z;

        if ("spiral".equals(style)) {
            // Spiral staircase around a central column
            int cx = x + 2; // center of spiral
            int cz = z + 2;

            // Central column
            for (int h = 0; h <= height; h++) {
                placer.setBlock(cx, y + h, cz, wallBlock);
                maxY = Math.max(maxY, y + h);
            }

            // 4 directions: +x, +z, -x, -z
            int[][] offsets = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};

            for (int step = 0; step < height; step++) {
                int dir = step % 4;
                int stepY = y + step;
                int sx = cx + offsets[dir][0];
                int sz = cz + offsets[dir][1];

                // Step block
                placer.setBlock(sx, stepY, sz, floorBlock);
                // Extended step for width
                int perpDir = (dir + 1) % 4;
                for (int w = 1; w < Math.min(width - 1, 2); w++) {
                    placer.setBlock(sx + offsets[perpDir][0] * w, stepY,
                            sz + offsets[perpDir][1] * w, floorBlock);
                }

                minX = Math.min(minX, sx - 1); maxX = Math.max(maxX, sx + 1);
                minZ = Math.min(minZ, sz - 1); maxZ = Math.max(maxZ, sz + 1);
                maxY = Math.max(maxY, stepY);
            }

            // Landing platform at top
            int topY = y + height;
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dz == 0) continue; // skip column
                    placer.setBlock(cx + dx, topY, cz + dz, floorBlock);
                }
            }
            maxY = Math.max(maxY, topY);
            minX = Math.min(minX, cx - 1); maxX = Math.max(maxX, cx + 1);
            minZ = Math.min(minZ, cz - 1); maxZ = Math.max(maxZ, cz + 1);

        } else {
            // Straight staircase
            for (int step = 0; step < height; step++) {
                int stepY = y + step;

                for (int w = 0; w < width; w++) {
                    int bx, bz;
                    if ("x".equals(direction)) {
                        bx = x + step;
                        bz = z + w;
                    } else {
                        bx = x + w;
                        bz = z + step;
                    }

                    // Step surface
                    placer.setBlock(bx, stepY, bz, floorBlock);

                    // Fill below step for solid appearance
                    for (int fill = y; fill < stepY; fill++) {
                        placer.setBlock(bx, fill, bz, wallBlock);
                    }

                    maxX = Math.max(maxX, bx);
                    maxZ = Math.max(maxZ, bz);
                }
                maxY = Math.max(maxY, stepY);
            }

            // Railings on edges
            if (railings) {
                for (int step = 0; step < height; step++) {
                    int stepY = y + step;
                    int bx1, bz1, bx2, bz2;
                    if ("x".equals(direction)) {
                        bx1 = x + step; bz1 = z;
                        bx2 = x + step; bz2 = z + width - 1;
                    } else {
                        bx1 = x; bz1 = z + step;
                        bx2 = x + width - 1; bz2 = z + step;
                    }
                    placer.setBlock(bx1, stepY + 1, bz1, accentBlock);
                    placer.setBlock(bx2, stepY + 1, bz2, accentBlock);
                    maxY = Math.max(maxY, stepY + 1);
                }
            }
        }

        long elapsed = System.currentTimeMillis() - start;
        return new StructureResult(
                placer.getBlockCount(),
                minX, minY, minZ,
                maxX, maxY, maxZ,
                elapsed, "staircase"
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
