%{
    #include <stdio.h>
    #include <stdlib.h>
    int yylex(void);
    void yyerror(char const *s);
    int yylineno;
%}
%token NUMBER LPAR RPAR PLUS ENTER
%left PLUS
%%
line: expr ENTER ;
expr: expr PLUS expr | NUMBER;
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