package org.krystilize.recursilize;

import com.github.jinahya.bit.io.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import org.krystilize.recursilize.tree.RecursilizeTree;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

public class ReadWriteTest {

    @Test
    public void readWriteIntInMemory() {
        RecursilizeTree.Dynamic<Integer> tree = RecursilizeTree.dynamic(0);
        randomPlacements(tree, () -> ThreadLocalRandom.current().nextInt(0, 100));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BitOutput out = BitOutputAdapter.from(StreamByteOutput.from(baos));
        assertDoesNotThrow(() -> RecursivePaletteFormat.writeTree(tree, out, BitOutput::writeInt32));

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        BitInput in = BitInputAdapter.from(StreamByteInput.from(bais));
        assertDoesNotThrow(() -> {
            RecursilizeTree<Integer> read = RecursivePaletteFormat.readTree(in, BitInput::readInt32);
            assertTrue(RecursilizeTree.equals(tree, read), "Tree read from stream is not equal to original tree");
        });
    }

    @Test
    public void readWriteStringToFile() {
        RecursilizeTree.Dynamic<String> tree = RecursilizeTree.dynamic("");
        randomPlacements(tree, () -> "test");

        assertDoesNotThrow(() -> {
            try (FileOutputStream fos = new FileOutputStream("test.txt")) {
                BitOutput out = BitOutputAdapter.from(StreamByteOutput.from(fos));
                RecursivePaletteFormat.writeTree(tree, out, Utils.UTF_8_WRITER::write);
            }

            try (FileInputStream fis = new FileInputStream("test.txt")) {
                BitInput in = BitInputAdapter.from(StreamByteInput.from(fis));
                RecursilizeTree<String> read = RecursivePaletteFormat.readTree(in, Utils.UTF_8_READER::read);
                assertTrue(RecursilizeTree.equals(tree, read), "Tree read from stream is not equal to original tree");
            }
            Files.delete(Path.of("test.txt"));
        });
    }

    private static <T> void randomPlacements(RecursilizeTree.Dynamic<T> tree, Supplier<T> random) {
        for (int x = -30; x < 30; x++) {
            for (int y = -30; y < 30; y++) {
                for (int z = -30; z < 30; z++) {
                    tree.set(x, y, z, random.get());
                }
            }
        }
    }
}
