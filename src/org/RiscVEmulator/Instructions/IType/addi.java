package org.RiscVEmulator.Instructions.IType;

import org.RiscVEmulator.Instructions.InstructionMetadata.ITypeMetadata;
import org.RiscVEmulator.Registers.Immediate;
import org.RiscVEmulator.Registers.Register;
import org.RiscVEmulator.State;

public class addi extends ITypeInstruction {
    public addi(Register rd, Register rs1, Immediate immediate, ITypeMetadata meta, State state) {
        super("addi", rd, rs1, immediate, meta, state);
    }

    @Override
    public void execute() {
        int rs1Val = state.getRegisterValue(rs1.colloquialName);
        int immVal = imm.value();
        int result = rs1Val + immVal;
        state.setRegisterInt(rd.colloquialName, result);
    }
}
