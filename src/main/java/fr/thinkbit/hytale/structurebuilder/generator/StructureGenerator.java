package fr.thinkbit.hytale.structurebuilder.generator;

import fr.thinkbit.hytale.structurebuilder.material.MaterialPalette;

import java.util.Map;

public interface StructureGenerator {
    String getType();
    StructureResult generate(Map<String, Object> args, MaterialPalette palette, BlockPlacer placer);
}
