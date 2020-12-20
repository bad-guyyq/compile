package miniplc0java.tokenizer;
public enum TokenType {
    /** 空 */
    None,
    
//关键字
    Fn,
    Let,
    Const,
    As,
    While,
    If,
    Else,
    Return,
    //扩展
    Break,
    Continue,
    
//基础类型 void
    VOID_LITERAL,//空类型，暂时不确定要不要加上
    UINT_LITERAL,//int无符号整数
    STRING_LITERAL,//字符串常量
    //扩展
    DOUBLE_LITERAL,//double 浮点
    CHAR_LITERAL,//字符常量
    
    IDENT,//标识符 IDENT -> [_a-zA-Z] [_a-zA-Z0-9]*  ty->IDENT
  
    PLUS,      //-> '+'
    MINUS,     //-> '-'
    MUL,       //-> '*'
    DIV,       //-> '/'
    ASSIGN,    //-> '='
    EQ,        //-> '=='
    NEQ,       //-> '!='
    LT,        //-> '<'
    GT,        //-> '>'
    LE,        //-> '<='
    GE,        //-> '>='
    L_PAREN,   //-> '('
    R_PAREN,   //-> ')'
    L_BRACE,   //-> '{'
    R_BRACE,   //-> '}'
    ARROW,     //-> '->'
    COMMA,     //-> ','
    COLON,     //-> ':'
    SEMICOLON, //-> ';'
    /* 文件尾 */
    EOF;
    
    //暂时不改好像没用
//    @Override
//    public String toString() {
//        switch (this) {
//            case None:
//                return "NullToken";
//            case Begin:
//                return "Begin";
//            case Const:
//                return "Const";
//            case Div:
//                return "DivisionSign";
//            case EOF:
//                return "EOF";
//            case End:
//                return "End";
//            case Equal:
//                return "EqualSign";
//            case Ident:
//                return "Identifier";
//            case LParen:
//                return "LeftBracket";
//            case Minus:
//                return "MinusSign";
//            case Mult:
//                return "MultiplicationSign";
//            case Plus:
//                return "PlusSign";
//            case Print:
//                return "Print";
//            case RParen:
//                return "RightBracket";
//            case Semicolon:
//                return "Semicolon";
//            case Uint:
//                return "UnsignedInteger";
//            case Var:
//                return "Var";
//            default:
//                return "InvalidToken";
//        }
//    }
}