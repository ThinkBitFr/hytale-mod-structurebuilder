package fr.thinkbit.hytale.structurebuilder;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.common.plugin.PluginIdentifier;
import com.hypixel.hytale.server.core.plugin.PluginManager;
import com.top_serveurs.hytale.plugins.mcp.McpPlugin;
import fr.thinkbit.hytale.structurebuilder.feature.BuildStructureFeature;
import fr.thinkbit.hytale.structurebuilder.feature.CreateFlatWorldFeature;

import javax.annotation.Nonnull;

public class StructureBuilderPlugin extends JavaPlugin {

    public StructureBuilderPlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        var mcp = (McpPlugin) PluginManager.get().getPlugin(new PluginIdentifier("Top-Games", "MCP"));
        mcp.getFeatureRegistry().registerFeature(new BuildStructureFeature());
        mcp.getFeatureRegistry().registerFeature(new CreateFlatWorldFeature());
        getLogger().atInfo().log("StructureBuilder: registered build_structure and create_flat_world MCP tools");
    }
}
