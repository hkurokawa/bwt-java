package net.hydrakecat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Does BurrowsWheeler transformation for the provided string. The encoded string contains the
 * specified end-of-text character as the marker of the end of the text.
 *
 * <p>When decoding the result of a BurrowsWheeler transform back to the original text, both the
 * encoded string and the end-of-text character need to be provided.
 */
public final class BurrowsWheelerTransformer {
  private BurrowsWheelerTransformer() {
  }

  public static String encode(CharSequence str, char endOfText) {
    for (int i = 0; i < str.length(); i++) {
      if (str.charAt(i) == endOfText) {
        String msg = "The specified End-of-Text is not included in the string: [" + endOfText + "]";
        throw new IllegalArgumentException(msg);
      }
    }
    StringBuilder strb = new StringBuilder();
    strb.append(str);
    strb.append(endOfText);
    str = strb;
    int[] suffixArray = SuffixArrayBuilder.build(str);
    StringBuilder sb = new StringBuilder();
    for (int suffix : suffixArray) {
      int pos;
      if (suffix == 0) {
        pos = str.length() - 1;
      } else {
        pos = suffix - 1;
      }
      sb.append(str.charAt(pos));
    }
    return sb.toString();
  }

  public static String decode(String str, char endOfText) {
    int endPos = -1;
    char[] tb = str.toCharArray();
    for (int i = 0; i < tb.length; i++) {
      char ch = tb[i];
      if (ch == endOfText) {
        if (endPos >= 0) {
          throw new IllegalArgumentException("The provided string contains more than one"
              + " End-of-Text marker: ["
              + endOfText
              + "]");
        }
        endPos = i;
      }
    }
    if (endPos < 0) {
      throw new IllegalArgumentException("The provided string does not "
          + "contain the End-of-Text marker:"
          + " ["
          + endOfText
          + "]");
    }
    int[] next = next(tb);
    StringBuilder sb = new StringBuilder();
    int pos = next[endPos];
    while (pos != endPos) {
      sb.append(tb[pos]);
      pos = next[pos];
    }
    return sb.toString();
  }

  private static int[] next(char[] tb) {
    int n = tb.length;
    Set<Character> set = new HashSet<>();
    for (char ch : tb) set.add(ch);
    List<Character> uniqChars = set.stream().sorted().toList();
    Map<Character, Integer> charToId = new HashMap<>();
    int numUniqChars = uniqChars.size();
    for (int i = 0; i < numUniqChars; i++) charToId.put(uniqChars.get(i), i);
    int[] ntb = new int[n];
    for (int i = 0; i < n; i++) ntb[i] = charToId.get(tb[i]);

    int[] freq = new int[numUniqChars];
    for (int ch : ntb) freq[ch]++;

    int[] cstart = new int[numUniqChars];
    for (int i = 1; i < numUniqChars; i++) cstart[i] = cstart[i - 1] + freq[i - 1];

    int[] next = new int[n];
    for (int i = 0; i < n; i++) {
      int ch = ntb[i];
      next[cstart[ch]] = i;
      cstart[ch]++;
    }
    return next;
  }
}
