package org.example;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Class provides common functionality used in both encoding and decoding.
 *
 * @author Ľuboslav Halama <lubo.halama@gmail.com>
 */
public class Bech32mUtils {

	public static final int BECH32M_CONST = 0x2bc830a3;

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

	/**
	 * Checks validity of HRP (Human-readable part).
	 *
	 * Method checks two things:
	 *   1.) HRP is either in lowercase or uppercase, but not both (only one set of characters)
	 *   2.) HRP contains only valid characters (33-126 value range)
	 *
	 * @param hrp human-readable part
	 * @return boolean true, if hrp is valid, false otherwise
	 */
	public static boolean isHRPValid(final String hrp) {

		boolean isValid = true;
		boolean containsLetter = hrp.matches(".*[a-zA-Z].*");

		// only one of them can be true (it is either lowercase or uppercase)
		if (hrp.equals(hrp.toLowerCase(Locale.ROOT)) == hrp.equals(hrp.toUpperCase(Locale.ROOT))) {
			// last check, if HRP does not contain any letter, lowercase and uppercase will be the same,
			// thus it must contain at least one letter to make it invalid
			if (containsLetter) {
				isValid = false;
			}
		}

		for (char x : hrp.toCharArray()) {
			if (x < 33 || x > 126) {
				isValid = false;
			}
		}

		if (hrp.length() < 1 || hrp.length() > 83) {
			isValid = false;
		}

		return isValid;
	}

}