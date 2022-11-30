package org.krystilize.recursilize.tree;

/**
 * This interface represents an immutable section of a tree.
 *
 * @param <T> the type of tree entry
 */
public interface TreeSection<T> {

    /**
     * @return the entry at the given position.
     * @throws IndexOutOfBoundsException if the position is out of bounds.
     */
    T get(int x, int y, int z);

    /**
     * @return the min x coordinate of this section
     */
    int minX();

    /**
     * @return the min y coordinate of this section
     */
    int minY();

    /**
     * @return the min z coordinate of this section
     */
    int minZ();

    /**
     * @return the max x coordinate of this section
     */
    int maxX();

    /**
     * @return the max y coordinate of this section
     */
    int maxY();

    /**
     * @return the max z coordinate of this section
     */
    int maxZ();

    static <T> TreeSection<T> view(RecursilizeTree<T> tree, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        return new TreeSection<T>() {
            @Override
            public T get(int x, int y, int z) {
                if (x < minX || x >= maxX || y < minY || y >= maxY || z < minZ || z >= maxZ) {
                    throw new IndexOutOfBoundsException("x (" + x + ") < minX (" + minX + ") || x (" + x + ") >= maxX ("
                            + maxX + ") || y (" + y + ") < minY (" + minY + ") || y (" + y + ") >= maxY (" + maxY +
                            ") || z (" + z + ") < minZ (" + minZ + ") || z (" + z + ") >= maxZ (" + maxZ + ")");
                }
                return tree.get(x, y, z);
            }

            @Override public int minX() { return minX; }
            @Override public int minY() { return minY; }
            @Override public int minZ() { return minZ; }
            @Override public int maxX() { return maxX; }
            @Override public int maxY() { return maxY; }
            @Override public int maxZ() { return maxZ; }
        };
    }
}
