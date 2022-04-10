package org.example.integration;

import org.example.App;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Test class for integration testing of whole application.
 *
 * @author Ľuboslav Halama <lubo.halama@gmail.com>
 */
public class AppIntegrationTest {

	private final String HRP_PRINTOUT_PREFIX = "";
	private final String HRP_PRINTOUT_SUFFIX = "" + System.lineSeparator();

	private final String PAYLOAD_PRINTOUT_PREFIX = "";
	private final String PAYLOAD_PRINTOUT_SUFFIX = "" + System.lineSeparator();

	private final String UNKNOWN_FLAG_MSG_PREFIX = "Unknown flag provided: ";
	private final String HELP_PRINTOUT = "Bech32m encoding tool usage:" + System.lineSeparator()
			+ "<[-e [--in-format <format>]] | -d [--out-format <format>]> <-i <file> | <input>> <-hrp <value>> [-o <file>]" + System.lineSeparator()
			+ "Options:" + System.lineSeparator()
			+ "  -e                   | encoding mode (by default)" + System.lineSeparator()
			+ "  -d                   | decoding mode" + System.lineSeparator()
			+ "  -i <file>            | input file (stdin by default)" + System.lineSeparator()
			+ "  -o <file>            | output file (stdout by default)" + System.lineSeparator()
			+ "  --in-format <value>  | format of input; possible values: base64 (by default), hex, binary" + System.lineSeparator()
			+ "  --out-format <value> | format of output; possible values: base64 (by default), hex, binary" + System.lineSeparator()
			+ "  --hrp <value>        | definition of human readable part" + System.lineSeparator();

	// streams used for testing (standard output and error output is moved to them)
	private ByteArrayOutputStream out = new ByteArrayOutputStream();
	private ByteArrayOutputStream err = new ByteArrayOutputStream();

	// original streams
	private PrintStream originalOut = System.out;
	private PrintStream originalErr = System.err;

	@Before
	public void setOutputStreams() throws IOException {
		System.setOut(new PrintStream(out));
		System.setErr(new PrintStream(err));
		out.flush();
		err.flush();
	}

	@After
	public void fixOutputStreams() {
		System.setOut(originalOut);
		System.setErr(originalErr);
	}

	@Test
	public void decodeValidWithHexOutputAndEmptyData() {
		String[] flags = {"-d", "a1lqfn3a", "--out-format", "HEX"};

		App.main(flags);

		// Human-readable part should not be changed
		final String expectedOut = HRP_PRINTOUT_PREFIX + "a" + HRP_PRINTOUT_SUFFIX
				+ PAYLOAD_PRINTOUT_PREFIX + "" + PAYLOAD_PRINTOUT_SUFFIX;

		Assert.assertEquals(expectedOut, out.toString());
		Assert.assertEquals("", err.toString());
	}

	@Test
	public void decodeValidWithHexOutput() {
		String[] flags = {"-d", "abcdef1l7aum6echk45nj3s0wdvt2fg8x9yrzpqzd3ryx", "--out-format", "HEX"};

		App.main(flags);

		// Human-readable part should not be changed
		final String expectedOut = HRP_PRINTOUT_PREFIX + "abcdef" + HRP_PRINTOUT_SUFFIX
				+ PAYLOAD_PRINTOUT_PREFIX + "1f1e1d1c1b1a191817161514131211100f0e0d0c0b0a09080706050403020100" + PAYLOAD_PRINTOUT_SUFFIX;

		Assert.assertEquals(expectedOut, out.toString());
		Assert.assertEquals("", err.toString());
	}


	// ===========================
	// ||  INVALID INPUT TESTS  ||
	// ===========================

	@Test
	public void decodeInvalidOperationFlag() {
		final String invalidFlag = "--dec";
		String[] flags = {invalidFlag, "a1lqfn3a"};

		App.main(flags);

		// next unknown flag will be considered as invalid, since '--dec' will be considered as decode input
		final String expectedOut = UNKNOWN_FLAG_MSG_PREFIX + "a1lqfn3a" + System.lineSeparator() + HELP_PRINTOUT;

		Assert.assertEquals(expectedOut, out.toString());
		Assert.assertEquals("", err.toString());
	}


}
