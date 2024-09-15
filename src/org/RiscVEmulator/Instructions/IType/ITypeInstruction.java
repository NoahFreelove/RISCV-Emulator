package org.RiscVEmulator.Instructions.IType;

import org.RiscVEmulator.Instructions.Instruction;
import org.RiscVEmulator.Instructions.InstructionMetadata.RTypeMetadata;
import org.RiscVEmulator.Instructions.InstructionType;
import org.RiscVEmulator.Registers.Immediate;
import org.RiscVEmulator.Registers.Register;
import org.RiscVEmulator.State;

public abstract class ITypeInstruction  extends Instruction {
    protected Register rd;
    protected Register rs1;
    protected Immediate imm;
    public ITypeInstruction(String instName, Register rd, Register rs1, Immediate imm, RTypeMetadata meta, State state) {
        super(instName, InstructionType.R_TYPE, meta, state);
        this.rd = rd;
        this.rs1 = rs1;
        this.imm = imm;
    }


    private String formatToSize(String input, int size){
        while(input.length() < size){
            input = "0" + input;
        }
        if (input.length() > size){
            input = input.substring(input.length()-size);
        }

        return input;
    }


    @Override
    public String toBinary(boolean spaceSeparated) {
        // should be encoded in LITTLE ENDIAN as: imm[11:0], rs1, funct3, rd, opcode
        // Metadata holds the opcode, funct3, and funct7 while the registers hold the register numbers
        // The instruction is encoded as follows:
        String binOutput = "";

        // ensure imm is 12 bits
        String imm = this.imm.toBinary();
        imm = formatToSize(imm, 12);
        binOutput += imm;

        // ensure rs1 is 5 bits
        String rs1 = Integer.toBinaryString(this.rs1.name);
        rs1 = formatToSize(rs1, 5);

        binOutput += rs1;

        // ensure funct3 is 3 bits
        String funct3 = Integer.toBinaryString(((RTypeMetadata)metadata).getFunct3());
        funct3 = formatToSize(funct3, 3);
        binOutput += funct3;

        // ensure rd is 5 bits
        String rd = Integer.toBinaryString(this.rd.name);
        rd = formatToSize(rd, 5);
        binOutput += rd;

        // Opcode will always be 7 bits because its hardcoded in the metadata
        String opcode = Integer.toBinaryString(metadata.opcode);
        // but we should make sure it is 7 bits
        opcode = formatToSize(opcode, 7);
        binOutput += opcode;

        if(!spaceSeparated)
            return binOutput;

        for (int i = 8; i < binOutput.length(); i+=9){
            binOutput = binOutput.substring(0, i) + " " + binOutput.substring(i);
        }

        return binOutput;
    }
}
