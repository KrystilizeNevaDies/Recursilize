package org.krystilize.recursilize;

import com.github.jinahya.bit.io.BitOutput;

import java.io.IOException;

public interface ObjectWriter<T> {
    void write(BitOutput output, T value) throws IOException;
}
