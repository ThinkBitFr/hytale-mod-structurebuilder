package fr.thinkbit.hytale.structurebuilder;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.system.WorldEventSystem;
import com.hypixel.hytale.server.core.prefab.event.PrefabPasteEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

/**
 * Cancels all PrefabPasteEvent events to prevent world-gen structures
 * (buildings, camps, etc.) from being placed during chunk generation.
 * Natural terrain (hills, caves, biomes) is unaffected.
 */
public class PrefabBlockerSystem extends WorldEventSystem<EntityStore, PrefabPasteEvent> {

    private volatile boolean blocking = true;

    public PrefabBlockerSystem() {
        super(PrefabPasteEvent.class);
    }

    public boolean isBlocking() {
        return blocking;
    }

    public void setBlocking(boolean blocking) {
        this.blocking = blocking;
    }

    @Override
    public void handle(Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer, PrefabPasteEvent event) {
        if (blocking) {
            event.setCancelled(true);
        }
    }
}
