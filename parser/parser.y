%{
    #include <stdio.h>
    #include <stdlib.h>
    int yylex(void);
    void yyerror(char const *s);
    int yylineno;
%}
/* Tipos */
%token NUMBER STRING
/* Operadores  */
%token PLUS MINUS STAR OVER DOUBLESTAR 
/* Operadores lógicos */
%token EQEQUAL NOTEQUAL LESS LESSEQUAL GREATER GREATEREQUAL
/* Símbolos */
%token LPAR RPAR EQUAL LSQB RSQB COMMA
/* Outros */
%token NAME ENTER
/* Definindo prioridade */
%left PLUS MINUS
%left STAR OVER
%precedence UMINUS
%right DOUBLESTAR
%%
/* Garante que não para na primeira linha */
lines: lines line | line;
line: 
      expr ENTER 
    | attr ENTER;
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
attr: NAME EQUAL expr | NAME EQUAL dtstrct;
/* Para definir uma lista, não cobre o caso de uma lista com uma variavel */
types: NUMBER | STRING;
opt: types COMMA opt | types | %empty ;
dtstrct: LSQB opt RSQB;
%%

void yyerror (char const *s) {
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