## RISC-V 32 (partial) Emulator

This is a simple RISC-V 32-bit emulator written in Java to execute RISC-V instructions.
Please note that this is not designed to be a 100% faithful *simulation* but is a crude emulation of RISC-V instructions
for purposes of quick testing RISC-V assembly code. It should more-so resemble an interpreted language.

This implementation does not convert pseudo-instructions to real instructions, instead it treats them as real instructions (for simplicity).
This may change as more features get added, but for example, %pcrel_hi(label) isn't supported so you cant use it to load a label address into a register.

Does not currently support all RISC-V instructions, but supports a subset of them, will get to the rest of 'em later.
