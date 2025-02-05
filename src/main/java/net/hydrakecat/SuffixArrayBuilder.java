package net.hydrakecat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SuffixArrayBuilder {
  /**
   * Builds the suffix array for the given {@code str}.
   *
   * @param str a string the suffix array is built for
   * @return the suffix array for the provided string
   */
  public static int[] build(CharSequence str) {
    int[] chars = str.chars().boxed().collect(Collectors.toSet()).stream().mapToInt(i -> i).sorted()
        .toArray();
    Map<Integer, Integer> mapping = new HashMap<>();
    for (int i = 0; i < chars.length; i++) {
      mapping.put(chars[i], i);
    }
    int[] input = new int[str.length()];
    for (int i = 0; i < input.length; i++) {
      input[i] = mapping.get((int) str.charAt(i));
    }
    return suffixArray(input, chars.length);
  }

  // Receives the input that consists of a set of integers [0..numUniqueChars] and returns the
  // suffix array for it.
  static int[] suffixArray(int[] in, int numUniqueChars) {
    if (in.length == 0) {
      return new int[] {};
    }
    if (in.length == 1) {
      return new int[] {0};
    }
    // Increase the size by 1 for the sentinel char ($)
    int n = in.length + 1;
    numUniqueChars++;
    int[] input = new int[n];
    for (int i = 0; i < in.length; i++) {
      input[i] = in[i] + 1;
    }
    input[n - 1] = 0; // the sentinel char ($)

    // Compute the cumulative frequency of the characters
    int[] freq = new int[numUniqueChars];
    for (int i : input) freq[i]++;

    // Type of the suffixes (true: S, false: L)
    boolean[] types = new boolean[n];
    types[n - 1] = true; // The last suffix ("$") is always type S
    for (int i = n - 2; i >= 0; i--) {
      if (input[i] == input[i + 1]) {
        types[i] = types[i + 1];
      } else {
        types[i] = input[i] < input[i + 1];
      }
    }

    // Find the LMS (Left-Most-S)
    List<Integer> lms = new ArrayList<>();
    for (int i = 1; i < n; i++) {
      if (types[i] && !types[i - 1]) lms.add(i);
    }

    // Put the LMS in the array making sure they are bin-sorted (i.e., sorted by only the first character)
    int[] sa = new int[n];
    {
      Arrays.fill(sa, -1);
      int[] cend = new int[freq.length];
      for (int i = 1; i < cend.length; i++) cend[i] = cend[i - 1] + freq[i];
      for (int i : lms) {
        int ch = input[i];
        sa[cend[ch]] = i;
        cend[ch]--;
      }
    }

    // Do an induced sorting with the bin-sorted LMS
    inducedSort(n, sa, input, freq, types);

    // Now LMS-substrings are completely sorted.
    // Construct a string that represents a concatenated LMS-substrings.
    Set<Integer> lmsSet = new HashSet<>(lms);
    int[] lmsOrder = new int[n];
    Arrays.fill(lmsOrder, -1);
    for (int i = 0; i < lms.size(); i++) {
      lmsOrder[lms.get(i)] = i;
    }
    int[] lmsStr = new int[lms.size()];
    {
      int i = 0;
      for (int suffix : sa) {
        if (lmsSet.contains(suffix)) {
          lmsStr[lmsOrder[suffix]] = i;
          i++;
        }
      }
    }
    int[] lmsSa = suffixArray(lmsStr, lms.size());

    // Put the LMS in the array making sure they are sorted
    sa = new int[n];
    {
      Arrays.fill(sa, -1);
      int[] cend = new int[freq.length];
      for (int i = 1; i < cend.length; i++) cend[i] = cend[i - 1] + freq[i];
      for (int i = lmsSa.length - 1; i >= 0; i--) {
        int suffix = lms.get(lmsSa[i]);
        int ch = input[suffix];
        sa[cend[ch]] = suffix;
        cend[ch]--;
      }
    }

    // Do an induced sorting with the sorted LMS
    inducedSort(n, sa, input, freq, types);

    // Remove the first element
    int[] ret = new int[n - 1];
    System.arraycopy(sa, 1, ret, 0, n - 1);
    return ret;
  }

  static void inducedSort(int n, int[] sa, int[] input, int[] freq, boolean[] types) {
    boolean[] prefilled = new boolean[sa.length];
    for (int i = 0; i < sa.length; i++) if (sa[i] >= 0) prefilled[i] = true;

    // Fill in the L suffixes
    int[] cstart = new int[freq.length];
    for (int i = 1; i < cstart.length; i++) cstart[i] = cstart[i - 1] + freq[i - 1];
    for (int i = 0; i < n; i++) {
      if (sa[i] <= 0) continue;
      // If S(j - 1) is L
      if (!types[sa[i] - 1]) {
        int ch = input[sa[i] - 1];
        sa[cstart[ch]] = sa[i] - 1;
        cstart[ch]++;
      }
    }

    // Clear the prefilled LMS values
    for (int i = 0; i < sa.length; i++) if (prefilled[i]) sa[i] = -1;

    // Fill in the S suffixes
    int[] cend = new int[freq.length];
    for (int i = 1; i < cend.length; i++) cend[i] = cend[i - 1] + freq[i];
    for (int i = n - 1; i >= 0; i--) {
      if (sa[i] <= 0) continue;
      // If S(i - 1) is type S
      if (types[sa[i] - 1]) {
        int ch = input[sa[i] - 1];
        sa[cend[ch]] = sa[i] - 1;
        cend[ch]--;
      }
    }
    sa[0] = n - 1; // The first element is always the sentinel char ($)
  }
}
