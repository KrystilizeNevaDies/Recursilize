package org.krystilize.recursilize;

import org.krystilize.recursilize.tree.RecursilizeTree;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Test2 {
    public static void main(String[] args) {
        BinarySource source = BinarySource.file("test2.world");
        Path path = Path.of("test2.world");
        try {
            Files.deleteIfExists(path);
            Files.createFile(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MinecraftBlock air = MinecraftBlock.from("minecraft:air");
        {
            var loader = EntryLoader.binary(source, MinecraftBlock.SERIALIZER, air);

            loader.apply(setter -> {
                setter.set(0, 0, 0, MinecraftBlock.from("minecraft:stone"));
            });

            loader.save(0, 0, 0, 0, 0, 0).join();
        }

        {
            var loader = EntryLoader.binary(source, MinecraftBlock.SERIALIZER, air);

            loader.load(RecursilizeTree.COORD_MIN, RecursilizeTree.COORD_MIN, RecursilizeTree.COORD_MIN,
                    RecursilizeTree.COORD_MAX, RecursilizeTree.COORD_MAX, RecursilizeTree.COORD_MAX).thenAccept(section -> {
                System.out.println(section.get(0, 0, 0));
            }).join();
        }
    }
}
