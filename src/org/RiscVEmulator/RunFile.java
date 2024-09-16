package org.RiscVEmulator;

import org.RiscVEmulator.Instructions.Decoder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

public class RunFile {
    public static void main(String[] args) {
        if(args.length == 0)
        {
            System.out.println("No file provided");
            System.exit(0);
            return;
        }
        boolean step = false;

        if(args.length == 2){
            if(args[1].equals("-s"))
                step = true;
            else{
                System.out.println("Invalid flag: " + args[1]);
                System.exit(0);
                return;
            }
        }

        String fp = args[0];
        // make sure ends with.s
        if(!fp.endsWith(".s"))
        {
            System.out.println("Invalid file type, must be .s file");
            System.exit(0);
            return;
        }
        // read all lines to string arr
        String[] lines = new String[0];
        try {
            lines = Files.readAllLines(Paths.get(fp)).toArray(String[]::new);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
            return;
        }
        State s = new State();
        s.insertLabel("main", 0);
        for (String inst : lines){
            Decoder.decode(inst, s);
        }

        if(!step) {
            s.start(false);
            System.out.println("Post-Program Registers:");
            s.dumpRegisters();
        }
        else{
            Scanner scanner = new Scanner(System.in);
            s.start(true);
            System.out.println("Press enter to step through the program");

            while (s.step()){
                if(s.lastInstruction != null) {
                    String hexPC = Integer.toHexString(s.PC-4);
                    // prepend 0x and any 0's to make it of length 8
                    hexPC = "0x" + "0".repeat(8 - hexPC.length()) + hexPC;
                    System.out.println(hexPC + " : " + s.lastInstruction.toString());
                }
                s.dumpTemps();
                scanner.nextLine();

            }
        }
    }
}
