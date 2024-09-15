package org.RiscVEmulator.Instructions.InstructionMetadata;

public class RTypeMetadata extends InstructionMetadata{
    int funct3;
    int funct7;

    public RTypeMetadata(int funct3, int funct7){
        this.opcode = 0b0110011;
        this.funct3 = funct3;
        this.funct7 = funct7;
    }
    public int getFunct3(){
        return funct3;
    }
    public int getFunct7(){
        return funct7;
    }
}
