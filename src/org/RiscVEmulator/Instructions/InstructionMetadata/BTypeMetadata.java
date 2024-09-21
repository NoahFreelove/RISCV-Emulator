package org.RiscVEmulator.Instructions.InstructionMetadata;

public class BTypeMetadata extends InstructionMetadata{
    public final int funct3;
    public BTypeMetadata(int funct3) {
        super(1100011);
        this.funct3 = funct3;
    }
}
