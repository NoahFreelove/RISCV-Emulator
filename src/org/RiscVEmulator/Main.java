package org.RiscVEmulator;

import org.RiscVEmulator.Instructions.Decoder;
import org.RiscVEmulator.Instructions.IType.addi;
import org.RiscVEmulator.Instructions.InstructionMetadata.ITypeMetadata;
import org.RiscVEmulator.Instructions.InstructionMetadata.RTypeMetadata;
import org.RiscVEmulator.Instructions.RType.add;
import org.RiscVEmulator.Instructions.SType.sw;
import org.RiscVEmulator.Registers.Immediate;
import org.RiscVEmulator.Registers.RegNameColloquial;
import org.RiscVEmulator.Registers.Register;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("RISC-V Emulator");
        System.out.println("Noah Freelove - 2024");
        System.out.println("Only supports I and R type instructions (for now)");

        State s = new State();
        s.insertLabel("main", 0);

        s.setRegisterInt(RegNameColloquial.t0, 50);
//        s.insertInstruction(new add(new Register(RegNameColloquial.t1), new Register(RegNameColloquial.zero), new Register(RegNameColloquial.sp), (RTypeMetadata) Decoder.instructionTypeIndex.get("add"),s));
//        s.insertInstruction(new addi(new Register(RegNameColloquial.t1), new Register(RegNameColloquial.t1), new Immediate(-16, 12), (ITypeMetadata) Decoder.instructionTypeIndex.get("addi"),s));

//        s.insertInstruction(new sw(new Immediate(0,12), ));
        s.start(true);
        Scanner scanner = new Scanner(System.in);
        do{
            try {
                s.dumpRegisters();
                System.out.println("press enter to step through the program or enter any text to interpret it as a RISC-V instruction and append it to the end of this program");
                String input = scanner.nextLine();
                if(input.isEmpty())
                    continue;
                Decoder.decode(input, s);

            }catch (Exception ignore){}
        } while (s.step());


    }
}