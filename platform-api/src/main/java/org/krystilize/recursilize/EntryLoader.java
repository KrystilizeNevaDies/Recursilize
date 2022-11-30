package org.krystilize.recursilize;

import org.krystilize.recursilize.tree.TreeSection;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface EntryLoader<E> {
    /**
     * Loads this section of the tree. The loaded section must cover the entire requested area, and may be larger.
     * @param minX the minimum x coordinate to load
     * @param minY the minimum y coordinate to load
     * @param minZ the minimum z coordinate to load
     * @param maxX the maximum x coordinate to load
     * @param maxY the maximum y coordinate to load
     * @param maxZ the maximum z coordinate to load
     * @return the loaded section
     */
    CompletableFuture<TreeSection<E>> load(int minX, int minY, int minZ, int maxX, int maxY, int maxZ);

    /**
     * Saves this section of the tree. The saved section must cover the entire requested area, and may be
     * larger. (e.g. saving the entire world every call is valid.)
     * @param minX the minimum x coordinate to save
     * @param minY the minimum y coordinate to save
     * @param minZ the minimum z coordinate to save
     * @param maxX the maximum x coordinate to save
     * @param maxY the maximum y coordinate to save
     * @param maxZ the maximum z coordinate to save
     */
    CompletableFuture<Void> save(int minX, int minY, int minZ, int maxX, int maxY, int maxZ);

    static <T> EntryLoader<T> binary(BinarySource source, BinarySerializer<T> serializer, T defaultEntry) {
        return new PaletteBinaryEntryLoader<>(source, serializer, defaultEntry);
    }

    CompletableFuture<Void> apply(Consumer<EntrySetter<E>> entrySetter);

    interface EntrySetter<E> {
        void set(int x, int y, int z, E entry);
    }
}
