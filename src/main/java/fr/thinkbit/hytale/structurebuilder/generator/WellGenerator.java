package fr.thinkbit.hytale.structurebuilder.generator;

import fr.thinkbit.hytale.structurebuilder.material.MaterialPalette;

import java.util.Map;

public class WellGenerator implements StructureGenerator {

    @Override
    public String getType() {
        return "well";
    }

    @Override
    public StructureResult generate(Map<String, Object> args, MaterialPalette palette, BlockPlacer placer) {
        long start = System.currentTimeMillis();

        int cx = getInt(args, "x");
        int y = getInt(args, "y");
        int cz = getInt(args, "z");
        int radius = getInt(args, "radius", 2);
        int wallHeight = getInt(args, "wallHeight", 3);
        boolean roof = getBool(args, "roof", true);
        int roofHeight = getInt(args, "roofHeight", 3);
        int shaftDepth = getInt(args, "depth", 5);

        String wallBlock = palette.getWall();
        String accentBlock = palette.getWallAccent();
        String foundationBlock = palette.getFoundation();
        String roofBlock = palette.getRoof();
        String trimBlock = palette.getRoofTrim();
        String lanternBlock = palette.getLantern();

        int minY = y - shaftDepth;
        int maxY = y;

        // Shaft floor
        int r2 = (radius - 1) * (radius - 1);
        for (int dx = -(radius - 1); dx <= (radius - 1); dx++) {
            for (int dz = -(radius - 1); dz <= (radius - 1); dz++) {
                if (dx * dx + dz * dz <= r2) {
                    placer.setBlock(cx + dx, y - shaftDepth, cz + dz, foundationBlock);
                }
            }
        }

        // Shaft walls (underground)
        int wallR2 = radius * radius;
        int innerR2 = (radius - 1) * (radius - 1);
        for (int h = -(shaftDepth - 1); h < 0; h++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    int dist2 = dx * dx + dz * dz;
                    if (dist2 <= wallR2 && dist2 > innerR2) {
                        placer.setBlock(cx + dx, y + h, cz + dz, wallBlock);
                    }
                }
            }
            // Clear interior
            for (int dx = -(radius - 1); dx <= (radius - 1); dx++) {
                for (int dz = -(radius - 1); dz <= (radius - 1); dz++) {
                    if (dx * dx + dz * dz <= innerR2) {
                        placer.setBlock(cx + dx, y + h, cz + dz, MaterialPalette.AIR);
                    }
                }
            }
        }

        // Above-ground wall ring
        for (int h = 0; h < wallHeight; h++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    int dist2 = dx * dx + dz * dz;
                    if (dist2 <= wallR2 && dist2 > innerR2) {
                        placer.setBlock(cx + dx, y + h, cz + dz, wallBlock);
                    }
                }
            }
            maxY = Math.max(maxY, y + h);
        }

        // Roof structure
        if (roof) {
            int roofBaseY = y + wallHeight;

            // 4 support posts at corners
            int[][] posts = {
                {cx - radius, cz - radius},
                {cx + radius, cz - radius},
                {cx - radius, cz + radius},
                {cx + radius, cz + radius}
            };
            for (int[] post : posts) {
                for (int h = 0; h < roofHeight; h++) {
                    placer.setBlock(post[0], roofBaseY + h, post[1], accentBlock);
                }
            }

            // Pyramid roof (narrows upward)
            for (int layer = 0; layer <= roofHeight; layer++) {
                int roofR = radius + 1 - layer;
                if (roofR < 0) break;
                int roofY = roofBaseY + roofHeight - 1 + layer;
                for (int dx = -roofR; dx <= roofR; dx++) {
                    for (int dz = -roofR; dz <= roofR; dz++) {
                        boolean isEdge = Math.abs(dx) == roofR || Math.abs(dz) == roofR;
                        placer.setBlock(cx + dx, roofY, cz + dz, isEdge ? trimBlock : roofBlock);
                    }
                }
                maxY = Math.max(maxY, roofY);
            }

            // Lantern hanging from roof center
            placer.setBlock(cx, roofBaseY + roofHeight - 2, cz, lanternBlock);
        }

        long elapsed = System.currentTimeMillis() - start;
        return new StructureResult(
                placer.getBlockCount(),
                cx - radius, minY, cz - radius,
                cx + radius, maxY, cz + radius,
                elapsed, "well"
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
