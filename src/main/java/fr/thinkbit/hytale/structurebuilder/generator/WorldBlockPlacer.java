package fr.thinkbit.hytale.structurebuilder.generator;

import com.hypixel.hytale.server.core.universe.world.World;

public class WorldBlockPlacer implements BlockPlacer {

    private final World world;
    private int count;

    public WorldBlockPlacer(World world) {
        this.world = world;
    }

    @Override
    public void setBlock(int x, int y, int z, String blockType) {
        world.setBlock(x, y, z, blockType);
        count++;
    }

    @Override
    public int getBlockCount() {
        return count;
    }
}
