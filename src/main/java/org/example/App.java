package org.example;

import com.google.common.primitives.Bytes;

import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Main class for Bech32m encoding tool
 *
 * @author Oliver Svetlik
 */
public class App 
{
    private final static int SUCCESS = 0;
    private final static int FAILURE = 1;

    private final static String E_FLAG = "-e";
    private final static String D_FLAG = "-d";
    private final static String IN_FLAG = "-i";
    private final static String OUT_FLAG = "-o";
    private final static String IN_FORMAT_FLAG = "--in-format";
    private final static String OUT_FORMAT_FLAG = "--out-format";
    private final static String HRP_FLAG = "--hrp";
    private final static Set<String> FLAGS = Set.of(E_FLAG, D_FLAG, IN_FLAG, OUT_FLAG, IN_FORMAT_FLAG, OUT_FORMAT_FLAG, HRP_FLAG);

    // BINARY_FLAGS are flags expecting one value immediately after flag itself
    private final static Set<String> BINARY_FLAGS = Set.of(IN_FLAG, OUT_FLAG, IN_FORMAT_FLAG, OUT_FORMAT_FLAG, HRP_FLAG);

    private static Operation operation = Operation.ENCODE;
    private static List<Byte> input = new ArrayList<>();
    private static String hrp = null;
    private static String inputFile = null;
    private static String outputFile = null;
    private static InOutFormat inputFormat = InOutFormat.BASE64;
    private static InOutFormat outputFormat = InOutFormat.BECH32M;

    public static void main( String[] args )
    {
        if (initSettings(args) == FAILURE) {
            return;
        }

        if (input.size() == 0 && inputFile != null) {
            loadInputFromFile();
        }

        if (operation.equals(Operation.ENCODE)) {
            String output = Encoder.bech32mEncode(hrp, Bytes.toArray(input));
            writeOutput(output, List.of());
        } else if (operation.equals(Operation.DECODE)) {
            List<Object> hrpAndPayload = Decoder.bech32mDecode(bech32mBytesToString(input));
            if (hrpAndPayload.isEmpty()) {
                System.out.println("Decoding failed!");
                return;
            }
            String outHRP = (String) hrpAndPayload.get(0);
            List<Byte> outPayload = (List<Byte>) hrpAndPayload.get(1);
            writeOutput(outHRP, outPayload);
        }
    }

    /**
     * Method initializes all settings based on flags and stores the input,
     * if it was provided on stdin.
     *
     * @param args arguments of program
     */
    private static int initSettings(String[] args) {
        int i = 0;
        String presubmittedInput = null;

        while (i < args.length) {
            // if unknown flag was provided, it could be input itself,
            // however if it is already set (not null), it must be some known flag
            if (presubmittedInput != null && !FLAGS.contains(args[i])) {
                printHelp("Unknown flag provided: " + args[i]);
                return FAILURE;
            }

            // if binary flag found, index i can't be last one (args.length - 1)
            if (BINARY_FLAGS.contains(args[i]) && i >= args.length - 1) {
                printHelp("Missing value for " + args[i]);
                return FAILURE;
            }

            switch (args[i]) {
                case D_FLAG:
                    operation = Operation.DECODE;
                    inputFormat = InOutFormat.BECH32M;
                    outputFormat = outputFormat != InOutFormat.BECH32M
                            ? outputFormat
                            : InOutFormat.BASE64;
                    break;
                case IN_FLAG:
                    inputFile = args[++i];
                    break;
                case OUT_FLAG:
                    outputFile = args[++i];
                    break;
                case IN_FORMAT_FLAG:
                    inputFormat = InOutFormat.valueOf(args[++i].toUpperCase());
                    break;
                case OUT_FORMAT_FLAG:
                    outputFormat = InOutFormat.valueOf(args[++i].toUpperCase());
                    break;
                case HRP_FLAG:
                    hrp = args[++i];
                    break;
                default:
                    presubmittedInput = args[i];
                    break;
            }
            i++;
        }
        if (hrp == null && operation.equals(Operation.ENCODE)) {
            return FAILURE;
        }
        input = textToBinaryInput(presubmittedInput);
        return SUCCESS;
    }

    /**
     * Method transforms text representation of data to list of bytes, based on
     * encoding in static class attribute format.
     *
     * @param data text representation of data
     * @return data transformed to list of bytes
     */
    private static List<Byte> textToBinaryInput(String data) {
        byte[] decoded;
        if (inputFormat.equals(InOutFormat.HEX)) {
            decoded = new BigInteger(data, 16).toByteArray();
        } else if (inputFormat.equals(InOutFormat.BASE64)) {
            decoded = Base64.getDecoder().decode(data.getBytes());
        } else {
            // All other possibilities of InOutFormat should not be transformed, because:
            // - BECH32M input is valid only for decode and decode function expects this format
            // - BINARY input is valid only for encode and encode function expects this format
            decoded = data.getBytes();
        }
        return IntStream.range(0, decoded.length)
                .mapToObj(i -> decoded[i])
                .collect(Collectors.toList());
    }

    /**
     * Handles the load of input data.
     *
     * @return SUCCESS or FAILURE, based on if load of data succeeded
     */
    private static int loadInputFromFile() {
        try(InputStreamReader isr = new InputStreamReader(new FileInputStream(inputFile))) {
            if (inputFormat.equals(InOutFormat.BINARY)) {
                loadBinaryInputFromFile(isr);
            } else {
                loadTextInputFromFile(isr);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return FAILURE;
        }
        return SUCCESS;
    }

    /**
     * Simply reads all bytes from isr and stores them to input attribute.
     * Method expects binary data to be provided.
     *
     * @param isr InputStreamReader of file provided as program argument
     * @throws IOException if read operation fails
     */
    private static void loadBinaryInputFromFile(InputStreamReader isr) throws IOException {
        int byteRead;
        while ((byteRead = isr.read()) != -1) {
            input.add((byte) byteRead);
        }
    }

    /**
     * Method reads input from isr as a String. Then the result of textToBinary(...)
     * is saved to input attribute.
     *
     * @param isr InputStreamReader of file provided as program argument
     * @throws IOException if read operation fails
     */
    private static void loadTextInputFromFile(InputStreamReader isr) throws IOException {
        StringBuilder inputRead = new StringBuilder();
        int byteRead;
        while ((byteRead = isr.read()) != -1) {
            inputRead.append((char) byteRead);
        }
        input = textToBinaryInput(inputRead.toString());
    }


    private static String bech32mBytesToString(List<Byte> data) {
        return new String(Bytes.toArray(data));
    }

    private static void writeOutput(String outputHrp, List<Byte> payload) {
        if (outputFormat.equals(InOutFormat.BINARY)) {
            writeBinaryOutput(outputHrp, payload);
        } else {
            writeTextOutput(outputHrp, payload);
        }
    }

    private static void writeBinaryOutput(String outputHrp, List<Byte> payload) {
        try(PrintStream printStream = outputFile != null ? new PrintStream(outputFile) : System.out) {
            printStream.print(outputHrp);
            for (Byte b : payload) {
                printStream.print(b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeTextOutput(String outputHrp, List<Byte> payload) {
        byte[] outputBytes = Bytes.toArray(payload);

        String stringPayload = "";
        if (outputFormat.equals(InOutFormat.BASE64)) {
            outputBytes = Base64.getEncoder().encode(outputBytes);
            stringPayload = outputBytes.length == 0
                    ? ""
                    : new BigInteger(outputBytes).toString();
        } else if (outputFormat.equals(InOutFormat.HEX)) {
            stringPayload = outputBytes.length == 0
                    ? ""
                    : new BigInteger(outputBytes).toString(16);
        }

        try(PrintStream printStream = outputFile != null ? new PrintStream(outputFile) : System.out) {
            printStream.println(outputHrp);
            printStream.println(stringPayload);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printHelp() {
        System.out.println("Bech32m encoding tool usage:");
        System.out.println("<[-e [--in-format <format>]] | -d [--out-format <format>]> <-i <file> | <input>> <-hrp <value>> [-o <file>]");
        System.out.println("Options:");
        System.out.println("  -e                   | encoding mode (by default)");
        System.out.println("  -d                   | decoding mode");
        System.out.println("  -i <file>            | input file (stdin by default)");
        System.out.println("  -o <file>            | output file (stdout by default)");
        System.out.println("  --in-format <value>  | format of input; possible values: base64 (by default), hex, binary");
        System.out.println("  --out-format <value> | format of output; possible values: base64 (by default), hex, binary");
        System.out.println("  --hrp <value>        | definition of human readable part");
    }

    private static void printHelp(String message) {
        System.out.println(message);
        printHelp();
    }

    private enum Operation {
        ENCODE,
        DECODE
    }

    private enum InOutFormat {
        BECH32M,
        BASE64,
        HEX,
        BINARY
    }
}
