package org.krystilize.recursilize;

import com.github.jinahya.bit.io.*;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.packet.server.SendablePacket;
import net.minestom.server.network.packet.server.play.PlayerInfoPacket;
import net.minestom.server.utils.chunk.ChunkUtils;
import org.jetbrains.annotations.NotNull;
import org.krystilize.recursilize.tree.TreeSector;
import org.krystilize.recursilize.tree.RecursilizeTree;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class Test {

    public static void main(String[] args) {

        RecursilizeTree.Dynamic<MinecraftBlock> world = RecursilizeTree.dynamic(MinecraftBlock.from("kry:air"));
        world.set(0, 0, 0, MinecraftBlock.from("kry:test1"));
        world.set(-1, 1, 2, MinecraftBlock.from("kry:test2"));
        world.set(-302, 1, -1, MinecraftBlock.from("kry:test3"));

        Random random = new Random();

        MinecraftBlock a = MinecraftBlock.from("kry", "type", "a");
        MinecraftBlock b = MinecraftBlock.from("kry", "type", "b");

        int count = 5120000;

        long start = System.nanoTime();
        int cubeRoot = (int) Math.cbrt(count);
        for (int x = 0; x < cubeRoot; x++) {
            for (int y = 0; y < cubeRoot; y++) {
                for (int z = 0; z < cubeRoot; z++) {
                    world.set(x, y, z, random.nextInt(1000) == 0 ? a : b);
                }
            }
        }

        world.set(0, 0, 3, MinecraftBlock.from("kry:test4"));

        System.out.println("Place blocks per second: " + (count / ((System.nanoTime() - start) / 1_000_000_000.0)));

        start = System.nanoTime();
        for (int i = 0; i < count; i++) {
            int x = random.nextInt(cubeRoot);
            int y = random.nextInt(cubeRoot);
            int z = random.nextInt(cubeRoot);
            world.get(x, y, z);
        }

        System.out.println("Get blocks per second: " + (count / ((System.nanoTime() - start) / 1_000_000_000.0)));

        System.out.println("Compressing " + count + " blocks...");

        int before = world.size();
        start = System.currentTimeMillis();
        world.compress();
        System.out.println("Compressed in " + (System.currentTimeMillis() - start) + "ms");
        int after = world.size();
        System.out.println("Compressed " + before + " layers to " + after + " layers (" + (100.0 - (after * 100.0 / before)) + "% better)");

//        sql(world);
        world.root().sectors().forEach(Test::checkSimpleOptimization);
//        print(world.root(), 0);
        System.out.println();

        byte[] array = write(world);
        RecursilizeTree<MinecraftBlock> read = read(array);

        System.out.println("Written " + world.size() + " layers");
        System.out.println("Read " + read.size() + " layers");

        start = System.nanoTime();

        // 1000 writes
        for (int i = 0; i < 100; i++) {
            write(world);
        }

        System.out.println("Writes per second: " + (100.0 / ((System.nanoTime() - start) / 1_000_000_000.0)));

        start = System.nanoTime();

        // 1000 reads
        for (int i = 0; i < 100; i++) {
            read(array);
        }

        System.out.println("Reads per second: " + (100.0 / ((System.nanoTime() - start) / 1_000_000_000.0)));

        start = System.nanoTime();

        MinecraftServer.init();
        InstanceContainer instance = MinecraftServer.getInstanceManager().createInstanceContainer();

        System.out.println("Loading anvil world...");

        ChunkUtils.forChunksInRange(0, 0, 10, (x, z) -> {
            instance.loadChunk(x, z).join();
        });

        System.out.println("Loaded in " + (System.nanoTime() - start) / 1_000_000_000.0 + " seconds");

        start = System.nanoTime();

        System.out.println("Setting blocks in new dynamic tree...");

        Map<String, MinecraftBlock> blocks = new HashMap<>();
        MinecraftBlock air = MinecraftBlock.from("kry:air");
        blocks.put("minecraft:air", air);

        RecursilizeTree.Dynamic<MinecraftBlock> newWorld = RecursilizeTree.dynamic(air);
        //noinspection UnstableApiUsage
        ChunkUtils.forChunksInRange(0, 0, 10, (chunkX, chunkZ) -> {
            if (!instance.isChunkLoaded(chunkX, chunkZ)) {
                return;
            }

            Chunk chunk = instance.getChunk(chunkX, chunkZ);
            assert chunk != null;

            int minX = chunk.getChunkX() * Chunk.CHUNK_SIZE_X;
            int minY = chunk.getMinSection() * Chunk.CHUNK_SECTION_SIZE;
            int maxY = chunk.getMaxSection() * Chunk.CHUNK_SECTION_SIZE;
            int minZ = chunk.getChunkZ() * Chunk.CHUNK_SIZE_Z;

            for (int x = minX; x < minX + Chunk.CHUNK_SIZE_X; x++) {
                for (int y = minY; y < maxY; y++) {
                    for (int z = minZ; z < minZ + Chunk.CHUNK_SIZE_Z; z++) {
                        Block block = instance.getBlock(x, y, z);

                        String namespace = block.namespace().toString();
                        MinecraftBlock minecraftBlock = blocks.computeIfAbsent(namespace, k -> MinecraftBlock.from(namespace));

                        if (minecraftBlock == air) {
                            continue;
                        }

                        newWorld.set(x, y, z, minecraftBlock);
                    }
                }
            }
        });

        System.out.println("Set blocks in " + (System.nanoTime() - start) / 1_000_000_000.0 + " seconds");

        MinecraftServer.stopCleanly();
    }

    private static RecursilizeTree<MinecraftBlock> read(byte[] array) {
        BitInput input = BitInputAdapter.from(StreamByteInput.from(new ByteArrayInputStream(array)));
        try {
            return RecursivePaletteFormat.readTree(input, MinecraftBlock::read);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] write(RecursilizeTree<MinecraftBlock> world) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BitOutput output = BitOutputAdapter.from(StreamByteOutput.from(baos));

        try {
            RecursivePaletteFormat.writeTree(world, output, MinecraftBlock::write);
            output.close();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void checkSimpleOptimization(Object sector) {
        if (!(sector instanceof TreeSector multi)) {
            return;
        }

        var sectors = multi.sectors();

        sectors.forEach(Test::checkSimpleOptimization);

        int count = (int) sectors.stream()
                .filter(s -> !(s instanceof TreeSector))
                .count();

        if (count == 8) {
            Object identity = sectors.stream().findAny().orElseThrow();

            if (sectors.stream().allMatch(other -> Objects.equals(identity, other))) {
                throw new IllegalStateException("All entry sectors are the same in a multi sector");
            }
        }
    }
}
