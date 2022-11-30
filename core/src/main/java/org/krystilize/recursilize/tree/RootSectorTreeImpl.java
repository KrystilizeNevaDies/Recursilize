package org.krystilize.recursilize.tree;

record RootSectorTreeImpl<T>(TreeSector sector) implements RecursilizeTree<T> {
    @Override
    public TreeSector root() {
        return sector;
    }
}
