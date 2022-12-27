package org.krystilize.recursilize;

import org.junit.jupiter.api.Test;
import org.krystilize.recursilize.tree.DynamicStatefulTree;
import org.krystilize.recursilize.tree.RecursilizeTree;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestDynamicImpl {

    private record Vec(int x, int y, int z) {
    }

    @Test
    public void speedTest() {
        RecursilizeTree.Dynamic<String> tree = new DynamicStatefulTree<>("default");

        Map<Vec, String> map = new HashMap<>();

        Random random = new Random(0);

        long count = 512000;
        long start = System.nanoTime();
        int cubeRoot = (int) Math.cbrt(count);

        for (int x = 0; x < cubeRoot; x++) {
            for (int y = 0; y < cubeRoot; y++) {
                for (int z = 0; z < cubeRoot; z++) {
                    String value = random.nextInt(2) == 0 ? "a" : "b";
                    tree.set(x, y, z, value);
                    map.put(new Vec(x, y, z), value);
                }
            }
        }

        System.out.println("Place blocks per second: " + (count / ((System.nanoTime() - start) / 1_000_000_000.0)));
        start = System.nanoTime();

        for (int i = 0; i < count; i++) {
            int x = random.nextInt(cubeRoot);
            int y = random.nextInt(cubeRoot);
            int z = random.nextInt(cubeRoot);
            assertEquals(tree.get(x, y, z), map.get(new Vec(x, y, z)));
        }

        System.out.println("Get blocks per second: " + (count / ((System.nanoTime() - start) / 1_000_000_000.0)));
    }

    @Test
    public void testDynamicCopyEquality() {
        RecursilizeTree.Dynamic<String> world = RecursilizeTree.dynamic("kry:air");
        Random random = new Random(0);

        for (int i = 0; i < 10000; i++) {
            int x = random.nextInt(RecursilizeTree.COORD_MIN, RecursilizeTree.COORD_MAX);
            int y = random.nextInt(RecursilizeTree.COORD_MIN, RecursilizeTree.COORD_MAX);
            int z = random.nextInt(RecursilizeTree.COORD_MIN, RecursilizeTree.COORD_MAX);

            world.set(x, y, z, "kry:" + random.nextInt());
        }

        RecursilizeTree<String> copy = RecursilizeTree.copyOf(world);
        assertTrue(RecursilizeTree.equals(world, copy));
    }

    @Test
    public void testRandomDynamicPlacements() {
        RecursilizeTree.Dynamic<String> world = RecursilizeTree.dynamic("kry:air");
        Map<Vec, String> map = new HashMap<>();

        Random random = new Random(0);

        for (int i = 0; i < 10000; i++) {
            int x = random.nextInt(RecursilizeTree.COORD_MIN, RecursilizeTree.COORD_MAX);
            int y = random.nextInt(RecursilizeTree.COORD_MIN, RecursilizeTree.COORD_MAX);
            int z = random.nextInt(RecursilizeTree.COORD_MIN, RecursilizeTree.COORD_MAX);

            String value = "kry:" + random.nextInt(0, 1000);
            Vec pos = new Vec(x, y, z);
            world.set(x, y, z, value);
            map.put(pos, value);
        }

        for (Map.Entry<Vec, String> entry : map.entrySet()) {
            String expected = entry.getValue();
            Vec pos2 = entry.getKey();
            String actual = world.get(pos2.x(), pos2.y(), pos2.z());
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testRandomIdenticalPlacements() {
        RecursilizeTree.Dynamic<String> world = RecursilizeTree.dynamic("kry:air");
        Random random = new Random();

        for (int i = 0; i < 1000; i++) {
            int x = random.nextInt(RecursilizeTree.COORD_MIN, RecursilizeTree.COORD_MAX);
            int y = random.nextInt(RecursilizeTree.COORD_MIN, RecursilizeTree.COORD_MAX);
            int z = random.nextInt(RecursilizeTree.COORD_MIN, RecursilizeTree.COORD_MAX);

            world.set(x, y, z, "kry:test1");
            assertEquals("kry:test1", world.get(x, y, z), "Value at " + x + ", " + y + ", " + z + " is not equal to the value set");
            world.set(x, y, z, "kry:test2");
            assertEquals("kry:test2", world.get(x, y, z), "Value at " + x + ", " + y + ", " + z + " is not equal to the value set");
        }
    }
}
