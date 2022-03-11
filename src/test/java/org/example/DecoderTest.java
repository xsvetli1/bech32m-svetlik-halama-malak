package org.example;

import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class DecoderTest {
//    private String toSegwitScriptpubkey(int witnessVersion, List<Integer> witnessProgram){
//        List<Integer> scriptpubkey = new java.util.ArrayList<>(List.of(witnessVersion >= 1 ? witnessVersion + 0x50 : 0, witnessProgram.size()));
//        scriptpubkey.addAll(witnessProgram);
//        return scriptpubkey.stream().map(Integer::toHexString).collect(Collectors.joining());
//    }

    private String getHexString(Message message) {
        return message.getContent().stream().map(c -> String.format("%02x", c)).collect(Collectors.joining());
    }

    @Test
    public void basicTest() {
        List<Object> decoded = Decoder.bech32Decode("A1LQFN3A");
        assertNotNull(decoded);
        assertEquals("a", decoded.get(0));
        decoded = Decoder.bech32Decode("abcdef1l7aum6echk45nj3s0wdvt2fg8x9yrzpqzd3ryx");
        assertNotNull(decoded);
        assertEquals("abcdef", decoded.get(0));
        assertNull(Decoder.bech32Decode("alqfn3a"));
        assertNull(Decoder.decode("A", "A1LQFN3A"));
        assertNull(Decoder.decode("tb", "BC1QW508D6QEJXTDG4Y5R3ZARVARY0C5XW7KV8F3T4"));
        assertNull(Decoder.decode("1", "11llllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllludsr8"));
        assertNull(Decoder.decode("split", "split1checkupstagehandshakeupstreamerranterredcaperredlc445v"));
    }

    @Test
    public void segwitTestVectorsTest1() {
        Message message = Decoder.decode("bc", "BC1QW508D6QEJXTDG4Y5R3ZARVARY0C5XW7KV8F3T4");
        assertNotNull(message);
        assertEquals(0, message.getType());
        assertEquals("751e76e8199196d454941c45d1b3a323f1433bd6", getHexString(message));
    }

    @Test
    public void segwitTestVectorsTest2() {
        Message message = Decoder.decode("tb", "tb1qrp33g0q5c5txsp9arysrx4k6zdkfs4nce4xj0gdcccefvpysxf3q0sl5k7");
        assertNotNull(message);
        assertEquals(0, message.getType());
        assertEquals("1863143c14c5166804bd19203356da136c985678cd4d27a1b8c6329604903262", getHexString(message));
    }

    @Test
    public void segwitTestVectorsTest3() {
        Message message = Decoder.decode("bc", "bc1pw508d6qejxtdg4y5r3zarvary0c5xw7kw508d6qejxtdg4y5r3zarvary0c5xw7kt5nd6y");
        assertNotNull(message);
        assertEquals(1, message.getType());
        assertEquals("751e76e8199196d454941c45d1b3a323f1433bd6751e76e8199196d454941c45d1b3a323f1433bd6", getHexString(message));
    }

    @Test
    public void segwitTestVectorsTest4() {
        Message message = Decoder.decode("bc", "BC1SW50QGDZ25J");
        assertNotNull(message);
        assertEquals(16, message.getType());
        assertEquals("751e", getHexString(message));
    }

    @Test
    public void segwitTestVectorsTest5() {
        Message message = Decoder.decode("bc", "bc1zw508d6qejxtdg4y5r3zarvaryvaxxpcs");
        assertNotNull(message);
        assertEquals(2, message.getType());
        assertEquals("751e76e8199196d454941c45d1b3a323", getHexString(message));
    }

    @Test
    public void segwitTestVectorsTest6() {
        Message message = Decoder.decode("tb", "tb1qqqqqp399et2xygdj5xreqhjjvcmzhxw4aywxecjdzew6hylgvsesrxh6hy");
        assertNotNull(message);
        assertEquals(0, message.getType());
        assertEquals("000000c4a5cad46221b2a187905e5266362b99d5e91c6ce24d165dab93e86433", getHexString(message));
    }

    @Test
    public void segwitTestVectorsTest7() {
        Message message = Decoder.decode("tb", "tb1pqqqqp399et2xygdj5xreqhjjvcmzhxw4aywxecjdzew6hylgvsesf3hn0c");
        assertNotNull(message);
        assertEquals(1, message.getType());
        assertEquals("000000c4a5cad46221b2a187905e5266362b99d5e91c6ce24d165dab93e86433", getHexString(message));
    }

    @Test
    public void segwitTestVectorsTest8() {
        Message message = Decoder.decode("bc", "bc1p0xlxvlhemja6c4dqv22uapctqupfhlxm9h8z3k2e72q4k9hcz7vqzk5jj0");
        assertNotNull(message);
        assertEquals(1, message.getType());
        assertEquals("79be667ef9dcbbac55a06295ce870b07029bfcdb2dce28d959f2815b16f81798", getHexString(message));
    }
}
