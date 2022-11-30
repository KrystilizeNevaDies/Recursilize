package org.krystilize.recursilize;

import java.util.Map;
import java.util.Objects;

record MinecraftBlockImpl(String namespace, Map<String, String> properties, int hash) implements MinecraftBlock {

    public MinecraftBlockImpl(String namespace, Map<String, String> properties) {
        this(namespace, properties, Objects.hash(namespace, properties));
    }

    public MinecraftBlockImpl {
        properties = Map.copyOf(properties);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MinecraftBlockImpl that = (MinecraftBlockImpl) o;
        return MinecraftBlock.super.equals(that);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public String toString() {
        return namespace + properties;
    }
}
