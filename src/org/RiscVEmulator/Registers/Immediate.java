package org.RiscVEmulator.Registers;

public class Immediate {
    public final int value;

    public Immediate(int value) {
        this.value = value;
    }

    public String toBinary() {
        return String.format("%032d", Integer.parseInt(Integer.toBinaryString(value)));
    }


}
