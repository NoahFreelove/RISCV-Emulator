package org.RiscVEmulator.Instructions.IType;

import org.RiscVEmulator.Instructions.InstructionMetadata.ITypeMetadata;
import org.RiscVEmulator.Instructions.InstructionMetadata.RTypeMetadata;
import org.RiscVEmulator.Registers.Immediate;
import org.RiscVEmulator.Registers.Register;
import org.RiscVEmulator.State;

public class sltiu extends ITypeInstruction {
    public sltiu(Register rd, Register rs1, Immediate immediate, ITypeMetadata meta, State state) {
        super("sltiu", rd, rs1, immediate, meta, state);
    }

    @Override
    public void execute() {
        int rs1Val = state.getRegisterValue(rs1.colloquialName);
        int immVal = imm.value();
        int result = Integer.compareUnsigned(rs1Val, immVal) < 0 ? 1 : 0;
        state.setRegisterInt(rd.colloquialName, result);
    }
}
