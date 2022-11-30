package org.krystilize.recursilize;

import com.github.jinahya.bit.io.*;
import org.krystilize.recursilize.tree.RecursilizeTree;
import org.krystilize.recursilize.tree.TreeSection;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class PaletteBinaryEntryLoader<T> extends BinaryEntryLoader<T> {

    private RecursilizeTree.Dynamic<T> tree;
    private final T defaultEntry;

    PaletteBinaryEntryLoader(BinarySource source, BinarySerializer<T> serializer, T defaultEntry) {
        super(source, serializer);
        this.defaultEntry = defaultEntry;
    }

    @Override
    public CompletableFuture<TreeSection<T>> load(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        return loadIfNull().thenApply((ignored) -> {
            assert tree != null;
            return TreeSection.view(tree, minX, minY, minZ, maxX, maxY, maxZ);
        });
    }

    @Override
    public CompletableFuture<Void> save(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        System.out.println("Saving... ");
        System.out.println("minX: " + minX + " minY: " + minY + " minZ: " + minZ);
        System.out.println("maxX: " + maxX + " maxY: " + maxY + " maxZ: " + maxZ);
        if (tree == null) {
            throw new IllegalStateException("The tree to save has not yet been loaded.");
        }
        CompletableFuture<Void> future = new CompletableFuture<>();
        try {
            source.write(output -> {
                var bitOutput = BitOutputAdapter.from(StreamByteOutput.from(output));
                RecursivePaletteFormat.writeTree(tree, bitOutput, serializer);
                future.complete(null);
                System.out.println("Saved!");
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return future;
    }

    @Override
    public CompletableFuture<Void> apply(Consumer<EntrySetter<T>> entrySetter) {
        return loadIfNull().thenRun(() -> {
            entrySetter.accept(tree::set);
            tree.compress();
        });
    }

    private CompletableFuture<Void> loadIfNull() {
        if (tree != null) {
            return CompletableFuture.completedFuture(null);
        }
        CompletableFuture<Void> future = new CompletableFuture<>();
        try {
            source.read(input -> {
                BitInput bitInput = BitInputAdapter.from(StreamByteInput.from(input));
                RecursilizeTree<T> result = RecursivePaletteFormat.readTree(bitInput, serializer);
                Objects.requireNonNull(result, "RecursivePaletteFormat.readTree returned null");
                tree = RecursilizeTree.dynamic(result, null);
                future.complete(null);
            });
        } catch (Throwable ignored) {
            tree = RecursilizeTree.dynamic(defaultEntry);
            future.complete(null);
        }
        tree.compress();
        return future;
    }
}
