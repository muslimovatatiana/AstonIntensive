package ru.aston.hometask1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AstonHashMapTest {

    private AstonHashMap<String, Integer> map;

    @BeforeEach
    void setUp() {
        map = new AstonHashMap<>();
    }

    @Test
    void testPutAndGet() {
        map.put("One", 1);
        map.put("Two", 2);

        assertEquals(2, map.size());
        assertEquals(1, map.get("One"));
        assertEquals(2, map.get("Two"));

        map.put("One", 11);
        assertEquals(2, map.size());
        assertEquals(11, map.get("One"));
    }

    @Test
    void testGetNonExistentKey() {
        assertNull(map.get("Missing"));
    }

    @Test
    void testRemove() {
        map.put("Target", 42);

        Integer removedValue = map.remove("Target");

        assertEquals(42, removedValue);
        assertEquals(0, map.size());
        assertNull(map.get("Target"));
    }

    @Test
    void testRemoveNonExistentKey() {
        assertNull(map.remove("Missing"));
        assertEquals(0, map.size());
    }

    @Test
    void testNullKeySupport() {
        map.put(null, 999);

        assertEquals(1, map.size());
        assertEquals(999, map.get(null));

        Integer removed = map.remove(null);
        assertEquals(999, removed);
        assertEquals(0, map.size());
    }

    @Test
    void testResize() {
        for (int i = 1; i <= 15; i++) {
            map.put("Key" + i, i);
        }

        assertEquals(15, map.size());
        for (int i = 1; i <= 15; i++) {
            assertEquals(i, map.get("Key" + i));
        }
    }

    @Test
    void testCollisions() {
        record CollidingKey(String id) {
            @Override public int hashCode() { return 100; }
        }

        AstonHashMap<CollidingKey, String> collisionMap = new AstonHashMap<>();
        CollidingKey k1 = new CollidingKey("A");
        CollidingKey k2 = new CollidingKey("B");
        CollidingKey k3 = new CollidingKey("C");

        collisionMap.put(k1, "First");
        collisionMap.put(k2, "Second");
        collisionMap.put(k3, "Third");

        assertEquals(3, collisionMap.size());
        assertEquals("First", collisionMap.get(k1));
        assertEquals("Second", collisionMap.get(k2));
        assertEquals("Third", collisionMap.get(k3));

        String removed = collisionMap.remove(k2);
        assertEquals("Second", removed);
        assertEquals(2, collisionMap.size());

        assertNull(collisionMap.get(k2));
        assertEquals("Third", collisionMap.get(k3));
        assertEquals("First", collisionMap.get(k1));
    }

    @Test
    void testStreams() {
        map.put("A", 1);
        map.put("B", 2);
        map.put("C", 3);

        List<String> keys = map.keysStream().toList();
        List<Integer> values = map.valuesStream().toList();

        assertEquals(3, keys.size());
        assertTrue(keys.containsAll(List.of("A", "B", "C")));

        assertEquals(3, values.size());
        assertTrue(values.containsAll(List.of(1, 2, 3)));
    }
}
