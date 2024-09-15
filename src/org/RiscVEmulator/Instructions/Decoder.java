package org.RiscVEmulator.Instructions;

import org.RiscVEmulator.Instructions.InstructionMetadata.InstructionMetadata;
import org.RiscVEmulator.Instructions.InstructionMetadata.RTypeMetadata;
import org.RiscVEmulator.Instructions.InstructionMetadata.ITypeMetadata;
import org.RiscVEmulator.Instructions.RType.*;
import org.RiscVEmulator.Registers.RegNameColloquial;
import org.RiscVEmulator.Registers.Register;
import org.RiscVEmulator.State;

import java.util.Arrays;
import java.util.HashMap;

public class Decoder {
    private static final HashMap<String, InstructionMetadata> instructionTypeIndex = new HashMap<>();
    static
    {
        instructionTypeIndex.put("add", new RTypeMetadata(0x0,0x00));
        instructionTypeIndex.put("sub", new RTypeMetadata(0x0,0x20));
        instructionTypeIndex.put("xor", new RTypeMetadata(0x4,0x00));
        instructionTypeIndex.put("or", new RTypeMetadata(0x6,0x00));
        instructionTypeIndex.put("and", new RTypeMetadata(0x7,0x00));
        instructionTypeIndex.put("sll", new RTypeMetadata(0x1,0x00));
        instructionTypeIndex.put("srl", new RTypeMetadata(0x5,0x00));
        instructionTypeIndex.put("sra", new RTypeMetadata(0x5,0x20));
        instructionTypeIndex.put("slt", new RTypeMetadata(0x2,0x00));
        instructionTypeIndex.put("sltu", new RTypeMetadata(0x3,0x00));

        instructionTypeIndex.put("addi", new ITypeMetadata(0x0));
        instructionTypeIndex.put("xori", new ITypeMetadata(0x4));
        instructionTypeIndex.put("ori", new ITypeMetadata(0x6));
        instructionTypeIndex.put("andi", new ITypeMetadata(0x7));
        instructionTypeIndex.put("slti", new ITypeMetadata(0x2));
        instructionTypeIndex.put("sltiu", new ITypeMetadata(0x3));
        instructionTypeIndex.put("srai", new ITypeMetadata(0x5, 0x20));
        instructionTypeIndex.put("slli", new ITypeMetadata(0x1, 0x00));
        instructionTypeIndex.put("srli", new ITypeMetadata(0x5, 0x00));

    }
    public static Instruction decode(String input, State s){
        input = input.stripLeading().stripTrailing();

        int firstSpace = input.indexOf(" ");
        String[] parts = new String[2];
        if(firstSpace == -1){
            parts[0] = input;
            parts[1] = "";
        }
        else{
            parts[0] = input.substring(0, firstSpace);
            parts[1] = input.substring(firstSpace+1);
        }
        System.out.println(Arrays.toString(parts));
        if(parts.length != 2){
            System.err.println("Invalid instruction format, requires <inst> <space> <rest>");
            return null;
        }
        InstructionMetadata meta = instructionTypeIndex.get(parts[0]);

        if(meta == null){
            System.err.println("Unknown instruction: " + parts[0]);
            return null;
        }
        if (meta instanceof RTypeMetadata data){
            return decodeRType(data, parts[0], parts[1], s);
        }
        return null;
    }

    private static Instruction decodeRType(RTypeMetadata meta, String instName, String input, State state){
        String[] split = input.split(",");
        if(split.length != 3)
        {
            System.err.println("Invalid R-Type instruction format, requires <rd>,<rs1>,<rs2>. Got: " + input);
            return null;
        }
        for (int i = 0; i < split.length; i++) {
            split[i] = split[i].stripLeading().stripTrailing();
            split[i] = split[i].toLowerCase();
            split[i] = split[i].replace(" ", "");
        }
        try {
            int regInt = -1;
            if (split[0].startsWith("x")){
                regInt = Integer.parseInt(split[0].substring(1));
            }
            else{
                RegNameColloquial regName = RegNameColloquial.valueOf(split[0]);
                regInt = Register.colloquialNameToNumber(regName);
            }

            Register rd = new Register(regInt);

            if (split[1].startsWith("x")){
                regInt = Integer.parseInt(split[1].substring(1));
            }
            else{
                RegNameColloquial regName = RegNameColloquial.valueOf(split[1]);
                regInt = Register.colloquialNameToNumber(regName);
            }

            Register rs1 = new Register(regInt);

            if (split[2].startsWith("x")){
                regInt = Integer.parseInt(split[2].substring(1));
            }
            else{
                RegNameColloquial regName = RegNameColloquial.valueOf(split[2]);
                regInt = Register.colloquialNameToNumber(regName);
            }

            Register rs2 = new Register(regInt);

            return switch (instName){
                case "add" -> new add(rd, rs1, rs2, meta, state);
                case "sub" -> new sub(rd, rs1, rs2, meta, state);
                case "xor" -> new xor(rd, rs1, rs2, meta, state);
                case "or" -> new or(rd, rs1, rs2, meta, state);
                case "and" -> new and(rd, rs1, rs2, meta, state);
                case "sll" -> new sll(rd, rs1, rs2, meta, state);
                case "srl" -> new srl(rd, rs1, rs2, meta, state);
                case "sra" -> new sra(rd, rs1, rs2, meta, state);
                case "slt" -> new slt(rd, rs1, rs2, meta, state);
                default -> throw new IllegalStateException("Unexpected value: " + instName);
            };
        }
        catch (Exception e){
            System.err.println("Error when parsing instruction: " + e.getMessage());
            return null;
        }
    }
}
