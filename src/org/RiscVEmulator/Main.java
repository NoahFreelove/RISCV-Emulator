package org.RiscVEmulator;

import org.RiscVEmulator.Instructions.Decoder;
import org.RiscVEmulator.Instructions.Instruction;
import org.RiscVEmulator.Registers.RegNameColloquial;

public class Main {
    public static void main(String[] args) {
        State s = new State();
        s.insertLabel("main", 0);
        Instruction inst = Decoder.decode("add t0, t1, t2", s);
        if(inst != null) {
            s.insertInstruction(inst);
            System.out.println(inst.toHex());
        }

        inst = Decoder.decode("sll t0, t0, t3", s);
        if(inst != null)
            s.insertInstruction(inst);

        s.setRegisterInt(RegNameColloquial.t3, 1);
        s.setRegisterInt(RegNameColloquial.t2, 3);
        s.setRegisterInt(RegNameColloquial.t1, 5);
        // t0 = 3 + 5 = 8, t3 = 8-5 = 3
        s.start(false);
        s.dumpRegisters();


    }
}