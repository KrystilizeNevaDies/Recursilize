package org.krystilize.recursilize.tree;

public record TreeSectorImpl(Object xnynzn, Object xpynzn, Object xnypzn, Object xpypzn,
                             Object xnynzp, Object xpynzp, Object xnypzp, Object xpypzp) implements TreeSector {
    public TreeSectorImpl(Object value) {
        this(value, value, value, value, value, value, value, value);
    }
}
