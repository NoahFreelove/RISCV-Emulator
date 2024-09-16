package org.RiscVEmulator.Instructions.InstructionMetadata;

// Instruction metadata is used for static data about an instruction
// like opcode, funct3, funct7, etc. This is not for registers or immediate values as those change based on the instruction inputs
public abstract class InstructionMetadata{
    public final int opcode;

    public InstructionMetadata(int opcode) {
        this.opcode = opcode;
    }
}
