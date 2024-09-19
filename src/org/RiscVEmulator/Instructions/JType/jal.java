package org.RiscVEmulator.Instructions.JType;

import org.RiscVEmulator.Instructions.InstructionMetadata.ITypeMetadata;
import org.RiscVEmulator.Instructions.InstructionMetadata.JTypeMetadata;
import org.RiscVEmulator.Registers.Immediate;
import org.RiscVEmulator.Registers.Register;
import org.RiscVEmulator.State;

public class jal extends JTypeInstruction {
    public jal(Register rd, Immediate immediate, String label, JTypeMetadata meta, State state) {
        super("jal", rd, immediate, label, meta, state);
    }

    @Override
    public void execute() {
        int addr = imm.value();

        if(!label.isEmpty()){
            addr = state.getRelativeLabelAddress(label);
        }

        state.setRegisterInt(rd.colloquialName, state.PC+4);

        state.PC += addr;
        state.postJump = true;
    }
}
