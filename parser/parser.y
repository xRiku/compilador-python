%{
    #include <stdio.h>
    #include <stdlib.h>
    #define YYERROR_VERBOSE 1
    extern int yylineno;
    int yylex(void);
    void yyerror(char const *s);
    int yylineno;
%}

/* Tipos */
%token NUMBER STRING 
/* Bool */
%token TRUE FALSE
/* Operadores  */
%token PLUS MINUS STAR OVER DOUBLESTAR 
/* Operadores lógicos */
%token EQEQUAL NOTEQUAL LESS LESSEQUAL GREATER GREATEREQUAL IN
/* Símbolos */
%token LPAR RPAR EQUAL LSQB RSQB COMMA LBRACE RBRACE COLON
/* Estruturas de repetição */
%token FOR WHILE
/* Outros */
%token NAME ENTER DEF PASS BREAK CONTINUE RETURN
/* Definindo prioridade */
%left EQEQUAL NOTEQUAL LESS LESSEQUAL GREATER GREATEREQUAL
%left PLUS MINUS
%left STAR OVER
%precedence UMINUS
%right DOUBLESTAR


%%
/* Garante que não para na primeira linha */
lines: lines line | line;
line: 
      expr ENTER 
    | attr ENTER
    | dtstrct ENTER
    | def ENTER 
    | func ENTER
    | for ENTER
    | while ENTER
    | continue ENTER
    | break ENTER
    | pass ENTER
    | return ENTER
    | ENTER;
/* Tipos booleanos */
bool: TRUE | FALSE;
/* Operações com números */
expr:
      expr PLUS expr
    | expr MINUS expr
    | expr STAR expr
    | expr OVER expr
    | MINUS expr %prec UMINUS
    | expr DOUBLESTAR expr
    | expr NOTEQUAL expr
    | expr EQEQUAL expr
    | expr LESS expr
    | expr LESSEQUAL expr
    | expr GREATER expr
    | expr GREATEREQUAL expr
    | LPAR expr RPAR
    | NAME
    | NUMBER ;
/* Operações de atribuição */
attr: NAME EQUAL expr | NAME EQUAL dtstrct | NAME EQUAL func;
/* Para definir uma lista, não cobre o caso de uma lista com uma variavel */
types: expr | STRING;
opt: 
      types COMMA opt 
    | types 
    | %empty ;
list: LSQB opt RSQB;
/* Para definir um dicionário */
dict: 
      LBRACE types COLON types RBRACE 
    | LBRACE RBRACE;
/* Para definir uma tupla */
tuple: 
      LPAR types COMMA opt RPAR 
    | LPAR RPAR; 
dtstrct: list | dict | tuple;
/* Definição de função */
args: NAME COMMA args | NAME;
def: 
      DEF NAME LPAR args RPAR COLON
    | DEF NAME LPAR RPAR COLON;
/* Chamada de função */
func:
      NAME LPAR args RPAR
    | NAME LPAR types RPAR
    | NAME LPAR RPAR;
/* Laços de repetição */
valid_types: list | STRING | NAME;
for: FOR NAME IN valid_types COLON;
condition: func | bool;
while:
      WHILE expr COLON
    | WHILE LPAR condition RPAR COLON
    | WHILE condition COLON;
/* Outros */
pass: PASS;
break: BREAK;
continue: CONTINUE;
return: 
      RETURN types
    | RETURN bool;
%%

void yyerror (char const *s) {
    // if_stmt: 
    //   IF exp-bool COLON lines 
    // | IF exp-bool COLON lines ELIF exp-bool COLON lines 
    // | IF exp-bool COLON lines ELIF exp-bool COLON lines ELSE lines; *
    printf("SYNTAX ERROR (%d): %s\n", yylineno, s);
    exit(EXIT_FAILURE);
}

int main(void) {
    if (yyparse() == 0) 
        printf("PARSE SUCCESSFUL!\n");
    else
        printf("PARSE FAILED!\n");
    return 0;
}