package org.krystilize.recursilize;

import com.github.jinahya.bit.io.BitInput;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.utils.Either;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBTReader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

class MinestomObjectReader {
    private final BitInput input;
    private NBTReader nbtReader = null;

    public MinestomObjectReader(@NotNull BitInput input) {
        this.input = input;
    }

    public int readVarInt() throws IOException {
        // https://github.com/jvm-profiling-tools/async-profiler/blob/a38a375dc62b31a8109f3af97366a307abb0fe6f/src/converter/one/jfr/JfrReader.java#L393
        int result = 0;
        for (int shift = 0; ; shift += 7) {
            byte b = input.readByte8();
            result |= (b & 0x7f) << shift;
            if (b >= 0) {
                return result;
            }
        }
    }

    public long readVarLong() throws IOException {
        // https://github.com/jvm-profiling-tools/async-profiler/blob/a38a375dc62b31a8109f3af97366a307abb0fe6f/src/converter/one/jfr/JfrReader.java#L404
        long result = 0;
        for (int shift = 0; shift < 56; shift += 7) {
            byte b = input.readByte8();
            result |= (b & 0x7fL) << shift;
            if (b >= 0) {
                return result;
            }
        }
        return result | (input.readByte8() & 0xffL) << 56;
    }

    public boolean readBoolean() throws IOException {
        return input.readByte8() == 1;
    }

    public byte readByte() throws IOException {
        return input.readByte8();
    }

    public short readShort() throws IOException {
        return input.readShort16();
    }

    public char readChar() throws IOException {
        return input.readChar16();
    }

    public int readUnsignedShort() throws IOException {
        return input.readShort16() & 0xFFFF;
    }

    /**
     * Same as readInt
     */
    public int readInteger() throws IOException {
        return input.readInt32();
    }

    /**
     * Same as readInteger, created for parity with BinaryWriter
     */
    public int readInt() throws IOException {
        return input.readInt32();
    }

    public long readLong() throws IOException {
        return input.readLong64();
    }

    public float readFloat() throws IOException {
        return input.readFloat32();
    }

    public double readDouble() throws IOException {
        return input.readDouble64();
    }

    /**
     * Reads a string size by a var-int.
     * <p>
     * If the string length is higher than {@code maxLength},
     * the code throws an exception and the string bytes are not read.
     *
     * @param maxLength the max length of the string
     * @return the string
     * @throws IllegalStateException if the string length is invalid or higher than {@code maxLength}
     */
    public String readSizedString(int maxLength) throws IOException {
        int length = readVarInt();
        byte[] bytes = readBytes(length);
        final String str = new String(bytes, StandardCharsets.UTF_8);
        Check.stateCondition(str.length() > maxLength,
                "String length ({0}) was higher than the max length of {1}", length, maxLength);
        return str;
    }

    public String readSizedString() throws IOException {
        return readSizedString(Integer.MAX_VALUE);
    }

    public byte[] readBytes(int length) throws IOException {
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[i] = input.readByte8();
        }
        return bytes;
    }

    public byte[] readByteArray() throws IOException {
        return readBytes(readVarInt());
    }

    public String[] readSizedStringArray(int maxLength) throws IOException {
        final int size = readVarInt();
        String[] strings = new String[size];
        for (int i = 0; i < size; i++) {
            strings[i] = readSizedString(maxLength);
        }
        return strings;
    }

    public String[] readSizedStringArray() throws IOException {
        return readSizedStringArray(Integer.MAX_VALUE);
    }

    public int[] readVarIntArray() throws IOException {
        final int size = readVarInt();
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = readVarInt();
        }
        return array;
    }

    public long[] readVarLongArray() throws IOException {
        final int size = readVarInt();
        long[] array = new long[size];
        for (int i = 0; i < size; i++) {
            array[i] = readVarLong();
        }
        return array;
    }

    public long[] readLongArray() throws IOException {
        final int size = readVarInt();
        long[] array = new long[size];
        for (int i = 0; i < size; i++) {
            array[i] = readLong();
        }
        return array;
    }

    public UUID readUuid() throws IOException {
        return new UUID(readLong(), readLong());
    }

    public Component readComponent(int maxLength) throws IOException {
        final String jsonObject = readSizedString(maxLength);
        return GsonComponentSerializer.gson().deserialize(jsonObject);
    }

    public Component readComponent() throws IOException {
        return readComponent(Integer.MAX_VALUE);
    }

    public <T> List<T> readVarIntList(@NotNull Function<MinestomObjectReader, T> supplier) throws IOException {
        return readList(readVarInt(), supplier);
    }

    public <T> List<T> readByteList(@NotNull Function<MinestomObjectReader, T> supplier) throws IOException {
        return readList(readByte(), supplier);
    }

    public <L, R> Either<L, R> readEither(Function<MinestomObjectReader, L> leftReader, Function<MinestomObjectReader, R> rightReader) throws IOException {
        if (readBoolean()) {
            return Either.left(leftReader.apply(this));
        } else {
            return Either.right(rightReader.apply(this));
        }
    }

    private <T> List<T> readList(int length, @NotNull Function<MinestomObjectReader, T> supplier) {
        List<T> list = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            list.add(supplier.apply(this));
        }
        return list;
    }
}