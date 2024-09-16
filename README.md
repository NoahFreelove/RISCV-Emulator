## RISC-V 32 (partial) Emulator

This is a simple RISC-V 32-bit emulator written in Java to execute RISC-V instructions.

This implementation does not convert pseudo-instructions to real instructions, instead it treats them as real instructions (for simplicity).
This may change as more features get added, but for example, %pcrel_hi(label) isn't supported so you cant use it to load a label address into a register.

Does not currently support all RISC-V instructions, but supports a subset of them.
Does not currently support data memory, but supports register memory.