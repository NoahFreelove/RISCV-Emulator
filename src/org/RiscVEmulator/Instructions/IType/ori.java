package org.RiscVEmulator.Instructions.IType;

import org.RiscVEmulator.Instructions.InstructionMetadata.ITypeMetadata;
import org.RiscVEmulator.Instructions.InstructionMetadata.RTypeMetadata;
import org.RiscVEmulator.Instructions.RType.RTypeInstruction;
import org.RiscVEmulator.Registers.Immediate;
import org.RiscVEmulator.Registers.Register;
import org.RiscVEmulator.State;

public class ori extends ITypeInstruction {
    public ori(Register rd, Register rs1, Immediate immediate, ITypeMetadata meta, State state) {
        super("ori", rd, rs1, immediate, meta, state);
    }

    @Override
    public void execute() {
        int rs1Val = state.getRegisterValue(rs1.colloquialName);
        int immVal = imm.value();
        int result = rs1Val | immVal;
        state.setRegisterInt(rd.colloquialName, result);
    }
}
