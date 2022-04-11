package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class wrapper for Bech32m decoding functions
 *
 * @author Kristián Malák
 */
public class Decoder {

    /**
     * private override of default public constructor
     */
    private Decoder() {
    }

    /**
     * Checks the encoding type
     *
     * @param hrp  Human readable part
     * @param data Payload
     * @return boolean true if everything was OK, false otherwise
     */
    private static boolean verifyChecksum(String hrp, byte[] data) {
        // expand HRP
        byte[] expandedHRP = Bech32mUtils.hrpExpand(hrp);

        // create large enough array of bytes
        byte[] values = new byte[expandedHRP.length + data.length];

        // copy both expanded HRP and data (right after it) into newly created array
        System.arraycopy(expandedHRP, 0, values, 0, expandedHRP.length);
        System.arraycopy(data, 0, values, expandedHRP.length, data.length);

        // check if encoding really is bech32m
        return Bech32mUtils.bech32Polymod(values) == Bech32mUtils.BECH32M_CONST;
    }

    /**
     * Decodes the Bech32m message
     *
     * @param bech message
     * @return Human readable part, Payload
     */
    static List<Object> bech32mDecode(String bech) {
        if (!bech.toLowerCase().equals(bech) && !bech.toUpperCase().equals(bech)) {
            return Collections.emptyList();
        }
        for (char x : bech.toCharArray()) {
            if (x < 33 || x > 126) {
                return Collections.emptyList();
            }
        }
        bech = bech.toLowerCase();
        int pos = bech.lastIndexOf('1');
        if (pos < 1 || pos + 7 > bech.length() || bech.length() > 90) {
            return Collections.emptyList();
        }
        for (char c : bech.substring(pos + 1).toCharArray()) {
            if (Bech32mUtils.BECH32M_CHARSET.indexOf(c) == -1) {
                return Collections.emptyList();
            }
        }
        String hrp = bech.substring(0, pos);
        Byte[] temporaryData = bech.substring(pos + 1).chars().map(Bech32mUtils.BECH32M_CHARSET::indexOf).boxed().map(Integer::byteValue).toArray(Byte[]::new);
        byte[] data = new byte[temporaryData.length];
        for (int i = 0; i < temporaryData.length; i++) {
            data[i] = temporaryData[i];
        }
        boolean spec = verifyChecksum(hrp, data);

        if (!spec) {
            return Collections.emptyList();
        }
        return List.of(hrp, new ArrayList<>(List.of(temporaryData).subList(0, data.length - 6)));
    }
}