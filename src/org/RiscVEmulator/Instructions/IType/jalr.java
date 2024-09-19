package org.RiscVEmulator.Instructions.IType;

import org.RiscVEmulator.Instructions.InstructionMetadata.ITypeMetadata;
import org.RiscVEmulator.Registers.Immediate;
import org.RiscVEmulator.Registers.RegNameColloquial;
import org.RiscVEmulator.Registers.Register;
import org.RiscVEmulator.State;

public class jalr extends ITypeInstruction {
    public jalr(Register rd, Register rs1, Immediate immediate, ITypeMetadata meta, State state) {
        super("jalr", rd, rs1, immediate, meta, state);
    }

    @Override
    public void execute() {
        state.setRegisterInt(rd.colloquialName, state.PC + 4);
        int rs1Val = state.getRegisterValue(rs1.colloquialName);
        state.PC = rs1Val + imm.value(); // when next instruction is called PC will be incremented by 4 so we subtract 4
        state.postJump = true;
    }
}
