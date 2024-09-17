package org.RiscVEmulator.Instructions.InstructionMetadata;

public class ITypeMetadata extends InstructionMetadata{
    int funct3;
    int funct7 = 0; // Only usedfor slli,srli,srai because they have the same opcode as IType instructions

    public ITypeMetadata(int funct3){
        super(0b0010011);
        this.funct3 = funct3;
    }
    public ITypeMetadata(int funct3, int funct7){
        super(0b0010011);
        this.funct3 = funct3;
        this.funct7 = funct7;
    }

    public ITypeMetadata(int opcode, int funct3, int funct7){
        super(opcode);
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
