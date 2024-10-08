package org.RiscVEmulator.Instructions.PseudoInstruction;

import org.RiscVEmulator.Instructions.Decoder;
import org.RiscVEmulator.Instructions.InstructionMetadata.ITypeMetadata;
import org.RiscVEmulator.Instructions.InstructionMetadata.PseudoMetadata;
import org.RiscVEmulator.Registers.Immediate;
import org.RiscVEmulator.Registers.RegNameColloquial;
import org.RiscVEmulator.Registers.Register;
import org.RiscVEmulator.State;

import java.util.Arrays;

public class li extends PseudoTypeInstruction{
    public li(Register rd, Immediate imm, PseudoMetadata meta, State state) {
        super("li", rd, null, null, imm, meta, state);
    }

    @Override
    public void execute() {
        // if label is empty, then imm is the immediate value
        int rs1Val = state.getRegisterValue(RegNameColloquial.zero);
        int immVal = imm.value();
        int result = rs1Val + immVal;
        state.setRegisterInt(rd.colloquialName, result);
        /*
        * // if label is not empty, then imm is 0 and label is the label to load
            int labelAddress = state.getRelativeLabelAddress(label);
            state.setRegisterInt(rd.name, labelAddress);*/
    }

    public static li decode(String[] tokens, State state) throws Exception{
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
        try {
            int imm = 0;
            // if starts with 0x or 0b, then parse as hex or binary
            if (tokens[1].startsWith("0x")){
                imm = Integer.parseInt(tokens[1].substring(2), 16);
            }
            else if (tokens[1].startsWith("0b")){
                imm = Integer.parseInt(tokens[1].substring(2), 2);
            }
            else{
                imm = Integer.parseInt(tokens[1]);
            }
            return new li(rd, new Immediate(imm, 12), (PseudoMetadata) Decoder.instructionTypeIndex.get("li"), state);

        }catch (NumberFormatException e){
            throw new Exception("Invalid number format for pseudo instruction format, requires <rd>,<imm>. Got: " + Arrays.toString(tokens));
        }
    }

    @Override
    public String toString() {
        return friendlyName + " " + rd.colloquialName + ", " + imm.value();
    }
}
