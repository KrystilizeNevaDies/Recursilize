package org.krystilize.recursilize;

/**
 * The visitor is used to visit every entry in a tree.
 */
public interface Visitor<T> {
    /**
     * Visits an entry in a tree.
     * <p>
     * This visited entry may apply for the coordinates within a cuboid shape.
     *
     * @param entry the entry
     * @param minX  the minimum x coordinate of the entry
     * @param minY  the minimum y coordinate of the entry
     * @param minZ  the minimum z coordinate of the entry
     * @param maxX  the maximum x coordinate of the entry
     * @param maxY  the maximum y coordinate of the entry
     * @param maxZ  the maximum z coordinate of the entry
     */
    void visit(T entry, int minX, int minY, int minZ, int maxX, int maxY, int maxZ);
}
