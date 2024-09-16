package org.RiscVEmulator.Instructions.RType;

import org.RiscVEmulator.Instructions.Instruction;
import org.RiscVEmulator.Instructions.InstructionMetadata.RTypeMetadata;
import org.RiscVEmulator.Instructions.InstructionType;
import org.RiscVEmulator.Registers.Register;
import org.RiscVEmulator.State;

public abstract class RTypeInstruction extends Instruction {
    protected Register rd;
    protected Register rs1;
    protected Register rs2;

    public RTypeInstruction(String instName, Register rd, Register rs1, Register rs2, RTypeMetadata meta, State state) {
        super(instName, InstructionType.R_TYPE, meta, state);
        this.rd = rd;
        this.rs1 = rs1;
        this.rs2 = rs2;
    }

    @Override
    public String toBinary(boolean spaceSeparated) {
        // should be encoded in LITTLE ENDIAN as: funct7, rs2, rs1, funct3, rd, opcode
        // Metadata holds the opcode, funct3, and funct7 while the registers hold the register numbers
        // The instruction is encoded as follows:
        String binOutput = "";

        // ensure funct7 is 7 bits
        String funct7 = Integer.toBinaryString(((RTypeMetadata)metadata).getFunct7());
        funct7 = formatToSize(funct7, 7);
        binOutput += funct7;

        // ensure rs2 is 5 bits
        String rs2 = Integer.toBinaryString(this.rs2.name);
        rs2 = formatToSize(rs2, 5);
        binOutput += rs2;

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
        // every 8 bits, add a space
        for (int i = 8; i < binOutput.length(); i+=9){
            binOutput = binOutput.substring(0, i) + " " + binOutput.substring(i);
        }

        return binOutput;
    }
    @Override
    public String toString() {
        return friendlyName + " " + rd.colloquialName + ", " + rs1.colloquialName + ", " + rs2.colloquialName;
    }
}
