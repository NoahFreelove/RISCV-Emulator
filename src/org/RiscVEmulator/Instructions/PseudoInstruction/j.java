package org.RiscVEmulator.Instructions.PseudoInstruction;

import org.RiscVEmulator.Instructions.Decoder;
import org.RiscVEmulator.Instructions.InstructionMetadata.PseudoMetadata;
import org.RiscVEmulator.Registers.RegNameColloquial;
import org.RiscVEmulator.Registers.Register;
import org.RiscVEmulator.State;

import java.util.Arrays;

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

    public static j decode(String[] tokens, State state) throws Exception{

        if(tokens.length != 1)
        {
            throw new Exception("Invalid pseudo instruction format, requires label. Got: " + Arrays.toString(tokens));
        }
        try{

            return new j(tokens[0], (PseudoMetadata) Decoder.instructionTypeIndex.get("j"), state);


        }catch (NumberFormatException e){
            throw new Exception("Invalid number format for pseudo instruction format, requires <rd>,<imm>. Got: " + Arrays.toString(tokens));
        }
    }

    @Override
    public String toString() {
        return friendlyName + " " + label;
    }
}
