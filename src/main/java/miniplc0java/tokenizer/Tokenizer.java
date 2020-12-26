package miniplc0java.tokenizer;

import miniplc0java.error.TokenizeError;
import miniplc0java.error.ErrorCode;
import miniplc0java.util.Pos;

import java.io.PipedOutputStream;

public class Tokenizer {

    private StringIter it;

    public Tokenizer(StringIter it) {
        this.it = it;
    }

    // 这里本来是想实现 Iterator<Token> 的，但是 Iterator 不允许抛异常，于是就这样了
    /**
     * 获取下一个 Token
     * 
     * @return
     * @throws TokenizeError 如果解析有异常则抛出
     */
    public Token nextToken() throws TokenizeError {
        it.readAll();

        // 跳过之前的所有空白字符
        skipSpaceCharacters();

        if (it.isEOF()) {
            return new Token(TokenType.EOF, "", it.currentPos(), it.currentPos());
        }

        char peek = it.peekChar();
        if (Character.isDigit(peek)) {
            return lexUint_Double();
        } else if (peek == '"') {
            it.nextChar();
            return lexString();
        } else if (peek=='\''){
            it.nextChar();
            return lexChar();
        }else if (Character.isAlphabetic(peek)||peek=='_') {
            return lexIdentOrKeyword();
        }else if(peek=='\\'){
            it.nextChar();
            if(it.peekChar()=='\\'){
                it.nextLineByAnnotation();
                return nextToken();
            }else
                throw new Error("Not implemented");
        }else {
            return lexOperatorOrUnknown();
        }
    }
//整数
    private Token lexUint_Double() throws TokenizeError {
        // 请填空：
        // 直到查看下一个字符不是数字为止:
        // -- 前进一个字符，并存储这个字符
        //
        // 解析存储的字符串为无符号整数
        // 解析成功则返回无符号整数类型的token，否则返回编译错误

        // Token 的 Value 应填写数字的值
        char peek = it.nextChar();
        Pos pre=it.previousPos();
        StringBuilder str=new StringBuilder().append(peek);
        while (!it.isEOF() && (Character.isDigit(it.peekChar()))) {
            peek=it.nextChar();
            str.append(peek);
        }
        if(it.isEOF()||it.peekChar()!='.') {
        	long  temp=Long.parseLong(str.toString());
            if(temp>=0){//不检查越界&&temp<=1844407370955
                return new Token(TokenType.UINT_LITERAL, temp, pre, it.currentPos());
            }
            throw new Error("Not implemented");
        }else {
            peek = it.nextChar();
            //Pos pre=it.previousPos();
            //StringBuilder str=new StringBuilder().append(peek);
            while (!it.isEOF() && (Character.isDigit(it.peekChar()))) {
                peek=it.nextChar();
                str.append(peek);
            }
            if(it.isEOF()||it.peekChar()!='e'||it.peekChar()!='E'){
                double  temp=Double.parseDouble(str.toString());
                return new Token(TokenType.DOUBLE_LITERAL, temp, pre, it.currentPos());
                //throw new Error("Not implemented");
            }else{
                peek = it.nextChar();
                //Pos pre=it.previousPos();
                //StringBuilder str=new StringBuilder().append(peek);
                if(it.isEOF()||!Character.isDigit(it.peekChar())){
                    throw new Error("Not implemented");
                }
                while (!it.isEOF() && (Character.isDigit(it.peekChar()))) {
                    peek=it.nextChar();
                    str.append(peek);
                }
                double  temp=Double.parseDouble(str.toString());//转换未确定可行
                return new Token(TokenType.DOUBLE_LITERAL, temp, pre, it.currentPos());
                //throw new Error("Not implemented");
            }
        }
        
    }
//关键字或变量名
    private Token lexIdentOrKeyword() throws TokenizeError {
        // 请填空：
        // 直到查看下一个字符不是数字或字母为止:
        // -- 前进一个字符，并存储这个字符
        //
        // 尝试将存储的字符串解释为关键字
        // -- 如果是关键字，则返回关键字类型的 token
        // -- 否则，返回标识符
        //
        // Token 的 Value 应填写标识符或关键字的字符串
        char peek = it.nextChar();
        Pos pre=it.previousPos();
        StringBuilder str_temp=new StringBuilder().append(peek);
        while (!it.isEOF() && (Character.isDigit(it.peekChar())||Character.isAlphabetic(it.peekChar())||it.peekChar()=='_') ){
            peek=it.nextChar();
            str_temp.append(peek);
        }
        String str= str_temp.toString();
        if(str.equals("fn")){
            return new Token(TokenType.Fn, str, pre, it.currentPos());
            //throw new Error("Not implemented");
        }else if(str.equals("let")){
            return new Token(TokenType.Let, str, pre, it.currentPos());
            //throw new Error("Not implemented");
        }else if(str.equals("const")){
            return new Token(TokenType.Const, str, pre, it.currentPos());
            //throw new Error("Not implemented");
        }else if(str.equals("as")){
            return new Token(TokenType.As, str, pre, it.currentPos());
            //throw new Error("Not implemented");
        }else if(str.equals("while")){
            return new Token(TokenType.While, str, pre, it.currentPos());
            //throw new Error("Not implemented");
        }else if(str.equals("if")){
            return new Token(TokenType.If, str, pre, it.currentPos());
            //throw new Error("Not implemented");
        }else if(str.equals("else")){
            return new Token(TokenType.Else, str, pre, it.currentPos());
            //throw new Error("Not implemented");
        }else if(str.equals("return")){
            return new Token(TokenType.Return, str, pre, it.currentPos());
            //throw new Error("Not implemented");
        }else if(str.equals("break")){
            return new Token(TokenType.Break, str, pre, it.currentPos());
            //throw new Error("Not implemented");
        }else if(str.equals("continue")){
            return new Token(TokenType.Continue, str, pre, it.currentPos());
            //throw new Error("Not implemented");
        }else{
            return new Token(TokenType.IDENT, str, pre, it.currentPos());
            //throw new Error("Not implemented");
        }
    }
//操作符
    private Token lexOperatorOrUnknown() throws TokenizeError {
        switch (it.nextChar()) {
            case '+':
                return new Token(TokenType.PLUS, '+', it.previousPos(), it.currentPos());

            case '-':
                // 填入返回语句
                if(it.peekChar()!='>')
                    return new Token(TokenType.MINUS, '-', it.previousPos(), it.currentPos());
                else{
                    Pos pre=it.previousPos();
                    it.nextChar();
                    return new Token(TokenType.ARROW, '=', pre, it.currentPos());
                }
                //throw new Error("Not implemented");

            case '*':
                // 填入返回语句
                return new Token(TokenType.MUL, '*', it.previousPos(), it.currentPos());
                //throw new Error("Not implemented");

            case '/':
                // 填入返回语句
                return new Token(TokenType.DIV, '/', it.previousPos(), it.currentPos());
                //throw new Error("Not implemented");

            case '=':
                // 填入返回语句
                if(it.peekChar()!='=')
                    return new Token(TokenType.ASSIGN, '=', it.previousPos(), it.currentPos());
                else{//==
                    Pos pre=it.previousPos();
                    it.nextChar();
                    return new Token(TokenType.EQ, '=', pre, it.currentPos());
                }
                //throw new Error("Not implemented");

            case '!':
                // 填入返回语句
                if(it.peekChar()!='=')
                    throw new Error("Not implemented");
                else{//==
                    Pos pre=it.previousPos();
                    it.nextChar();
                    return new Token(TokenType.NEQ, '=', pre, it.currentPos());
                }
                //throw new Error("Not implemented");

            case '<':
                // 填入返回语句
                if(it.peekChar()!='=')
                    return new Token(TokenType.LT, ';', it.previousPos(), it.currentPos());
                else{
                    Pos pre=it.previousPos();
                    it.nextChar();
                    return new Token(TokenType.LE, '=', pre, it.currentPos());
                }
                // 填入返回语句

            //throw new Error("Not implemented");

            case '>':
                // 填入返回语句
                if(it.peekChar()!='=')
                    return new Token(TokenType.GT, ';', it.previousPos(), it.currentPos());
                else{
                    Pos pre=it.previousPos();
                    it.nextChar();
                    return new Token(TokenType.GE, '=', pre, it.currentPos());
                }
            //throw new Error("Not implemented");

            case '(':
                // 填入返回语句
                return new Token(TokenType.L_PAREN, '(', it.previousPos(), it.currentPos());
            //throw new Error("Not implemented");

            case ')':
                // 填入返回语句
                return new Token(TokenType.R_PAREN, ')', it.previousPos(), it.currentPos());
            //throw new Error("Not implemented");

            case '{':
                // 填入返回语句
                return new Token(TokenType.L_BRACE, '{', it.previousPos(), it.currentPos());
            //throw new Error("Not implemented");

            case '}':
                // 填入返回语句
                return new Token(TokenType.R_BRACE, '}', it.previousPos(), it.currentPos());
            //throw new Error("Not implemented");

            case ',':
                // 填入返回语句
                return new Token(TokenType.COMMA, ':', it.previousPos(), it.currentPos());
            //throw new Error("Not implemented");

            case ':':
                // 填入返回语句
                return new Token(TokenType.COLON, ';', it.previousPos(), it.currentPos());
            //throw new Error("Not implemented");

            case ';':
                // 填入返回语句
                return new Token(TokenType.SEMICOLON, ';', it.previousPos(), it.currentPos());
                //throw new Error("Not implemented");

            default:
                // 不认识这个输入，摸了
                throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
        }
    }
    private Token lexString() throws TokenizeError {
        char peek = it.nextChar();
        if(peek=='"'){//空白
            throw new Error("Not implemented");
        }
        Pos pre=it.previousPos();
        StringBuilder str_temp=new StringBuilder().append(peek);
        //”\“ "\\" "\\\" "\e"
        while (!it.isEOF() && it.peekChar()!='"'){
            peek=it.nextChar();
            if(peek=='\\'){
                char temp=it.peekChar();
                if(temp=='\\'){
                    str_temp.append('\\');
                }else if(temp=='t'){
                    str_temp.append('\t');
                }else if(temp=='n'){
                    str_temp.append('\n');
                }else if(temp=='r'){
                    str_temp.append('\r');
                }else if(temp=='"'){
                    str_temp.append('\"');
                }else if(temp=='\''){
                    str_temp.append('\'');
                }else{
                    throw new Error("Not implemented");
                }
            }else {
                str_temp.append(peek);
            }
        }
        if(it.peekChar()=='"'){//不检查越界&&temp<=1844407370955]
            peek=it.nextChar();
            return new Token(TokenType.STRING_LITERAL, str_temp.toString(), pre, it.currentPos());
        }
        throw new Error("Not implemented");
    }
    private Token lexChar() throws TokenizeError {
        char peek = it.nextChar(),ans;
        if(peek=='\''){//空白
            throw new Error("Not implemented");
        }
        Pos pre=it.previousPos();
        if(peek=='\\'){
            char temp=it.peekChar();
            if(temp=='\\'){
                ans='\\';
            }else if(temp=='t'){
                ans='\t';
            }else if(temp=='n'){
                ans='\n';
            }else if(temp=='r'){
                ans='\r';
            }else if(temp=='"'){
                ans='\"';
            }else if(temp=='\''){
                ans='\'';
            }else{
                throw new Error("Not implemented");
            }
        }else {
            ans=peek;
        }
        if(it.peekChar()=='\''){//不检查越界&&temp<=1844407370955]
            peek=it.nextChar();
            return new Token(TokenType.CHAR_LITERAL, ans, pre, it.currentPos());
        }
        throw new Error("Not implemented");
    }
    private void skipSpaceCharacters() {
        while (!it.isEOF() && Character.isWhitespace(it.peekChar())) {
            it.nextChar();
        }
    }
}
