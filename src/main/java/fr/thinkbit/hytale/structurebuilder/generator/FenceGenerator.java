package fr.thinkbit.hytale.structurebuilder.generator;

import fr.thinkbit.hytale.structurebuilder.material.MaterialPalette;

import java.util.Map;

public class FenceGenerator implements StructureGenerator {

    @Override
    public String getType() {
        return "fence";
    }

    @Override
    public StructureResult generate(Map<String, Object> args, MaterialPalette palette, BlockPlacer placer) {
        long start = System.currentTimeMillis();

        int x = getInt(args, "x");
        int y = getInt(args, "y");
        int z = getInt(args, "z");
        int width = getInt(args, "width", 20);   // X dimension
        int depth = getInt(args, "depth", 20);    // Z dimension
        int height = getInt(args, "height", 2);
        boolean gate = getBool(args, "gate", true);
        String gateSide = getString(args, "gateSide", "south");
        int gateWidth = getInt(args, "gateWidth", 3);
        boolean posts = getBool(args, "posts", true);

        String wallBlock = palette.getWall();
        String accentBlock = palette.getWallAccent();
        String lanternBlock = palette.getLantern();
        String foundationBlock = palette.getFoundation();

        int maxY = y;

        // Gate position calculation
        int gateStartX = x + width / 2 - gateWidth / 2;
        int gateEndX = gateStartX + gateWidth - 1;
        int gateStartZ = z + depth / 2 - gateWidth / 2;
        int gateEndZ = gateStartZ + gateWidth - 1;

        // Build perimeter
        for (int h = 0; h < height; h++) {
            int by = y + h;

            // North wall (z = z)
            for (int dx = 0; dx < width; dx++) {
                if (gate && "north".equals(gateSide) && (x + dx) >= gateStartX && (x + dx) <= gateEndX) continue;
                placer.setBlock(x + dx, by, z, wallBlock);
            }

            // South wall (z = z + depth - 1)
            for (int dx = 0; dx < width; dx++) {
                if (gate && "south".equals(gateSide) && (x + dx) >= gateStartX && (x + dx) <= gateEndX) continue;
                placer.setBlock(x + dx, by, z + depth - 1, wallBlock);
            }

            // West wall (x = x)
            for (int dz = 1; dz < depth - 1; dz++) {
                if (gate && "west".equals(gateSide) && (z + dz) >= gateStartZ && (z + dz) <= gateEndZ) continue;
                placer.setBlock(x, by, z + dz, wallBlock);
            }

            // East wall (x = x + width - 1)
            for (int dz = 1; dz < depth - 1; dz++) {
                if (gate && "east".equals(gateSide) && (z + dz) >= gateStartZ && (z + dz) <= gateEndZ) continue;
                placer.setBlock(x + width - 1, by, z + dz, wallBlock);
            }

            maxY = Math.max(maxY, by);
        }

        // Corner posts (taller, accent material)
        if (posts) {
            int[][] corners = {
                {x, z}, {x + width - 1, z},
                {x, z + depth - 1}, {x + width - 1, z + depth - 1}
            };
            for (int[] corner : corners) {
                for (int h = 0; h <= height; h++) {
                    placer.setBlock(corner[0], y + h, corner[1], accentBlock);
                }
                maxY = Math.max(maxY, y + height);
            }
        }

        // Gate frame posts with lanterns
        if (gate) {
            int gatePostHeight = height + 1;
            int[][] gatePosts;
            switch (gateSide) {
                case "north" -> gatePosts = new int[][]{
                    {gateStartX - 1, z}, {gateEndX + 1, z}
                };
                case "east" -> gatePosts = new int[][]{
                    {x + width - 1, gateStartZ - 1}, {x + width - 1, gateEndZ + 1}
                };
                case "west" -> gatePosts = new int[][]{
                    {x, gateStartZ - 1}, {x, gateEndZ + 1}
                };
                default -> gatePosts = new int[][]{
                    {gateStartX - 1, z + depth - 1}, {gateEndX + 1, z + depth - 1}
                };
            }

            for (int[] post : gatePosts) {
                for (int h = 0; h <= gatePostHeight; h++) {
                    placer.setBlock(post[0], y + h, post[1], accentBlock);
                }
                // Lantern on top
                placer.setBlock(post[0], y + gatePostHeight + 1, post[1], lanternBlock);
                maxY = Math.max(maxY, y + gatePostHeight + 1);
            }

            // Gate threshold (foundation block at ground level)
            switch (gateSide) {
                case "north" -> {
                    for (int gx = gateStartX; gx <= gateEndX; gx++)
                        placer.setBlock(gx, y, z, foundationBlock);
                }
                case "south" -> {
                    for (int gx = gateStartX; gx <= gateEndX; gx++)
                        placer.setBlock(gx, y, z + depth - 1, foundationBlock);
                }
                case "west" -> {
                    for (int gz = gateStartZ; gz <= gateEndZ; gz++)
                        placer.setBlock(x, y, gz, foundationBlock);
                }
                case "east" -> {
                    for (int gz = gateStartZ; gz <= gateEndZ; gz++)
                        placer.setBlock(x + width - 1, y, gz, foundationBlock);
                }
            }
        }

        long elapsed = System.currentTimeMillis() - start;
        return new StructureResult(
                placer.getBlockCount(),
                x, y, z,
                x + width - 1, maxY, z + depth - 1,
                elapsed, "fence"
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
