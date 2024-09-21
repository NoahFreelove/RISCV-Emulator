package org.RiscVEmulator.Instructions.BType;

import org.RiscVEmulator.Instructions.Instruction;
import org.RiscVEmulator.Instructions.InstructionMetadata.BTypeMetadata;
import org.RiscVEmulator.Instructions.InstructionType;
import org.RiscVEmulator.Registers.Immediate;
import org.RiscVEmulator.Registers.Register;
import org.RiscVEmulator.State;

public abstract class BTypeInstruction extends Instruction {
    protected Register rs1;
    protected Register rs2;
    protected Immediate imm;
    protected String label;
    public BTypeInstruction(String instName, Register rs1, Register rs2, Immediate imm, String label, BTypeMetadata meta, State state) {
        super(instName, InstructionType.B_TYPE, meta, state);
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.label = label;
        this.imm = imm;
    }

    @Override
    public String toBinary(boolean spaceSeparated) {
        int addr = imm.value();

        if(!label.isEmpty()){
            addr = state.getRelativeLabelAddress(label);
        }

        // encode imm12, imm10:5, rs2, rs1, funct3, imm4:1, imm11, opcode
        String imm12 = String.format("%1$-12s", Integer.toBinaryString(addr)).replace(' ', '0');
        String imm10_5 = imm12.substring(0, 6);
        String imm4_1 = imm12.substring(6, 10);
        String imm11 = imm12.substring(10, 11);
        String rs2Str = String.format("%1$-5s", Integer.toBinaryString(rs2.name)).replace(' ', '0');
        String rs1Str = String.format("%1$-5s", Integer.toBinaryString(rs1.name)).replace(' ', '0');
        String funct3 = String.format("%1$-3s", Integer.toBinaryString(((BTypeMetadata)metadata).funct3)).replace(' ', '0');
        String opcode = String.format("%1$-7s", Integer.toBinaryString(metadata.opcode)).replace(' ', '0');

        return imm12 + imm10_5 + rs2Str + rs1Str + funct3 + imm4_1 + imm11 + opcode;
    }

    @Override
    public String toString() {
        if(label.isEmpty()){
            return friendlyName + " " + rs1.colloquialName + ", " + rs2.colloquialName + ", " + imm.value();
        }
        return friendlyName + " " + rs1.colloquialName + ", " + rs2.colloquialName + ", " + label;
    }
}
