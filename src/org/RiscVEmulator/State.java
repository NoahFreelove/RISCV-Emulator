package org.RiscVEmulator;

import org.RiscVEmulator.Instructions.Instruction;
import org.RiscVEmulator.Registers.RegNameColloquial;
import org.RiscVEmulator.Registers.Register;

import java.util.ArrayList;
import java.util.HashMap;

public class State {
    public long PC = 0;
    private HashMap<Integer, String> registers = new HashMap<>();
    private HashMap<String, Long> labels = new HashMap<>();
    private ArrayList<Instruction> instructions = new ArrayList<>();
    
    private static HashMap<Integer, String> getDefaultRegisterValues(){
        HashMap<Integer,String> out = new HashMap<>();
        for (int i = 0; i < 32; i++) {
            out.put(i, "00000000000000000000000000000000");
        }
        return out;
    }

    public int getRegisterValue(RegNameColloquial name){
        if(!registers.containsKey(Register.colloquialNameToNumber(name)))
            return -1;
        String binVal = registers.get(Register.colloquialNameToNumber(name));
        // Convert binary to decimal
        return (int) Long.parseLong(binVal, 2);
    }


    public int getRegisterValue(int name){
        if(!registers.containsKey(name))
            return -1;
        String binVal = registers.get(name);
        // Convert binary to decimal
        return (int) Long.parseLong(binVal, 2);
    }
    
    public String getRegisterHex(RegNameColloquial name){
        return getRegisterHex(Register.colloquialNameToNumber(name));
    }
    
    public String getRegisterHex(int name){
        if(!registers.containsKey(name))
            return null;
        String binVal = registers.get(name);
        int decVal = Integer.parseInt(binVal, 2);
        return Integer.toHexString(decVal);
    }

    
    public String getRegisterBinary(RegNameColloquial name){
        return getRegisterBinary(Register.colloquialNameToNumber(name));
    }
    
    public String getRegisterBinary(int name){
        if(!registers.containsKey(name))
            return null;
        String baseBinValue =registers.get(name);
        return String.format("%32s", baseBinValue.replace(' ', '0'));
    }

    public void setRegisterBinary(RegNameColloquial name, String value){
        if(name == RegNameColloquial.zero)
            return;

        // Ensure its a 32 bit number
        value = value.substring(value.length()-32);

        registers.put(Register.colloquialNameToNumber(name), value);
    }

    public void setRegisterInt(int reg, int value){
        setRegisterInt(Register.numberToColloquialName(reg), value);
    }

    public void setRegisterInt(RegNameColloquial name, int value) {
        if(name == RegNameColloquial.zero)
            return;
        // Convert decimal to binary
        String binVal = Integer.toBinaryString(value);
        // java ints are 32 bits
        binVal = String.format("%32s", binVal).replace(' ', '0');
        registers.put(Register.colloquialNameToNumber(name), String.format("%32s", binVal).replace(' ', '0'));
    }

    public void shiftPC(int shift){
        PC += shift;
    }

    public void shiftRegister(RegNameColloquial name, int shift){
        if(name == RegNameColloquial.zero)
            return;

        String binVal = registers.get(Register.colloquialNameToNumber(name));
        int decVal = Integer.parseInt(binVal, 2);
        decVal += shift;


        binVal = Integer.toBinaryString(decVal);
        registers.put(Register.colloquialNameToNumber(name), binVal);
    }

    public long getLabelAddress(String name){
        return labels.getOrDefault(name, -1L);
    }

    public Instruction getInstruction(long addr){
        if(addr%4 != 0){
            return null;
        }

        int arrLoc = (int) (addr/4);
        if(0 <= arrLoc && arrLoc<instructions.size())
            return instructions.get(arrLoc);
        return null;
    }
    
    public boolean hasNextInstruction(){
        return !(getInstruction(PC) == null);
    }
    
    // assumes there is a next instruction!
    public void nextInstruction(){
        getInstruction(PC).execute();
        PC += 4;
    }

    public State(){
        this.registers = getDefaultRegisterValues();
    }

    public void start(boolean step){
        startAt("main", step);
    }
    
    public void startAt(String labelName, boolean step){
        long addr = getLabelAddress(labelName);
        if(addr != -1){
            PC = addr;
        }
        if(!step)
            run();
    }
    public void step(){
        if(hasNextInstruction())
        {
            nextInstruction();
            return;
        }
        complete();
    }
    public void run(){
        while (hasNextInstruction())
            nextInstruction();
        complete();
    }
    
    public void complete(){
        System.out.println("complete");
    }
    
    public void dumpRegisters(){
        for (int i = 0; i < registers.size(); i++) {
            System.out.println(Register.numberToColloquialName(i) + ": " + getRegisterValue(i));
        }
    }
    
    public void insertLabel(String name, long addr){
        labels.put(name, addr);
    }

    public void insertInstruction(long addr, Instruction inst){
        if(addr%4 != 0)
        {
            System.err.println("Instruction address must be a multiple of 4");
            return;
        }
        System.out.println("Inserting instruction at address: " + addr);
        int arrLoc = (int) (addr/4);
        if (arrLoc < instructions.size())
            instructions.set(arrLoc, inst);
        else if (arrLoc == instructions.size())
            instructions.add(inst);
        else{
            while(instructions.size() < arrLoc)
                instructions.add(null);
            instructions.add(inst);
        }
    }

    public void insertInstruction(Instruction inst){
        insertInstruction(4L *(instructions.size()), inst);
    }


}
