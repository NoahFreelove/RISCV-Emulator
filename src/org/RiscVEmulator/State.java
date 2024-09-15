package org.RiscVEmulator;

import org.RiscVEmulator.Instructions.Instruction;
import org.RiscVEmulator.Registers.RegNameColloquial;
import org.RiscVEmulator.Registers.Register;

import java.util.ArrayList;
import java.util.HashMap;

public class State {
    public long PC = 0;
    private HashMap<Integer, String> registers = new HashMap<>();
    private HashMap<String, Integer> labels = new HashMap<>();
    private final ArrayList<Data> data = new ArrayList<>(); // Name, mem offset_bytes

    private ArrayList<Instruction> instructions = new ArrayList<>();
    private String memory = "";

    public int DATA_START = 0x0;
    public int DATA_MAX = 1024*8 + DATA_START; // 1kb data segment


    public int STACK_MIN = DATA_MAX;
    public int STACK_MAX = 1024*1024*8 + STACK_MIN; // 1mb stack
    public int SP(){
        return getRegisterValue(RegNameColloquial.sp);
    }

    public int loadWord(int addr_bytes){
        int addr_bits = addr_bytes*8;

        if (addr_bits < DATA_START || addr_bits >= STACK_MAX)
            return -1;

        return Integer.parseInt(memory.substring(addr_bits, addr_bits+32), 2);
    }

    public String loadWordHex(int addr_bytes){
        int out = loadWord(addr_bytes);
        if(out == -1)
            return "invalid data slice";
        return Integer.toHexString(out);
    }

    public String loadWordBinary(int addr_bytes){
        int out = loadWord(addr_bytes);
        if(out == -1)
            return "invalid data slice";
        return String.format("%32s", Integer.toBinaryString(out)).replace(' ', '0');
    }

    public int loadHalfWord(int addr_bytes){
        int addr_bits = addr_bytes*8;
        if (addr_bits < DATA_START || addr_bits >= STACK_MAX)
            return -1;

        return Integer.parseInt(memory.substring(addr_bits, addr_bits+16), 2);
    }

    public int loadByte(int addr_bytes){
        int addr_bits = addr_bytes*8;
        if (addr_bits < DATA_START || addr_bits >= STACK_MAX)
            return -1;

        return Integer.parseInt(memory.substring(addr_bits, addr_bits+8), 2);
    }

    public void storeWord(int addr_bytes, int value){
        int addr_bits = addr_bytes*8;
        if (addr_bits < DATA_START || addr_bits >= STACK_MAX)
            return;

        memory = memory.substring(0, addr_bits) + String.format("%32s", Integer.toBinaryString(value)).replace(' ', '0') + memory.substring(addr_bytes+32);
    }

    public void storeHalfWord(int addr_bytes, int value){
        int addr_bits = addr_bytes*8;
        if (addr_bits < DATA_START || addr_bits >= STACK_MAX)
            return;

        memory = memory.substring(0, addr_bits) + String.format("%16s", Integer.toBinaryString(value)).replace(' ', '0') + memory.substring(addr_bits+16);
    }

    public void storeByte(int addr_bytes, int value){
        int addr_bits = addr_bytes*8;
        if (addr_bits < DATA_START || addr_bits >= STACK_MAX)
            return;

        memory = memory.substring(0, addr_bits) + String.format("%8s", Integer.toBinaryString(value)).replace(' ', '0') + memory.substring(addr_bits+8);
    }

    public int getDataAddress(String name){
        Data res = null;
        for (Data d : data)
            if (d.name().equals(name))
                res = d;

        if (res == null)
            return -1;

        return res.offset_bytes()  + (DATA_START * 8);
    }

    public void insertWord(String name, int value){
        int offset = 0;
        if(data.isEmpty()){
            data.add(new Data(offset,4, name, DataType.WORD));
            storeWord(offset, value);
            return;
        }
        offset = data.getLast().offset_bytes();
        offset += data.getLast().size_bytes();
        data.add(new Data(offset,4, name, DataType.WORD));
        storeWord(offset, value);
    }

    public void insertHalfWord(String name, int value){
        int offset = 0;
        if(data.isEmpty()){
            data.add(new Data(offset,2, name, DataType.HWORD));
            storeHalfWord(offset, value);
            return;
        }
        offset = data.getLast().offset_bytes();
        offset += data.getLast().size_bytes();
        data.add(new Data(offset,2, name, DataType.HWORD));
        storeHalfWord(offset, value);
    }

    public void insertByte(String name, int value){
        int offset = 0;
        if(data.isEmpty()){
            data.add(new Data(offset,1, name, DataType.BYTE));
            storeByte(offset, value);
            return;
        }
        offset = data.getLast().offset_bytes();
        offset += data.getLast().size_bytes();
        data.add(new Data(offset,1, name, DataType.BYTE));
        storeByte(offset, value);
    }


    private void initMemory(){
        // Create string of all 0's of length STACK_MAX
        memory = "0".repeat(STACK_MAX);
    }

    private HashMap<Integer, String> getDefaultRegisterValues(){
        HashMap<Integer,String> out = new HashMap<>();
        for (int i = 0; i < 32; i++) {
            out.put(i, "00000000000000000000000000000000");
        }
        out.put(Register.colloquialNameToNumber(RegNameColloquial.sp), Long.toBinaryString(STACK_MAX));
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

    public void setRegisterTop20Bits(RegNameColloquial name, int value){
        if(name == RegNameColloquial.zero)
            return;
        // Convert decimal to binary
        String binVal = Integer.toBinaryString(value);

        binVal = String.format("%32s", binVal).replace(' ', '0');
        String current = registers.get(Register.colloquialNameToNumber(name));
        current = current.substring(0, 12) + binVal.substring(12);
        registers.put(Register.colloquialNameToNumber(name), current);
    }

    public void setRegisterBottom12Bits(RegNameColloquial name, int value){
        if(name == RegNameColloquial.zero)
            return;
        // Convert decimal to binary
        String binVal = Integer.toBinaryString(value);

        binVal = String.format("%32s", binVal).replace(' ', '0');
        String current = registers.get(Register.colloquialNameToNumber(name));
        current = binVal.substring(0, 20) + current.substring(20);
        registers.put(Register.colloquialNameToNumber(name), current);
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
        return labels.getOrDefault(name, -1);
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
        initMemory();
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
    public boolean step(){
        if(hasNextInstruction())
        {
            nextInstruction();
            return true;
        }
        complete();
        return false;
    }
    public void run(){
        while (hasNextInstruction())
            nextInstruction();
        complete();
    }
    
    public void complete(){
        System.out.println("Execution complete");
    }
    
    public void insertLabel(String name, int addr){
        labels.put(name, addr);
    }

    public void insertInstruction(long addr, Instruction inst){
        if(addr%4 != 0)
        {
            System.err.println("Instruction address must be a multiple of 4");
            return;
        }
//        System.out.println("Inserting instruction at address: " + addr);
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


    public void dumpRegisters(){
        for (int i = 0; i < registers.size(); i++) {
            System.out.println(Register.numberToColloquialName(i) + ": " + getRegisterValue(i));
        }
    }

    public void dumpTemps(){
        for (int i = 5; i < 8; i++) {
            System.out.println(Register.numberToColloquialName(i) + ": " + getRegisterValue(i));
        }

        for (int i = 28; i < 32; i++) {
            System.out.println(Register.numberToColloquialName(i) + ": " + getRegisterValue(i));
        }

    }

    public void dumpData(){
        for (Data d : data)
            System.out.println(d.name() + ": " + d.offset_bytes());
    }

    public void dumpDataMemory(){
        for (int i = 0; i < DATA_MAX; i+=32) {
            System.out.println(i + ": " + memory.substring(i, i+32));
        }
    }
    public void dumpStackMemory(){
        for (int i = STACK_MIN; i < STACK_MAX; i+=32) {
            System.out.println(i + ": " + loadWord(i));
        }
    }

    public void dumpMemory(){
        for (int i = 0; i < STACK_MAX; i+=32) {
            System.out.println(i + ": " + memory.substring(i, i+32));
        }
    }


}
