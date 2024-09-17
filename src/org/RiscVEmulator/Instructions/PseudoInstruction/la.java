package org.RiscVEmulator.Instructions.PseudoInstruction;

import org.RiscVEmulator.Instructions.Decoder;
import org.RiscVEmulator.Instructions.InstructionMetadata.PseudoMetadata;
import org.RiscVEmulator.Registers.Immediate;
import org.RiscVEmulator.Registers.RegNameColloquial;
import org.RiscVEmulator.Registers.Register;
import org.RiscVEmulator.State;
import org.RiscVEmulator.UserException;

import java.util.Arrays;

public class la extends PseudoTypeInstruction{
    private String labelData;
    public la(Register rd, String labelData, PseudoMetadata meta, State state) {
        super("la", rd, null, null, null, meta, state);
        this.labelData = labelData;
    }

    @Override
    public void execute() {
        int dataAddr = state.getDataAddress(labelData);
        int labelAddr = state.getLabelAddress(labelData);

        if(dataAddr == -1){
            state.setRegisterInt(rd.name, labelAddr);
        }
        else if (labelAddr == -1){
            state.setRegisterInt(rd.name, dataAddr);
        }
        else{
            throw new RuntimeException(new UserException("Cannot load address of a non-existent label or data"));
        }
    }

    public static la decode(String[] tokens, State state) throws Exception{
        if(tokens.length != 2)
        {
            throw new Exception("Invalid pseudo instruction format, requires <rd>,<imm>. Got: " + Arrays.toString(tokens));
        }

        int regInt = -1;
        if (tokens[0].startsWith("x")){
            regInt = Integer.parseInt(tokens[0].substring(1));
        }
        else{
            RegNameColloquial regName = RegNameColloquial.valueOf(tokens[0]);
            regInt = Register.colloquialNameToNumber(regName);
        }

        Register rd = new Register(regInt);

        // Try parsing as immediate

        return new la(rd, tokens[1], (PseudoMetadata) Decoder.instructionTypeIndex.get("la"), state);

    }

    @Override
    public String toString() {
        return friendlyName + " " + rd.colloquialName + ", " + labelData;
    }
}
