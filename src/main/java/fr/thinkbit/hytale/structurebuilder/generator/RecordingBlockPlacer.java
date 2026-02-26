package fr.thinkbit.hytale.structurebuilder.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RecordingBlockPlacer implements BlockPlacer {

    public record PlacedBlock(int x, int y, int z, String blockType) {}

    private final List<PlacedBlock> blocks = new ArrayList<>();

    @Override
    public void setBlock(int x, int y, int z, String blockType) {
        blocks.add(new PlacedBlock(x, y, z, blockType));
    }

    @Override
    public int getBlockCount() {
        return blocks.size();
    }

    public List<PlacedBlock> getBlocks() {
        return Collections.unmodifiableList(blocks);
    }

    public boolean hasBlockAt(int x, int y, int z, String blockType) {
        return blocks.stream().anyMatch(b -> b.x == x && b.y == y && b.z == z && b.blockType.equals(blockType));
    }

    public boolean hasBlockAt(int x, int y, int z) {
        return blocks.stream().anyMatch(b -> b.x == x && b.y == y && b.z == z);
    }

    public boolean hasAnyBlockOfType(String blockType) {
        return blocks.stream().anyMatch(b -> b.blockType.equals(blockType));
    }

    public Set<String> getBlockTypesUsed() {
        return blocks.stream().map(PlacedBlock::blockType).collect(Collectors.toSet());
    }

    public long countBlocksOfType(String blockType) {
        return blocks.stream().filter(b -> b.blockType.equals(blockType)).count();
    }
}
