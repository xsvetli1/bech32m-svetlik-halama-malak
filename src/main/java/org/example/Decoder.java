package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class wrapper for Bech32m decoding functions
 * @author Kristián Malák
 */
public class Decoder {
    private static final String CHARSET = "qpzry9x8gf2tvdw0s3jn54khce6mua7l";
    private static final int BECH32M_CONST = 0x2bc830a3;

    /**
     *
     * @param values
     * @return Bech32 checksum
     */
    private static int bech32Polymod(int[] values) {
        int[] generator = {0x3b6a57b2, 0x26508e6d, 0x1ea119fa, 0x3d4233dd, 0x2a1462b3};
        int chk = 1;
        for (int value : values) {
            int top = chk >> 25;
            chk = (chk & 0x1ffffff) << 5 ^ value;
            for (int i = 0; i < 5; ++i) {
                chk ^= ((top >> i) & 1) >= 1 ? generator[i] : 0;
            }
        }
        return chk;
    }

    /**
     *
     * @param hrp Human readable part
     * @return Human readable part expanded/ready for checksum computation
     */
    private static List<Integer> bech32HrpExpand(String hrp) {
        List<Integer> values = hrp.chars().map(x -> x >> 5).boxed().collect(Collectors.toList());
        values.add(0);
        values.addAll(hrp.chars().map(x -> x & 31).boxed().collect(Collectors.toList()));
        return values;
    }

    /**
     * Checks the encoding type
     * @param hrp Human readable part
     * @param data Payload
     * @return Encoding type (Bech32 or Bech32m) or null in case of error
     */
    private static Encoding bech32_verify_checksum(String hrp, List<Integer> data) {

        int type = bech32Polymod(Stream.concat(bech32HrpExpand(hrp).stream(), data.stream()).mapToInt(Integer::intValue).toArray());
        if (type ==1) {
            return Encoding.BECH32;
        }
        if (type ==BECH32M_CONST) {
            return Encoding.BECH32M;
        }
        return null;
    }

    /**
     * Decodes the Bech32(m) message
     * @param bech message
     * @return Human readable part, Payload, Encoding type or null in case of bad format
     */
    static List<Object> bech32Decode(String bech) {
        if (!bech.toLowerCase().equals(bech) && !bech.toUpperCase().equals(bech)) {
            return null;
        }
        for (char x : bech.toCharArray()) {
            if (x < 33 || x > 126){
                return null;
            }
        }
        bech = bech.toLowerCase();
        int pos = bech.lastIndexOf('1');
        if (pos < 1 || pos + 7 > bech.length() || bech.length() > 90){
            return null;
        }
        for (char c : bech.substring(pos+1).toCharArray()){
            if (CHARSET.indexOf(c) == -1){
                return null;
            }
        }
        String hrp = bech.substring(0, pos);
        List<Integer> data = bech.substring(pos+1).chars().map(CHARSET::indexOf).boxed().collect(Collectors.toList());
        Encoding spec = bech32_verify_checksum(hrp, data);
        if (spec == null) {
            return null;
        }

        return List.of(hrp, data.subList(0, data.size()-6), spec);
    }

    /**
     * General power-of-2 base conversion
     * @param data
     * @param frombits
     * @param tobits
     * @return converted data
     */
    private static List<Integer> convertBits(List<Integer> data, int frombits, int tobits) {
        int acc = 0;
        int bits = 0;
        List<Integer> ret = new ArrayList<Integer>();
        int maxv = (1 << tobits) - 1;
        int max_acc = (1 << (frombits + tobits - 1)) - 1;
        for (int value : data) {
            if (value < 0 || (value >> frombits) >= 1) {
                return null;
            }
            acc = ((acc << frombits) | value) & max_acc;
            bits += frombits;
            while (bits >= tobits) {
                bits -= tobits;
                ret.add((acc >> bits) & maxv);
            }
        }
        if (bits >= frombits || (((acc << (tobits - bits)) & maxv) >= 1)) {
            return null;
        }
        return ret;
    }

    /**
     * Decode a segwit address
     * @param hrp Human readable part
     * @param addr Segwit address
     * @return Message object containing the encoding type and payload, or null in case of malformed address
     */
    public static Segwit decode(String hrp, String addr) {
        List<Object> bech32Decoded = bech32Decode(addr);
        if (bech32Decoded == null){
            return null;
        }
        String hrpgot = (String) bech32Decoded.get(0);
        List<Integer> data = (List<Integer>) bech32Decoded.get(1);
        Encoding spec = (Encoding) bech32Decoded.get(2);
        if (!Objects.equals(hrpgot, hrp)) {
            return null;
        }
        List<Integer> decoded = convertBits(data.subList(1, data.size()), 5, 8);
        if (decoded == null || decoded.size() < 2 || decoded.size() > 40) {
            return null;
        }
        if (data.get(0) > 16) {
            return null;
        }
        if (data.get(0) == 0 && decoded.size() != 20 && decoded.size() != 32)
            return null;
        if (data.get(0) == 0 && spec != Encoding.BECH32 || data.get(0) != 0 && spec != Encoding.BECH32M)
            return null;
        return new Segwit(data.get(0), decoded);
    }

    public static void main(String[] args) {
        Segwit segwit = decode("bc", "BC1QW508D6QEJXTDG4Y5R3ZARVARY0C5XW7KV8F3T4");
        assert segwit != null;
        System.out.println(segwit.getType());
        System.out.println(segwit.getContent());
    }
}

/**
 * Enum to distinguish between Bech32 and Bech32m
 */
enum Encoding {
    BECH32,
    BECH32M
}


/**
 * Class representing a Segwit address, with encoding type (Bech32(m)) and the payload
 */
class Segwit {
    private final int type;
    private final List<Integer> content;

    public Segwit(int type, List<Integer> content) {
        this.type = type;
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public List<Integer> getContent() {
        return content;
    }
}