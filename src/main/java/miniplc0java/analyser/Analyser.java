package miniplc0java.analyser;

import miniplc0java.error.AnalyzeError;
import miniplc0java.error.CompileError;
import miniplc0java.error.ErrorCode;
import miniplc0java.error.ExpectedTokenError;
import miniplc0java.error.TokenizeError;
import miniplc0java.instruction.Instruction;
import miniplc0java.instruction.Operation;
import miniplc0java.instruction.Function;
import miniplc0java.tokenizer.Token;
import miniplc0java.tokenizer.TokenType;
import miniplc0java.tokenizer.Tokenizer;
import miniplc0java.util.Pos;

import java.util.*;

public final class Analyser {
    Tokenizer tokenizer;
    ArrayList<Function> Func;
    ArrayList<String> GlobalSymbol;
    LinkedList<Integer> ActScope;//当前的作用域
    //ActScope.getFirst();//当前函数下标 ActScope.size()-1;当作用域层数 ActScope.get()得到标号
    /** 当前偷看的 token */
    Token peekedToken = null;
    /** 下一个变量的栈偏移 */
    int nextVariableOffset = 0;
    int nextGlobalOffset = 0;
    int nextParamOffset = 0;
    //当前正在编译的函数
    Function nowFunc;
    /** 全局符号表 */
    HashMap<String, SymbolEntry> symbolTable = new HashMap<>();
    /** 当前block符号表*/
    HashMap<String, SymbolEntry> now_block_symbolTable = new HashMap<>();

    public Analyser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
        this.Func= new ArrayList<>();
        this.GlobalSymbol = new ArrayList<>();
        this.ActScope = new LinkedList<>();
    }

    public ArrayList<Function> analyse() throws CompileError {
        analyseProgram();
        return Func;
    }
    //*带有analyse的函数都是高
    private void analyseProgram() throws CompileError {
        // program -> decl_stmt* function*
        // 设置作用域，设置_start函数，定义全局变量,
        ActScope.add(0);
        Function nowFunc=new Function();
        nowFunc.setFunction("_start",0,0,0);
        Func.add(nowFunc);

        analyseGlobal();
        analyseFunction();
        // 'end'
        expect(TokenType.EOF);
    }
    //函数分析
    private void analyseFunction() throws CompileError{
        //decl_stmt -> let_decl_stmt | const_decl_stmt
        while(true){
            if (check(TokenType.Fn)) {
                function();
            }else{
                return;
            }
        }
    }
    private void function() throws CompileError{
        //function -> 'fn' IDENT '(' function_param_list? ')' '->' ty block_stmt
        //将函数的VariableOffset，重置作用域
        if(nextIf(TokenType.Fn)!=null){
            nextVariableOffset=0;
            nextParamOffset = 0;
            int now_act=ActScope.getFirst()+1;
            ActScope.remove();
            ActScope.add(now_act);
        }else{
            return;
        }
        //new一个函数，注意要最后才加入计数完局部变量之后才加入符号表
        nowFunc=new Function();
        //在这个函数的func_symbolList中初始化第一个table
        now_block_symbolTable=new HashMap<>();
        nowFunc.func_symbolList.add(now_block_symbolTable);

        var nameToken = expect(TokenType.IDENT);
        String name = (String) nameToken.getValue();//函数名
        //函数参数
        fun_param();
        //函数返回值类型,准备放在函数变量的表中
        var typeToken = expect(TokenType.IDENT);
        String type = (String) typeToken.getValue();

        block_stmt();
        // 加入符号表,判读全局
        SymbolEntry func=addSymbol(name, true,false, true,false, nameToken.getStartPos());
        if(!func.setType(type)){
            throw new Error("TypeWrong");
        };
        //返回值
        if(func.Type==TokenType.VOID_LITERAL){
            nowFunc.setFunction(name,nextVariableOffset,nextParamOffset,0);
        }else{
            nowFunc.setFunction(name,nextVariableOffset,nextParamOffset,1);
        }

    }
    private void fun_param()throws CompileError{
        //function_param_list -> function_param (',' function_param)*
        //function_param -> 'const'? IDENT ':' ty
        expect(TokenType.L_PAREN);
        param();
        while (true) {
            // 参数名,
            var op = peek();
            //没有逗号隔开
            if (op.getTokenType() != TokenType.COMMA) {
                break;
            }
            // 运算符
            next();
            param();
        }
        expect(TokenType.R_PAREN);
    }
    private void block_stmt()throws CompileError{

    }
    //*常量参数
    private void param() throws CompileError{
        // IDENT ':' ty
        // 变量名
        boolean isConstant=false;
        if(nextIf(TokenType.Const)!=null){
            isConstant=true;
        }
        var nameToken = expect(TokenType.IDENT);

        // 加入符号表,加入时会判读全局
        String name = (String) nameToken.getValue();
        SymbolEntry Constant=addSymbol(name, false,isConstant, false, true,nameToken.getStartPos());
        // *:号
        expect(TokenType.COLON);

        //*ty 设置变量类型
        var typeToken = expect(TokenType.IDENT);
        String type = (String) typeToken.getValue();
        if(!Constant.setType(type)){
            throw new Error("TypeWrong");
        };
    }

    //*变量参数
    private void variable_param() throws CompileError{
        // const_decl_stmt -> 'const' IDENT ':' ty '=' expr ';'
        // 如果下一个 token 是 const 就继续
        if (nextIf(TokenType.Const) != null) {
            // 变量名
            var nameToken = expect(TokenType.IDENT);

            // 加入符号表,加入时会判读全局
            String name = (String) nameToken.getValue();
            SymbolEntry Constant=addSymbol(name, false,true, true, false,nameToken.getStartPos());
            // *:号
            expect(TokenType.COLON);

            //*ty 设置变量类型
            var typeToken = expect(TokenType.IDENT);
            String type = (String) typeToken.getValue();
            if(!Constant.setType(type)){
                throw new Error("TypeWrong");
            };
            // 常表达式  *这里之后改成analyseExpr
            if(Constant.isGlobal){
                addInstruction(new Instruction(Operation.globa, Constant.getStackOffset()));
            }else{
                addInstruction(new Instruction(Operation.loca, Constant.getStackOffset()));
            }
            count_expr();

            // *;分号
            expect(TokenType.SEMICOLON);

            //栈顶现在为 变量地址 表达式值
            addInstruction(new Instruction(Operation.store64));
        }
    }
    //全局分析
    private void analyseGlobal() throws CompileError{
        //decl_stmt -> let_decl_stmt | const_decl_stmt
        while(true){
            if (check(TokenType.Let)) {
                let_decl_stmt();
            }else if(check(TokenType.Const)){
                const_decl_stmt();
            }else{
                return;
            }
        }
    }
    //*变量
    private void let_decl_stmt() throws CompileError{
        //let_decl_stmt -> 'let' IDENT ':' ty ('=' expr)? ';'
        if (nextIf(TokenType.Let) != null) {
            // 变量名
            var nameToken = expect(TokenType.IDENT);
            String name = (String) nameToken.getValue();
            // *:号
            expect(TokenType.COLON);
            //*ty 设置变量类型
            var typeToken = expect(TokenType.IDENT);
            String type = (String) typeToken.getValue();

            // 变量初始化了吗
            boolean isInitialized = false;
            // 下个 token 是等于号吗？如果是的话分析初始化
            if(nextIf(TokenType.ASSIGN)!=null){
                isInitialized=true;
            }
            // 加入符号表，请填写名字和当前位置（报错用）
            //是否初始化这里计入初始值算不算初始化过了
            SymbolEntry Variable=addSymbol(name, false,isInitialized, false,false,/* 当前位置 */ nameToken.getStartPos());
            if(!Variable.setType(type)){
                throw new Error("TypeWrong");
            }
            // 分析初始化的表达式
            if(isInitialized){
                if(Variable.isGlobal){
                    addInstruction(new Instruction(Operation.globa, Variable.getStackOffset()));
                }else{
                    addInstruction(new Instruction(Operation.loca, Variable.getStackOffset()));
                }
                count_expr();
                addInstruction(new Instruction(Operation.store64));
            }
            // 分号
            expect(TokenType.SEMICOLON);
             //如果没有初始化的话在栈里推入一个初始值]不管

//            if(ActScope.getLast()==0){
//                GlobalSymbol.add(name);
//                addInstruction(new Instruction(Operation.globa, GlobalSymbol.size()-1));
//                if (!isInitialized) {//未初始化直接配地址不用赋值
//                }else{//等待改
//                    addInstruction(new Instruction(Operation.push, value));
//                }
//            }

        }
    }
    //*常量
    private void const_decl_stmt() throws CompileError{
        // const_decl_stmt -> 'const' IDENT ':' ty '=' expr ';'
        // 如果下一个 token 是 const 就继续
        if (nextIf(TokenType.Const) != null) {
            // 变量名
            var nameToken = expect(TokenType.IDENT);

            // 加入符号表,加入时会判读全局
            String name = (String) nameToken.getValue();
            SymbolEntry Constant=addSymbol(name, false,true, true, false,nameToken.getStartPos());
            // *:号
            expect(TokenType.COLON);

            //*ty 设置变量类型
            var typeToken = expect(TokenType.IDENT);
            String type = (String) typeToken.getValue();
            if(!Constant.setType(type)){
                throw new Error("TypeWrong");
            };
            // 常表达式  *这里之后改成analyseExpr
            if(Constant.isGlobal){
                addInstruction(new Instruction(Operation.globa, Constant.getStackOffset()));
            }else{
                addInstruction(new Instruction(Operation.loca, Constant.getStackOffset()));
            }
            count_expr();

            // *;分号
            expect(TokenType.SEMICOLON);

            //栈顶现在为 变量地址 表达式值
            addInstruction(new Instruction(Operation.store64));
        }
    }

    /**布尔比较式,分析表示式是否为真，为假时为0，结果放在栈顶。
    //是否为浮点数之后实现
    //    EQ,        //-> '=='
    //    NEQ,       //-> '!='
    //    LT,        //-> '<'
    //    GT,        //-> '>'
    //    LE,        //-> '<='
    //    GE,        //-> '>='*/
    private void bool_expr() throws CompileError{
        expect(TokenType.L_PAREN);
        count_expr();
        Token nameToken=next();
        TokenType bool=nameToken.getTokenType();
        count_expr();
        //if与while都是为真时不跳为真时跳，但是指令的跳转位置需要分析
        if(bool==TokenType.EQ){ //相等时为0，取反为真
            addInstruction(new Instruction(Operation.cmpi));
            addInstruction(new Instruction(Operation.not));
        }else if(bool==TokenType.NEQ){//不相等时不为0，取反为真
            addInstruction(new Instruction(Operation.cmpi));
            addInstruction(new Instruction(Operation.not));
        }else if(bool==TokenType.LT){//< 为真时得到-1
            addInstruction(new Instruction(Operation.cmpi));
            addInstruction(new Instruction(Operation.setlt));//如果 lhs < 0 则推入 1，否则 0
        }else if(bool==TokenType.NEQ){//> 为真时得到1
            addInstruction(new Instruction(Operation.cmpi));
            addInstruction(new Instruction(Operation.setgt));
        }else if(bool==TokenType.LE){//<= 为真时为-1或0，为假时为1
            addInstruction(new Instruction(Operation.cmpi));
            addInstruction(new Instruction(Operation.setgt));//>0时为1
            addInstruction(new Instruction(Operation.not));//取反
        }else if(bool==TokenType.GE){//>= 为真时为0，1，为假时为-1
            addInstruction(new Instruction(Operation.cmpi));
            addInstruction(new Instruction(Operation.setlt));//<0时为1
            addInstruction(new Instruction(Operation.not));//取反
        }else{
            throw new Error("*/error");
        }
    }

    //*表达式->项(+/-项)*
    private void count_expr() throws CompileError{
        //operator_expr -> expr binary_operator expr
        // 表达式 -> 项 (加法运算符 项)*
        // 项
        boolean isDouble=false;
        count_expr_Item(isDouble);
        while (true) {
            // 预读可能是运算符的 token
            var op = peek();
            if (op.getTokenType() != TokenType.PLUS && op.getTokenType() != TokenType.MINUS) {
                break;
            }
            // 运算符
            next();
            // 项
            count_expr_Item(isDouble);
            // 生成代码
            if (op.getTokenType() == TokenType.PLUS) {
                if(isDouble){
                    addInstruction(new Instruction(Operation.addf));
                }else{
                    addInstruction(new Instruction(Operation.addi));
                }
            } else if (op.getTokenType() == TokenType.MINUS) {
                if(isDouble){
                    addInstruction(new Instruction(Operation.subf));
                }else{
                    addInstruction(new Instruction(Operation.subi));
                }
            }else{
                throw new Error("*/error");
            }
        }
    }
    //*项->因子(*//因子)*
    private void count_expr_Item(boolean isDouble) throws CompileError{
        // 项 -> 因子 (乘法运算符 因子)*
        // 因子
        count_expr_Factor(isDouble);
        while (true) {
            // 预读可能是运算符的 token
            Token op = peek();
            if (op.getTokenType()!= TokenType.MINUS &&op.getTokenType()!= TokenType.DIV){
                break;
            }
            // 运算符
            next();
            // 因子
            count_expr_Factor(isDouble);
            // 生成代码
            if (op.getTokenType() == TokenType.MINUS) {
                if(isDouble){
                    addInstruction(new Instruction(Operation.mulf));
                }else{
                    addInstruction(new Instruction(Operation.muli));
                }
            } else if (op.getTokenType() == TokenType.DIV) {
                if(isDouble){
                    addInstruction(new Instruction(Operation.divf));
                }else{
                    addInstruction(new Instruction(Operation.divi));
                }
            }else{
                throw new Error("*/error");
            }
        }
    }
    //*因子->符号?(标识符|无符号整数|'(' 表达式 ')')
    private void count_expr_Factor(boolean isDouble) throws CompileError{
        boolean negate;
        if (nextIf(TokenType.MINUS) != null) {
            negate = true;
            // 计算结果出来后加上neg指令
            //instructions.add(new Instruction(Operation.LIT, 0));
        } else {
            nextIf(TokenType.PLUS);
            negate = false;
        }
        if (check(TokenType.IDENT)) {
            // 是标识符
            Token nameToken = next();
            // 加载标识符的值
            loadVariable(nameToken);
        } else if (check(TokenType.UINT_LITERAL)) {
            // 是整数
            var nameToken = next();
            // 加载整数值
            int value = (int)nameToken.getValue();
            addInstruction(new Instruction(Operation.push, value));
        } else if (check(TokenType.DOUBLE_LITERAL)) {
            isDouble=true;
            // 是浮点数
            var nameToken = next();
            // 加载整数值
            Double value = (Double)nameToken.getValue();
            addInstruction(new Instruction(Operation.push, value));
        } else if (check(TokenType.L_PAREN)) {// 是(表达式)
            next();
            // 调用相应的处理函数
            count_expr();
            expect(TokenType.R_PAREN);
        } else { // 都不是，摸了
            throw new ExpectedTokenError(List.of(TokenType.IDENT, TokenType.UINT_LITERAL, TokenType.L_PAREN), next());
        }
        if (negate) {
            if(isDouble){
                addInstruction(new Instruction(Operation.negf));
            }
            else{
                addInstruction(new Instruction(Operation.negi));
            }
        }
    }
    //工具方法
    /**函数里加指令*/
    private void addInstruction(Instruction temp){
        Func.get(ActScope.getFirst()).Body.add(temp);
    }
    //Function nowFunc
    /**加载变量也就是找到变量的地址然后把变量的值压到栈顶*/
    private void loadVariable(Token nameToken) throws AnalyzeError {
        String name = (String) nameToken.getValue();/* 快填 */
        var symbol = symbolTable.get(name);
        if (symbol == null) {
            // 没有这个标识符
            throw new AnalyzeError(ErrorCode.NotDeclared, /* 当前位置 */ nameToken.getStartPos());
        } else if (!symbol.isInitialized) {
            // 标识符没初始化
            throw new AnalyzeError(ErrorCode.NotInitialized, /* 当前位置 */ nameToken.getStartPos());
        }
        if(symbol.isGlobal){
            var offset = getOffset(name, nameToken.getStartPos());//全局变量的位置，定义时设置
            addInstruction(new Instruction(Operation.globa, offset));
            addInstruction(new Instruction(Operation.load64));
        }else{
            var offset = getOffset(name, nameToken.getStartPos());//局部变量在栈中的位置
            addInstruction(new Instruction(Operation.loca, offset));
            addInstruction(new Instruction(Operation.load64));
        }
    }
    /**
     * 获取下一个变量的栈偏移
     */
    private int getNextVariableOffset() {
        return this.nextVariableOffset++;
    }
    private int getNextGlobalOffset() {
        return this.nextGlobalOffset++;
    }
    private int getNextParamOffset() {
        return this.nextParamOffset++;
    }
    /** 添加变量*/
    private SymbolEntry addSymbol(String name,boolean isFunction,  boolean isConstant, boolean isInitialized,boolean ipParam, Pos curPos) throws AnalyzeError {
        //全局变量
        if(this.ActScope.getFirst()==0||isFunction){
            if (this.symbolTable.get(name) != null) {
                throw new AnalyzeError(ErrorCode.DuplicateDeclaration, curPos);
            } else {
                SymbolEntry temp;
                GlobalSymbol.add(name);
                temp=new SymbolEntry(isFunction, isConstant, isInitialized,ipParam,getNextGlobalOffset());
                temp.setGlobal(true);
                temp.setActScope(this.ActScope);
                this.symbolTable.put(name,temp);
                return temp;
            }
        }else{//非全局
            return addBlockSymbol(name,isFunction, isConstant, isInitialized,ipParam,curPos);
        }

    }
    //Boolean isFunction,boolean isConstant, boolean isInitialized, boolean isParam,int stackOffset
    /**添加一个符号，非全局，设置作用域*/
    private SymbolEntry addBlockSymbol(String name,boolean isFunction,  boolean isConstant,boolean isInitialized,boolean ipParam, Pos curPos) throws AnalyzeError {
        if (now_block_symbolTable.get(name) != null) {
            throw new AnalyzeError(ErrorCode.DuplicateDeclaration, curPos);
        } else {
            SymbolEntry temp;
            int offset;
            if(ipParam){
                offset=getNextParamOffset();
            }else{
                offset= getNextVariableOffset();
            }
            temp=new SymbolEntry(isFunction, isConstant, isInitialized,ipParam,offset);
            temp.setGlobal(false);
            temp.setActScope(this.ActScope);
            now_block_symbolTable.put(name,temp);
            return temp;
        }
    }
    /**
     * 设置符号为已赋值
     *
     * @param name   符号名称
     * @param curPos 当前位置（报错用）
     * @throws AnalyzeError 如果未定义则抛异常
     */
    private void initializeSymbol(String name, Pos curPos) throws AnalyzeError {
        var entry = this.symbolTable.get(name);
        if (entry == null) {
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        } else {
            entry.setInitialized(true);
        }
    }
    /**
     * 获取变量在栈上的偏移
     *
     * @param name   符号名
     * @param curPos 当前位置（报错用）
     * @return 栈偏移
     * @throws AnalyzeError
     */
    private int getOffset(String name, Pos curPos) throws AnalyzeError {
        var entry = this.symbolTable.get(name);
        if (entry == null) {
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        } else {
            return entry.getStackOffset();
        }
    }
    /**
     * 获取变量是否是常量
     *
     * @param name   符号名
     * @param curPos 当前位置（报错用）
     * @return 是否为常量
     * @throws AnalyzeError
     */
    private boolean isConstant(String name, Pos curPos) throws AnalyzeError {
        var entry = this.symbolTable.get(name);
        if (entry == null) {
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        } else {
            return entry.isConstant();
        }
    }
    /**
     * 查看下一个 Token
     *
     * @return
     * @throws TokenizeError
     */
    private Token peek() throws TokenizeError {
        if (peekedToken == null) {
            peekedToken = tokenizer.nextToken();
        }
        return peekedToken;
    }
    /**
     * 获取下一个 Token
     *
     * @return
     * @throws TokenizeError
     */
    private Token next() throws TokenizeError {
        if (peekedToken != null) {
            var token = peekedToken;
            peekedToken = null;
            return token;
        } else {
            return tokenizer.nextToken();
        }
    }
    /**
     * 如果下一个 token 的类型是 tt，则返回 true
     *
     * @param tt
     * @return
     * @throws TokenizeError
     */
    private boolean check(TokenType tt) throws TokenizeError {
        var token = peek();
        return token.getTokenType() == tt;
    }
    /**
     * 如果下一个 token 的类型是 tt，则前进一个 token 并返回这个 token
     *
     * @param tt 类型
     * @return 如果匹配则返回这个 token，否则返回 null
     * @throws TokenizeError
     */
    private Token nextIf(TokenType tt) throws TokenizeError {
        var token = peek();
        if (token.getTokenType() == tt) {
            return next();
        } else {
            return null;
        }
    }
    /**
     * 如果下一个 token 的类型是 tt，则前进一个 token 并返回，否则抛出异常
     *
     * @param tt 类型
     * @return 这个 token
     * @throws CompileError 如果类型不匹配
     */
    private Token expect(TokenType tt) throws CompileError {
        var token = peek();
        if (token.getTokenType() == tt) {
            return next();
        } else {
            throw new ExpectedTokenError(tt, token);
        }
    }

}
