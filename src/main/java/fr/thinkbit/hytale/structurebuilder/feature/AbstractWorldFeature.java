package fr.thinkbit.hytale.structurebuilder.feature;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.top_serveurs.hytale.plugins.mcp.auth.McpAuthManager;
import com.top_serveurs.hytale.plugins.mcp.config.McpConfig;
import com.top_serveurs.hytale.plugins.mcp.features.McpFeature;
import com.top_serveurs.hytale.plugins.mcp.models.McpToolCall;

public abstract class AbstractWorldFeature implements McpFeature {

    protected static final Gson GSON = new GsonBuilder().create();

    @Override
    public boolean hasPermission(McpAuthManager.AuthLevel authLevel, McpConfig config) {
        return authLevel == McpAuthManager.AuthLevel.ADMIN;
    }

    protected int getInt(McpToolCall call, String key) {
        Object val = call.getArguments().get(key);
        if (val instanceof Number) return ((Number) val).intValue();
        return Integer.parseInt(val.toString());
    }

    protected int getInt(McpToolCall call, String key, int defaultValue) {
        Object val = call.getArguments().get(key);
        if (val == null) return defaultValue;
        if (val instanceof Number) return ((Number) val).intValue();
        return Integer.parseInt(val.toString());
    }

    protected String getString(McpToolCall call, String key) {
        Object val = call.getArguments().get(key);
        return val != null ? val.toString() : null;
    }

    protected String getString(McpToolCall call, String key, String defaultValue) {
        Object val = call.getArguments().get(key);
        return val != null ? val.toString() : defaultValue;
    }

    protected boolean getBool(McpToolCall call, String key, boolean defaultValue) {
        Object val = call.getArguments().get(key);
        if (val == null) return defaultValue;
        if (val instanceof Boolean) return (Boolean) val;
        return Boolean.parseBoolean(val.toString());
    }
}
