package net.hydrakecat;

import java.util.Arrays;

public class SuffixArrayBuilder {
  /**
   * Builds the suffix array for the given {@code str}.
   *
   * @param str a string the suffix array is built for
   * @return the suffix array for the provided string
   */
  public static int[] build(CharSequence str) {
    Pair[] indexSubstrPairs = new Pair[str.length() + 1];
    for (int i = 0; i < str.length(); i++) {
      indexSubstrPairs[i] = new Pair(i, str.subSequence(i, str.length()));
    }
    indexSubstrPairs[str.length()] = new Pair(str.length(), "");
    Arrays.sort(indexSubstrPairs);

    int[] ret = new int[indexSubstrPairs.length];
    for (int i = 0; i < indexSubstrPairs.length; i++) {
      ret[i] = indexSubstrPairs[i].i;
    }
    return ret;
  }

  private static class Pair implements Comparable<Pair> {
    final int i;
    final CharSequence str;

    private Pair(int i, CharSequence str) {
      this.i = i;
      this.str = str;
    }

    @Override public int compareTo(Pair o) {
      return CharSequence.compare(str, o.str);
    }
  }
}
