package org.RiscVEmulator.Instructions.RType;

import org.RiscVEmulator.Instructions.InstructionMetadata.RTypeMetadata;
import org.RiscVEmulator.Registers.Register;
import org.RiscVEmulator.State;

public class sltu extends RTypeInstruction {
    public sltu(Register rd, Register rs1, Register rs2, RTypeMetadata meta, State state) {
        super("sltu" , rd, rs1, rs2, meta, state);
    }

    @Override
    public void execute() {
        int rs1Val = state.getRegisterValue(rs1.colloquialName);
        int rs2Val = state.getRegisterValue(rs2.colloquialName);
        int result = Integer.compareUnsigned(rs1Val, rs2Val);
        state.setRegisterInt(rd.colloquialName, result);

    }
}
