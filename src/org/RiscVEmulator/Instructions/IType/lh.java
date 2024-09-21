package org.RiscVEmulator.Instructions.IType;

import org.RiscVEmulator.Instructions.InstructionMetadata.ITypeMetadata;
import org.RiscVEmulator.Registers.Immediate;
import org.RiscVEmulator.Registers.Register;
import org.RiscVEmulator.State;

// lw rs2, offset(rs1)
public class lh extends ITypeInstruction {
    public lh(Immediate imm, Register rs1, Register rs2, ITypeMetadata meta, State state) {
        super("lh", rs2, rs1,imm, meta, state);
    }

    @Override
    public void execute() {
        int offset = imm.value();
        int address = state.getRegisterValue(rs1.colloquialName);
        int word = state.loadHalfWord(offset + address);
        state.setRegisterInt(rd.colloquialName, word);
    }
}
