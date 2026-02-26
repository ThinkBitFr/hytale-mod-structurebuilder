package fr.thinkbit.hytale.structurebuilder.generator;

import fr.thinkbit.hytale.structurebuilder.material.MaterialPalette;

import java.util.Map;

public class PlatformGenerator implements StructureGenerator {

    @Override
    public String getType() {
        return "platform";
    }

    @Override
    public StructureResult generate(Map<String, Object> args, MaterialPalette palette, BlockPlacer placer) {
        long start = System.currentTimeMillis();

        int x = getInt(args, "x");
        int y = getInt(args, "y");
        int z = getInt(args, "z");
        int width = getInt(args, "width", 10);
        int depth = getInt(args, "depth", 10);
        int thickness = getInt(args, "thickness", 1);

        String block = palette.getFoundation();

        for (int dy = 0; dy < thickness; dy++) {
            for (int dx = 0; dx < width; dx++) {
                for (int dz = 0; dz < depth; dz++) {
                    placer.setBlock(x + dx, y + dy, z + dz, block);
                }
            }
        }

        long elapsed = System.currentTimeMillis() - start;
        return new StructureResult(
                placer.getBlockCount(),
                x, y, z,
                x + width - 1, y + thickness - 1, z + depth - 1,
                elapsed, "platform"
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
}
