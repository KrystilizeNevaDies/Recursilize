package org.krystilize.recursilize;

import com.github.jinahya.bit.io.BitInput;
import com.github.jinahya.bit.io.BitOutput;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.DynamicChunk;
import net.minestom.server.instance.IChunkLoader;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.binary.BinaryWriter;
import net.minestom.server.world.DimensionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public final class MinestomRecursilizeChunkLoader implements IChunkLoader {

    private final EntryLoader<Block> entryLoader;

    public MinestomRecursilizeChunkLoader(BinarySource source, Block defaultBlock) {
        this.entryLoader = EntryLoader.binary(source, BLOCK_SERIALIZER, defaultBlock);
    }

    @Override
    public boolean supportsParallelLoading() {
        return false;
    }

    @Override
    public boolean supportsParallelSaving() {
        return false;
    }

    @Override
    public @NotNull CompletableFuture<Void> saveChunks(@NotNull Collection<Chunk> chunks) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;

        for (Chunk chunk : chunks) {
            int chunkMinX = chunk.getChunkX() * Chunk.CHUNK_SIZE_X;
            int chunkMinY = chunk.getMinSection() * Chunk.CHUNK_SECTION_SIZE;
            int chunkMinZ = chunk.getChunkZ() * Chunk.CHUNK_SIZE_Z;
            int chunkMaxX = minX + Chunk.CHUNK_SIZE_X;
            int chunkMaxY = minY + Chunk.CHUNK_SECTION_SIZE;
            int chunkMaxZ = minZ + Chunk.CHUNK_SIZE_Z;

            minX = Math.min(minX, chunkMinX);
            minY = Math.min(minY, chunkMinY);
            minZ = Math.min(minZ, chunkMinZ);
            maxX = Math.max(maxX, chunkMaxX);
            maxY = Math.max(maxY, chunkMaxY);
            maxZ = Math.max(maxZ, chunkMaxZ);
        }

        AtomicInteger saved = new AtomicInteger(0);

        System.out.println("Saving " + chunks.size() + " chunks...");

        CompletableFuture<?>[] futures = chunks.stream()
                .map(this::setBlocks)
                .map(future -> future.thenRun(() -> System.out.println("Completed: " + saved.incrementAndGet() + "/" + chunks.size())))
                .toArray(CompletableFuture[]::new);

        int finalMinX = minX;
        int finalMinY = minY;
        int finalMinZ = minZ;
        int finalMaxX = maxX;
        int finalMaxY = maxY;
        int finalMaxZ = maxZ;
        CompletableFuture<Void> future = new CompletableFuture<>();
        CompletableFuture.allOf(futures)
            .thenRun(() -> {
                entryLoader.save(finalMinX, finalMinY, finalMinZ, finalMaxX, finalMaxY, finalMaxZ).thenRun(() -> {
                    System.out.println("Chunks written to file.");
                    future.complete(null);
                });
            });
        return future;
    }

    @Override
    public @NotNull CompletableFuture<@Nullable Chunk> loadChunk(@NotNull Instance instance, int chunkX, int chunkZ) {
        DimensionType dimension = instance.getDimensionType();
        int minX = chunkX * Chunk.CHUNK_SIZE_X;
        int minY = dimension.getMinY();
        int minZ = chunkZ * Chunk.CHUNK_SIZE_Z;
        int maxX = minX + Chunk.CHUNK_SIZE_X;
        int maxY = dimension.getMaxY();
        int maxZ = minZ + Chunk.CHUNK_SIZE_Z;
        return entryLoader.load(minX, minY, minZ, maxX, maxY, maxZ).thenApply(section -> {
            Chunk chunk = new DynamicChunk(instance, chunkX, chunkZ);
            System.out.println("Loading chunk " + chunkX + ", " + chunkZ);

            for (int x = minX; x < maxX; x++) {
                for (int y = minY; y < maxY; y++) {
                    for (int z = minZ; z < maxZ; z++) {
                        Block block = section.get(x, y, z);
                        if (block != null) {
                            chunk.setBlock(x, y, z, block);
                        }

                        if (Block.COBBLESTONE.compare(block)) {
                            System.out.println("Found cobblestone at " + x + ", " + y + ", " + z);
                        }
                    }
                }
            }

            System.out.println("Loaded chunk " + chunkX + ", " + chunkZ);

            return chunk;
        });
    }

    private CompletableFuture<Void> setBlocks(@NotNull Chunk chunk) {
        System.out.println("Saving chunk " + chunk.getChunkX() + ", " + chunk.getChunkZ());
        int minX = chunk.getChunkX() * Chunk.CHUNK_SIZE_X;
        int minY = chunk.getMinSection() * Chunk.CHUNK_SECTION_SIZE;
        int minZ = chunk.getChunkZ() * Chunk.CHUNK_SIZE_Z;
        int maxX = minX + Chunk.CHUNK_SIZE_X;
        int maxY = chunk.getMaxSection() * Chunk.CHUNK_SECTION_SIZE;
        int maxZ = minZ + Chunk.CHUNK_SIZE_Z;

        return entryLoader.apply(setter -> {
            for (int x = minX; x < maxX; x++) {
                for (int y = minY; y < maxY; y++) {
                    for (int z = minZ; z < maxZ; z++) {
                        Block block = chunk.getBlock(x, y, z);
                        setter.set(x, y, z, block);
                    }
                }
            }
        });
    }

    @Override
    public @NotNull CompletableFuture<Void> saveChunk(@NotNull Chunk chunk) {
        return saveChunks(List.of(chunk));
    }

    public static final BinarySerializer<Block> BLOCK_SERIALIZER = new BinarySerializer<>() {
        @Override
        public void write(Block block, BitOutput output) throws IOException {
            String namespace = block.namespace().toString();
            Map<String, String> properties = block.properties();

            BinaryWriter writer = new BinaryWriter();

            writer.writeSizedString(namespace);
            writer.writeVarInt(properties.size());
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                writer.writeSizedString(entry.getKey());
                writer.writeSizedString(entry.getValue());
            }

            // Write all the bytes
            for (byte b : writer.toByteArray()) {
                output.writeByte8(b);
            }

            writer.close();
        }

        @Override
        public Block read(BitInput input) throws IOException {
            MinestomObjectReader reader = new MinestomObjectReader(input);

            String namespace = reader.readSizedString();

            int propertyCount = reader.readVarInt();
            //noinspection unchecked
            Map.Entry<String, String>[] entries = new Map.Entry[propertyCount];

            for (int i = 0; i < propertyCount; i++) {
                String key = reader.readSizedString();
                String value = reader.readSizedString();
                entries[i] = Map.entry(key, value);
            }

            Map<String, String> properties = Map.ofEntries(entries);

            Block block = Block.fromNamespaceId(namespace);
            if (block == null)
                throw new IOException("Unknown block namespace: " + namespace);

            return block.withProperties(properties);
        }
    };
}
