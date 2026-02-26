package fr.thinkbit.hytale.structurebuilder.generator;

import fr.thinkbit.hytale.structurebuilder.material.MaterialPalette;

import java.util.Map;

public class ArchGenerator implements StructureGenerator {

    @Override
    public String getType() {
        return "arch";
    }

    @Override
    public StructureResult generate(Map<String, Object> args, MaterialPalette palette, BlockPlacer placer) {
        long start = System.currentTimeMillis();

        int x = getInt(args, "x");
        int y = getInt(args, "y");
        int z = getInt(args, "z");
        int archWidth = getInt(args, "width", 5);      // opening width
        int archHeight = getInt(args, "height", 7);     // total height including curve
        int archDepth = getInt(args, "depth", 2);       // thickness of the arch
        String direction = getString(args, "direction", "z"); // arch spans across this axis
        boolean lanterns = getBool(args, "lanterns", true);

        String wallBlock = palette.getWall();
        String accentBlock = palette.getWallAccent();
        String trimBlock = palette.getRoofTrim();
        String lanternBlock = palette.getLantern();

        // Pillar height = total height minus the curved top portion
        int halfW = archWidth / 2;
        int pillarHeight = archHeight - halfW - 1;
        if (pillarHeight < 1) pillarHeight = 1;

        int minX = x, minY = y, minZ = z;
        int maxX = x, maxY = y, maxZ = z;

        // Build depending on direction
        // "z" means arch spans in X direction, passage goes along Z
        // "x" means arch spans in Z direction, passage goes along X

        for (int d = 0; d < archDepth; d++) {
            // Left pillar
            for (int h = 0; h < pillarHeight; h++) {
                int bx, bz;
                if ("z".equals(direction)) {
                    bx = x;
                    bz = z + d;
                } else {
                    bx = x + d;
                    bz = z;
                }
                placer.setBlock(bx, y + h, bz, accentBlock);
                minX = Math.min(minX, bx); maxX = Math.max(maxX, bx);
                minZ = Math.min(minZ, bz); maxZ = Math.max(maxZ, bz);
                maxY = Math.max(maxY, y + h);
            }

            // Right pillar
            for (int h = 0; h < pillarHeight; h++) {
                int bx, bz;
                if ("z".equals(direction)) {
                    bx = x + archWidth - 1;
                    bz = z + d;
                } else {
                    bx = x + d;
                    bz = z + archWidth - 1;
                }
                placer.setBlock(bx, y + h, bz, accentBlock);
                maxX = Math.max(maxX, bx);
                maxZ = Math.max(maxZ, bz);
                maxY = Math.max(maxY, y + h);
            }

            // Arch curve (semicircle)
            for (int i = 0; i < archWidth; i++) {
                double dx = i - halfW;
                double curveHeight = Math.sqrt(Math.max(0, halfW * halfW - dx * dx));
                int blockY = pillarHeight + (int) Math.round(curveHeight);

                int bx, bz;
                if ("z".equals(direction)) {
                    bx = x + i;
                    bz = z + d;
                } else {
                    bx = x + d;
                    bz = z + i;
                }
                placer.setBlock(bx, y + blockY, bz, wallBlock);
                maxX = Math.max(maxX, bx);
                maxZ = Math.max(maxZ, bz);
                maxY = Math.max(maxY, y + blockY);

                // Fill pillars up to the curve on edges
                if (i == 0 || i == archWidth - 1) {
                    for (int h = pillarHeight; h < blockY; h++) {
                        placer.setBlock(bx, y + h, bz, accentBlock);
                    }
                }
            }

            // Top decorative trim row
            int topY = maxY + 1;
            for (int i = 0; i < archWidth; i++) {
                int bx, bz;
                if ("z".equals(direction)) {
                    bx = x + i;
                    bz = z + d;
                } else {
                    bx = x + d;
                    bz = z + i;
                }
                placer.setBlock(bx, topY, bz, trimBlock);
            }
            maxY = topY;
        }

        // Lanterns on pillar tops
        if (lanterns) {
            int lanternY = pillarHeight; // on top of pillars
            if ("z".equals(direction)) {
                placer.setBlock(x, y + lanternY, z - 1, lanternBlock);
                placer.setBlock(x + archWidth - 1, y + lanternY, z - 1, lanternBlock);
                minZ = Math.min(minZ, z - 1);
            } else {
                placer.setBlock(x - 1, y + lanternY, z, lanternBlock);
                placer.setBlock(x - 1, y + lanternY, z + archWidth - 1, lanternBlock);
                minX = Math.min(minX, x - 1);
            }
            maxY = Math.max(maxY, y + lanternY);
        }

        long elapsed = System.currentTimeMillis() - start;
        return new StructureResult(
                placer.getBlockCount(),
                minX, minY, minZ,
                maxX, maxY, maxZ,
                elapsed, "arch"
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
