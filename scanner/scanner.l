%{
#include<stdio.h>
int lines = 0, chars = 0;
/* Global vars */
%}

%option yylineno
name   [a-zA-Z0-9_]+
number [0-9]+\.[0-9]+
%%
\n              {
                    if (chars != 0){
                        printf("%d: %s -> %s\n", yylineno, "\\n", "NEWLINE");
                    } else {
                        printf("%d: %s -> %s\n", yylineno, "\\n", "NL");
                    }
                    chars = 0;
                }
[ ]{4}|\t       { printf("%d: %s -> %s\n", yylineno, "\\t", "INDENT"); chars++; }
[ ]             {chars++;}
{number}        { printf("%d: %s -> %s\n", yylineno, yytext, "NUMBER"); chars++; }
\"[^\"]*\"      { printf("%d: %s -> %s\n", yylineno, yytext, "STRING"); chars++; }
"+"             { printf("%d: %s -> %s\n", yylineno, yytext, "PLUS"); chars++; }
"-"             { printf("%d: %s -> %s\n", yylineno, yytext, "MINUS"); chars++; }
"*"             { printf("%d: %s -> %s\n", yylineno, yytext, "STAR"); chars++; }
"**"            { printf("%d: %s -> %s\n", yylineno, yytext, "DOUBLESTAR"); chars++; }
"/"             { printf("%d: %s -> %s\n", yylineno, yytext, "SLASH"); chars++; }
"="             { printf("%d: %s -> %s\n", yylineno, yytext, "EQUAL"); chars++; }
"=="            { printf("%d: %s -> %s\n", yylineno, yytext, "EQEQUAL"); chars++; }
"!="            { printf("%d: %s -> %s\n", yylineno, yytext, "NOTEQUAL"); chars++; }
"<"             { printf("%d: %s -> %s\n", yylineno, yytext, "LESS"); chars++; }
"<="            { printf("%d: %s -> %s\n", yylineno, yytext, "LESSEQUAL"); chars++; }
">"             { printf("%d: %s -> %s\n", yylineno, yytext, "GREATER"); chars++; }
">="            { printf("%d: %s -> %s\n", yylineno, yytext, "GREATEREQUAL"); chars++; }
"("             { printf("%d: %s -> %s\n", yylineno, yytext, "LPAR"); chars++; }
")"             { printf("%d: %s -> %s\n", yylineno, yytext, "RPAR"); chars++; }
"["             { printf("%d: %s -> %s\n", yylineno, yytext, "LSQB"); chars++; }
"]"             { printf("%d: %s -> %s\n", yylineno, yytext, "RSQB"); chars++; }
"{"             { printf("%d: %s -> %s\n", yylineno, yytext, "LBRACE"); chars++; }
"}"             { printf("%d: %s -> %s\n", yylineno, yytext, "RBRACE"); chars++; }
":"             { printf("%d: %s -> %s\n", yylineno, yytext, "COLON"); chars++; }
";"             { printf("%d: %s -> %s\n", yylineno, yytext, "SEMI"); chars++; }
","             { printf("%d: %s -> %s\n", yylineno, yytext, "COMMA"); chars++; }
{name}          { printf("%d: %s -> %s\n", yylineno, yytext, "NAME"); chars++; }
<<EOF>>         { printf("%d: %s -> %s\n", yylineno, yytext, "ENDMARKER"); return 0; }

%%