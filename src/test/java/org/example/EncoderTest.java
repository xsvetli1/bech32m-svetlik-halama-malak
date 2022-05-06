package org.example;

import org.junit.Assert;
import org.junit.Test;

public class EncoderTest {

	private class Bech32mBlock {

		private final String hrp;
		private final byte[] data;

		Bech32mBlock(final String hrp, final byte[] data) {
			this.hrp = hrp;
			this.data = data.clone();
		}
	}

	@Test
	public void encodeValidInput() {
		// all inputs are computed from referential implementations by decoding TEST VECTORS

		// inputs computed from test vectors
		Bech32mBlock[] inputs = {
				new Bech32mBlock("a", new byte[0]),
				new Bech32mBlock("an83characterlonghumanreadablepartthatcontainsthetheexcludedcharactersbioandnumber1", new byte[0]),
				new Bech32mBlock("abcdef", new byte[]{31, 30, 29, 28, 27, 26, 25, 24, 23, 22, 21, 20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0}),
				new Bech32mBlock("1", new byte[]{31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31}),
				new Bech32mBlock("split", new byte[]{24, 23, 25, 24, 22, 28, 1, 16, 11, 29, 8, 25, 23, 29, 19, 13, 16, 23, 29, 22, 25, 28, 1, 16, 11, 3, 25, 29, 27, 25, 3, 3, 29, 19, 11, 25, 3, 3, 25, 13, 24, 29, 1, 25, 3, 3, 25, 13}),
				new Bech32mBlock("?", new byte[0])
		};

		// expected outputs (TEST VECTORS)
		String[] expected = {
				"a1lqfn3a",
				"an83characterlonghumanreadablepartthatcontainsthetheexcludedcharactersbioandnumber11sg7hg6",
				"abcdef1l7aum6echk45nj3s0wdvt2fg8x9yrzpqzd3ryx",
				"11llllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllludsr8",
				"split1checkupstagehandshakeupstreamerranterredcaperredlc445v",
				"?1v759aa"
		};

		for (int i = 0; i < inputs.length; i++) {
			Assert.assertEquals(expected[i], Encoder.bech32mEncode(inputs[i].hrp, inputs[i].data));
		}
	}
}
