package org.krystilize.recursilize;

import java.io.*;
import java.util.function.Consumer;

public interface BinarySource {
    /**
     * Writes to the binary output.
     * @param writer the writer
     */
    void write(Writer writer) throws IOException;

    /**
     * Reads from the binary input.
     * @param reader the reader
     */
    void read(Reader reader) throws IOException;

    default BinarySource withProcessor(BinaryProcessor processor) {
        return BinarySource.from(writer -> {
            BinarySource.this.write(output -> {
                PipedOutputStream pipedOutput = new PipedOutputStream();
                PipedInputStream pipedInput = new PipedInputStream(pipedOutput);
                writer.write(pipedOutput);
                processor.processWrite(pipedInput, output);
            });
        }, reader -> {
            BinarySource.this.read(input -> {
                PipedOutputStream pipedOutput = new PipedOutputStream();
                PipedInputStream pipedInput = new PipedInputStream(pipedOutput);
                processor.processRead(input, pipedOutput);
                reader.read(pipedInput);
            });
        });
    }

    static BinarySource file(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                boolean ignored = file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        // Use the file object as a lock
        return BinarySource.from(writer -> {
            synchronized (file) {
                try (FileOutputStream out = new FileOutputStream(file)) {
                    writer.write(out);
                    out.flush();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }, reader -> {
            synchronized (file) {
                try (FileInputStream in = new FileInputStream(file)) {
                    reader.read(in);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    static BinarySource from(IOExceptionalConsumer<Writer> writable, IOExceptionalConsumer<Reader> readable) {
        return new BinarySource() {
            @Override
            public void write(Writer writer) throws IOException {
                writable.accept(writer);
            }

            @Override
            public void read(Reader reader) throws IOException {
                readable.accept(reader);
            }
        };
    }

    interface IOExceptionalConsumer<T> {
        void accept(T t) throws IOException;
    }

    interface Writer {
        void write(OutputStream output) throws IOException;
    }

    interface Reader {
        void read(InputStream input) throws IOException;
    }
}
