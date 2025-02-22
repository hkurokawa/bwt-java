package net.hydrakecat;

import java.util.Random;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BurrowsWheelerTransformerTest {
  @Test
  void encode_cacao() {
    String input = "cacao";
    String actual = BurrowsWheelerTransformer.encode(input, '$');

    assertEquals("occ$aa", actual);
  }

  @Test
  void encode_longText() {
    String input = "SIX.MIXED.PIXIES.SIFT.SIXTY.PIXIE.DUST.BOXES";
    String actual = BurrowsWheelerTransformer.encode(input, '$');

    assertEquals("STEXYDST.E.IXXIIXXSSMPPS.B..EE.$.USFXDIIOIIIT", actual);
  }

  @Test
  void decode_cacao() {
    String input = "occ$aa";
    String actual = BurrowsWheelerTransformer.decode(input, '$');

    assertEquals("cacao", actual);
  }

  @Test
  void decode_longText() {
    String input = "STEXYDST.E.IXXIIXXSSMPPS.B..EE.$.USFXDIIOIIIT";
    String actual = BurrowsWheelerTransformer.decode(input, '$');

    assertEquals("SIX.MIXED.PIXIES.SIFT.SIXTY.PIXIE.DUST.BOXES", actual);
  }

  @Test
  void encodeDecode_randomAscii_shouldBeIdentical() {
    Random rand = new Random();
    for (int i = 0; i < 10; i++) {
      StringBuilder sb = new StringBuilder();
      for (int j = 0; j < 1_000; j++) {
        sb.append((char) rand.nextInt(256));
      }
      String encoded = BurrowsWheelerTransformer.encode(sb, 'Ā');
      String decoded = BurrowsWheelerTransformer.decode(encoded, 'Ā');

      assertEquals(sb.toString(), decoded,
          "Encoded and then decoded text does not match the original: [" + sb + "]");
    }
  }

  @Test
  void encodeDecode_randomAb_shouldBeIdentical() {
    Random rand = new Random();
    for (int i = 0; i < 10; i++) {
      StringBuilder sb = new StringBuilder();
      for (int j = 0; j < 1_000; j++) {
        sb.append((char) rand.nextInt(2) + 'a');
      }
      String encoded = BurrowsWheelerTransformer.encode(sb, '$');
      String decoded = BurrowsWheelerTransformer.decode(encoded, '$');

      assertEquals(sb.toString(), decoded,
          "Encoded and then decoded text does not match the original: [" + sb + "]");
    }
  }
}