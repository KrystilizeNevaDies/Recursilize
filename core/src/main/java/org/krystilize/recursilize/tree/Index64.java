package org.krystilize.recursilize.tree;

import java.util.stream.IntStream;

/**
 * Index64 is used as an index for 64x backed trees.
 */
public class Index64 {
    public Index64() {
    }

    public Index64(Object defaultValue) {
        keys().forEach(key -> this.set(key, defaultValue));
    }

    //////////////
    // Dir Keys //
    //////////////

    private static final int XNN = 0;
    private static final int XPN = 1;
    private static final int XNP = 2;
    private static final int XPP = 3;

    private static final int YNN = 0;
    private static final int YPN = 4;
    private static final int YNP = 8;
    private static final int YPP = 12;

    private static final int ZNN = 0;
    private static final int ZPN = 16;
    private static final int ZNP = 32;
    private static final int ZPP = 48;

    /////////////
    // Indices //
    /////////////

    // Indices are all groups of three directions.
    // There are 64 of them.

    private Object XNN_YNN_ZNN; private static final int INDEX_XNN_YNN_ZNN = XNN | YNN | ZNN;
    private Object XPN_YNN_ZNN; private static final int INDEX_XPN_YNN_ZNN = XPN | YNN | ZNN;
    private Object XNP_YNN_ZNN; private static final int INDEX_XNP_YNN_ZNN = XNP | YNN | ZNN;
    private Object XPP_YNN_ZNN; private static final int INDEX_XPP_YNN_ZNN = XPP | YNN | ZNN;

    private Object XNN_YPN_ZNN; private static final int INDEX_XNN_YPN_ZNN = XNN | YPN | ZNN;
    private Object XPN_YPN_ZNN; private static final int INDEX_XPN_YPN_ZNN = XPN | YPN | ZNN;
    private Object XNP_YPN_ZNN; private static final int INDEX_XNP_YPN_ZNN = XNP | YPN | ZNN;
    private Object XPP_YPN_ZNN; private static final int INDEX_XPP_YPN_ZNN = XPP | YPN | ZNN;

    private Object XNN_YNP_ZNN; private static final int INDEX_XNN_YNP_ZNN = XNN | YNP | ZNN;
    private Object XPN_YNP_ZNN; private static final int INDEX_XPN_YNP_ZNN = XPN | YNP | ZNN;
    private Object XNP_YNP_ZNN; private static final int INDEX_XNP_YNP_ZNN = XNP | YNP | ZNN;
    private Object XPP_YNP_ZNN; private static final int INDEX_XPP_YNP_ZNN = XPP | YNP | ZNN;

    private Object XNN_YPP_ZNN; private static final int INDEX_XNN_YPP_ZNN = XNN | YPP | ZNN;
    private Object XPN_YPP_ZNN; private static final int INDEX_XPN_YPP_ZNN = XPN | YPP | ZNN;
    private Object XNP_YPP_ZNN; private static final int INDEX_XNP_YPP_ZNN = XNP | YPP | ZNN;
    private Object XPP_YPP_ZNN; private static final int INDEX_XPP_YPP_ZNN = XPP | YPP | ZNN;

    private Object XNN_YNN_ZPN; private static final int INDEX_XNN_YNN_ZPN = XNN | YNN | ZPN;
    private Object XPN_YNN_ZPN; private static final int INDEX_XPN_YNN_ZPN = XPN | YNN | ZPN;
    private Object XNP_YNN_ZPN; private static final int INDEX_XNP_YNN_ZPN = XNP | YNN | ZPN;
    private Object XPP_YNN_ZPN; private static final int INDEX_XPP_YNN_ZPN = XPP | YNN | ZPN;

    private Object XNN_YPN_ZPN; private static final int INDEX_XNN_YPN_ZPN = XNN | YPN | ZPN;
    private Object XPN_YPN_ZPN; private static final int INDEX_XPN_YPN_ZPN = XPN | YPN | ZPN;
    private Object XNP_YPN_ZPN; private static final int INDEX_XNP_YPN_ZPN = XNP | YPN | ZPN;
    private Object XPP_YPN_ZPN; private static final int INDEX_XPP_YPN_ZPN = XPP | YPN | ZPN;

    private Object XNN_YNP_ZPN; private static final int INDEX_XNN_YNP_ZPN = XNN | YNP | ZPN;
    private Object XPN_YNP_ZPN; private static final int INDEX_XPN_YNP_ZPN = XPN | YNP | ZPN;
    private Object XNP_YNP_ZPN; private static final int INDEX_XNP_YNP_ZPN = XNP | YNP | ZPN;
    private Object XPP_YNP_ZPN; private static final int INDEX_XPP_YNP_ZPN = XPP | YNP | ZPN;

    private Object XNN_YPP_ZPN; private static final int INDEX_XNN_YPP_ZPN = XNN | YPP | ZPN;
    private Object XPN_YPP_ZPN; private static final int INDEX_XPN_YPP_ZPN = XPN | YPP | ZPN;
    private Object XNP_YPP_ZPN; private static final int INDEX_XNP_YPP_ZPN = XNP | YPP | ZPN;
    private Object XPP_YPP_ZPN; private static final int INDEX_XPP_YPP_ZPN = XPP | YPP | ZPN;

    private Object XNN_YNN_ZNP; private static final int INDEX_XNN_YNN_ZNP = XNN | YNN | ZNP;
    private Object XPN_YNN_ZNP; private static final int INDEX_XPN_YNN_ZNP = XPN | YNN | ZNP;
    private Object XNP_YNN_ZNP; private static final int INDEX_XNP_YNN_ZNP = XNP | YNN | ZNP;
    private Object XPP_YNN_ZNP; private static final int INDEX_XPP_YNN_ZNP = XPP | YNN | ZNP;

    private Object XNN_YPN_ZNP; private static final int INDEX_XNN_YPN_ZNP = XNN | YPN | ZNP;
    private Object XPN_YPN_ZNP; private static final int INDEX_XPN_YPN_ZNP = XPN | YPN | ZNP;
    private Object XNP_YPN_ZNP; private static final int INDEX_XNP_YPN_ZNP = XNP | YPN | ZNP;
    private Object XPP_YPN_ZNP; private static final int INDEX_XPP_YPN_ZNP = XPP | YPN | ZNP;

    private Object XNN_YNP_ZNP; private static final int INDEX_XNN_YNP_ZNP = XNN | YNP | ZNP;
    private Object XPN_YNP_ZNP; private static final int INDEX_XPN_YNP_ZNP = XPN | YNP | ZNP;
    private Object XNP_YNP_ZNP; private static final int INDEX_XNP_YNP_ZNP = XNP | YNP | ZNP;
    private Object XPP_YNP_ZNP; private static final int INDEX_XPP_YNP_ZNP = XPP | YNP | ZNP;

    private Object XNN_YPP_ZNP; private static final int INDEX_XNN_YPP_ZNP = XNN | YPP | ZNP;
    private Object XPN_YPP_ZNP; private static final int INDEX_XPN_YPP_ZNP = XPN | YPP | ZNP;
    private Object XNP_YPP_ZNP; private static final int INDEX_XNP_YPP_ZNP = XNP | YPP | ZNP;
    private Object XPP_YPP_ZNP; private static final int INDEX_XPP_YPP_ZNP = XPP | YPP | ZNP;

    private Object XNN_YNN_ZPP; private static final int INDEX_XNN_YNN_ZPP = XNN | YNN | ZPP;
    private Object XPN_YNN_ZPP; private static final int INDEX_XPN_YNN_ZPP = XPN | YNN | ZPP;
    private Object XNP_YNN_ZPP; private static final int INDEX_XNP_YNN_ZPP = XNP | YNN | ZPP;
    private Object XPP_YNN_ZPP; private static final int INDEX_XPP_YNN_ZPP = XPP | YNN | ZPP;

    private Object XNN_YPN_ZPP; private static final int INDEX_XNN_YPN_ZPP = XNN | YPN | ZPP;
    private Object XPN_YPN_ZPP; private static final int INDEX_XPN_YPN_ZPP = XPN | YPN | ZPP;
    private Object XNP_YPN_ZPP; private static final int INDEX_XNP_YPN_ZPP = XNP | YPN | ZPP;
    private Object XPP_YPN_ZPP; private static final int INDEX_XPP_YPN_ZPP = XPP | YPN | ZPP;

    private Object XNN_YNP_ZPP; private static final int INDEX_XNN_YNP_ZPP = XNN | YNP | ZPP;
    private Object XPN_YNP_ZPP; private static final int INDEX_XPN_YNP_ZPP = XPN | YNP | ZPP;
    private Object XNP_YNP_ZPP; private static final int INDEX_XNP_YNP_ZPP = XNP | YNP | ZPP;
    private Object XPP_YNP_ZPP; private static final int INDEX_XPP_YNP_ZPP = XPP | YNP | ZPP;

    private Object XNN_YPP_ZPP; private static final int INDEX_XNN_YPP_ZPP = XNN | YPP | ZPP;
    private Object XPN_YPP_ZPP; private static final int INDEX_XPN_YPP_ZPP = XPN | YPP | ZPP;
    private Object XNP_YPP_ZPP; private static final int INDEX_XNP_YPP_ZPP = XNP | YPP | ZPP;
    private Object XPP_YPP_ZPP; private static final int INDEX_XPP_YPP_ZPP = XPP | YPP | ZPP;

    public IntStream keys() {
        return IntStream.range(0, 64);
    }

    public Object get(int key) {
        return switch(key) {
            case INDEX_XNN_YNN_ZNN -> XNN_YNN_ZNN;
            case INDEX_XPN_YNN_ZNN -> XPN_YNN_ZNN;
            case INDEX_XNP_YNN_ZNN -> XNP_YNN_ZNN;
            case INDEX_XPP_YNN_ZNN -> XPP_YNN_ZNN;

            case INDEX_XNN_YPN_ZNN -> XNN_YPN_ZNN;
            case INDEX_XPN_YPN_ZNN -> XPN_YPN_ZNN;
            case INDEX_XNP_YPN_ZNN -> XNP_YPN_ZNN;
            case INDEX_XPP_YPN_ZNN -> XPP_YPN_ZNN;

            case INDEX_XNN_YNP_ZNN -> XNN_YNP_ZNN;
            case INDEX_XPN_YNP_ZNN -> XPN_YNP_ZNN;
            case INDEX_XNP_YNP_ZNN -> XNP_YNP_ZNN;
            case INDEX_XPP_YNP_ZNN -> XPP_YNP_ZNN;

            case INDEX_XNN_YPP_ZNN -> XNN_YPP_ZNN;
            case INDEX_XPN_YPP_ZNN -> XPN_YPP_ZNN;
            case INDEX_XNP_YPP_ZNN -> XNP_YPP_ZNN;
            case INDEX_XPP_YPP_ZNN -> XPP_YPP_ZNN;

            case INDEX_XNN_YNN_ZPN -> XNN_YNN_ZPN;
            case INDEX_XPN_YNN_ZPN -> XPN_YNN_ZPN;
            case INDEX_XNP_YNN_ZPN -> XNP_YNN_ZPN;
            case INDEX_XPP_YNN_ZPN -> XPP_YNN_ZPN;

            case INDEX_XNN_YPN_ZPN -> XNN_YPN_ZPN;
            case INDEX_XPN_YPN_ZPN -> XPN_YPN_ZPN;
            case INDEX_XNP_YPN_ZPN -> XNP_YPN_ZPN;
            case INDEX_XPP_YPN_ZPN -> XPP_YPN_ZPN;

            case INDEX_XNN_YNP_ZPN -> XNN_YNP_ZPN;
            case INDEX_XPN_YNP_ZPN -> XPN_YNP_ZPN;
            case INDEX_XNP_YNP_ZPN -> XNP_YNP_ZPN;
            case INDEX_XPP_YNP_ZPN -> XPP_YNP_ZPN;

            case INDEX_XNN_YPP_ZPN -> XNN_YPP_ZPN;
            case INDEX_XPN_YPP_ZPN -> XPN_YPP_ZPN;
            case INDEX_XNP_YPP_ZPN -> XNP_YPP_ZPN;
            case INDEX_XPP_YPP_ZPN -> XPP_YPP_ZPN;

            case INDEX_XNN_YNN_ZNP -> XNN_YNN_ZNP;
            case INDEX_XPN_YNN_ZNP -> XPN_YNN_ZNP;
            case INDEX_XNP_YNN_ZNP -> XNP_YNN_ZNP;
            case INDEX_XPP_YNN_ZNP -> XPP_YNN_ZNP;

            case INDEX_XNN_YPN_ZNP -> XNN_YPN_ZNP;
            case INDEX_XPN_YPN_ZNP -> XPN_YPN_ZNP;
            case INDEX_XNP_YPN_ZNP -> XNP_YPN_ZNP;
            case INDEX_XPP_YPN_ZNP -> XPP_YPN_ZNP;

            case INDEX_XNN_YNP_ZNP -> XNN_YNP_ZNP;
            case INDEX_XPN_YNP_ZNP -> XPN_YNP_ZNP;
            case INDEX_XNP_YNP_ZNP -> XNP_YNP_ZNP;
            case INDEX_XPP_YNP_ZNP -> XPP_YNP_ZNP;

            case INDEX_XNN_YPP_ZNP -> XNN_YPP_ZNP;
            case INDEX_XPN_YPP_ZNP -> XPN_YPP_ZNP;
            case INDEX_XNP_YPP_ZNP -> XNP_YPP_ZNP;
            case INDEX_XPP_YPP_ZNP -> XPP_YPP_ZNP;

            case INDEX_XNN_YNN_ZPP -> XNN_YNN_ZPP;
            case INDEX_XPN_YNN_ZPP -> XPN_YNN_ZPP;
            case INDEX_XNP_YNN_ZPP -> XNP_YNN_ZPP;
            case INDEX_XPP_YNN_ZPP -> XPP_YNN_ZPP;

            case INDEX_XNN_YPN_ZPP -> XNN_YPN_ZPP;
            case INDEX_XPN_YPN_ZPP -> XPN_YPN_ZPP;
            case INDEX_XNP_YPN_ZPP -> XNP_YPN_ZPP;
            case INDEX_XPP_YPN_ZPP -> XPP_YPN_ZPP;

            case INDEX_XNN_YNP_ZPP -> XNN_YNP_ZPP;
            case INDEX_XPN_YNP_ZPP -> XPN_YNP_ZPP;
            case INDEX_XNP_YNP_ZPP -> XNP_YNP_ZPP;
            case INDEX_XPP_YNP_ZPP -> XPP_YNP_ZPP;

            case INDEX_XNN_YPP_ZPP -> XNN_YPP_ZPP;
            case INDEX_XPN_YPP_ZPP -> XPN_YPP_ZPP;
            case INDEX_XNP_YPP_ZPP -> XNP_YPP_ZPP;
            case INDEX_XPP_YPP_ZPP -> XPP_YPP_ZPP;

            default -> throw new IllegalStateException("Unexpected value: " + key);
        };
    }

    public void set(int key, Object obj) {
        switch(key) {
            case INDEX_XNN_YNN_ZNN -> XNN_YNN_ZNN = obj;
            case INDEX_XPN_YNN_ZNN -> XPN_YNN_ZNN = obj;
            case INDEX_XNP_YNN_ZNN -> XNP_YNN_ZNN = obj;
            case INDEX_XPP_YNN_ZNN -> XPP_YNN_ZNN = obj;

            case INDEX_XNN_YPN_ZNN -> XNN_YPN_ZNN = obj;
            case INDEX_XPN_YPN_ZNN -> XPN_YPN_ZNN = obj;
            case INDEX_XNP_YPN_ZNN -> XNP_YPN_ZNN = obj;
            case INDEX_XPP_YPN_ZNN -> XPP_YPN_ZNN = obj;

            case INDEX_XNN_YNP_ZNN -> XNN_YNP_ZNN = obj;
            case INDEX_XPN_YNP_ZNN -> XPN_YNP_ZNN = obj;
            case INDEX_XNP_YNP_ZNN -> XNP_YNP_ZNN = obj;
            case INDEX_XPP_YNP_ZNN -> XPP_YNP_ZNN = obj;

            case INDEX_XNN_YPP_ZNN -> XNN_YPP_ZNN = obj;
            case INDEX_XPN_YPP_ZNN -> XPN_YPP_ZNN = obj;
            case INDEX_XNP_YPP_ZNN -> XNP_YPP_ZNN = obj;
            case INDEX_XPP_YPP_ZNN -> XPP_YPP_ZNN = obj;

            case INDEX_XNN_YNN_ZPN -> XNN_YNN_ZPN = obj;
            case INDEX_XPN_YNN_ZPN -> XPN_YNN_ZPN = obj;
            case INDEX_XNP_YNN_ZPN -> XNP_YNN_ZPN = obj;
            case INDEX_XPP_YNN_ZPN -> XPP_YNN_ZPN = obj;

            case INDEX_XNN_YPN_ZPN -> XNN_YPN_ZPN = obj;
            case INDEX_XPN_YPN_ZPN -> XPN_YPN_ZPN = obj;
            case INDEX_XNP_YPN_ZPN -> XNP_YPN_ZPN = obj;
            case INDEX_XPP_YPN_ZPN -> XPP_YPN_ZPN = obj;

            case INDEX_XNN_YNP_ZPN -> XNN_YNP_ZPN = obj;
            case INDEX_XPN_YNP_ZPN -> XPN_YNP_ZPN = obj;
            case INDEX_XNP_YNP_ZPN -> XNP_YNP_ZPN = obj;
            case INDEX_XPP_YNP_ZPN -> XPP_YNP_ZPN = obj;

            case INDEX_XNN_YPP_ZPN -> XNN_YPP_ZPN = obj;
            case INDEX_XPN_YPP_ZPN -> XPN_YPP_ZPN = obj;
            case INDEX_XNP_YPP_ZPN -> XNP_YPP_ZPN = obj;
            case INDEX_XPP_YPP_ZPN -> XPP_YPP_ZPN = obj;

            case INDEX_XNN_YNN_ZNP -> XNN_YNN_ZNP = obj;
            case INDEX_XPN_YNN_ZNP -> XPN_YNN_ZNP = obj;
            case INDEX_XNP_YNN_ZNP -> XNP_YNN_ZNP = obj;
            case INDEX_XPP_YNN_ZNP -> XPP_YNN_ZNP = obj;

            case INDEX_XNN_YPN_ZNP -> XNN_YPN_ZNP = obj;
            case INDEX_XPN_YPN_ZNP -> XPN_YPN_ZNP = obj;
            case INDEX_XNP_YPN_ZNP -> XNP_YPN_ZNP = obj;
            case INDEX_XPP_YPN_ZNP -> XPP_YPN_ZNP = obj;

            case INDEX_XNN_YNP_ZNP -> XNN_YNP_ZNP = obj;
            case INDEX_XPN_YNP_ZNP -> XPN_YNP_ZNP = obj;
            case INDEX_XNP_YNP_ZNP -> XNP_YNP_ZNP = obj;
            case INDEX_XPP_YNP_ZNP -> XPP_YNP_ZNP = obj;

            case INDEX_XNN_YPP_ZNP -> XNN_YPP_ZNP = obj;
            case INDEX_XPN_YPP_ZNP -> XPN_YPP_ZNP = obj;
            case INDEX_XNP_YPP_ZNP -> XNP_YPP_ZNP = obj;
            case INDEX_XPP_YPP_ZNP -> XPP_YPP_ZNP = obj;

            case INDEX_XNN_YNN_ZPP -> XNN_YNN_ZPP = obj;
            case INDEX_XPN_YNN_ZPP -> XPN_YNN_ZPP = obj;
            case INDEX_XNP_YNN_ZPP -> XNP_YNN_ZPP = obj;
            case INDEX_XPP_YNN_ZPP -> XPP_YNN_ZPP = obj;

            case INDEX_XNN_YPN_ZPP -> XNN_YPN_ZPP = obj;
            case INDEX_XPN_YPN_ZPP -> XPN_YPN_ZPP = obj;
            case INDEX_XNP_YPN_ZPP -> XNP_YPN_ZPP = obj;
            case INDEX_XPP_YPN_ZPP -> XPP_YPN_ZPP = obj;

            case INDEX_XNN_YNP_ZPP -> XNN_YNP_ZPP = obj;
            case INDEX_XPN_YNP_ZPP -> XPN_YNP_ZPP = obj;
            case INDEX_XNP_YNP_ZPP -> XNP_YNP_ZPP = obj;
            case INDEX_XPP_YNP_ZPP -> XPP_YNP_ZPP = obj;

            case INDEX_XNN_YPP_ZPP -> XNN_YPP_ZPP = obj;
            case INDEX_XPN_YPP_ZPP -> XPN_YPP_ZPP = obj;
            case INDEX_XNP_YPP_ZPP -> XNP_YPP_ZPP = obj;
            case INDEX_XPP_YPP_ZPP -> XPP_YPP_ZPP = obj;

            default -> throw new IllegalStateException("Unexpected value: " + key);
        };
    }
}
