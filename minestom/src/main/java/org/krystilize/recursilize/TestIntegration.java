package org.krystilize.recursilize;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.Block;

import java.util.concurrent.CompletableFuture;

public class TestIntegration {
    public static void main(String[] args) {
        MinecraftServer server = MinecraftServer.init();

        InstanceManager manager = MinecraftServer.getInstanceManager();
        BinarySource world = BinarySource.file("test.world");
        InstanceContainer container = manager.createInstanceContainer();
//        container.setChunkLoader(new MinestomRecursilizeChunkLoader(world, Block.AIR));

        MinecraftServer.getExceptionManager().setExceptionHandler(exception -> {
            throw new RuntimeException(exception);
        });

        MinecraftServer.getGlobalEventHandler()
            .addListener(PlayerLoginEvent.class, event -> {
                event.setSpawningInstance(container);
                event.getPlayer().setRespawnPoint(new Pos(0, 40, 0));
                event.getPlayer().setGameMode(GameMode.CREATIVE);
            })
            .addListener(PlayerChatEvent.class, event -> {
                String message = event.getMessage();
                CompletableFuture<?> future = switch (message) {
                    case "save" -> {
                        container.setChunkLoader(new MinestomRecursilizeChunkLoader(world, Block.AIR));
                        yield container.saveChunksToStorage();
                    }
                    case "reload" -> {
                        System.out.println("Creating new instance");
                        InstanceContainer newInstance = manager.createInstanceContainer();
                        var loader = new MinestomRecursilizeChunkLoader(world, Block.AIR);
                        newInstance.setChunkLoader(loader);
                        container.loadChunk(0, 0).join();

                        System.out.println("Setting new instance");
                        yield event.getPlayer().setInstance(newInstance, new Pos(0, 40, 0)).thenRun(() -> {
                            System.out.println("New instance set");
                        });
                    }
                    default -> CompletableFuture.completedFuture(null);
                };
                future.thenRun(() -> event.getPlayer().sendMessage("Done!"));
            });

        server.start("0.0.0.0", 25565);
    }
}
