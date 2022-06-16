package kc;
enum Symbol {
    NULL,
    MAIN,       /* main ok*/
    IF,         /* if ok*/
    WHILE,      /* while ok*/
	FOR,        /* for ok*/
    INPUTINT,   /* inputint ok*/
    INPUTCHAR,  /* inputchar ok*/
    OUTPUTINT,  /* outputint ok*/
    OUTPUTCHAR, /* outputchar ok*/
    OUTPUTSTR,  /* outputstr(拡張用) */
    SETSTR,     /* setstr   (拡張用) */
    ELSE,       /* else     (拡張用) */
    DO,         /* do       (拡張用) */
    SWITCH,     /* switch   (拡張用) */
    CASE,       /* case     (拡張用) */
    BREAK,      /* break ok*/
    CONTINUE,   /* continue (拡張用) */
    INT,        /* int ok*/
    CHAR,       /* char     (拡張用) */
    BOOLEAN,    /* boolean  (拡張用) */
    TRUE,       /* true     (拡張用) */
    FALSE,      /* false    (拡張用) */
    EQUAL,      /* == ok*/
    NOTEQ,      /* !=ok */
    LESS,       /* < ok*/
    GREAT,      /* > ok*/
    LESSEQ,     /* <=       (拡張用) */
    GREATEQ,    /* >=       (拡張用) */
    AND,        /* && ok*/
    OR,         /* || ok*/
    NOT,        /* ! ok*/
    ADD,        /* + ok*/
    SUB,        /* - ok*/
    MUL,        /* * ok*/
    DIV,        /* / ok*/
    MOD,        /* % ok*/
    ASSIGN,     /* = ok*/
    ASSIGNADD,  /* += ok*/
    ASSIGNSUB,  /* -= ok*/
    ASSIGNMUL,  /* *= ok*/
    ASSIGNDIV,  /* /= ok*/
    ASSIGNMOD,  /* %=       (拡張用) */
    INC,        /* ++ ok*/
    DEC,        /* -- ok*/
    SEMICOLON,  /* ; ok*/
    LPAREN,     /* ( ok*/
    RPAREN,     /* ) ok*/
    LBRACE,     /* { ok*/
    RBRACE,     /* } ok*/
    LBRACKET,   /* [ ok*/
    RBRACKET,   /* ] ok*/
    COMMA,      /* , ok*/
    INTEGER,    /* 整数 ok*/
    CHARACTER,  /* 文字 ok*/
    NAME,       /* 変数名 ok*/
    STRING,     /* 文字列   (拡張用) */
    ERR,        /* エラー */
    EOF         /* end of file */
}