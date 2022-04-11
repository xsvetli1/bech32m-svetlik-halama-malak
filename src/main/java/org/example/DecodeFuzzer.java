package org.example;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;

public class DecodeFuzzer {
    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
        App.main(new String[]{"-d", data.consumeRemainingAsString(), "--out-format", "HEX"});
    }
}
