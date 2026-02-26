package fr.thinkbit.hytale.structurebuilder.generator;

import fr.thinkbit.hytale.structurebuilder.material.MaterialPalette;

import java.util.Map;

public class RoadGenerator implements StructureGenerator {

    @Override
    public String getType() {
        return "road";
    }

    @Override
    public StructureResult generate(Map<String, Object> args, MaterialPalette palette, BlockPlacer placer) {
        long start = System.currentTimeMillis();

        int x = getInt(args, "x");
        int y = getInt(args, "y");
        int z = getInt(args, "z");
        int length = getInt(args, "length", 20);
        int width = getInt(args, "width", 5);
        String direction = getString(args, "direction", "x");
        boolean borders = getBool(args, "borders", true);
        boolean lanterns = getBool(args, "lanterns", false);
        int lanternSpacing = getInt(args, "lanternSpacing", 8);

        String floorBlock = palette.getFloor();
        String foundationBlock = palette.getFoundation();
        String accentBlock = palette.getWallAccent();
        String lanternBlock = palette.getLantern();

        int maxX = x, maxY = y, maxZ = z;

        // Road surface
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
                maxX = Math.max(maxX, bx);
                maxZ = Math.max(maxZ, bz);
            }
        }

        // Border curbs
        if (borders) {
            for (int l = 0; l < length; l++) {
                // Alternating block types for visual interest
                String borderBlock = (l % 2 == 0) ? foundationBlock : accentBlock;

                int bx1, bz1, bx2, bz2;
                if ("x".equals(direction)) {
                    bx1 = x + l; bz1 = z - 1;
                    bx2 = x + l; bz2 = z + width;
                } else {
                    bx1 = x - 1; bz1 = z + l;
                    bx2 = x + width; bz2 = z + l;
                }
                placer.setBlock(bx1, y, bz1, borderBlock);
                placer.setBlock(bx2, y, bz2, borderBlock);
                maxX = Math.max(maxX, Math.max(bx1, bx2));
                maxZ = Math.max(maxZ, Math.max(bz1, bz2));
            }
        }

        // Street lanterns along one side
        if (lanterns) {
            for (int l = 0; l < length; l += lanternSpacing) {
                int postX, postZ;
                if ("x".equals(direction)) {
                    postX = x + l;
                    postZ = z - 1;
                    if (borders) postZ = z - 2;
                } else {
                    postX = x - 1;
                    if (borders) postX = x - 2;
                    postZ = z + l;
                }

                // Post (3 blocks tall)
                placer.setBlock(postX, y + 1, postZ, accentBlock);
                placer.setBlock(postX, y + 2, postZ, accentBlock);
                placer.setBlock(postX, y + 3, postZ, lanternBlock);
                maxY = Math.max(maxY, y + 3);
            }
        }

        long elapsed = System.currentTimeMillis() - start;
        return new StructureResult(
                placer.getBlockCount(),
                borders ? (("x".equals(direction)) ? x : x - 1) : x,
                y,
                borders ? (("x".equals(direction)) ? z - 1 : z) : z,
                maxX, maxY, maxZ,
                elapsed, "road"
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
