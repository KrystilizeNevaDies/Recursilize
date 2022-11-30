package org.krystilize.recursilize;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public interface BinaryProcessor {
    /**
     * Processes all binary data before it is written.
     * @param in the input stream
     * @param out the output stream
     */
    void processWrite(InputStream in, OutputStream out) throws IOException;

    /**
     * Processes all binary data after it is read.
     * @param in the input stream
     * @param out the output stream
     */
    void processRead(InputStream in, OutputStream out) throws IOException;

    BinaryProcessor RAW = new BinaryProcessor() {
        @Override
        public void processWrite(InputStream in, OutputStream out) throws IOException {
            in.transferTo(out);
        }

        @Override
        public void processRead(InputStream in, OutputStream out) throws IOException {
            in.transferTo(out);
        }
    };

    BinaryProcessor GZIP = new BinaryProcessor() {
        @Override
        public void processWrite(InputStream in, OutputStream out) throws IOException {
            GZIPOutputStream gzip = new GZIPOutputStream(out);
            in.transferTo(gzip);
            gzip.finish();
        }

        @Override
        public void processRead(InputStream in, OutputStream out) throws IOException {
            GZIPInputStream gzip = new GZIPInputStream(in);
            gzip.transferTo(out);
        }
    };
}
