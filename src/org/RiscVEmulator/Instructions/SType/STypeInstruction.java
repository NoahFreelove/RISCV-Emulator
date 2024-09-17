package org.RiscVEmulator.Instructions.SType;

import org.RiscVEmulator.Instructions.Instruction;
import org.RiscVEmulator.Instructions.InstructionMetadata.RTypeMetadata;
import org.RiscVEmulator.Instructions.InstructionMetadata.STypeMetadata;
import org.RiscVEmulator.Instructions.InstructionType;
import org.RiscVEmulator.Registers.Immediate;
import org.RiscVEmulator.Registers.Register;
import org.RiscVEmulator.State;

public abstract class STypeInstruction extends Instruction {
    protected Immediate imm;
    protected Register rs1;
    protected Register rs2;
    // remember that rs1 is the one in the parenthesis!
    // sw rs2, offset(rs1)
    public STypeInstruction(String instName, Immediate imm, Register rs1, Register rs2, STypeMetadata meta, State state) {
        super(instName, InstructionType.R_TYPE, meta, state);
        this.imm = imm;
        this.rs1 = rs1;
        this.rs2 = rs2;
    }

    @Override
    public String toBinary(boolean spaceSeparated) {
        // should be encoded in LITTLE ENDIAN as: imm[11:5], rs2, rs1, funct3, imm[4:0], opcode
        // Metadata holds the opcode, funct3, and funct7 while the registers hold the register numbers
        // The instruction is encoded as follows:
        String binOutput = "";

        // 7 bit imm
        String imm11_5 = imm.toBinary();
        imm11_5 = imm11_5.substring(0, 7);
        binOutput += imm11_5;

        // ensure rs2 is 5 bits
        binOutput += formatToSize(Integer.toBinaryString(this.rs2.name), 5);

        // ensure rs1 is 5 bits
        binOutput += formatToSize(Integer.toBinaryString(this.rs1.name), 5);

        // ensure funct3 is 3 bits
        binOutput += formatToSize(Integer.toBinaryString(((STypeMetadata)metadata).getFunct3()), 3);

        // 5 bit imm
        String imm4_0 = imm.toBinary(); // we don't need to check size because imm class does that for us
        imm4_0 = imm4_0.substring(7);
        binOutput += imm4_0;

        // ensure opcode is 7 bits
        binOutput += formatToSize(Integer.toBinaryString(metadata.opcode), 7);

        if(!spaceSeparated)
            return binOutput;
        // every 8 bits, add a space
        for (int i = 8; i < binOutput.length(); i+=9){
            binOutput = binOutput.substring(0, i) + " " + binOutput.substring(i);
        }

        return binOutput;
    }
}
