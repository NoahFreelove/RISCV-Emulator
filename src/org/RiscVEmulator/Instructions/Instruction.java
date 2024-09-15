package org.RiscVEmulator.Instructions;

import org.RiscVEmulator.Instructions.InstructionMetadata.InstructionMetadata;
import org.RiscVEmulator.State;


public abstract class Instruction {
    protected final InstructionMetadata metadata;
    protected final State state;

    public final String friendlyName;
    public final InstructionType type;

    public Instruction(String name, InstructionType type, InstructionMetadata meta, State state) {
        this.friendlyName = name;
        this.type = type;
        this.metadata = meta;
        this.state = state;
    }

    protected String formatToSize(String input, int size){
        while(input.length() < size){
            input = "0" + input;
        }
        if (input.length() > size){
            input = input.substring(input.length()-size);
        }

        return input;
    }

    public abstract void execute();

    public String toBinary(){
        return this.toBinary(true);
    }
    public abstract String toBinary(boolean spaceSeparated);

    public String toHex(){
        String bin = this.toBinary(false);
        String out = Long.toHexString(Long.parseLong(bin, 2));
        // prepend enough zeros so its 8 characters long
        while(out.length() < 8){
            out = "0" + out;
        }
        // prepend 0x
        out = "0x" + out;
        return out;
    }


    @Override
    public String toString() {
        return this.friendlyName;
    }
}
