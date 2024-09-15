package org.RiscVEmulator;

import org.RiscVEmulator.Instructions.Decoder;
import org.RiscVEmulator.Registers.RegNameColloquial;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("RISC-V Emulator");
        System.out.println("Noah Freelove - HTN 2024");
        System.out.println("Only supports I and R type instructions (for now)");

        State s = new State();
        s.insertLabel("main", 0);

        s.setRegisterInt(RegNameColloquial.t1, 1);
        s.setRegisterInt(RegNameColloquial.t2, 2);
        s.setRegisterInt(RegNameColloquial.t3, 5);


        s.start(true);
        Scanner scanner = new Scanner(System.in);
        do{
            try {
                s.dumpTemps();
                System.out.println("press enter to step through the program or enter any text to interpret it as a RISC-V instruction and append it to the end of this program");
                String input = scanner.nextLine();
                if(input.isEmpty())
                    continue;
                Decoder.decode(input, s);

            }catch (Exception ignore){}
        } while (s.step());


    }
}