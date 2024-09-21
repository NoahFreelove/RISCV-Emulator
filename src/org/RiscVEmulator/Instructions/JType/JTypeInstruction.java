package org.RiscVEmulator.Instructions.JType;

import org.RiscVEmulator.Instructions.Instruction;
import org.RiscVEmulator.Instructions.InstructionMetadata.ITypeMetadata;
import org.RiscVEmulator.Instructions.InstructionMetadata.JTypeMetadata;
import org.RiscVEmulator.Instructions.InstructionType;
import org.RiscVEmulator.Registers.Immediate;
import org.RiscVEmulator.Registers.Register;
import org.RiscVEmulator.State;

public abstract class JTypeInstruction extends Instruction {
    protected Register rd;
    protected Immediate imm;
    protected String label;
    public JTypeInstruction(String instName, Register rd, Immediate imm, String label, JTypeMetadata meta, State state) {
        super(instName, InstructionType.J_TYPE, meta, state);
        this.rd = rd;
        this.label = label;
        this.imm = imm;
    }

    @Override
    public String toBinary(boolean spaceSeparated) {
        int addr = imm.value();

        if(!label.isEmpty()){
            addr = state.getRelativeLabelAddress(label);
        }

        // encode imm20, imm10:1, imm19:12, rd, opcode
        String imm20 = String.format("%1$-20s", Integer.toBinaryString(addr)).replace(' ', '0');
        String imm10_1 = imm20.substring(0, 10);
        String imm11 = imm20.substring(10, 11);
        String imm19_12 = imm20.substring(11, 20);
        String rdStr = String.format("%1$-5s", Integer.toBinaryString(rd.name)).replace(' ', '0');
        String opcode = String.format("%1$-7s", Integer.toBinaryString(metadata.opcode)).replace(' ', '0');

        return imm20 + imm10_1 + imm11 + imm19_12 + rdStr + opcode;
    }

    @Override
    public String toString() {
        if(label.isEmpty()){
            return friendlyName + " " + rd.colloquialName + ", " + imm.value();
        }
        return friendlyName + " " + rd.colloquialName + ", " + label;
    }
}
