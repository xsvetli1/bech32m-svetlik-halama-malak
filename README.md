# bech32m-svetlik-halama-malak

Bech32m encoding format is an upgraded version of its predecessor Bech32.
More information can be found on:

[Bech32](https://github.com/bitcoin/bips/blob/master/bip-0173.mediawiki) <br/>
[Bech32m](https://github.com/bitcoin/bips/blob/master/bip-0350.mediawiki)

This project supports simple Bech32m encoding/decoding (without Segwit addresses)
with support of `BASE64|HEX|BINARY`encoding <b><u>input</u></b> format and `BASE64|HEX|BINARY`
decoding <b><u>output</u></b> format.

Application has simple command line interface (CLI) implemented, which can be used as following:
```
Bech32m encoding tool usage:
<[-e [--in-format <format>]] | -d [--out-format <format>]> <-i <file> | <input>> <-hrp <value>> [-o <file>]
Options:
  -e                   | encoding mode (by default)
  -d                   | decoding mode
  -i <file>            | input file (stdin by default)
  -o <file>            | output file (stdout by default)
  --in-format <value>  | format of input; possible values: base64 (by default), hex, binary
  --out-format <value> | format of output; possible values: base64 (by default), hex, binary
  --hrp <value>        | definition of human readable part
```


