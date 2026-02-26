package fr.thinkbit.hytale.structurebuilder;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.common.plugin.PluginIdentifier;
import com.hypixel.hytale.server.core.plugin.PluginManager;
import com.top_serveurs.hytale.plugins.mcp.McpPlugin;
import fr.thinkbit.hytale.structurebuilder.feature.BuildStructureFeature;
import fr.thinkbit.hytale.structurebuilder.feature.CreateFlatWorldFeature;
import fr.thinkbit.hytale.structurebuilder.feature.CreateWorldFeature;

import javax.annotation.Nonnull;

public class StructureBuilderPlugin extends JavaPlugin {

    private static StructureBuilderPlugin instance;
    private final PrefabBlockerSystem prefabBlocker = new PrefabBlockerSystem();

    public StructureBuilderPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
    }

    public static StructureBuilderPlugin get() {
        return instance;
    }

    public PrefabBlockerSystem getPrefabBlocker() {
        return prefabBlocker;
    }

    @Override
    protected void setup() {
        // Register prefab blocker to cancel world-gen structures
        getEntityStoreRegistry().registerSystem(prefabBlocker);

        // Register MCP tools
        var mcp = (McpPlugin) PluginManager.get().getPlugin(new PluginIdentifier("Top-Games", "MCP"));
        mcp.getFeatureRegistry().registerFeature(new BuildStructureFeature());
        mcp.getFeatureRegistry().registerFeature(new CreateFlatWorldFeature());
        mcp.getFeatureRegistry().registerFeature(new CreateWorldFeature());

        getLogger().atInfo().log("StructureBuilder: registered MCP tools and prefab blocker (blocking=" + prefabBlocker.isBlocking() + ")");
    }
}
