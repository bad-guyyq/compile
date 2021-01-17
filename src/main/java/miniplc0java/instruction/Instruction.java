package miniplc0java.instruction;

import com.sun.jdi.ByteValue;

import java.util.Objects;

public class Instruction {
    private Operation opt;
    Object x;

    public Instruction(Operation opt) {
        this.opt = opt;
        this.x = null;
    }

    public Instruction(Operation opt, Object x) {
        this.opt = opt;
        this.x = x;
    }

    public Instruction() {
        this.opt = Operation.nop;
        this.x = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Instruction that = (Instruction) o;
        return opt == that.opt && Objects.equals(x, that.x);
    }

    @Override
    public int hashCode() {
        return Objects.hash(opt, x);
    }

    public Operation getOpt() {
        return opt;
    }

    public void setOpt(Operation opt) {
        this.opt = opt;
    }

    public Object getX() {
        return x;
    }

    public void setX(Object x) {
        this.x = x;
    }

    @Override
    public String toString() {
        switch (this.opt) {
            case nop:
            case pop:
            case dup:
            case load8:
            case load16:
            case load32:
            case load64:
            case store8:
            case store16:
            case store32:
            case store64:
            case alloc:
            case free:
            case addi:
            case subi:
            case muli:
            case divi:
            case addf:
            case subf:
            case mulf:
            case divf:
            case divu:
            case shl:
            case shr:
            case and:
            case or:
            case xor:
            case not:
            case cmpi:
            case cmpu:
            case cmpf:
            case negi:
            case negf:
            case itof:
            case ftoi:
            case shrl:
            case setlt:
            case setgt:
            case ret:
            case scani:
            case scanc:
            case scanf:
            case printi:
            case printc:
            case printf:
            case prints:
            case println:
                return String.format("%s", this.opt);
            case push:
            case popn:
            case loca:
            case arga:
            case globa:
            case stackalloc:
            case br:
            case brfalse:
            case brtrue:
            case call:
            case callname:
                return String.format("%s %s", this.opt, this.x);
            case panic:
            default:
                return "panic";
        }
    }

    public static byte[] intToByte4B(Object nO) {
        byte[] b = new byte[4];
        int n= Integer.parseInt(String.valueOf(nO));

        b[0] = (byte) (n >> 24 & 0xff); //数据组起始位,存放内存起始位, 即:高字节在前
        b[1] = (byte) (n >> 16 & 0xff); //高字节在前是与java存放内存一样的, 与书写顺序一样
        b[2] = (byte) (n >> 8 & 0xff);
        b[3] = (byte) (n & 0xff);
        return b;
    }
    public static byte[] intToByte8B(Object nO) {
        byte[] b = new byte[8];
        int n= Integer.parseInt(String.valueOf(nO));
        b[0] = (byte) (0x00);
        b[1] = (byte) (0x00);
        b[2] = (byte) (0x00);
        b[3] = (byte) (0x00);
        b[4] = (byte) (n >> 24 & 0xff);
        b[5] = (byte) (n >> 16 & 0xff);
        b[6] = (byte) (n >> 8 & 0xff);
        b[7] = (byte) (n & 0xff);
//        if(nO instanceof Integer){
//            int n=(Integer)nO;
//            b[0] = (byte) (0x00);
//            b[1] = (byte) (0x00);
//            b[2] = (byte) (0x00);
//            b[3] = (byte) (0x00);
//            b[4] = (byte) (n >> 24 & 0xff);
//            b[5] = (byte) (n >> 16 & 0xff);
//            b[6] = (byte) (n >> 8 & 0xff);
//            b[7] = (byte) (n & 0xff);
//        }else if(nO instanceof Double){
//            double n=(double)nO;
//            b[0] = (byte) (n >> 56 & 0xff);
//            b[1] = (byte) (n >> 48 & 0xff);
//            b[2] = (byte) (n >> 40 & 0xff);
//            b[3] = (byte) (n >> 32 & 0xff);
//            b[4] = (byte) (n >> 24 & 0xff);
//            b[5] = (byte) (n >> 16 & 0xff);
//            b[6] = (byte) (n >> 8 & 0xff);
//            b[7] = (byte) (n & 0xff);
//        }
        return b;
    }
    public static byte[] intToByte1B(Object nO) {
        byte[] b = new byte[1];
        int n= Integer.parseInt(String.valueOf(nO));
        b[0] = (byte) (n & 0xff);
        return b;
    }
    public byte[] toO0() {
        byte op;
        byte[] num=null;
        byte[] ans=null;
        switch (this.opt) {
            case nop:
                op=0x00;
                break;
            case pop:
                op=0x02;
                break;
            case dup:
                op=0x04;
                break;
            case load8:
                op=0x010;
                break;
            case load16:
                op=0x11;
                break;
            case load32:
                op=0x12;
                break;
            case load64:
                op=0x13;
                break;
            case store8:
                op=0x14;
                break;
            case store16:
                op=0x15;
                break;
            case store32:
                op=0x16;
                break;
            case store64:
                op=0x17;
                break;
            case alloc:
                op=0x18;
                break;
            case free:
                op=0x19;
                break;
            case addi:
                op=0x20;
                break;
            case subi:
                op=0x21;
                break;
            case muli:
                op=0x22;
                break;
            case divi:
                op=0x23;
                break;
            case addf:
                op=0x24;
                break;
            case subf:
                op=0x25;
                break;
            case mulf:
                op=0x26;
                break;
            case divf:
                op=0x27;
                break;
            case divu:
                op=0x28;
                break;
            case shl:
                op=0x29;
                break;
            case shr:
                op=0x2a;
                break;
            case and:
                op=0x2b;
                break;
            case or:
                op=0x2c;
                break;
            case xor:
                op=0x2d;
                break;
            case not:
                op=0x2e;
                break;
            case cmpi:
                op=0x30;
                break;
            case cmpu:
                op=0x31;
                break;
            case cmpf:
                op=0x32;
                break;
            case negi:
                op=0x34;
                break;
            case negf:
                op=0x35;
                break;
            case itof:
                op=0x36;
                break;
            case ftoi:
                op=0x37;
                break;
            case shrl:
                op=0x38;
                break;
            case setlt:
                op=0x39;
                break;
            case setgt:
                op=0x3a;
                break;
            case ret:
                op=0x49;
                break;
            case scani:
                op=0x50;
                break;
            case scanc:
                op=0x51;
                break;
            case scanf:
                op=0x52;
                break;
            case printi:
                op=0x54;
                break;
            case printc:
                op=0x55;
                break;
            case printf:
                op=0x56;
                break;
            case prints:
                op=0x57;
                break;
            case println:
                op=0x58;
                break;
            //x
            case push:
                op=0x01;
                num=intToByte8B(this.x);
                break;
            case popn:
                op=0x03;
                num=intToByte4B(this.x);
                break;
            case loca:
                op=0x0a;
                num=intToByte4B(this.x);
                break;
            case arga:
                op=0x0b;
                num=intToByte4B(this.x);
                break;
            case globa:
                op=0x0c;
                num=intToByte4B(this.x);
                break;
            case stackalloc:
                op=0x1a;
                num=intToByte4B(this.x);
                break;
            case br:
                op=0x41;
                num=intToByte4B(this.x);
                break;
            case brfalse:
                op=0x42;
                num=intToByte4B(this.x);
                break;
            case brtrue:
                op=0x43;
                num=intToByte4B(this.x);
                break;
            case call:
                op=0x48;
                num=intToByte4B(this.x);
                break;
            case callname:
                op=0x4a;
                num=intToByte4B(this.x);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + this.opt);
        }
        if(num==null){
            ans = new byte[1];
            ans[0]=op;
        }else if(num.length==4){
            ans = new byte[5];
            ans[0]=op;
            for (int i=0;i<4;i++){
                ans[1+i]=num[i];
            }
        }else if(num.length==8){
            ans = new byte[9];
            ans[0]=op;
            for (int i=0;i<8;i++){
                ans[1+i]=num[i];
            }
        }else{
            return null;
        }
        return ans;
    }
}
