package miniplc0java.instruction;

import miniplc0java.analyser.SymbolEntry;
import miniplc0java.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

//fn [7] 0 0 -> 0
//【地址】局部 参数-》返回{}
public class Function {
    public String fucName;
    public int locSlots;
    public int paramSlots;
    public int retSlots;
    public ArrayList<TokenType> paramType;
    public LinkedList<HashMap<String, SymbolEntry>> funcSymbolStack;
    public ArrayList<Instruction> Body;
    public Function(){this.Body=new ArrayList<>();}
    public void setFunction(String fucName,int paramSlots, int retSlots){
        this.fucName=fucName;
        this.retSlots=retSlots;
        this.paramSlots=paramSlots;
        this.Body=new ArrayList<>();
        this.paramType=new ArrayList<>();
    }
    public void setlocSlots(int locSlots){
        this.locSlots=locSlots;
    }
}
