package fr.thinkbit.hytale.structurebuilder.material;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class MaterialPresets {

    private static final Map<String, MaterialPalette> PRESETS = new LinkedHashMap<>();

    public static final MaterialPalette RUSTIC_WOOD = register(new MaterialPalette.Builder("rustic_wood")
            .foundation("Rock_Stone_Cobble")
            .wall("Wood_Softwood_Planks")
            .wallAccent("Wood_Softwood_Beam")
            .floor("Wood_Softwood_Planks")
            .ceiling("Wood_Softwood_Planks")
            .roof("Wood_Softwood_Planks")
            .roofTrim("Wood_Softwood_Beam")
            .build());

    public static final MaterialPalette STONE_CASTLE = register(new MaterialPalette.Builder("stone_castle")
            .foundation("Rock_Stone_Cobble")
            .wall("Rock_Stone_Brick")
            .wallAccent("Wood_Softwood_Beam")
            .floor("Wood_Softwood_Planks")
            .ceiling("Wood_Softwood_Planks")
            .roof("Wood_Softwood_Planks")
            .roofTrim("Wood_Softwood_Beam")
            .build());

    public static final MaterialPalette COBBLESTONE = register(new MaterialPalette.Builder("cobblestone")
            .foundation("Rock_Stone_Cobble")
            .wall("Rock_Stone_Cobble")
            .wallAccent("Rock_Stone_Brick")
            .floor("Rock_Stone_Cobble")
            .ceiling("Rock_Stone_Cobble")
            .roof("Rock_Stone_Cobble")
            .roofTrim("Rock_Stone_Brick")
            .build());

    private MaterialPresets() {}

    private static MaterialPalette register(MaterialPalette palette) {
        PRESETS.put(palette.getName(), palette);
        return palette;
    }

    public static MaterialPalette getByName(String name) {
        return PRESETS.get(name);
    }

    public static MaterialPalette getDefault() {
        return STONE_CASTLE;
    }

    public static List<String> getAvailablePresets() {
        return List.copyOf(PRESETS.keySet());
    }
}
