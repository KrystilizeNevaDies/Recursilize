package org.krystilize.recursilize.tree;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public interface TreeSector {

    Object xnynzn();

    Object xpynzn();

    Object xnypzn();

    Object xpypzn();

    Object xnynzp();

    Object xpynzp();

    Object xnypzp();

    Object xpypzp();

    default Object get(boolean xLow, boolean yLow, boolean zLow) {
        if (zLow) {
            if (yLow) {
                if (xLow) {
                    return xnynzn();
                } else {
                    return xpynzn();
                }
            } else {
                if (xLow) {
                    return xnypzn();
                } else {
                    return xpypzn();
                }
            }
        } else {
            if (yLow) {
                if (xLow) {
                    return xnynzp();
                } else {
                    return xpynzp();
                }
            } else {
                if (xLow) {
                    return xnypzp();
                } else {
                    return xpypzp();
                }
            }
        }
    }

    default List<Object> sectors() {
        return List.of(
                xnynzn(), xpynzn(), xnypzn(), xpypzn(),
                xnynzp(), xpynzp(), xnypzp(), xpypzp()
        );
    }

    static Object fetchBlock(int x, int y, int z,
                             int minX, int minY, int minZ,
                             int maxX, int maxY, int maxZ,
                             TreeSector sector) {
        int midX = (minX + maxX) / 2;
        int midY = (minY + maxY) / 2;
        int midZ = (minZ + maxZ) / 2;

        boolean xInLower = x < midX;
        boolean yInLower = y < midY;
        boolean zInLower = z < midZ;

        Object child = sector.get(xInLower, yInLower, zInLower);

        if (!(child instanceof TreeSector)) {
            return child;
        }

        int newXMin = xInLower ? minX : midX;
        int newYMin = yInLower ? minY : midY;
        int newZMin = zInLower ? minZ : midZ;

        int newXMax = xInLower ? midX : maxX;
        int newYMax = yInLower ? midY : maxY;
        int newZMax = zInLower ? midZ : maxZ;

        return fetchBlock(x, y, z,
                newXMin, newYMin, newZMin,
                newXMax, newYMax, newZMax,
                (TreeSector) child);
    }

    static int size(Object sector) {
        if (!(sector instanceof TreeSector multi)) {
            return 1;
        }

        int result = 0;

        result += size(multi.xnynzn());
        result += size(multi.xnynzp());
        result += size(multi.xnypzn());
        result += size(multi.xnypzp());
        result += size(multi.xpynzn());
        result += size(multi.xpynzp());
        result += size(multi.xpypzn());
        result += size(multi.xpypzp());

        return result;
    }

    static boolean deepEquals(Object a, Object b) {
        if (a == b) {
            return true;
        }
        if (!(a instanceof TreeSector multiA && b instanceof TreeSector multiB)) {
            return Objects.equals(a, b);
        }

        return deepEquals(multiA.xnynzn(), multiB.xnynzn())
                && deepEquals(multiA.xnynzp(), multiB.xnynzp())
                && deepEquals(multiA.xnypzn(), multiB.xnypzn())
                && deepEquals(multiA.xnypzp(), multiB.xnypzp())
                && deepEquals(multiA.xpynzn(), multiB.xpynzn())
                && deepEquals(multiA.xpynzp(), multiB.xpynzp())
                && deepEquals(multiA.xpypzn(), multiB.xpypzn())
                && deepEquals(multiA.xpypzp(), multiB.xpypzp());
    }

    static TreeSector copyOf(TreeSector sector) {
        return (TreeSector) copyOf((Object) sector);
    }

    private static Object copyOf(Object sector) {
        if (!(sector instanceof TreeSector multi)) {
            return sector;
        }

        Object xnynzn = copyOf(multi.xnynzn());
        Object xpynzn = copyOf(multi.xpynzn());
        Object xnypzn = copyOf(multi.xnypzn());
        Object xpypzn = copyOf(multi.xpypzn());
        Object xnynzp = copyOf(multi.xnynzp());
        Object xpynzp = copyOf(multi.xpynzp());
        Object xnypzp = copyOf(multi.xnypzp());
        Object xpypzp = copyOf(multi.xpypzp());

        return new TreeSectorImpl(xnynzn, xpynzn, xnypzn, xpypzn, xnynzp, xpynzp, xnypzp, xpypzp);
    }

    static void toString(Object sector, int indent, Consumer<String> accumulator, Function<Object, String> toString) {
        String indentStr = " ".repeat(indent);

        if (!(sector instanceof TreeSector multi)) {
            accumulator.accept(indentStr + toString.apply(sector));
            return;
        }

        accumulator.accept(indentStr + "{");
        toString(multi.xnynzn(), indent + 1, accumulator, toString);
        toString(multi.xnynzp(), indent + 1, accumulator, toString);
        toString(multi.xnypzn(), indent + 1, accumulator, toString);
        toString(multi.xnypzp(), indent + 1, accumulator, toString);
        toString(multi.xpynzn(), indent + 1, accumulator, toString);
        toString(multi.xpynzp(), indent + 1, accumulator, toString);
        toString(multi.xpypzn(), indent + 1, accumulator, toString);
        toString(multi.xpypzp(), indent + 1, accumulator, toString);
        accumulator.accept(indentStr + "}");
    }
}
