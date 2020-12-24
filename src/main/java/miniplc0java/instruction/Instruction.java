package miniplc0java.instruction;

import java.util.Objects;

public class Instruction<T> {
    private Operation opt;
    T x;

    public Instruction(Operation opt) {
        this.opt = opt;
        this.x = null;
    }

    public Instruction(Operation opt, T x) {
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

    public T getX() {
        return x;
    }

    public void setX(T x) {
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
}
