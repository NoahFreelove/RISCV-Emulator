package org.RiscVEmulator.Data;

public record Data(int offset_bytes, int size_bytes, String name, DataType type) {
}
