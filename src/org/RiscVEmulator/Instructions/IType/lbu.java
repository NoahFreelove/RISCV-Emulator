package org.RiscVEmulator.Instructions.IType;

import org.RiscVEmulator.Instructions.InstructionMetadata.ITypeMetadata;
import org.RiscVEmulator.Registers.Immediate;
import org.RiscVEmulator.Registers.Register;
import org.RiscVEmulator.State;

// lw rs2, offset(rs1)
public class lbu extends ITypeInstruction {
    public lbu(Immediate imm, Register rs1, Register rs2, ITypeMetadata meta, State state) {
        super("lbu", rs2, rs1,imm, meta, state);
    }

    @Override
    public void execute() {
        int offset = imm.value();
        int address = state.getRegisterValue(rs1.colloquialName);
        int word = state.loadByte(offset + address);
        // convert to unsigned, i.e. zero extend
        word = word & 0xFF;

        state.setRegisterInt(rd.colloquialName, word);
    }
}
