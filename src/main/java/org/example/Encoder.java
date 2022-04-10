package org.example;

import java.util.Locale;

/**
 * Provides basic functionality for bech32m encoding/decoding.
 *
 * @author Ľuboslav Halama
 */
public class Encoder {

	private static final byte CHECKSUM_LEN = 6;

	private static final String BECH32M_CHARSET = "qpzry9x8gf2tvdw0s3jn54khce6mua7l";

	/**
	 * Computes the checksum using given human-readable part (HRP) and data
	 *
	 * @param hrp human-readable part
	 * @param data
	 * @return checksum
	 */
	private static byte[] createCheckSum(final String hrp, final byte[] data) {

		// expand HRP
		byte[] expandedHRP = Bech32mUtils.hrpExpand(hrp);

		// create large enough array of bytes
		byte[] values = new byte[expandedHRP.length + data.length + CHECKSUM_LEN];

		// copy both expanded HRP and data (right after it) into newly created array
		System.arraycopy(expandedHRP, 0, values, 0, expandedHRP.length);
		System.arraycopy(data, 0, values, expandedHRP.length, data.length);

		int polymod = Bech32mUtils.bech32Polymod(values) ^ Bech32mUtils.BECH32M_CONST;

		byte[] checksum = new byte[CHECKSUM_LEN];
		for (short i = 0; i < CHECKSUM_LEN; i++) {
			checksum[i] = (byte) ((polymod >>> (CHECKSUM_LEN - 1) * ((CHECKSUM_LEN - 1) - i)) & 0x1F);
		}

		return checksum;
	}

	public static String encode(String hrp, byte[] data) {

		// check input
		if (!Bech32mUtils.isHRPValid(hrp)) {
			return null;
		}

		// convert to lower
		hrp = hrp.toLowerCase(Locale.ROOT);

		byte[] dataWithChecksum = new byte[data.length + CHECKSUM_LEN];

		// Combine (serialize) data and checksum (right behind data)
		System.arraycopy(data, 0, dataWithChecksum, 0, data.length);
		System.arraycopy(createCheckSum(hrp, data), 0, dataWithChecksum, data.length, CHECKSUM_LEN);

		StringBuilder encoded = new StringBuilder(hrp.length() + 1 +dataWithChecksum.length);

		// Firstly, append hrp and separator
		encoded.append(hrp);
		encoded.append('1');

		for (byte value : dataWithChecksum) {
			encoded.append(BECH32M_CHARSET.charAt(value));
		}

		return encoded.toString();
	}
}
