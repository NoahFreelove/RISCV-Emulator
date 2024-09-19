package org.RiscVEmulator;

import org.RiscVEmulator.Data.Data;
import org.RiscVEmulator.Data.DataType;
import org.RiscVEmulator.Instructions.Instruction;
import org.RiscVEmulator.Registers.RegNameColloquial;
import org.RiscVEmulator.Registers.Register;

import java.util.ArrayList;
import java.util.HashMap;

public class State {
    public int PC = 0;

    public boolean postJump = false;

    public Instruction lastInstruction = null;

    private HashMap<Integer, String> registers = new HashMap<>();
    private HashMap<String, Integer> labels = new HashMap<>();
    private final ArrayList<Data> data = new ArrayList<>(); // Name, mem offset_bytes

    private ArrayList<Instruction> instructions = new ArrayList<>();
    private String memory = "";

    public final int DATA_START = 0x0;
    public final int DATA_MAX = 1024*8 + DATA_START; // 1kb data segment


    public final int STACK_MIN = DATA_MAX;
    public final int STACK_MAX = 1024*1024*8 + STACK_MIN; // 1mb stack
    public int SP(){
        return getRegisterValue(RegNameColloquial.sp);
    }

    // region Memory
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
//        System.out.println("addr bytes, stack start, stack end: " + addr_bytes + ", " + STACK_MIN/8 + ", " + STACK_MAX/8);
        if (addr_bits < DATA_START || addr_bits >= STACK_MAX)
            return;
        String newVal = Integer.toBinaryString(value);
        newVal = String.format("%32s", newVal).replace(' ', '0');
        memory = memory.substring(0, addr_bits) + newVal + memory.substring(addr_bits+32);
    }

    public void storeHalfWord(int addr_bytes, int value){
        int addr_bits = addr_bytes*8;
        if (addr_bits < DATA_START || addr_bits >= STACK_MAX)
            return;

        // only take last 16 bits of <value>
        String newVal = Integer.toBinaryString(value);
        newVal = newVal.substring(newVal.length()-16);
        // now extend it to 32 bit by prepending 0s
        newVal = String.format("%16s", newVal).replace(' ', '0');

        memory = memory.substring(0, addr_bits) + newVal + memory.substring(addr_bits+16);
    }

    public void storeByte(int addr_bytes, int value){
        int addr_bits = addr_bytes*8;
        if (addr_bits < DATA_START || addr_bits >= STACK_MAX)
            return;

        String newVal = Integer.toBinaryString(value);
        newVal = newVal.substring(newVal.length()-8);
        // now extend it to 32 bit by prepending 0s
        newVal = String.format("%8s", newVal).replace(' ', '0');

        memory = memory.substring(0, addr_bits) + newVal + memory.substring(addr_bits+8);

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

    private boolean doesDataHaveName(String name){
        boolean[] res = new boolean[1];

        data.forEach(data -> {
            if(data.name().equals(name))
                res[0] = true;
        });
        return res[0];
    }


    public void insertWord(String name, int value){
        if(doesDataHaveName(name))
            throw new RuntimeException(new UserException("Data already contains name <" + name + ">"));
        if(labels.containsKey(name))
            throw new RuntimeException(new UserException("Label already exists with name <" + name + ">, cannot add data."));

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
        out.put(Register.colloquialNameToNumber(RegNameColloquial.sp), Long.toBinaryString(STACK_MAX/8));
        return out;
    }
// endregion


    // region Registers
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

    public void shiftRegister(RegNameColloquial name, int shift){
        if(name == RegNameColloquial.zero)
            return;

        String binVal = registers.get(Register.colloquialNameToNumber(name));
        int decVal = Integer.parseInt(binVal, 2);
        decVal += shift;


        binVal = Integer.toBinaryString(decVal);
        registers.put(Register.colloquialNameToNumber(name), binVal);
    }
    //endregion

    public void shiftPC(int shift){
        if(shift % 4 != 0)
        {
            System.err.println("tried to shift PC by non-multiple of 4");
            return;
        }
        PC += shift;
    }

    public int getLabelAddress(String name){
        return labels.getOrDefault(name, -1);
    }

    public int getRelativeLabelAddress(String name){
        int addr = getLabelAddress(name);
        if(addr == -1)
            return -1;
        return addr - PC;
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
        lastInstruction = getInstruction(PC);
        getInstruction(PC).execute();

        if(postJump){
            postJump = false;
        }
        else{
            PC += 4;
        }

        if(PC % 4 != 0)
            PC += 4 - (PC % 4);
    }

    public State(){
        this.registers = getDefaultRegisterValues();
        initMemory();
    }

    public void start(boolean step){
        startAt("main", step);
    }
    
    public void startAt(String labelName, boolean step){
        int addr = getLabelAddress(labelName);
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
        if(labels.containsKey(name))
            throw new RuntimeException(new UserException("Cannot insert two labels with the same name!"));
        if(doesDataHaveName(name))
            throw new RuntimeException(new UserException("Cannot insert a label with the same name as a data entry!"));

        labels.put(name, addr);
    }

    public int getInstructionCount(){
        return instructions.size();
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
        dumpMemory(STACK_MIN, STACK_MAX, 32);
    }

    public void dumpStackMemory(int relativeMaxBytes){
        dumpMemory(STACK_MIN, STACK_MIN + relativeMaxBytes*8, 32);
    }


    private String intToHex(int i){
        // convert to hex, prepend 0x and ensure its 8 characters long (int max in hex)
        return "0x" + String.format("%8s", Integer.toHexString(i)).replace(' ', '0');
    }

    public void rawDumpMemory(){
        System.out.println(memory);
    }

    public void dumpMemory(int start, int end, int step){
        if(step <= 0){
            System.out.println("Memory Dump: <step> must be greater than 0");
            return;
        }
        for (int i = start; (i+step <= end && i+step < STACK_MAX); i+=step) {
            if(step == 32){
                System.out.println("Word @" + (i/8) + ": " + loadWord(i/8));
            }
            else if(step == 16){
                System.out.println("Halfword @" + intToHex(i/8) + ": " + loadHalfWord(i/8));
            }
            else if(step == 8){
                System.out.println("Byte @" + intToHex(i/8) + ": " + loadByte(i/8));
            }
            else if (step == 1){
                System.out.println("Bit @" + intToHex(i/8) + " ( " + (i%8) + ") : " + loadByte(i/8));

            }
            else{
                System.out.println("Memory <" + step + " bits> @" + intToHex(i/8) + " (" + (i) + ") : " + loadByte(i/8));
            }
        }
    }


}
