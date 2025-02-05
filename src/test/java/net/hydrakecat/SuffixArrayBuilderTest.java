package net.hydrakecat;

import java.util.Arrays;
import java.util.Random;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class SuffixArrayBuilderTest {

  // Suffix Array builder implementation using a naive algorithm that takes O(n^2).
  // This is used to generate the expected output.
  private static int[] buildSuffixArrayNaive(CharSequence str) {
    Pair[] indexSubstrPairs = new Pair[str.length()];
    for (int i = 0; i < str.length(); i++) {
      indexSubstrPairs[i] = new Pair(i, str.subSequence(i, str.length()));
    }
    Arrays.sort(indexSubstrPairs);

    int[] ret = new int[indexSubstrPairs.length];
    for (int i = 0; i < indexSubstrPairs.length; i++) {
      ret[i] = indexSubstrPairs[i].i;
    }
    return ret;
  }

  @Test
  public void build_abracadabra() {
    int[] actual = SuffixArrayBuilder.build("abracadabra");

    assertArrayEquals(new int[] {10, 7, 0, 3, 5, 8, 1, 4, 6, 9, 2}, actual);
  }

  @Test
  public void build_mmiissiissiippii() {
    int[] actual = SuffixArrayBuilder.build("mmiissiissiippii");

    assertArrayEquals(new int[] {15, 14, 10, 6, 2, 11, 7, 3, 1, 0, 13, 12, 9, 5, 8, 4}, actual);
  }

  @Test
  public void build_randomLowerAlphabet() {
    Random random = new Random();
    for (int i = 0; i < 10; i++) {
      StringBuilder sb = new StringBuilder();
      for (int j = 0; j < 100; j++) {
        sb.append((char) (random.nextInt('z' - 'a' + 1) + 'a'));
      }
      int[] actual = SuffixArrayBuilder.build(sb.toString());
      int[] expected = buildSuffixArrayNaive(sb.toString());

      assertArrayEquals(expected, actual,
          "Suffix array for the input string [" + sb + "] do not match");
    }
  }

  @Test
  public void build_random() {
    Random random = new Random();
    for (int i = 0; i < 10; i++) {
      StringBuilder sb = new StringBuilder();
      for (int j = 0; j < 100; j++) {
        sb.append((char) (random.nextInt('z' - 'A' + 1) + 'A'));
      }
      try {
        int[] actual = SuffixArrayBuilder.build(sb.toString());
        int[] expected = buildSuffixArrayNaive(sb.toString());

        assertArrayEquals(expected, actual,
            "Suffix array for the input string [" + sb + "] do not match");
      } catch (Exception e) {
        throw new RuntimeException("Failed to build suffix array. Input: [" + sb + "]", e);
      }
    }
  }

  @Test
  @Disabled
  public void build_benchmark() {
    Random random = new Random();
    for (int t = 10; t <= 1_000_000; t *= 10) {
      long sumElapsedTime = 0;
      for (int i = 0; i < 10; i++) {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < t; j++) {
          sb.append((char) (random.nextInt('z' - 'A' + 1) + 'A'));
        }
        long start = System.nanoTime();
        SuffixArrayBuilder.build(sb.toString());
        long end = System.nanoTime();
        sumElapsedTime += (end - start);
      }
      System.out.printf("%d %f\n", t, (double) sumElapsedTime / 10);
    }
  }

  private record Pair(int i, CharSequence str) implements Comparable<Pair> {

    @Override public int compareTo(Pair o) {
      return CharSequence.compare(str, o.str);
    }
  }
}