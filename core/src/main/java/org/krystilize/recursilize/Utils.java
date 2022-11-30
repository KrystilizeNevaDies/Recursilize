package org.krystilize.recursilize;

import com.github.jinahya.bit.io.BitInput;
import com.github.jinahya.bit.io.BitOutput;
import com.github.jinahya.bit.io.StringReader;
import com.github.jinahya.bit.io.StringWriter;
import org.krystilize.recursilize.tree.TreeSector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static StringWriter UTF_8_WRITER = StringWriter.utf8();
    public static StringReader UTF_8_READER = StringReader.utf8();

    public static <T> void genericTreeVisit(Visitor<T> visitor,
                                            Object sector,
                                            int minX, int minY, int minZ,
                                            int maxX, int maxY, int maxZ) {
        if (!(sector instanceof TreeSector multi)) {
            //noinspection unchecked
            visitor.visit((T) sector, minX, minY, minZ, maxX, maxY, maxZ);
            return;
        }

        int midX = (minX + maxX) / 2;
        int midY = (minY + maxY) / 2;
        int midZ = (minZ + maxZ) / 2;
        genericTreeVisit(visitor, multi.xnynzn(), minX, minY, minZ, midX, midY, midZ);
        genericTreeVisit(visitor, multi.xpynzn(), midX, minY, minZ, maxX, midY, midZ);
        genericTreeVisit(visitor, multi.xnypzn(), minX, midY, minZ, midX, maxY, midZ);
        genericTreeVisit(visitor, multi.xpypzn(), midX, midY, minZ, maxX, maxY, midZ);
        genericTreeVisit(visitor, multi.xnynzp(), minX, minY, midZ, midX, midY, maxZ);
        genericTreeVisit(visitor, multi.xpynzp(), midX, minY, midZ, maxX, midY, maxZ);
        genericTreeVisit(visitor, multi.xnypzp(), minX, midY, midZ, midX, maxY, maxZ);
        genericTreeVisit(visitor, multi.xpypzp(), midX, midY, midZ, maxX, maxY, maxZ);
    }

    public static <T> void writeList(BitOutput output, List<T> entries, ObjectWriter<T> writer) throws IOException {
        output.writeUnsignedLong(32, entries.size());
        for (T entry : entries) {
            writer.write(entry, output);
        }
    }

    public static <T> List<T> readList(BitInput input, ObjectReader<T> reader) throws IOException {
        int size = (int) input.readUnsignedLong(32);
        List<T> entries = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            entries.add(reader.read(input));
        }
        return entries;
    }

    public static void writeIntList(BitOutput output, int[] entries, IntBitWriter writer) throws IOException {
        output.writeUnsignedLong(32, entries.length);
        for (int entry : entries) {
            writer.write(output, entry);
        }
    }

    public interface IntBitWriter {
        void write(BitOutput output, int value) throws IOException;
    }

    /**
     * Gets the bits required to store the given unique entries.
     * @param size The number of unique entries.
     * @return The number of bits required to store the given unique entries, minimum of 1.
     */
    public static int bitsRequired(int size) {
        return Math.max(1, 64 - Long.numberOfLeadingZeros(size - 1));
    }
}
