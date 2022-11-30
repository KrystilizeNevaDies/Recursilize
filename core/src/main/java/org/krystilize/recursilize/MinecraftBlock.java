package org.krystilize.recursilize;

import com.github.jinahya.bit.io.BitInput;
import com.github.jinahya.bit.io.BitOutput;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface MinecraftBlock {

    static MinecraftBlock from(String... namespaceAndProperties) {
        if (namespaceAndProperties.length == 0) {
            throw new IllegalArgumentException("namespaceAndProperties must not be empty");
        }
        String namespace = namespaceAndProperties[0];
        Map<String, String> properties = new HashMap<>();
        for (int i = 1; i < namespaceAndProperties.length - 1; i += 2) {
            String key = namespaceAndProperties[i];
            String value = namespaceAndProperties[i + 1];
            properties.put(key, value);
        }
        return from(namespace, properties);
    }

    static MinecraftBlock from(String namespace, Map<String, String> properties) {
        return new MinecraftBlockImpl(namespace, properties);
    }

    Map<String, String> properties();

    String namespace();

    default boolean equals(MinecraftBlock other) {
        return namespace().equals(other.namespace()) &&
                properties().equals(other.properties());
    }

    static void write(MinecraftBlock block, BitOutput output) throws IOException {
        Utils.UTF_8_WRITER.write(output, block.namespace());
        Utils.writeList(output, new ArrayList<>(block.properties().entrySet()), (e, o) -> {
            Utils.UTF_8_WRITER.write(o, e.getKey());
            Utils.UTF_8_WRITER.write(o, e.getValue());
        });
    }

    static MinecraftBlock read(BitInput input) throws IOException {
        String namespace = Utils.UTF_8_READER.read(input);
        int size = (int) input.readUnsignedLong(32);
        Map<String, String> properties = new HashMap<>();
        for (int i = 0; i < size; i++) {
            String key = Utils.UTF_8_READER.read(input);
            String value = Utils.UTF_8_READER.read(input);
            properties.put(key, value);
        }
        return from(namespace, properties);
    }

    BinarySerializer<MinecraftBlock> SERIALIZER = new BinarySerializer<>() {
        @Override
        public MinecraftBlock read(BitInput input) throws IOException {
            return MinecraftBlock.read(input);
        }

        @Override
        public void write(MinecraftBlock value, BitOutput output) throws IOException {
            MinecraftBlock.write(value, output);
        }
    };
}
