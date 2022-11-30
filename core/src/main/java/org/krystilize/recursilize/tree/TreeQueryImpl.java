package org.krystilize.recursilize.tree;

record TreeQueryImpl(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) implements TreeQuery {

    private TreeQueryImpl(Start start) {
        this(start.minX, start.minY, start.minZ, start.maxX, start.maxY, start.maxZ);
    }

    static class Start implements TreeQuery.Start, TreeQuery.Min, TreeQuery.Max {

        int minX, minY, minZ = 0;
        int maxX, maxY, maxZ = 0;

        @Override
        public Max min(int x, int y, int z) {
            minX = x;
            minY = y;
            minZ = z;
            return this;
        }

        @Override
        public TreeQuery max(int x, int y, int z) {
            maxX = x;
            maxY = y;
            maxZ = z;
            return new TreeQueryImpl(this);
        }
    }

    @Override
    public <E> TreeSection<E> execute(RecursilizeTree<E> tree) {
        RecursilizeTree<E> copy = RecursilizeTree.copyOf(tree);
        // TODO: Find optimizations for this
        return new TreeSection<>() {
            @Override
            public E get(int x, int y, int z) {
                if (x < minX || x >= maxX || y < minY || y >= maxY || z < minZ || z >= maxZ) {
                    throw new IndexOutOfBoundsException("x (" + x + ") < minX (" + minX + ") || x (" + x + ") >= maxX ("
                            + maxX + ") || y (" + y + ") < minY (" + minY + ") || y (" + y + ") >= maxY (" + maxY +
                            ") || z (" + z + ") < minZ (" + minZ + ") || z (" + z + ") >= maxZ (" + maxZ + ")");
                }
                return copy.get(x, y, z);
            }

            @Override
            public int minX() {
                return minX;
            }

            @Override
            public int minY() {
                return minY;
            }

            @Override
            public int minZ() {
                return minZ;
            }

            @Override
            public int maxX() {
                return maxX;
            }

            @Override
            public int maxY() {
                return maxY;
            }

            @Override
            public int maxZ() {
                return maxZ;
            }
        };
    }
}
