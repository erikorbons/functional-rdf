package io.github.erikorbons.functionalrdf.core.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Optional;
import org.junit.Test;

public class TreeDictTest {
  @Test
  public void testInsertRightLeaningPath() {
    Dict<String, String> current = TreeDict.empty();

    for (int i = 0; i < 26; ++ i) {
      final String v = String.valueOf((char)('a' + i));
      current = current.put(v, v);
      // assertConditions(current);
    }

    System.out.println(current);

    for (int i = 0; i < 10; ++ i) {
      final String key = String.valueOf((char)('a' + i));
      final Optional<String> value = current.lookup(key);
      assertTrue("Element " + key + " is missing.", value.isPresent());
      assertEquals("Element " + key + " is missing or invalid", key, value.get());
    }
  }

  @Test
  public void testInsertLeftLeaningPath() {
    Dict<String, String> current = TreeDict.empty();

    for (int i = 0; i < 26; ++ i) {
      final String v = String.valueOf((char)('z' - i));
      current = current.put(v, v);
      // assertConditions(current);
    }

    System.out.println(current);

    for (int i = 0; i < 10; ++ i) {
      final String key = String.valueOf((char)('z' - i));
      final Optional<String> value = current.lookup(key);
      assertTrue("Element " + key + " is missing.", value.isPresent());
      assertEquals("Element " + key + " is missing or invalid", key, value.get());
    }
  }

  @Test
  public void testLargeTree() {
    Dict<Integer, Integer> current = TreeDict.empty();

    for (int i = 0; i < 100; ++ i) {
      current = current.put(i, i);
    }

    assertEquals(100, current.getSize());

    System.out.println(current);
  }

  @Test
  public void testDeletes() {
    Dict<Integer, Integer> current = TreeDict.empty();

    for (int i = 0; i < 100; ++ i) {
      current = current.put(i, i);
      assertEquals(i + 1, current.getSize());
    }

    assertEquals(100, current.getSize());

    for (int i = 99; i >= 0; -- i) {
      current = current.delete(i);
      assertEquals(i, current.getSize());

      for (int j = i - 1; j >= 0; -- j) {
        assertTrue(current.lookup(j).isPresent());
      }

      for (int j = i; j < 100; ++ j) {
        assertFalse(current.lookup(j).isPresent());
      }
    }

    assertTrue(current.isEmpty());
  }
}
