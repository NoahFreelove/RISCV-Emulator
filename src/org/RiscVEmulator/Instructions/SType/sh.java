package org.RiscVEmulator.Instructions.SType;

import org.RiscVEmulator.Instructions.InstructionMetadata.STypeMetadata;
import org.RiscVEmulator.Registers.Immediate;
import org.RiscVEmulator.Registers.Register;
import org.RiscVEmulator.State;

public class sh extends STypeInstruction{
    public sh(Immediate imm, Register rs1, Register rs2, STypeMetadata meta, State state) {
        super("sh", imm, rs1, rs2, meta, state);
    }

    @Override
    public void execute() {
        int offset = imm.value();
        int val = state.getRegisterValue(rs2.colloquialName);
        int address = state.getRegisterValue(rs1.colloquialName);
        address += offset;
        state.storeHalfWord(address, val);
    }
}
