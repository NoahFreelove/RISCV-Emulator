package org.RiscVEmulator.Instructions.BType;

import org.RiscVEmulator.Instructions.InstructionMetadata.BTypeMetadata;
import org.RiscVEmulator.Registers.Immediate;
import org.RiscVEmulator.Registers.Register;
import org.RiscVEmulator.State;

public class blt extends BTypeInstruction {
    public blt(Register rs1, Register rs2, Immediate immediate, String label, BTypeMetadata meta, State state) {
        super("blt", rs1, rs2, immediate, label, meta, state);
    }

    @Override
    public void execute() {
        int addr = imm.value();

        if(!label.isEmpty()){
            addr = state.getRelativeLabelAddress(label);
        }

        int val1 = state.getRegisterValue(rs1.colloquialName);
        int val2 = state.getRegisterValue(rs2.colloquialName);

        if(val1 < val2){
            state.PC += addr;
            state.postJump = true;
        }
    }
}
