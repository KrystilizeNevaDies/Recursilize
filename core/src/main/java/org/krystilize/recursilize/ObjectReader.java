package org.krystilize.recursilize;

import com.github.jinahya.bit.io.BitInput;

import java.io.IOException;

public interface ObjectReader<T> {
    T read(BitInput input) throws IOException;
}
