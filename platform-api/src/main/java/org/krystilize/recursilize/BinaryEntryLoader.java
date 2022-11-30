package org.krystilize.recursilize;

abstract class BinaryEntryLoader<T> implements EntryLoader<T> {

    protected final BinarySource source;
    protected final BinarySerializer<T> serializer;

    BinaryEntryLoader(BinarySource source, BinarySerializer<T> serializer) {
        this.source = source;
        this.serializer = serializer;
    }
}
