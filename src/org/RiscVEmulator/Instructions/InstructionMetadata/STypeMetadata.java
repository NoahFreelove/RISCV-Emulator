package org.RiscVEmulator.Instructions.InstructionMetadata;

public class STypeMetadata extends InstructionMetadata{
    int funct3;

    public STypeMetadata(int funct3){
        super(0b0100011);
        this.funct3 = funct3;
    }
    public int getFunct3(){
        return funct3;
    }
}
