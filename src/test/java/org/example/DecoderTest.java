package org.example;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DecoderTest {

    @Test
    public void bech32mTestVectorsTest1() {
        List<Object> decoded = Decoder.bech32mDecode("a1lqfn3a");
        assertNotNull(decoded);

        // HRP should not be changed
        assertEquals("a", decoded.get(0));

        // data part should be empty for this test vector input
        assertEquals(new ArrayList<>(), decoded.get(1));
    }

    @Test
    public void bech32mTestVectorsTest2() {
        List<Object> decoded = Decoder.bech32mDecode("a1lqfn3a");
        assertNotNull(decoded);

        // HRP should not be changed
        assertEquals("a", decoded.get(0));

        // data part should be empty for this test vector input
        assertEquals(new ArrayList<>(), decoded.get(1));
    }

    @Test
    public void bech32mTestVectorsTest3() {
        List<Object> decoded = Decoder.bech32mDecode("an83characterlonghumanreadablepartthatcontainsthetheexcludedcharactersbioandnumber11sg7hg6");
        assertNotNull(decoded);

        // HRP should not be changed
        assertEquals("an83characterlonghumanreadablepartthatcontainsthetheexcludedcharactersbioandnumber1", decoded.get(0));

        // data part should be empty for this test vector input
        assertEquals(new ArrayList<>(), decoded.get(1));  // data part should be empty for this test vector input
    }

    @Test
    public void bech32mTestVectorsTest4() {
        List<Object> decoded = Decoder.bech32mDecode("abcdef1l7aum6echk45nj3s0wdvt2fg8x9yrzpqzd3ryx");
        assertNotNull(decoded);

        // HRP should not be changed
        assertEquals("abcdef", decoded.get(0));
        assertEquals(Stream.of(31, 30, 29, 28, 27, 26, 25, 24, 23, 22, 21, 20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0).map(Integer::byteValue).collect(Collectors.toList()), decoded.get(1));
    }

    @Test
    public void bech32mTestVectorsTest5() {
        List<Object> decoded = Decoder.bech32mDecode("11llllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllludsr8");
        assertNotNull(decoded);

        // HRP should not be changed
        assertEquals("1", decoded.get(0));
        assertEquals(Stream.of(31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31).map(Integer::byteValue).collect(Collectors.toList()), decoded.get(1));
    }

    @Test
    public void bech32mTestVectorsTest6() {
        List<Object> decoded = Decoder.bech32mDecode("split1checkupstagehandshakeupstreamerranterredcaperredlc445v");
        assertNotNull(decoded);

        // HRP should not be changed
        assertEquals("split", decoded.get(0));
        assertEquals(Stream.of(24, 23, 25, 24, 22, 28, 1, 16, 11, 29, 8, 25, 23, 29, 19, 13, 16, 23, 29, 22, 25, 28, 1, 16, 11, 3, 25, 29, 27, 25, 3, 3, 29, 19, 11, 25, 3, 3, 25, 13, 24, 29, 1, 25, 3, 3, 25, 13).map(Integer::byteValue).collect(Collectors.toList()), decoded.get(1));
    }

    @Test
    public void bech32mTestVectorsTest7() {
        List<Object> decoded = Decoder.bech32mDecode("?1v759aa");
        assertNotNull(decoded);

        // HRP should not be changed
        assertEquals("?", decoded.get(0));

        // data part should be empty for this test vector input
        assertEquals(new ArrayList<>(), decoded.get(1));
    }
}
