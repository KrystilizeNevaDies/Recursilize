package org.krystilize.recursilize.tree;

import java.util.Objects;

public class DynamicStatefulTree<T> implements RecursilizeTree.Dynamic<T> {

    private final StatefulSector root;
    private final T defaultElement;

    public DynamicStatefulTree(T defaultElement) {
        this.root = new StatefulSector(RecursilizeTree.COORD_MIN, RecursilizeTree.COORD_MAX, defaultElement);
        this.defaultElement = defaultElement;
    }

    @Override
    public TreeSector root() {
        return root;
    }

    @Override
    public boolean set(int x, int y, int z, T value) {
        return internalSet(root, x, y, z, value);
    }

    private boolean internalSet(StatefulSector section, int x, int y, int z, T value) {
        boolean xSmall = x < section.midX();
        boolean ySmall = y < section.midY();
        boolean zSmall = z < section.midZ();
        Object sector = section.get(xSmall, ySmall, zSmall);

        // If this is the lowest level, we can just set the value
        if (section.isLowest()) {
            return section.set(xSmall, ySmall, zSmall, value);
        }

        // Check if we can recurse
        if (sector instanceof DynamicStatefulTree.StatefulSector stateful) {
            return internalSet(stateful, x, y, z, value);
        }

        // If the sector and the value are the same at this point, we don't need to do anything more
        if (Objects.equals(sector, value)) {
            return false;
        }

        // Otherwise create the new stateful section
        // Otherwise, we need to create a new stateful section and continue the internal set calls
        int newMinX = section.minX + (xSmall ? 0 : section.dx() / 2);
        int newMinY = section.minY + (ySmall ? 0 : section.dy() / 2);
        int newMinZ = section.minZ + (zSmall ? 0 : section.dz() / 2);
        int newMaxX = section.maxX - (xSmall ? section.dx() / 2 : 0);
        int newMaxY = section.maxY - (ySmall ? section.dy() / 2 : 0);
        int newMaxZ = section.maxZ - (zSmall ? section.dz() / 2 : 0);
        StatefulSector newSector = new StatefulSector(newMinX, newMinY, newMinZ,
                newMaxX, newMaxY, newMaxZ, defaultElement);
        section.set(xSmall, ySmall, zSmall, newSector);
        return internalSet(newSector, x, y, z, value);
    }


    @Override
    public void compress() {
        root.xnynzn = internalCompress(root.xnynzn);
        root.xpynzn = internalCompress(root.xpynzn);
        root.xnypzn = internalCompress(root.xnypzn);
        root.xpypzn = internalCompress(root.xpypzn);
        root.xnynzp = internalCompress(root.xnynzp);
        root.xpynzp = internalCompress(root.xpynzp);
        root.xnypzp = internalCompress(root.xnypzp);
        root.xpypzp = internalCompress(root.xpypzp);
    }

    private Object internalCompress(Object sector) {
        if (sector instanceof StatefulSector multi) {
            // Compress all children
            multi.xnynzn = internalCompress(multi.xnynzn);
            multi.xpynzn = internalCompress(multi.xpynzn);
            multi.xnypzn = internalCompress(multi.xnypzn);
            multi.xpypzn = internalCompress(multi.xpypzn);
            multi.xnynzp = internalCompress(multi.xnynzp);
            multi.xpynzp = internalCompress(multi.xpynzp);
            multi.xnypzp = internalCompress(multi.xnypzp);
            multi.xpypzp = internalCompress(multi.xpypzp);

            // Check for inline compression
            int count = (int) multi.sectors()
                    .stream()
                    .filter(s -> !(s instanceof StatefulSector))
                    .count();

            if (count == 8) {
                // All elements are non-sectors, lets see if we can compress through inlining
                Object identity = multi.sectors().stream().findAny().orElseThrow();
                boolean canMerge = multi.sectors().stream().allMatch(other -> Objects.equals(identity, other));
                if (canMerge) {
                    return multi.sectors().get(0);
                }
            }
        }
        return sector;
    }

    private static class StatefulSector implements TreeSector {

        int minX, minY, minZ;
        int maxX, maxY, maxZ;
        Object xnynzn, xpynzn, xnypzn, xpypzn, xnynzp, xpynzp, xnypzp, xpypzp;

        public StatefulSector(int min, int max, Object defaultElement) {
            this(min, min, min, max, max, max, defaultElement);
        }

        public StatefulSector(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, Object defaultElement) {
            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxY = maxY;
            this.maxZ = maxZ;

            this.xnynzn = defaultElement;
            this.xpynzn = defaultElement;
            this.xnypzn = defaultElement;
            this.xpypzn = defaultElement;
            this.xnynzp = defaultElement;
            this.xpynzp = defaultElement;
            this.xnypzp = defaultElement;
            this.xpypzp = defaultElement;
        }

        int midX() { return (minX + maxX) / 2; }
        int midY() { return (minY + maxY) / 2; }
        int midZ() { return (minZ + maxZ) / 2; }

        @Override public Object xnynzn() { return xnynzn; }
        @Override public Object xpynzn() { return xpynzn; }
        @Override public Object xnypzn() { return xnypzn; }
        @Override public Object xpypzn() { return xpypzn; }
        @Override public Object xnynzp() { return xnynzp; }
        @Override public Object xpynzp() { return xpynzp; }
        @Override public Object xnypzp() { return xnypzp; }
        @Override public Object xpypzp() { return xpypzp; }

        public boolean isLowest() {
            return minX == maxX - 2 && minY == maxY - 2 && minZ == maxZ - 2;
        }

        public int dx() { return maxX - minX; }
        public int dy() { return maxY - minY; }
        public int dz() { return maxZ - minZ; }


        public boolean set(boolean xSmall, boolean ySmall, boolean zSmall, Object sector) {
            Object old;
            if (zSmall) {
                if (ySmall) {
                    if (xSmall) {
                        old = xnynzn; xnynzn = sector;
                    } else {
                        old = xpynzn; xpynzn = sector;
                    }
                } else {
                    if (xSmall) {
                        old = xnypzn; xnypzn = sector;
                    } else {
                        old = xpypzn; xpypzn = sector;
                    }
                }
            } else {
                if (ySmall) {
                    if (xSmall) {
                        old = xnynzp; xnynzp = sector;
                    } else {
                        old = xpynzp; xpynzp = sector;
                    }
                } else {
                    if (xSmall) {
                        old = xnypzp; xnypzp = sector;
                    } else {
                        old = xpypzp; xpypzp = sector;
                    }
                }
            }

            return !Objects.equals(old, sector);
        }
    }
}
