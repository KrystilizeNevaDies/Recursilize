package org.krystilize.recursilize;

import com.github.jinahya.bit.io.*;
import org.krystilize.recursilize.tree.RecursilizeTree;
import org.krystilize.recursilize.tree.TreeSection;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class PaletteBinaryEntryLoader<T> extends BinaryEntryLoader<T> {

    private final RecursilizeTree.Dynamic<T> tree;
    private final T defaultEntry;

    PaletteBinaryEntryLoader(BinarySource source, BinarySerializer<T> serializer, T defaultEntry) {
        super(source, serializer);
        this.defaultEntry = defaultEntry;
        this.tree = loadFromSource().join();
    }

    @Override
    public CompletableFuture<TreeSection<T>> load(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        return CompletableFuture.completedFuture(TreeSection.view(tree, minX, minY, minZ, maxX, maxY, maxZ));
    }

    @Override
    public CompletableFuture<Void> save(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        System.out.println("Saving... ");
        System.out.println("minX: " + minX + " minY: " + minY + " minZ: " + minZ);
        System.out.println("maxX: " + maxX + " maxY: " + maxY + " maxZ: " + maxZ);
        CompletableFuture<Void> future = new CompletableFuture<>();
        try {
            source.write(output -> {
                var bitOutput = BitOutputAdapter.from(StreamByteOutput.from(output));
                synchronized (tree) {
                    RecursivePaletteFormat.writeTree(tree, bitOutput, serializer);
                }
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
        return CompletableFuture.runAsync(() -> {
            synchronized (tree) {
                entrySetter.accept(tree::set);
                tree.compress();
            }
        });
    }

    private CompletableFuture<RecursilizeTree.Dynamic<T>> loadFromSource() {
        CompletableFuture<RecursilizeTree.Dynamic<T>> future = new CompletableFuture<>();
        try {
            source.read(input -> {
                BitInput bitInput = BitInputAdapter.from(StreamByteInput.from(input));
                RecursilizeTree<T> result = RecursivePaletteFormat.readTree(bitInput, serializer);
                Objects.requireNonNull(result, "RecursivePaletteFormat.readTree returned null");
                synchronized (tree) {
                    RecursilizeTree.Dynamic<T> tree = RecursilizeTree.dynamic(result, defaultEntry);
                    tree.compress();
                }
                future.complete(tree);
            });
        } catch (Throwable thrown) {
            throw new RuntimeException(thrown);
        }
        return future;
    }
}
