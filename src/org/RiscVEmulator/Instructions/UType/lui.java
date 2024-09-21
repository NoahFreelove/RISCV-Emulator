package org.RiscVEmulator.Instructions.UType;

import org.RiscVEmulator.Instructions.Instruction;
import org.RiscVEmulator.Instructions.InstructionMetadata.UTypeMetadata;
import org.RiscVEmulator.Instructions.InstructionType;
import org.RiscVEmulator.Registers.Immediate;
import org.RiscVEmulator.Registers.Register;
import org.RiscVEmulator.State;

public class lui extends UTypeInstruction {

    public lui(Register rd, Immediate imm, UTypeMetadata meta, State state) {
        super("lui", rd, imm, meta, state);
    }

    @Override
    public void execute() {
        int immVal = imm.value();
        int result = immVal << 12;
        state.setRegisterInt(rd.name, result);
    }
}
