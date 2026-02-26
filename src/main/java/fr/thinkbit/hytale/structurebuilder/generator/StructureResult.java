package fr.thinkbit.hytale.structurebuilder.generator;

import com.google.gson.JsonObject;

public class StructureResult {

    private final int blocksPlaced;
    private final int minX, minY, minZ;
    private final int maxX, maxY, maxZ;
    private final long buildTimeMs;
    private final String structureType;

    public StructureResult(int blocksPlaced, int minX, int minY, int minZ,
                           int maxX, int maxY, int maxZ, long buildTimeMs, String structureType) {
        this.blocksPlaced = blocksPlaced;
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.buildTimeMs = buildTimeMs;
        this.structureType = structureType;
    }

    public int getBlocksPlaced() { return blocksPlaced; }
    public int getMinX() { return minX; }
    public int getMinY() { return minY; }
    public int getMinZ() { return minZ; }
    public int getMaxX() { return maxX; }
    public int getMaxY() { return maxY; }
    public int getMaxZ() { return maxZ; }
    public long getBuildTimeMs() { return buildTimeMs; }
    public String getStructureType() { return structureType; }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("status", "success");
        json.addProperty("structureType", structureType);
        json.addProperty("blocksPlaced", blocksPlaced);
        json.addProperty("buildTimeMs", buildTimeMs);
        JsonObject bbox = new JsonObject();
        bbox.addProperty("minX", minX);
        bbox.addProperty("minY", minY);
        bbox.addProperty("minZ", minZ);
        bbox.addProperty("maxX", maxX);
        bbox.addProperty("maxY", maxY);
        bbox.addProperty("maxZ", maxZ);
        json.add("boundingBox", bbox);
        return json;
    }
}
