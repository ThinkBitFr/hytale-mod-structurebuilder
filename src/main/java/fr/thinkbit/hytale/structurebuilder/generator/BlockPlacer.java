package fr.thinkbit.hytale.structurebuilder.generator;

public interface BlockPlacer {
    void setBlock(int x, int y, int z, String blockType);
    int getBlockCount();
}
