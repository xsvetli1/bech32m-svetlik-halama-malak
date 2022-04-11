package org.example;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;

public class SimpleDecodeFuzzer {
    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
        Decoder.bech32mDecode(data.consumeRemainingAsAsciiString());
    }

    public static void main(String[] args) {
        System.out.println("here!!");
    }
}
