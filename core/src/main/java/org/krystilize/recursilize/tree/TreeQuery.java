package org.krystilize.recursilize.tree;

public interface TreeQuery {

    interface Start extends Min {
    }

    interface Min {
        Max min(int x, int y, int z);
    }

    interface Max {
        TreeQuery max(int x, int y, int z);
    }

    <E> TreeSection<E> execute(RecursilizeTree<E> tree);
}
