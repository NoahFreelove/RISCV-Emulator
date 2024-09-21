package org.RiscVEmulator.Instructions.BType;

import org.RiscVEmulator.Instructions.InstructionMetadata.BTypeMetadata;
import org.RiscVEmulator.Registers.Immediate;
import org.RiscVEmulator.Registers.Register;
import org.RiscVEmulator.State;

public class bltu extends BTypeInstruction {
    public bltu(Register rs1, Register rs2, Immediate immediate, String label, BTypeMetadata meta, State state) {
        super("bltu", rs1, rs2, immediate, label, meta, state);
    }

    @Override
    public void execute() {
        int addr = imm.value();

        if(!label.isEmpty()){
            addr = state.getRelativeLabelAddress(label);
        }

        int val1 = state.getRegisterValue(rs1.colloquialName);
        int val2 = state.getRegisterValue(rs2.colloquialName);

        int res = Integer.compareUnsigned(val1, val2);
        if(res < 0){
            state.PC += addr;
            state.postJump = true;
        }
    }
}
