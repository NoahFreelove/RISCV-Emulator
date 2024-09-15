package org.RiscVEmulator.Registers;

public record Immediate(int value, int size) {

    public String toBinary() {
        String binStr = Integer.toBinaryString(value);
        // if binStr is longer than size_bytes, truncate it
        if (binStr.length() > size) {
            binStr = binStr.substring(binStr.length() - size);
        }
        return binStr;
    }


}
