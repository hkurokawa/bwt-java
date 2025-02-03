package net.hydrakecat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SuffixArrayBuilder {
  /**
   * Builds the suffix array for the given {@code str}.
   *
   * @param str a string the suffix array is built for
   * @return the suffix array for the provided string
   */
  public static int[] build(CharSequence str) {
    int[] chars = new int[str.length()];
    for (int i = 0; i < str.length(); i++) {
      chars[i] = str.charAt(i);
    }
    return suffixArray(chars);
  }

  static int[] suffixArray(int[] input) {
    if (input.length == 0) {
      return new int[] {};
    }
    if (input.length == 1) {
      return new int[] {0};
    }
    // Increase the size by 1 for the sentinel char ($)
    int n = input.length + 1;

    // Compute the cumulative frequency of the characters excluding the sentinel char ($)
    int[] freq = new int[Character.MAX_VALUE];
    for (int i : input) freq[i]++;

    // Type of the suffixes (true: S, false: L)
    boolean[] types = new boolean[n];
    types[n - 1] = true; // The last suffix ("$") is always type S
    types[n - 2] = false; // The second to the last suffix is always type L
    for (int i = n - 3; i >= 0; i--) {
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
      int[] idx = new int[freq.length];
      System.arraycopy(freq, 0, idx, 0, freq.length);
      for (int i = 1; i < idx.length; i++) idx[i] += idx[i - 1];
      for (int i : lms) {
        if (i == n - 1) continue; // Ignore the sentinel char ($)
        int ch = input[i];
        sa[idx[ch]] = i;
        idx[ch]--;
      }
      // The sentinel char ($) is the smallest and is always placed at the head
      sa[0] = n - 1;
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
    int[] lmsSa = suffixArray(lmsStr);

    // Put the LMS in the array making sure they are sorted
    sa = new int[n];
    {
      Arrays.fill(sa, -1);
      int[] idx = new int[freq.length];
      System.arraycopy(freq, 0, idx, 0, freq.length);
      for (int i = 1; i < idx.length; i++) idx[i] += idx[i - 1];
      // Ignore the first element since it is the sentinel char ($)
      for (int i = lmsSa.length - 1; i > 0; i--) {
        int suffix = lms.get(lmsSa[i]);
        if (suffix == n - 1) continue; // Ignore the sentinel char ($)
        int ch = input[suffix];
        sa[idx[ch]] = suffix;
        idx[ch]--;
      }
      // The sentinel char ($) is the smallest and is always placed at the head
      sa[0] = n - 1;
    }

    // Do an induced sorting with the sorted LMS
    inducedSort(n, sa, input, freq, types);

    return sa;
  }

  static void inducedSort(int n, int[] sa, int[] input, int[] freq, boolean[] types) {
    boolean[] prefilled = new boolean[sa.length];
    for (int i = 0; i < sa.length; i++) if (sa[i] >= 0) prefilled[i] = true;

    // Fill in the L suffixes
    int[] idx = new int[freq.length];
    idx[0] = 1;
    for (int i = 1; i < idx.length; i++) {
      idx[i] = idx[i - 1] + freq[i - 1];
    }
    for (int i = 0; i < n - 1; i++) {
      if (sa[i] == 0 || sa[i] < 0) continue;
      // If S(j - 1) is L
      if (!types[sa[i] - 1]) {
        int ch = input[sa[i] - 1];
        sa[idx[ch]] = sa[i] - 1;
        idx[ch]++;
      }
    }

    // Clear the prefilled LMS values
    for (int i = 0; i < sa.length; i++) if (prefilled[i]) sa[i] = -1;

    // Fill in the S suffixes
    System.arraycopy(freq, 0, idx, 0, freq.length);
    for (int i = 1; i < idx.length; i++) idx[i] += idx[i - 1];
    for (int i = n - 1; i > 0; i--) {
      if (sa[i] == 0) continue;
      // If S(i - 1) is type S
      if (sa[i] >= 0 && types[sa[i] - 1]) {
        int ch = input[sa[i] - 1];
        sa[idx[ch]] = sa[i] - 1;
        idx[ch]--;
      }
    }
    sa[0] = n - 1; // The first element is always the sentinel char ($)
  }
}
