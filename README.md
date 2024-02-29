# CCT-FCT Converter
## Summary
A simple _minecraft connected texture atlas_ conversion tool, converts between different formats.
Create Connected Textures (standard + compact) and Fusion Connected Textures mappings are already available (see `src/mappings/ * .txt`)
> It's "Create Connected Textures to Fusion Connected Textures converter", but you can really use it with any format if you have the mapping for it :P

## Usage
#### Three files need to be in the src/ directory:
- `input.png`: The texture that you need to convert
- `input.txt`: Mapping for the input texture's format
- `output.txt`: Mapping describing the desired output (output format's mapping)
#### Assert those three files are available and correctly named. Alternatively, use command line arguments to pass custom paths:
- `arg1`: Path to the texture that you need to convert (.png)
- `arg2`: Path to the mapping for the input texture's format (.txt)
- `arg3`: Path to the mapping for the output format (.txt)

### Run src/Main.java, it will output the formatted texture in src/out/output.png
