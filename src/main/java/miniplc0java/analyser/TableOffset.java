package miniplc0java.analyser;

import java.util.HashMap;
import java.util.LinkedList;

public class TableOffset {
    LinkedList<Integer> ActScope;//当前的作用域
    int Offset;
    public TableOffset(){
        this.ActScope=new LinkedList<>();
        this.Offset=0;
    }
}
