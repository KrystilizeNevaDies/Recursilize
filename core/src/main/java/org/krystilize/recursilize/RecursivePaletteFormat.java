package org.krystilize.recursilize;

import com.github.jinahya.bit.io.BitInput;
import com.github.jinahya.bit.io.BitOutput;
import org.krystilize.recursilize.tree.RecursilizeTree;
import org.krystilize.recursilize.tree.TreeSector;
import org.krystilize.recursilize.tree.TreeSectorImpl;

import java.io.IOException;
import java.util.*;

/**
 * The RecursivePalette format is a format for storing entries in an n-dimensional, integer-coordinate space.
 */
class RecursivePaletteFormat {
    public static <T> void writeTree(RecursilizeTree<T> world, BitOutput output,
                                     ObjectWriter<T> writer) throws IOException {
        // Magic bytes "rpf"
        output.writeByte8((byte) 'r');
        output.writeByte8((byte) 'p');
        output.writeByte8((byte) 'f');

        // Version
        Utils.UTF_8_WRITER.write(output, "in-dev");

        // World size
        // x-min, y-min, z-min, x-max, y-max, z-max
        output.writeInt32(RecursilizeTree.COORD_MIN);
        output.writeInt32(RecursilizeTree.COORD_MIN);
        output.writeInt32(RecursilizeTree.COORD_MIN);

        output.writeInt32(RecursilizeTree.COORD_MAX);
        output.writeInt32(RecursilizeTree.COORD_MAX);
        output.writeInt32(RecursilizeTree.COORD_MAX);

        // Collect all the entries for the global Palette
        Palette<T> palette = new Palette<>();
        world.visit((sector, minX, minY, minZ, maxX, maxY, maxZ) -> palette.addGeneric(sector));

        // Write the global palette
        Utils.writeList(output, palette.entries, writer);

        // Write the tree
        writeSector(world.root(), output, palette, new Palette<>());

        // Now align to a byte
        output.align();
    }

    private static <T> void writeSector(Object sector,
                                        BitOutput output,
                                        Palette<T> global,
                                        Palette<Integer> local) throws IOException {
        // Sector format
        if (!(sector instanceof TreeSector multi)) {
            //noinspection unchecked
            T entry = (T) sector;
            output.writeBoolean(false); // 0b0
            Integer globalIndex = global.getInt(entry);
            Objects.requireNonNull(globalIndex, () -> "Global index mismatch -> entry (" + entry + ") not found in global palette");
            int localIndex = local.getInt(globalIndex);
            Objects.requireNonNull(localIndex, () -> "Local index mismatch -> global index (" + globalIndex + ") not found in local palette");
            int bits = Utils.bitsRequired(global.size());
            output.writeUnsignedInt(bits, localIndex);
        } else {
            output.writeBoolean(true); // 0b1

            // Multi format
            // > Local palette additions
            // > 8 Sectors in this order: xnynzn, xpynzn, xnypzn, xpypzn, xnynzp, xpynzp, xnypzp, xpypzp

            // Collect all entries in this MultiSector, add them to the local palette, and convert to a list
            // Order does not matter.
            //noinspection unchecked
            int[] localPaletteAdditions = multi.sectors()
                    .stream()
                    .filter(sect -> !(sect instanceof TreeSector))
                    .map(entry -> (T) entry)
                    .distinct()
                    .map(global::getInt)
                    .mapToInt(Objects::requireNonNull)
                    .filter(index -> !local.contains(index))
                    .sorted()
                    .toArray();

            for (int addition : localPaletteAdditions) {
                local.addGeneric(addition);
            }

            // Write the local palette additions
            Utils.writeIntList(output, localPaletteAdditions, (out, index) -> {
                int bits = Utils.bitsRequired(global.size());
                out.writeUnsignedInt(bits, index);
            });

            // Write the 8 sectors
            writeSector(multi.xnynzn(), output, global, local);
            writeSector(multi.xpynzn(), output, global, local);
            writeSector(multi.xnypzn(), output, global, local);
            writeSector(multi.xpypzn(), output, global, local);
            writeSector(multi.xnynzp(), output, global, local);
            writeSector(multi.xpynzp(), output, global, local);
            writeSector(multi.xnypzp(), output, global, local);
            writeSector(multi.xpypzp(), output, global, local);

            // Remove the local palette additions from the local palette
            for (int i = localPaletteAdditions.length - 1; i >= 0; i--) {
                local.removeGeneric(localPaletteAdditions[i]);
            }
        }
    }

    public static <T> RecursilizeTree<T> readTree(BitInput input, ObjectReader<T> reader) throws IOException {
        // Refer to writeTree for the format

        // Magic bytes "rpf"
        byte r = input.readByte8();
        byte p = input.readByte8();
        byte f = input.readByte8();

        if (r != 'r' || p != 'p' || f != 'f') {
            throw new IOException("Invalid magic bytes");
        }

        // Version
        String version = Utils.UTF_8_READER.read(input);

        // World size
        // x-min, y-min, z-min, x-max, y-max, z-max
        int xMin = input.readInt32();
        int yMin = input.readInt32();
        int zMin = input.readInt32();

        int xMax = input.readInt32();
        int yMax = input.readInt32();
        int zMax = input.readInt32();

        // Global palette
        Palette<T> globalPalette = Palette.from(Utils.readList(input, reader));

        // Tree root
        Object root = readSector(input, globalPalette, new Palette<>());

        if (xMin != RecursilizeTree.COORD_MIN || yMin != RecursilizeTree.COORD_MIN || zMin != RecursilizeTree.COORD_MIN) {
            throw new IOException("Unsupported world size");
        }

        if (xMax != RecursilizeTree.COORD_MAX || yMax != RecursilizeTree.COORD_MAX || zMax != RecursilizeTree.COORD_MAX) {
            throw new IOException("Unsupported world size");
        }

        TreeSector rootSector = root instanceof TreeSector rootSector1 ? rootSector1 : new TreeSectorImpl(root);
        return RecursilizeTree.from(rootSector);
    }

    private static <T> Object readSector(BitInput input,
                                         Palette<T> global,
                                         Palette<Integer> local) throws IOException {
        // Sector format
        boolean isMulti = input.readBoolean();
        return isMulti ? readMulti(input, global, local) : readEntry(input, global, local);
    }

    private static <T> T readEntry(BitInput input, Palette<T> global, Palette<Integer> local) throws IOException {
        int bits = Utils.bitsRequired(global.size());
        int index = input.readUnsignedInt(bits);
        Integer globalIndex = local.getGeneric(index);
        Objects.requireNonNull(globalIndex, () -> "Local palette index mismatch -> index (" + index + ") was not found in local palette");
        T entry = global.getGeneric(globalIndex);
        Objects.requireNonNull(entry, () -> "Global palette index mismatch -> index (" + globalIndex + ") was not found in global palette");
        return entry;
    }

    private static <T> TreeSector readMulti(BitInput input, Palette<T> global, Palette<Integer> local) throws IOException {
        // Multi format
        // > Local palette additions
        // > 8 Sectors in this order: xnynzn, xpynzn, xnypzn, xpypzn, xnynzp, xpynzp, xnypzp, xpypzp

        // Read the local palette additions
        List<Integer> localPaletteAdditions = Utils.readList(input, in -> {
            int bits = Utils.bitsRequired(global.size());
            return in.readUnsignedInt(bits);
        });

        // Add the local palette additions to the local palette
        localPaletteAdditions.forEach(local::addGeneric);

        // Read the 8 sectors
        Object xnynzn = readSector(input, global, local);
        Object xpynzn = readSector(input, global, local);
        Object xnypzn = readSector(input, global, local);
        Object xpypzn = readSector(input, global, local);
        Object xnynzp = readSector(input, global, local);
        Object xpynzp = readSector(input, global, local);
        Object xnypzp = readSector(input, global, local);
        Object xpypzp = readSector(input, global, local);

        // Remove the local palette additions from the local palette
        for (int i = localPaletteAdditions.size() - 1; i >= 0; i--) {
            local.removeGeneric(localPaletteAdditions.get(i));
        }

        return new TreeSectorImpl(xnynzn, xpynzn, xnypzn, xpypzn, xnynzp, xpynzp, xnypzp, xpypzp);
    }

    private static class Palette<T> {
        private final List<T> entries = new ArrayList<>();
        private final Map<T, Integer> entryMap = new HashMap<>();

        public static <T> Palette<T> from(List<T> list) {
            Palette<T> palette = new Palette<>();
            for (T t : list)
                palette.addGeneric(t);
            return palette;
        }

        public int addGeneric(T entry) {
            if (entryMap.containsKey(entry)) {
                return entryMap.get(entry);
            } else {
                int index = entries.size();
                entries.add(entry);
                entryMap.put(entry, index);
                return index;
            }
        }

        public boolean contains(T entry) {
            return entryMap.containsKey(entry);
        }

        public int size() {
            return entries.size();
        }

        public T getGeneric(int index) {
            if (index < 0 || index >= entries.size()) {
                throw new IndexOutOfBoundsException();
            }
            return entries.get(index);
        }

        public int getInt(T entry) {
            return Objects.requireNonNull(entryMap.get(entry), "Entry not in palette");
        }

        public T removeInt(int index) {
            T entry = entries.remove(index);
            entryMap.remove(entry);
            return entry;
        }

        public int removeGeneric(T entry) {
            int index = entryMap.remove(entry);
            entries.remove(index);
            return index;
        }

        @Override
        public String toString() {
            return "Palette{" +
                    "entries=" + entries +
                    '}';
        }
    }
}
