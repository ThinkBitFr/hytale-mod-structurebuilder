package fr.thinkbit.hytale.structurebuilder.material;

public class MaterialPalette {

    private final String name;
    private final String foundation;
    private final String wall;
    private final String wallAccent;
    private final String floor;
    private final String ceiling;
    private final String roof;
    private final String roofTrim;
    private final String table;
    private final String chair;
    private final String bed;
    private final String lantern;
    private final String chest;
    private final String wardrobe;
    private final String ladder;

    public static final String AIR = "Empty";

    private MaterialPalette(Builder b) {
        this.name = b.name;
        this.foundation = b.foundation;
        this.wall = b.wall;
        this.wallAccent = b.wallAccent;
        this.floor = b.floor;
        this.ceiling = b.ceiling;
        this.roof = b.roof;
        this.roofTrim = b.roofTrim;
        this.table = b.table;
        this.chair = b.chair;
        this.bed = b.bed;
        this.lantern = b.lantern;
        this.chest = b.chest;
        this.wardrobe = b.wardrobe;
        this.ladder = b.ladder;
    }

    public String getName() { return name; }
    public String getFoundation() { return foundation; }
    public String getWall() { return wall; }
    public String getWallAccent() { return wallAccent; }
    public String getFloor() { return floor; }
    public String getCeiling() { return ceiling; }
    public String getRoof() { return roof; }
    public String getRoofTrim() { return roofTrim; }
    public String getTable() { return table; }
    public String getChair() { return chair; }
    public String getBed() { return bed; }
    public String getLantern() { return lantern; }
    public String getChest() { return chest; }
    public String getWardrobe() { return wardrobe; }
    public String getLadder() { return ladder; }

    public static class Builder {
        private final String name;
        private String foundation = "Rock_Stone_Cobble";
        private String wall = "Rock_Stone_Brick";
        private String wallAccent = "Wood_Softwood_Beam";
        private String floor = "Wood_Softwood_Planks";
        private String ceiling = "Wood_Softwood_Planks";
        private String roof = "Wood_Softwood_Planks";
        private String roofTrim = "Wood_Softwood_Beam";
        private String table = "Furniture_Lumberjack_Table";
        private String chair = "Furniture_Lumberjack_Chair";
        private String bed = "Furniture_Lumberjack_Bed";
        private String lantern = "Furniture_Lumberjack_Lantern";
        private String chest = "Furniture_Lumberjack_Chest_Large";
        private String wardrobe = "Furniture_Lumberjack_Wardrobe";
        private String ladder = "Furniture_Lumberjack_Ladder";

        public Builder(String name) { this.name = name; }
        public Builder foundation(String v) { this.foundation = v; return this; }
        public Builder wall(String v) { this.wall = v; return this; }
        public Builder wallAccent(String v) { this.wallAccent = v; return this; }
        public Builder floor(String v) { this.floor = v; return this; }
        public Builder ceiling(String v) { this.ceiling = v; return this; }
        public Builder roof(String v) { this.roof = v; return this; }
        public Builder roofTrim(String v) { this.roofTrim = v; return this; }
        public Builder table(String v) { this.table = v; return this; }
        public Builder chair(String v) { this.chair = v; return this; }
        public Builder bed(String v) { this.bed = v; return this; }
        public Builder lantern(String v) { this.lantern = v; return this; }
        public Builder chest(String v) { this.chest = v; return this; }
        public Builder wardrobe(String v) { this.wardrobe = v; return this; }
        public Builder ladder(String v) { this.ladder = v; return this; }
        public MaterialPalette build() { return new MaterialPalette(this); }
    }
}
