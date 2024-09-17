package org.RiscVEmulator.Instructions.PseudoInstruction;

import org.RiscVEmulator.Instructions.InstructionMetadata.PseudoMetadata;
import org.RiscVEmulator.State;

public class nop extends PseudoTypeInstruction {
    public nop(PseudoMetadata meta, State state) {
        super("nop", null, null, null, null, meta, state);
    }

    @Override
    public void execute() {
        return;
    }

    @Override
    public String toString() {
        return "nop";
    }

    @Override
    public String toBinary(boolean spaceSeparated) {
        // nop is an addi x0, x0, 0
        return "00000000000000000000000000010011";
    }
}
