package org.RiscVEmulator.Instructions;

import org.RiscVEmulator.Instructions.InstructionMetadata.*;
import org.RiscVEmulator.Instructions.JType.jal;
import org.RiscVEmulator.Instructions.RType.*;
import org.RiscVEmulator.Instructions.IType.*;
import org.RiscVEmulator.Instructions.SType.*;
import org.RiscVEmulator.Instructions.PseudoInstruction.*;
import org.RiscVEmulator.Registers.Immediate;
import org.RiscVEmulator.Registers.RegNameColloquial;
import org.RiscVEmulator.Registers.Register;
import org.RiscVEmulator.State;

import java.util.Arrays;
import java.util.HashMap;

public class Decoder {
    public static final HashMap<String, InstructionMetadata> instructionTypeIndex = new HashMap<>();
    static
    {
        // R Type
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

        // I Type
        instructionTypeIndex.put("addi", new ITypeMetadata(0x0));
        instructionTypeIndex.put("xori", new ITypeMetadata(0x4));
        instructionTypeIndex.put("ori", new ITypeMetadata(0x6));
        instructionTypeIndex.put("andi", new ITypeMetadata(0x7));
        instructionTypeIndex.put("slti", new ITypeMetadata(0x2));
        instructionTypeIndex.put("sltiu", new ITypeMetadata(0x3));
        instructionTypeIndex.put("srai", new ITypeMetadata(0x5, 0x20));
        instructionTypeIndex.put("slli", new ITypeMetadata(0x1, 0x00));
        instructionTypeIndex.put("srli", new ITypeMetadata(0x5, 0x00));
        instructionTypeIndex.put("jalr", new ITypeMetadata(0b1100111,0x0, 0x0));

        instructionTypeIndex.put("lb", new ITypeMetadata(0b0000011,0x0, 0x0));
        instructionTypeIndex.put("lh", new ITypeMetadata(0b0000011,0x1, 0x0));
        instructionTypeIndex.put("lw", new ITypeMetadata(0b0000011,0x2, 0x0));
        instructionTypeIndex.put("lbu", new ITypeMetadata(0b0000011,0x4, 0x0));
        instructionTypeIndex.put("lhu", new ITypeMetadata(0b0000011,0x5, 0x0));



        // S Type
        instructionTypeIndex.put("sw", new STypeMetadata(0x2));
        instructionTypeIndex.put("sh", new STypeMetadata(0x1));
        instructionTypeIndex.put("sb", new STypeMetadata(0x0));

        // J Type
        instructionTypeIndex.put("jal", new JTypeMetadata());

        // pseudo instructions (these dont really have a metadata, so we just use their equivalent real metadata)
        instructionTypeIndex.put("li", new PseudoMetadata(0b0010011, 0x0, 0x0));
        instructionTypeIndex.put("la", new PseudoMetadata(0b0000000, 0x0, 0x0));
        instructionTypeIndex.put("nop", new PseudoMetadata(0b0010011, 0x7, 0x0));
        instructionTypeIndex.put("mv", new PseudoMetadata(0b0010011, 0x0, 0x0));
        instructionTypeIndex.put("j", new PseudoMetadata(0b11001111));

    }


    public static Instruction decode(String input, State s){
        if(input.isEmpty())
            return null;

        if (checkLabel(input, s)){
            return null;
        }

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

        InstructionMetadata meta = instructionTypeIndex.get(parts[0]);
        if (meta == null){
            System.err.println("Unknown instruction: " + Arrays.toString(parts));
            return null;
        }
        switch (meta) {
            case RTypeMetadata data -> {
                Instruction res = decodeRType(data, parts[0], parts[1], s);
                s.insertInstruction(res);
                return res;
            }
            case ITypeMetadata data -> {
                if(parts[0].equals("lw") || parts[0].equals("lh") || parts[0].equals("lb") || parts[0].equals("lhu") || parts[0].equals("lbu"))
                {
                    Instruction res = decodeLType(data, parts[0], parts[1], s);
                    s.insertInstruction(res);
                    return res;
                }
                Instruction res = decodeIType(data, parts[0], parts[1], s);
                s.insertInstruction(res);
                return res;
            }
            case STypeMetadata data -> {
                Instruction res = decodeSType(data, parts[0], parts[1], s);
                s.insertInstruction(res);
                return res;
            }

            case JTypeMetadata data -> {
                Instruction res = decodeJType(data, parts[0], parts[1], s);
                s.insertInstruction(res);
                return res;
            }
            case PseudoMetadata data -> {
                Instruction res = decodePseudo(data, parts[0], parts[1], s);
                s.insertInstruction(res);
                return res;
            }
            default -> {
                System.err.println("Unknown instruction: " + parts[0]);
            }
        }

        return null;
    }

    private static boolean checkLabel(String input, State state) {

        // check if this is a label using regex
        if (input.matches("^[a-zA-Z0-9_]+:$")){
            // add label to state

            String labelName = input.substring(0, input.length()-1);
            state.insertLabel(labelName, state.getInstructionCount() * 4); // each instruction is 4 bytes

            return true;
        }

        return false;
    }

    private static Instruction decodePseudo(PseudoMetadata data, String instName, String input, State s) {
        String[] split = input.split(",");
        for (int i = 0; i < split.length; i++) {
            split[i] = split[i].stripLeading().stripTrailing();
            split[i] = split[i].toLowerCase();
            split[i] = split[i].replace(" ", "");
        }

        switch (instName){
            case "li" -> {
                try {
                    return li.decode(split, s);
                }
                catch (Exception e){
                    System.err.println("Error when parsing <li> instruction: " + e.getMessage());
                    return null;
                }
            }
            case "la" -> {
                try {
                    return la.decode(split, s);
                }
                catch (Exception e){
                    System.err.println("Error when parsing <la> instruction: " + e.getMessage());
                    return null;
                }
            }
            case "mv" -> {
                try {
                    return mv.decode(split, s);
                }
                catch (Exception e){
                    System.err.println("Error when parsing <mv> instruction: " + e.getMessage());
                    return null;
                }
            }
            case "nop" ->{
                return new nop(data, s);
            }
            case "j" ->{
                if(split.length == 1)
                    return new j(split[0], data, s);
                System.err.println("Too few args when parsing <j> instruction! Requires 1, got " + split.length);
            }
            default -> {
                System.err.println("Unknown pseudo instruction: " + instName);
            }
        }
        return null;
    }
    public static Register tryParseRegister(String input) throws NumberFormatException{
        int regInt = -1;
        if (input.startsWith("x")){
            regInt = Integer.parseInt(input.substring(1));
        }
        else{
            RegNameColloquial regName = RegNameColloquial.valueOf(input);
            regInt = Register.colloquialNameToNumber(regName);
        }

        return new Register(regInt);
    }

    private static Instruction decodeRType(RTypeMetadata meta, String instName, String input, State state){
        String[] split = input.split(",");
        if(split.length != 3)
        {
            System.err.println("Invalid R-Type instruction format, requires <rd>,<rs1>,<rs2>. Got: " + Arrays.toString(split));
            return null;
        }
        for (int i = 0; i < split.length; i++) {
            split[i] = split[i].stripLeading().stripTrailing();
            split[i] = split[i].toLowerCase();
            split[i] = split[i].replace(" ", "");
        }
        try {
            Register rd = tryParseRegister(split[0]);
            Register rs1 = tryParseRegister(split[1]);
            Register rs2 = tryParseRegister(split[2]);

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
                case "sltu" -> new sltu(rd, rs1, rs2, meta, state);
                default -> throw new IllegalStateException("Unexpected value: " + instName);
            };
        }
        catch (Exception e){
            System.err.println("Error when parsing instruction: " + e.getMessage());
            return null;
        }
    }

    private static Instruction decodeIType(ITypeMetadata meta, String instName, String input, State state){
        String[] split = input.split(",");
        if(split.length != 3)
        {
            System.err.println("Invalid I-Type instruction format, requires <rd>,<rs1>,<imm>. Got: " + Arrays.toString(split));
            return null;
        }
        for (int i = 0; i < split.length; i++) {
            split[i] = split[i].stripLeading().stripTrailing();
            split[i] = split[i].toLowerCase();
            split[i] = split[i].replace(" ", "");
        }
        try {
            Register rd = tryParseRegister(split[0]);
            Register rs1 = tryParseRegister(split[1]);

            int imm = 0;
            // if starts with 0x or 0b, then parse as hex or binary
            if (split[2].startsWith("0x")){
                imm = Integer.parseInt(split[2].substring(2), 16);
            }
            else if (split[2].startsWith("0b")){
                imm = Integer.parseInt(split[2].substring(2), 2);
            }
            else{
                imm = Integer.parseInt(split[2]);
            }
            Immediate immediate = new Immediate(imm, 12);


            return switch (instName){
                case "addi" -> new addi(rd, rs1, immediate, meta, state);
                case "xori" -> new xori(rd, rs1, immediate, meta, state);
                case "ori" -> new ori(rd, rs1, immediate, meta, state);
                case "andi" -> new andi(rd, rs1, immediate, meta, state);
                case "slli" -> new slli(rd, rs1, immediate, meta, state);
                case "srli" -> new srli(rd, rs1, immediate, meta, state);
                case "srai" -> new srai(rd, rs1, immediate, meta, state);
                case "slti" -> new slti(rd, rs1, immediate, meta, state);
                case "sltiu" -> new sltiu(rd, rs1, immediate, meta, state);
                case "jalr" -> new jalr(rd, rs1, immediate, meta, state);

                default -> throw new IllegalStateException("Unexpected value: " + instName);
            };
        }
        catch (Exception e){
            System.err.println("Error when parsing instruction: " + e.getMessage());
            return null;
        }
    }

    private static Instruction decodeJType(JTypeMetadata meta, String instName, String input, State state){
        String[] split = input.split(",");
        if(split.length != 2)
        {
            System.err.println("Invalid J-Type instruction format, requires <rd>,<imm>. Got: " + Arrays.toString(split));
            return null;
        }
        for (int i = 0; i < split.length; i++) {
            split[i] = split[i].stripLeading().stripTrailing();
            split[i] = split[i].toLowerCase();
            split[i] = split[i].replace(" ", "");
        }
        try {
            Register rd = tryParseRegister(split[0]);

            int imm = 0;
            String label = "";
            // if starts with 0x or 0b, then parse as hex or binary
            if (split[1].startsWith("0x")){
                imm = Integer.parseInt(split[1].substring(2), 16);
            }
            else if (split[1].startsWith("0b")){
                imm = Integer.parseInt(split[1].substring(2), 2);
            }
            else{
                // if not number, its a label
                try{
                    imm = Integer.parseInt(split[1]);
                }
                catch (Exception ignore){
                    label = split[1];
                }
            }
            // imm
            Immediate immediate = new Immediate(imm, 12);


            return switch (instName){
                case "jal" -> new jal(rd, immediate, label, meta, state);

                default -> throw new IllegalStateException("Unexpected value: " + instName);
            };
        }
        catch (Exception e){
            System.err.println("Error when parsing instruction: " + e.getMessage());
            return null;
        }
    }

    private static Instruction decodeLType(ITypeMetadata meta, String instName, String input, State state){
        String[] split = input.split(",");
        if(split.length != 2)
        {
            System.err.println("Invalid L-Type instruction format, requires <rs2>,<offset>(<rs1>). Got: " + Arrays.toString(split));
            return null;
        }
        for (int i = 0; i < split.length; i++) {
            split[i] = split[i].stripLeading().stripTrailing();
            split[i] = split[i].toLowerCase();
            split[i] = split[i].replace(" ", "");
        }
        try {
            Register rs2 = tryParseRegister(split[0]);


            // use regex to parse offset and dest register. form: rs2, offset(rs1)
            String[] parts = split[1].split("\\(");
            if(parts.length != 2){
                System.err.println("Invalid L-Type instruction format, requires <rs2>,<offset>(<rs1>). Got: " + Arrays.toString(parts));
                return null;
            }
            parts[1] = parts[1].substring(0, parts[1].length()-1); // remove the last character which is ')'

            Register rs1 = tryParseRegister(parts[1]);

            int imm;
            // if starts with 0x or 0b, then parse as hex or binary
            if (parts[0].startsWith("0x")){
                imm = Integer.parseInt(parts[0].substring(2), 16);
            }
            else if (parts[0].startsWith("0b")){
                imm = Integer.parseInt(parts[0].substring(2), 2);
            }
            else{
                imm = Integer.parseInt(parts[0]);
            }

            Immediate immediate = new Immediate(imm, 12);

            return switch (instName){
                case "lw" -> new lw(immediate, rs1, rs2, meta, state);
                case "lh" -> new lh(immediate, rs1, rs2, meta, state);
                case "lb" -> new lb(immediate, rs1, rs2, meta, state);
                case "lhu" -> new lhu(immediate, rs1, rs2, meta, state);
                case "lbu" -> new lbu(immediate, rs1, rs2, meta, state);
                default -> throw new IllegalStateException("Unexpected value: " + instName);
            };
        }
        catch (Exception e){
            System.err.println("Error when parsing instruction: " + e.getMessage());
            return null;
        }
    }

    private static Instruction decodeSType(STypeMetadata meta, String instName, String input, State state){
        String[] split = input.split(",");
        if(split.length != 2)
        {
            System.err.println("Invalid S-Type instruction format, requires <rs2>,<offset>(<rs1>). Got: " + Arrays.toString(split));
            return null;
        }
        for (int i = 0; i < split.length; i++) {
            split[i] = split[i].stripLeading().stripTrailing();
            split[i] = split[i].toLowerCase();
            split[i] = split[i].replace(" ", "");
        }
        try {
            Register rs2 = tryParseRegister(split[0]);


            // use regex to parse offset and dest register. form: rs2, offset(rs1)
            String[] parts = split[1].split("\\(");
            if(parts.length != 2){
                System.err.println("Invalid S-Type instruction format, requires <rs2>,<offset>(<rs1>). Got: " + Arrays.toString(parts));
                return null;
            }
            parts[1] = parts[1].substring(0, parts[1].length()-1); // remove the last character which is ')'
            
            Register rs1 = tryParseRegister(parts[1]);
            
            int imm;
            // if starts with 0x or 0b, then parse as hex or binary
            if (parts[0].startsWith("0x")){
                imm = Integer.parseInt(parts[0].substring(2), 16);
            }
            else if (parts[0].startsWith("0b")){
                imm = Integer.parseInt(parts[0].substring(2), 2);
            }
            else{
                imm = Integer.parseInt(parts[0]);
            }

            Immediate immediate = new Immediate(imm, 12);

            return switch (instName){
                case "sw" -> new sw(immediate, rs1, rs2, meta, state);
                case "sh" -> new sh(immediate, rs1, rs2, meta, state);
                case "sb" -> new sb(immediate, rs1, rs2, meta, state);
                default -> throw new IllegalStateException("Unexpected value: " + instName);
            };
        }
        catch (Exception e){
            System.err.println("Error when parsing instruction: " + e.getMessage());
            return null;
        }
    }

    // TODO: Decode binary to instruction
}
