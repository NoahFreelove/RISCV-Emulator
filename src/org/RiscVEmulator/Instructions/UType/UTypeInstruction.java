package org.RiscVEmulator.Instructions.UType;

import org.RiscVEmulator.Instructions.Instruction;
import org.RiscVEmulator.Instructions.InstructionMetadata.STypeMetadata;
import org.RiscVEmulator.Instructions.InstructionMetadata.UTypeMetadata;
import org.RiscVEmulator.Instructions.InstructionType;
import org.RiscVEmulator.Registers.Immediate;
import org.RiscVEmulator.Registers.Register;
import org.RiscVEmulator.State;

public abstract class UTypeInstruction extends Instruction {
    protected Immediate imm;
    protected Register rd;
    // remember that rs1 is the one in the parenthesis!
    // sw rs2, offset(rs1)
    public UTypeInstruction(String instName, Register rd, Immediate imm, UTypeMetadata meta, State state) {
        super(instName, InstructionType.U_TYPE, meta, state);
        this.imm = imm;
        this.rd = rd;
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
