package fr.thinkbit.hytale.structurebuilder;

import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;

import java.util.concurrent.CompletableFuture;

public final class WorldUtil {

    private WorldUtil() {}

    public static World getDefaultWorld() {
        return Universe.get().getDefaultWorld();
    }

    public static <T> CompletableFuture<T> executeOnWorldThread(World world, java.util.function.Supplier<T> task) {
        CompletableFuture<T> future = new CompletableFuture<>();
        world.execute(() -> {
            try {
                future.complete(task.get());
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    public static CompletableFuture<Void> executeOnWorldThread(World world, Runnable task) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        world.execute(() -> {
            try {
                task.run();
                future.complete(null);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }
}
