package org.RiscVEmulator.Registers;

public record Immediate(int value, int size) {

    public Immediate(int value, int size) {
        this.size = size;
        // if value is larger than size, truncate it in its binary representation
        if (value > Math.pow(2, size) - 1) {
            this.value = (int) (value % Math.pow(2, size));
        } else {
            this.value = value;
        }
    }

    public String toBinary() {
        String binStr = Integer.toBinaryString(value);
        // if binStr is longer than size_bits, truncate it
        if (binStr.length() > size) {
            binStr = binStr.substring(binStr.length() - size);
        }
        return binStr;
    }


}
