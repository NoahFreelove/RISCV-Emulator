package org.RiscVEmulator.Instructions.PseudoInstruction;

import org.RiscVEmulator.Instructions.InstructionMetadata.PseudoMetadata;
import org.RiscVEmulator.State;

public class j extends PseudoTypeInstruction{
    private String label;
    public j(String label, PseudoMetadata meta, State state) {
        super("j", null, null, null, null, meta, state);
        this.label = label;
    }

    @Override
    public void execute() {
        state.PC += state.getRelativeLabelAddress(label);
        state.postJump = true;
    }

    @Override
    public String toString() {
        return friendlyName + " " + label;
    }
}
