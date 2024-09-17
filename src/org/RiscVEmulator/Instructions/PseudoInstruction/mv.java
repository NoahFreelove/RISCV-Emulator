package org.RiscVEmulator.Instructions.PseudoInstruction;

import org.RiscVEmulator.Instructions.Decoder;
import org.RiscVEmulator.Instructions.InstructionMetadata.PseudoMetadata;
import org.RiscVEmulator.Registers.RegNameColloquial;
import org.RiscVEmulator.Registers.Register;
import org.RiscVEmulator.State;

import java.util.Arrays;

public class mv extends PseudoTypeInstruction{
    public mv(Register rd, Register rs1, PseudoMetadata meta, State state) {
        super("mv", rd, rs1, null, null, meta, state);
    }

    @Override
    public void execute() {
        int r2Val = state.getRegisterValue(RegNameColloquial.zero);
        int rs1val = state.getRegisterValue(rs1.colloquialName);
        int result = r2Val + rs1val;
        state.setRegisterInt(rd.colloquialName, result);
    }

    public static mv decode(String[] tokens, State state) throws Exception{

        if(tokens.length != 2)
        {
            throw new Exception("Invalid pseudo instruction format, requires <rd>,<imm>. Got: " + Arrays.toString(tokens));
        }
        try{
            Register rd = Decoder.tryParseRegister(tokens[0]);
            Register rs1 = Decoder.tryParseRegister(tokens[1]);

            return new mv(rd, rs1, (PseudoMetadata) Decoder.instructionTypeIndex.get("mv"), state);


        }catch (NumberFormatException e){
            throw new Exception("Invalid number format for pseudo instruction format, requires <rd>,<imm>. Got: " + Arrays.toString(tokens));
        }
    }

    @Override
    public String toString() {
        return friendlyName + " " + rd.colloquialName + ", " + imm.value();
    }
}
