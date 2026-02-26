package fr.thinkbit.hytale.structurebuilder.generator;

import fr.thinkbit.hytale.structurebuilder.material.MaterialPalette;

import java.util.Map;

public class TowerGenerator implements StructureGenerator {

    @Override
    public String getType() {
        return "tower";
    }

    @Override
    public StructureResult generate(Map<String, Object> args, MaterialPalette palette, BlockPlacer placer) {
        long start = System.currentTimeMillis();

        int cx = getInt(args, "x");
        int y = getInt(args, "y");
        int cz = getInt(args, "z");
        int radius = getInt(args, "radius", 4);
        int height = getInt(args, "height", 10);
        String shape = getString(args, "shape", "round"); // "round" or "square"
        boolean battlements = getBool(args, "battlements", true);

        String wallBlock = palette.getWall();
        String accentBlock = palette.getWallAccent();
        String floorBlock = palette.getFloor();

        int minX = cx - radius, maxX = cx + radius;
        int minZ = cz - radius, maxZ = cz + radius;
        int maxY = y;

        // Foundation layer
        placeLayer(placer, cx, y, cz, radius, shape, palette.getFoundation(), true);

        // Walls
        for (int h = 1; h <= height; h++) {
            placeLayer(placer, cx, y + h, cz, radius, shape, wallBlock, false);
            maxY = y + h;
        }

        // Floor at mid-height (interior platform)
        int midFloor = y + height / 2;
        placeLayer(placer, cx, midFloor, cz, radius - 1, shape, floorBlock, true);

        // Roof / top floor
        int roofY = y + height + 1;
        placeLayer(placer, cx, roofY, cz, radius, shape, floorBlock, true);
        maxY = roofY;

        // Battlements
        if (battlements) {
            int batY = roofY + 1;
            placeBattlements(placer, cx, batY, cz, radius, shape, accentBlock);
            maxY = batY;
        }

        // Door opening (south side)
        placer.setBlock(cx, y + 1, cz + radius, MaterialPalette.AIR);
        placer.setBlock(cx, y + 2, cz + radius, MaterialPalette.AIR);

        long elapsed = System.currentTimeMillis() - start;
        return new StructureResult(
                placer.getBlockCount(),
                minX, y, minZ,
                maxX, maxY, maxZ,
                elapsed, "tower"
        );
    }

    private void placeLayer(BlockPlacer placer, int cx, int y, int cz, int radius,
                            String shape, String block, boolean filled) {
        if ("square".equals(shape)) {
            placeSquareLayer(placer, cx, y, cz, radius, block, filled);
        } else {
            placeCircleLayer(placer, cx, y, cz, radius, block, filled);
        }
    }

    private void placeSquareLayer(BlockPlacer placer, int cx, int y, int cz, int radius,
                                  String block, boolean filled) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (filled || Math.abs(dx) == radius || Math.abs(dz) == radius) {
                    placer.setBlock(cx + dx, y, cz + dz, block);
                }
            }
        }
    }

    private void placeCircleLayer(BlockPlacer placer, int cx, int y, int cz, int radius,
                                  String block, boolean filled) {
        int r2 = radius * radius;
        int inner2 = (radius - 1) * (radius - 1);
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                int dist2 = dx * dx + dz * dz;
                if (dist2 <= r2) {
                    if (filled || dist2 > inner2) {
                        placer.setBlock(cx + dx, y, cz + dz, block);
                    }
                }
            }
        }
    }

    private void placeBattlements(BlockPlacer placer, int cx, int y, int cz, int radius,
                                  String shape, String block) {
        if ("square".equals(shape)) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) == radius || Math.abs(dz) == radius) {
                        // Merlon every other block
                        if ((dx + dz + radius * 2) % 2 == 0) {
                            placer.setBlock(cx + dx, y, cz + dz, block);
                        }
                    }
                }
            }
        } else {
            int r2 = radius * radius;
            int inner2 = (radius - 1) * (radius - 1);
            int i = 0;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    int dist2 = dx * dx + dz * dz;
                    if (dist2 <= r2 && dist2 > inner2) {
                        if (i % 2 == 0) {
                            placer.setBlock(cx + dx, y, cz + dz, block);
                        }
                        i++;
                    }
                }
            }
        }
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
