package org.RiscVEmulator.Registers;

public class Register {
    public final RegNameColloquial colloquialName;
    public final int name;

    public static int colloquialNameToNumber(RegNameColloquial colloquialName){
        return switch (colloquialName){
            case zero -> 0;
            case ra -> 1;
            case sp -> 2;
            case gp -> 3;
            case tp -> 4;
            case t0 -> 5;
            case t1 -> 6;
            case t2 -> 7;
            case s0 -> 8;
            case fp -> 8;
            case s1 -> 9;
            case a0 -> 10;
            case a1 -> 11;
            case a2 -> 12;
            case a3 -> 13;
            case a4 -> 14;
            case a5 -> 15;
            case a6 -> 16;
            case a7 -> 17;
            case s2 -> 18;
            case s3 -> 19;
            case s4 -> 20;
            case s5 -> 21;
            case s6 -> 22;
            case s7 -> 23;
            case s8 -> 24;
            case s9 -> 25;
            case s10 -> 26;
            case s11 -> 27;
            case t3 -> 28;
            case t4 -> 29;
            case t5 -> 30;
            case t6 -> 31;
            case UNKNOWN -> -1;
        };
    }

    public static RegNameColloquial numberToColloquialName(int number){
        return switch (number){
            case 0 -> RegNameColloquial.zero;
            case 1 -> RegNameColloquial.ra;
            case 2 -> RegNameColloquial.sp;
            case 3 -> RegNameColloquial.gp;
            case 4 -> RegNameColloquial.tp;
            case 5 -> RegNameColloquial.t0;
            case 6 -> RegNameColloquial.t1;
            case 7 -> RegNameColloquial.t2;
            case 8 -> RegNameColloquial.s0;
            case 9 -> RegNameColloquial.s1;
            case 10 -> RegNameColloquial.a0;
            case 11 -> RegNameColloquial.a1;
            case 12 -> RegNameColloquial.a2;
            case 13 -> RegNameColloquial.a3;
            case 14 -> RegNameColloquial.a4;
            case 15 -> RegNameColloquial.a5;
            case 16 -> RegNameColloquial.a6;
            case 17 -> RegNameColloquial.a7;
            case 18 -> RegNameColloquial.s2;
            case 19 -> RegNameColloquial.s3;
            case 20 -> RegNameColloquial.s4;
            case 21 -> RegNameColloquial.s5;
            case 22 -> RegNameColloquial.s6;
            case 23 -> RegNameColloquial.s7;
            case 24 -> RegNameColloquial.s8;
            case 25 -> RegNameColloquial.s9;
            case 26 -> RegNameColloquial.s10;
            case 27 -> RegNameColloquial.s11;
            case 28 -> RegNameColloquial.t3;
            case 29 -> RegNameColloquial.t4;
            case 30 -> RegNameColloquial.t5;
            case 31 -> RegNameColloquial.t6;
            default -> RegNameColloquial.UNKNOWN;
        };
    }

    public Register(RegNameColloquial colloquialName) {
        this.colloquialName = colloquialName;
        this.name = colloquialNameToNumber(colloquialName);
    }

    public Register(int name) {
        this.colloquialName = numberToColloquialName(name);
        this.name = name;
    }

    public String toBinary(){
        return Integer.toBinaryString(name);
    }

}
