package org.example;

/**
 * Class provides common functionality used in both encoding and decoding.
 *
 * @author Ľuboslav Halama <lubo.halama@gmail.com>
 */
public class Bech32mUtils {
	/**
	 * private override of default public constructor
	 */
	private Bech32mUtils() {}

    public static final int BECH32M_CONST = 0x2bc830a3;

    public static final byte CHECKSUM_LEN = 6;

    public static final String BECH32M_CHARSET = "qpzry9x8gf2tvdw0s3jn54khce6mua7l";

    /**
     * Computes the Bech32 checksum.
     *
     * @param values used to compute checksum
     * @return checksum
     */
    public static int bech32Polymod(byte[] values) {
        int[] generator = {0x3b6a57b2, 0x26508e6d, 0x1ea119fa, 0x3d4233dd, 0x2a1462b3};

        int checksum = 1;
        int top;

        for (int charIndex = 0; charIndex < values.length; charIndex++) {
            top = (checksum >>> 25) & 0xFF;
            checksum = ((checksum & 0x1FFFFFF) << 5) ^ (values[charIndex] & 0xFF);

            for (int j = 0; j < 5; j++) {
                checksum ^= ((top >> j) & 1) == 1 ? generator[j] : 0;
            }
        }

        return checksum;
    }

    /**
     * Expand human-readable part (hrp) into values for checksum computation.
     *
     * @param hrp human readable part
     * @return expanded hrp
     */
    public static byte[] hrpExpand(final String hrp) {
        byte[] expanded = new byte[hrp.length() * 2 + 1];

        for (int index = 0; index < hrp.length(); index++) {

            // first half of expanded array
            expanded[index] = (byte) (hrp.charAt(index) >>> 5);

            // second half of expanded array
            expanded[index + hrp.length() + 1] = (byte) (hrp.charAt(index) & 0x1F);
        }

        // put zero into the middle of expanded array
        expanded[hrp.length()] = 0;

        return expanded;
    }
}
