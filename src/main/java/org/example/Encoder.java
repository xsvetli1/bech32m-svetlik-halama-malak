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

	/**
	 * Computes the checksum using given human-readable part (HRP) and data
	 *
	 * @param hrp human-readable part
	 * @param data payload
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

	/**
	 * Encodes given HRP and data into bech32m String.
	 *
	 * Firstly, method checks whether HRP has correct parameters (format, length).
	 * Then, HRP is converted into lower-case and combine data with checksum.
	 * Later, HRP and its separator are combined and last step (data encoding) follows.
	 * After that, encoded data are appended to HRP and its separator.
	 *
	 * @param hrp human-readable part
	 * @param data payload
	 * @return String HRP + '1' + bech32m encoded data
	 */
	public static String bech32mEncode(String hrp, byte[] data) {

		// check input
		if (!Bech32mUtils.isHRPValid(hrp)) {
			return null;
		}

		// convert to lower
		hrp = hrp.toLowerCase(Locale.ROOT);

		byte[] dataWithChecksum = new byte[data.length + Bech32mUtils.CHECKSUM_LEN];

		// Combine (serialize) data and checksum (right behind data)
		System.arraycopy(data, 0, dataWithChecksum, 0, data.length);
		System.arraycopy(createCheckSum(hrp, data), 0, dataWithChecksum, data.length, Bech32mUtils.CHECKSUM_LEN);

		StringBuilder encoded = new StringBuilder(hrp.length() + 1 + dataWithChecksum.length);

		// Firstly, append hrp and separator
		encoded.append(hrp);
		encoded.append('1');

		// encode data with checksum
		for (byte value : dataWithChecksum) {
			encoded.append(Bech32mUtils.BECH32M_CHARSET.charAt(value));
		}

		return encoded.toString();
	}
}
