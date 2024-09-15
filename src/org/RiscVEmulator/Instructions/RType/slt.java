package org.RiscVEmulator.Instructions.RType;

import org.RiscVEmulator.Instructions.InstructionMetadata.RTypeMetadata;
import org.RiscVEmulator.Registers.Register;
import org.RiscVEmulator.State;

public class slt extends RTypeInstruction {
    public slt(Register rd, Register rs1, Register rs2, RTypeMetadata meta, State state) {
        super("slt" , rd, rs1, rs2, meta, state);
    }

    @Override
    public void execute() {
        int rs1Val = state.getRegisterValue(rs1.colloquialName);
        int rs2Val = state.getRegisterValue(rs2.colloquialName);
        int result = rs1Val < rs2Val ? 1 : 0;
        state.setRegisterInt(rd.colloquialName, result);

    }
}
