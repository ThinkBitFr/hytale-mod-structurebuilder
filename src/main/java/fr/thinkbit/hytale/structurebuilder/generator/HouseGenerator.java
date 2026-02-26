package fr.thinkbit.hytale.structurebuilder.generator;

import fr.thinkbit.hytale.structurebuilder.material.MaterialPalette;

import java.util.Map;

public class HouseGenerator implements StructureGenerator {

    @Override
    public String getType() {
        return "house";
    }

    @Override
    public StructureResult generate(Map<String, Object> args, MaterialPalette palette, BlockPlacer placer) {
        long start = System.currentTimeMillis();

        int x = getInt(args, "x");
        int y = getInt(args, "y");
        int z = getInt(args, "z");
        int width = getInt(args, "width", 10);       // X dimension
        int depth = getInt(args, "depth", 8);         // Z dimension
        int floors = getInt(args, "floors", 1);
        int floorHeight = getInt(args, "floorHeight", 4); // wall height per floor (not counting floor slab)
        String roofStyle = getString(args, "roofStyle", "gable"); // flat, gable, hip
        boolean furniture = getBool(args, "furniture", true);
        boolean windows = getBool(args, "windows", true);
        String doorSide = getString(args, "doorSide", "south"); // north, south, east, west

        int maxY = y;

        // 1. Foundation
        for (int dx = 0; dx < width; dx++) {
            for (int dz = 0; dz < depth; dz++) {
                placer.setBlock(x + dx, y, z + dz, palette.getFoundation());
            }
        }

        // 2. Build each floor
        int currentY = y + 1;
        for (int floor = 0; floor < floors; floor++) {
            // Walls
            buildFloorWalls(placer, x, currentY, z, width, depth, floorHeight, palette);

            // Windows
            if (windows) {
                buildWindows(placer, x, currentY, z, width, depth, floorHeight);
            }

            // Door (ground floor only)
            if (floor == 0) {
                buildDoor(placer, x, currentY, z, width, depth, doorSide);
            }

            // Floor/ceiling slab (between floors, not below first floor)
            if (floor < floors - 1) {
                int slabY = currentY + floorHeight;
                for (int dx = 0; dx < width; dx++) {
                    for (int dz = 0; dz < depth; dz++) {
                        placer.setBlock(x + dx, slabY, z + dz, palette.getFloor());
                    }
                }
                maxY = Math.max(maxY, slabY);
            }

            // Furniture (ground floor)
            if (furniture && floor == 0) {
                buildFurniture(placer, x, currentY, z, width, depth, palette);
            }

            // Ladder for multi-floor
            if (floors > 1) {
                for (int h = 0; h < floorHeight; h++) {
                    placer.setBlock(x + width - 2, currentY + h, z + 1, palette.getLadder());
                }
            }

            maxY = Math.max(maxY, currentY + floorHeight - 1);
            currentY += floorHeight + 1; // +1 for the floor slab
        }

        // Adjust currentY: top of the last floor walls
        int roofBaseY = currentY - 1; // the row above the last wall block
        // Actually: last floor starts at (y + 1 + floor*(floorHeight+1))
        // Top wall block = lastFloorStart + floorHeight - 1
        // Roof base = top wall block + 1
        int lastFloorStart = y + 1 + (floors - 1) * (floorHeight + 1);
        roofBaseY = lastFloorStart + floorHeight;

        // 3. Roof
        maxY = buildRoof(placer, x, roofBaseY, z, width, depth, roofStyle, palette);

        // 4. Door step
        buildDoorStep(placer, x, y, z, width, depth, doorSide, palette);

        long elapsed = System.currentTimeMillis() - start;
        return new StructureResult(
                placer.getBlockCount(),
                x - 1, y, z - 1, // step can extend 1 block out
                x + width, maxY, z + depth,
                elapsed, "house"
        );
    }

    private void buildFloorWalls(BlockPlacer placer, int x, int baseY, int z,
                                 int width, int depth, int height, MaterialPalette palette) {
        String wall = palette.getWall();
        String accent = palette.getWallAccent();

        for (int h = 0; h < height; h++) {
            int by = baseY + h;
            for (int dx = 0; dx < width; dx++) {
                for (int dz = 0; dz < depth; dz++) {
                    boolean isEdgeX = (dx == 0 || dx == width - 1);
                    boolean isEdgeZ = (dz == 0 || dz == depth - 1);
                    if (isEdgeX || isEdgeZ) {
                        // Corner pillars
                        boolean isCorner = isEdgeX && isEdgeZ;
                        placer.setBlock(x + dx, by, z + dz, isCorner ? accent : wall);
                    }
                }
            }
        }
    }

    private void buildWindows(BlockPlacer placer, int x, int baseY, int z,
                              int width, int depth, int height) {
        // Windows at height 1-2 relative to floor base
        int winLow = baseY + 1;
        int winHigh = baseY + 2;

        if (height < 3) return; // too short for windows

        // North wall (z=0): windows spaced along X
        for (int dx = 2; dx < width - 2; dx += 3) {
            placer.setBlock(x + dx, winLow, z, MaterialPalette.AIR);
            placer.setBlock(x + dx, winHigh, z, MaterialPalette.AIR);
        }
        // South wall (z=depth-1)
        for (int dx = 2; dx < width - 2; dx += 3) {
            placer.setBlock(x + dx, winLow, z + depth - 1, MaterialPalette.AIR);
            placer.setBlock(x + dx, winHigh, z + depth - 1, MaterialPalette.AIR);
        }
        // West wall (x=0)
        for (int dz = 2; dz < depth - 2; dz += 3) {
            placer.setBlock(x, winLow, z + dz, MaterialPalette.AIR);
            placer.setBlock(x, winHigh, z + dz, MaterialPalette.AIR);
        }
        // East wall (x=width-1)
        for (int dz = 2; dz < depth - 2; dz += 3) {
            placer.setBlock(x + width - 1, winLow, z + dz, MaterialPalette.AIR);
            placer.setBlock(x + width - 1, winHigh, z + dz, MaterialPalette.AIR);
        }
    }

    private void buildDoor(BlockPlacer placer, int x, int baseY, int z,
                           int width, int depth, String side) {
        int doorX, doorZ;
        switch (side) {
            case "north" -> { doorX = x + width / 2; doorZ = z; }
            case "east" -> { doorX = x + width - 1; doorZ = z + depth / 2; }
            case "west" -> { doorX = x; doorZ = z + depth / 2; }
            default -> { doorX = x + width / 2; doorZ = z + depth - 1; } // south
        }
        // 1x3 door opening
        placer.setBlock(doorX, baseY, doorZ, MaterialPalette.AIR);
        placer.setBlock(doorX, baseY + 1, doorZ, MaterialPalette.AIR);
        placer.setBlock(doorX, baseY + 2, doorZ, MaterialPalette.AIR);
    }

    private void buildDoorStep(BlockPlacer placer, int x, int y, int z,
                               int width, int depth, String side, MaterialPalette palette) {
        int stepX, stepZ;
        switch (side) {
            case "north" -> { stepX = x + width / 2; stepZ = z - 1; }
            case "east" -> { stepX = x + width; stepZ = z + depth / 2; }
            case "west" -> { stepX = x - 1; stepZ = z + depth / 2; }
            default -> { stepX = x + width / 2; stepZ = z + depth; } // south
        }
        placer.setBlock(stepX, y, stepZ, palette.getFoundation());
    }

    private void buildFurniture(BlockPlacer placer, int x, int baseY, int z,
                                int width, int depth, MaterialPalette palette) {
        // Place furniture in the interior (offset 2 from walls)
        int interiorX = x + 2;
        int interiorZ = z + 2;

        // Table + chairs in center-ish area
        if (width >= 6 && depth >= 6) {
            placer.setBlock(interiorX, baseY, interiorZ, palette.getTable());
            placer.setBlock(interiorX + 1, baseY, interiorZ, palette.getChair());
            placer.setBlock(interiorX - 1, baseY, interiorZ, palette.getChair());
        }

        // Bed in corner
        if (width >= 6 && depth >= 6) {
            placer.setBlock(x + width - 3, baseY, z + depth - 3, palette.getBed());
        }

        // Lantern
        placer.setBlock(interiorX, baseY, z + depth - 3, palette.getLantern());

        // Chest
        if (width >= 8) {
            placer.setBlock(x + 1, baseY, z + 1, palette.getChest());
        }
    }

    private int buildRoof(BlockPlacer placer, int x, int roofY, int z,
                          int width, int depth, String style, MaterialPalette palette) {
        String roof = palette.getRoof();
        String trim = palette.getRoofTrim();
        int maxY = roofY;

        switch (style) {
            case "flat" -> {
                // Simple flat roof
                for (int dx = 0; dx < width; dx++) {
                    for (int dz = 0; dz < depth; dz++) {
                        boolean isEdge = dx == 0 || dx == width - 1 || dz == 0 || dz == depth - 1;
                        placer.setBlock(x + dx, roofY, z + dz, isEdge ? trim : roof);
                    }
                }
                maxY = roofY;
            }
            case "hip" -> {
                // Hip roof: shrinks on both X and Z
                int offsetX = 0, offsetZ = 0;
                int curWidth = width, curDepth = depth;
                int layer = 0;
                while (curWidth > 0 && curDepth > 0) {
                    for (int dx = 0; dx < curWidth; dx++) {
                        for (int dz = 0; dz < curDepth; dz++) {
                            boolean isEdge = dx == 0 || dx == curWidth - 1 || dz == 0 || dz == curDepth - 1;
                            placer.setBlock(x + offsetX + dx, roofY + layer, z + offsetZ + dz,
                                    isEdge ? trim : roof);
                        }
                    }
                    maxY = roofY + layer;
                    offsetX++;
                    offsetZ++;
                    curWidth -= 2;
                    curDepth -= 2;
                    layer++;
                }
            }
            default -> {
                // Gable roof: steps along Z axis, ridge runs along X
                int halfDepth = depth / 2;
                for (int layer = 0; layer <= halfDepth; layer++) {
                    int layerY = roofY + layer;
                    int zStart = z + layer;
                    int zEnd = z + depth - 1 - layer;
                    for (int dx = 0; dx < width; dx++) {
                        boolean isEdge = dx == 0 || dx == width - 1;
                        if (zStart <= zEnd) {
                            placer.setBlock(x + dx, layerY, zStart, isEdge ? trim : roof);
                            if (zStart != zEnd) {
                                placer.setBlock(x + dx, layerY, zEnd, isEdge ? trim : roof);
                            }
                        }
                    }
                    maxY = layerY;

                    // Gable end walls (triangular fill at x=0 and x=width-1)
                    if (layer > 0 && zStart < zEnd) {
                        for (int dz = zStart + 1; dz < zEnd; dz++) {
                            placer.setBlock(x, layerY, dz, palette.getWall());
                            placer.setBlock(x + width - 1, layerY, dz, palette.getWall());
                        }
                    }
                }
            }
        }

        return maxY;
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
