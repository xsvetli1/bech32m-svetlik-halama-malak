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
    private static boolean bech32_verify_checksum(String hrp, List<Integer> data) {
        return bech32Polymod(Stream.concat(bech32HrpExpand(hrp).stream(), data.stream()).mapToInt(Integer::intValue).toArray()) == BECH32M_CONST;
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
        boolean spec = bech32_verify_checksum(hrp, data);

        if (!spec) {
            return null;
        }
        return List.of(hrp, data.subList(0, data.size()-6));
    }
}