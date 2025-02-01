package net.hydrakecat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class SuffixArrayBuilderTest {
  @Test
  public void build_abracadabra() {
    int[] actual = SuffixArrayBuilder.build("abracadabra");

    assertArrayEquals(new int[] {11, 10, 7, 0, 3, 5, 8, 1, 4, 6, 9, 2}, actual);
  }
}