package fr.thinkbit.hytale.structurebuilder;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.common.plugin.PluginIdentifier;
import com.hypixel.hytale.server.core.plugin.PluginManager;
import com.top_serveurs.hytale.plugins.mcp.McpPlugin;
import fr.thinkbit.hytale.structurebuilder.feature.BuildStructureFeature;

import javax.annotation.Nonnull;

public class StructureBuilderPlugin extends JavaPlugin {

    public StructureBuilderPlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        var mcp = (McpPlugin) PluginManager.get().getPlugin(new PluginIdentifier("Top-Games", "MCP"));
        mcp.getFeatureRegistry().registerFeature(new BuildStructureFeature());
        getLogger().atInfo().log("StructureBuilder: registered build_structure MCP tool");
    }
}
