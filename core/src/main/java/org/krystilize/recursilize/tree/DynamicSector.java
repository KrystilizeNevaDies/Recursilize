package org.krystilize.recursilize.tree;

import java.util.List;

public class DynamicSector implements TreeSector {

    volatile Object xnynzn;
    volatile Object xpynzn;
    volatile Object xnypzn;
    volatile Object xpypzn;
    volatile Object xnynzp;
    volatile Object xpynzp;
    volatile Object xnypzp;
    volatile Object xpypzp;

    boolean compressed = false;

    public DynamicSector(Object contents) {
        xnynzn = contents;
        xpynzn = contents;
        xnypzn = contents;
        xpypzn = contents;
        xnynzp = contents;
        xpynzp = contents;
        xnypzp = contents;
        xpypzp = contents;
    }

    public DynamicSector(List<Object> contents) {
        if (contents.size() != 8) {
            throw new IllegalArgumentException("Expected 8 sectors, got " + contents.size());
        }
        xnynzn = contents.get(0);
        xpynzn = contents.get(1);
        xnypzn = contents.get(2);
        xpypzn = contents.get(3);
        xnynzp = contents.get(4);
        xpynzp = contents.get(5);
        xnypzp = contents.get(6);
        xpypzp = contents.get(7);
    }

    public static DynamicSector from(TreeSector sector) {
        if (sector instanceof DynamicSector dynamic) {
            return dynamic;
        }

        return new DynamicSector(sector.sectors());
    }

    @Override
    public Object xnynzn() {
        return xnynzn;
    }

    @Override
    public Object xpynzn() {
        return xpynzn;
    }

    @Override
    public Object xnypzn() {
        return xnypzn;
    }

    @Override
    public Object xpypzn() {
        return xpypzn;
    }

    @Override
    public Object xnynzp() {
        return xnynzp;
    }

    @Override
    public Object xpynzp() {
        return xpynzp;
    }

    @Override
    public Object xnypzp() {
        return xnypzp;
    }

    @Override
    public Object xpypzp() {
        return xpypzp;
    }

    public void set(boolean xLow, boolean yLow, boolean zLow, Object sector) {
        if (zLow) {
            if (yLow) {
                if (xLow) {
                    xnynzn = sector;
                } else {
                    xpynzn = sector;
                }
            } else {
                if (xLow) {
                    xnypzn = sector;
                } else {
                    xpypzn = sector;
                }
            }
        } else {
            if (yLow) {
                if (xLow) {
                    xnynzp = sector;
                } else {
                    xpynzp = sector;
                }
            } else {
                if (xLow) {
                    xnypzp = sector;
                } else {
                    xpypzp = sector;
                }
            }
        }
    }
}