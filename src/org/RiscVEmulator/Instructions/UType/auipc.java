package org.RiscVEmulator.Instructions.UType;

import org.RiscVEmulator.Instructions.InstructionMetadata.UTypeMetadata;
import org.RiscVEmulator.Registers.Immediate;
import org.RiscVEmulator.Registers.Register;
import org.RiscVEmulator.State;

public class auipc extends UTypeInstruction {

    public auipc(Register rd, Immediate imm, UTypeMetadata meta, State state) {
        super("auipc", rd, imm, meta, state);
    }

    @Override
    public void execute() {
        int result = state.PC + (imm.value() << 12);
        state.setRegisterInt(rd.name, result);
    }

    @Override
    public String toBinary(boolean spaceSeparated) {
        String binOutput = "";

        // 20 bit imm
        String immStr = imm.toBinary();
        immStr = immStr.substring(0, 20);
        binOutput += immStr;

        // ensure rd is 5 bits
        binOutput += formatToSize(Integer.toBinaryString(this.rd.name), 5);

        // ensure opcode is 7 bits
        binOutput += formatToSize(Integer.toBinaryString(metadata.opcode), 7);

        if(!spaceSeparated)
            return binOutput;
        // every 8 bits, add a space
        for (int i = 8; i < binOutput.length(); i+=9){
            binOutput = binOutput.substring(0, i) + " " + binOutput.substring(i);
        }

        return binOutput;
    }
}
