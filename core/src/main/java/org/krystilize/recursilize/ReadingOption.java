package org.krystilize.recursilize;

public enum ReadingOption {
    /**
     * Eager reading of the world. This will read the entire world into memory.
     */
    EAGER,
    /**
     * Lazy reading of the world. This will read the world as it is accessed.
     */
    LAZY
}
