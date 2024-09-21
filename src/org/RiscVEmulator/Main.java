package org.RiscVEmulator;

import org.RiscVEmulator.Instructions.Decoder;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("RISC-V Emulator");
        System.out.println("Noah Freelove - 2024");
        System.out.println("Supports all standard instruction types.");

        State s = new State();
        s.insertLabel("main", 0);

        s.start(true);
        Scanner scanner = new Scanner(System.in);
        do{
            try {
                s.dumpTemps();
                if(s.lastInstruction != null) {
                    String hexPC = Integer.toHexString(s.PC);
                    // prepend 0x and any 0's to make it of length 8
                    hexPC = "0x" + "0".repeat(8 - hexPC.length()) + hexPC;
                    System.out.println("Current Position: " + hexPC + " : Current Instruction: " + s.getInstruction(s.PC) +  ": Last Instruction:" + s.lastInstruction.toString());
                }
//                System.out.println("press enter to step through the program or enter any text to interpret it as a RISC-V instruction and append it to the end of this program (not PC)");
                String input = scanner.nextLine();
                if(!input.isEmpty()) {
                    if(input.equals("exit") || input.equals("quit"))
                    {
                        System.exit(0);
                        return;
                    }
                    Decoder.decode(input, s);
                }
                s.step();

            }catch (Exception ignore){}
        } while (true);


    }
}