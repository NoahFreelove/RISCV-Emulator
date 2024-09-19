package org.RiscVEmulator.Instructions.InstructionMetadata;

public class PseudoMetadata extends InstructionMetadata{
    int funct3;
    int funct7;

    public PseudoMetadata(int opcode, int funct3, int funct7){
        super(opcode);
        this.funct3 = funct3;
        this.funct7 = funct7;
    }

    public PseudoMetadata(int opcode, int funct3){
        super(opcode);
        this.funct3 = funct3;
        this.funct7 = 0;
    }

    public PseudoMetadata(int opcode){
        super(opcode);
        this.funct3 = 0;
        this.funct7 = 0;
    }

    public int getFunct3(){
        return funct3;
    }

    public int getFunct7(){
        return funct7;
    }
}
