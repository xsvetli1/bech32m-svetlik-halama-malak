package org.example;

import java.util.Locale;

/**
 * Provides basic functionality for bech32m encoding/decoding.
 *
 * @author Ľuboslav Halama
 */
public class Encoder {
	/**
	 * private override of default public constructor
	 */
	private Encoder() {}

	private static boolean checkInput(final String hrp) {

		// if HRP does not contain any letter, its not worth checking,
		// since it will be both lowercase and uppercase
		if (!hrp.matches(".*[a-zA-Z].*")) {
			return true;
		}

		// only one of them can be true (it is either lowercase or uppercase)
		if (hrp.equals(hrp.toLowerCase(Locale.ROOT)) == hrp.equals(hrp.toUpperCase(Locale.ROOT))) {
			return false;
		}

		// double check (swapped order)
		if ((hrp.equals(hrp.toUpperCase(Locale.ROOT)) == hrp.equals(hrp.toLowerCase(Locale.ROOT)))) {
			return false;
		}

		return true;
	}

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
		byte[] values = new byte[expandedHRP.length + data.length + Bech32mUtils.CHECKSUM_LEN];

		// copy both expanded HRP and data (right after it) into newly created array
		System.arraycopy(expandedHRP, 0, values, 0, expandedHRP.length);
		System.arraycopy(data, 0, values, expandedHRP.length, data.length);

		int polymod = Bech32mUtils.bech32Polymod(values) ^ Bech32mUtils.BECH32M_CONST;

		byte[] checksum = new byte[Bech32mUtils.CHECKSUM_LEN];
		for (short i = 0; i < Bech32mUtils.CHECKSUM_LEN; i++) {
			checksum[i] = (byte) ((polymod >>> (Bech32mUtils.CHECKSUM_LEN - 1) * ((Bech32mUtils.CHECKSUM_LEN - 1) - i)) & 0x1F);
		}

		return checksum;
	}

	public static String encode(String hrp, byte[] data) {

		// check input
		if (!checkInput(hrp)) {
			return null;
		}

		final boolean isLowerCase = hrp.equals(hrp.toLowerCase(Locale.ROOT));

		// convert to lower
		hrp = hrp.toLowerCase(Locale.ROOT);

		byte[] dataWithChecksum = new byte[data.length + Bech32mUtils.CHECKSUM_LEN];

		// Combine (serialize) data and checksum (right behind data)
		System.arraycopy(data, 0, dataWithChecksum, 0, data.length);
		System.arraycopy(createCheckSum(hrp, data), 0, dataWithChecksum, data.length, Bech32mUtils.CHECKSUM_LEN);

		StringBuilder encoded = new StringBuilder(hrp.length() + 1 +dataWithChecksum.length);

		// Firstly, append hrp and separator
		encoded.append(hrp);
		encoded.append('1');

		for (byte value : dataWithChecksum) {
			encoded.append(Bech32mUtils.BECH32M_CHARSET.charAt(value));
		}

		return isLowerCase ? encoded.toString() : encoded.toString().toUpperCase(Locale.ROOT);
	}
}
