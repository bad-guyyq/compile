package miniplc0java.analyser;
import miniplc0java.error.TokenizeError;
import miniplc0java.tokenizer.TokenType;
import miniplc0java.util.Pos;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;
public class SymbolEntry {
    boolean isConstant=false;//常量
    boolean isInitialized=false;//是否初始化
    boolean isFunction=false;//函数嘛
    boolean isGlobal=false;//全局
    boolean isParam=false;
    //int FactionId;
    int stackOffset;//符号在栈中排位或者全局中排位
    TokenType Type;
//    LinkedList<Integer> ActScope;//该变量的作用域


    /**
     * @param isConstant
     * @param isInitialized
     * @param stackOffset
     */
    //初始化
    public SymbolEntry(boolean isFunction,boolean isConstant, boolean isInitialized, boolean isParam,int stackOffset) {
        this.isFunction=isFunction;
        this.isConstant = isConstant;
        this.isInitialized = isInitialized;
        this.isParam=isParam;
        this.stackOffset = stackOffset;
    }
    /**
     * @return the stackOffset
     */
    public void setGlobal(boolean isGlobal){
        this.isGlobal= isGlobal;
    }
    public boolean setType(String type) {
        if(type.equals("int")){
            this.Type=TokenType.UINT_LITERAL;
        }else if(type.equals("double")){
            this.Type=TokenType.DOUBLE_LITERAL;
        }else if(isFunction&&type.equals("void")){
            this.Type=TokenType.VOID_LITERAL;
        }else{
            return false;
        }
        return true;
    }
    public int getStackOffset() {
        return stackOffset;
    }
    /**
     * @return the isConstant
     */
    public boolean isConstant() {
        return isConstant;
    }
    /**
     * @return the isInitialized
     */
    public boolean isInitialized() {
        return isInitialized;
    }
    /**
     * @param isInitialized the isInitialized to set
     */
    public void setInitialized(boolean isInitialized) {
        this.isInitialized = isInitialized;
    }
    /**
     * @param stackOffset the stackOffset to set
     */
    public void setStackOffset(int stackOffset) {
        this.stackOffset = stackOffset;
    }
}
