package org.example;

import java.util.Locale;

/**
 * Provides basic functionality for bech32m encoding/decoding.
 *
 * @author Kristian Malak
 * @author Ľuboslav Halama
 */
public class Bech32m {

	private static final int BECH32M_CONST = 0x2bc830a3;

	private static final byte CHECKSUM_LEN = 6;

	private static final String BECH32M_CHARSET = "qpzry9x8gf2tvdw0s3jn54khce6mua7l";

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
	 * Computes the Bech32 checksum.
	 *
	 * @param values used to compute checksum
	 * @return checksum
	 */
	private static int bech32Polymod(byte[] values) {
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
	private static byte[] hrpExpand(final String hrp) {
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

	/**
	 * Computes the checksum using given human-readable part (HRP) and data
	 *
	 * @param hrp human-readable part
	 * @param data
	 * @return checksum
	 */
	private static byte[] createCheckSum(final String hrp, final byte[] data) {

		// expand HRP
		byte[] expandedHRP = hrpExpand(hrp);

		// create large enough array of bytes
		byte[] values = new byte[expandedHRP.length + data.length + CHECKSUM_LEN];

		// copy both expanded HRP and data (right after it) into newly created array
		System.arraycopy(expandedHRP, 0, values, 0, expandedHRP.length);
		System.arraycopy(data, 0, values, expandedHRP.length, data.length);

		int polymod = bech32Polymod(values) ^ BECH32M_CONST;

		byte[] checksum = new byte[CHECKSUM_LEN];
		for (short i = 0; i < CHECKSUM_LEN; i++) {
			checksum[i] = (byte) ((polymod >>> (CHECKSUM_LEN - 1) * ((CHECKSUM_LEN - 1) - i)) & 0x1F);
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

		return isLowerCase ? encoded.toString() : encoded.toString().toUpperCase(Locale.ROOT);
	}
}