package org.krystilize.recursilize.tree;

import org.krystilize.recursilize.Utils;
import org.krystilize.recursilize.Visitable;
import org.krystilize.recursilize.Visitor;

import java.util.function.Function;

public interface RecursilizeTree<T> extends Visitable<T> {

    // Unfortunately, using min and max int value causes stack overflow...
    int COORD_MIN = -536_870_912;
    int COORD_MAX = 536_870_912;

    default T get(int x, int y, int z) {
        //noinspection unchecked
        return (T) TreeSector.fetchBlock(x, y, z, COORD_MIN, COORD_MIN, COORD_MIN, COORD_MAX, COORD_MAX, COORD_MAX, root());
    }

    TreeSector root();

    @Override
    default void visit(Visitor<T> visitor) {
        Utils.genericTreeVisit(visitor, root(), COORD_MIN, COORD_MIN, COORD_MIN, COORD_MAX, COORD_MAX, COORD_MAX);
    }

    /**
     * Creates a new tree query builder.
     *
     * @return a new tree query builder
     */
    static TreeQuery.Start query() {
        return new TreeQueryImpl.Start();
    }

    /**
     * This creates a dynamic, in-memory implementation of the {@link RecursilizeTree} interface.
     *
     * @param identity The identity value for the tree.
     * @param <T>      The type of the tree.
     * @return A new, empty tree.
     */
    static <T> Dynamic<T> dynamic(T identity) {
        return new DynamicStatefulTree<>(identity);
    }


    /**
     * This creates a dynamic, in-memory implementation of the {@link RecursilizeTree} interface from an existing tree.
     *
     * @param tree The tree to copy.
     * @param <T>  The type of the tree.
     * @return A new, empty tree.
     */
    static <T> Dynamic<T> dynamic(RecursilizeTree<T> tree, T defaultElement) {
        Dynamic<T> dynamic = dynamic(defaultElement);
        tree.visit((entry, minX, minY, minZ, maxX, maxY, maxZ) -> {
            for (int x = minX; x < maxX; x++) {
                for (int y = minY; y < maxY; y++) {
                    for (int z = minZ; z < maxZ; z++) {
                        dynamic.set(x, y, z, entry);
                    }
                }
            }
        });
        dynamic.compress();
        return dynamic;
    }

    /**
     * Creates an immutable, in-memory implementation of the {@link RecursilizeTree} interface, using the provided root node.
     *
     * @param root The root node of the tree.
     * @param <T>  The type of the tree.
     * @return A new, immutable tree.
     */
    static <T> RecursilizeTree<T> from(TreeSector root) {
        return new RootSectorTreeImpl<>(root);
    }

    // TODO: Javadoc
    static <E> RecursilizeTree<E> copyOf(RecursilizeTree<E> tree) {
        return from(TreeSector.copyOf(tree.root()));
    }

    default int size() {
        return TreeSector.size(root());
    }

    interface Dynamic<T> extends RecursilizeTree<T> {
        /**
         * Sets the element at the given coordinates.
         *
         * @param x     the x coordinate
         * @param y     the y coordinate
         * @param z     the z coordinate
         * @param value the value to set
         * @return true if the tree changed as a result of this call, false otherwise
         */
        boolean set(int x, int y, int z, T value);

        /**
         * Attempts to compress this dynamic tree into a tree with the least amount of nodes possible.
         * <p>
         * Note that this method does not guarantee that the tree will be compressed optimally or that it will be
         * compressed at all. Implementations are expected (not required) to compress the tree as much as possible.
         */
        default void compress() {
        }
    }

    /**
     * Compares the two given trees for equality. This implementation does so by comparing every level of the tree with
     * the other tree, and if any level is not equal, the trees are not equal.
     * Level comparison entails that both level's objects are either equal according to {@link Object#equals(Object)} or
     * if the level's object is another level, the two levels are compared recursively.
     *
     * @param a The first tree.
     * @param b The second tree.
     * @return {@code true} if the trees are equal, {@code false} otherwise.
     */
    static boolean equals(RecursilizeTree<?> a, RecursilizeTree<?> b) {
        return TreeSector.deepEquals(a.root(), b.root());
    }

    /**
     * Converts this tree into a string representation.
     * @param tree The tree to convert.
     * @return A string representation of the tree.
     */
    static String toString(RecursilizeTree<?> tree) {
        return toString(tree, Object::toString);
    }

    static <T> String toString(RecursilizeTree<T> tree, Function<T, String> toString) {
        StringBuilder sb = new StringBuilder();
        TreeSector.toString(tree.root(), 0,
                sb::append, // .append('\n'),
                obj -> toString.apply((T) obj));
        return sb.toString();
    }
}
