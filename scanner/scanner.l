%option yylineno

number [0-9]+\.[0-9]+
%%

{number}        { printf("%d: %s -> %s\n", yylineno, yytext, "NUMBER"); }
\"[^\"]*\"      { printf("%d: %s -> %s\n", yylineno, yytext, "STRING"); }



"+"             { printf("%d: %s -> %s\n", yylineno, yytext, "PLUS"); }
"-"             { printf("%d: %s -> %s\n", yylineno, yytext, "MINUS"); }
"*"             { printf("%d: %s -> %s\n", yylineno, yytext, "STAR"); }
"**"            { printf("%d: %s -> %s\n", yylineno, yytext, "DOUBLESTAR"); }
"/"             { printf("%d: %s -> %s\n", yylineno, yytext, "SLASH"); }
";"             { printf("%d: %s -> %s\n", yylineno, yytext, "SEMI"); }
"="             { printf("%d: %s -> %s\n", yylineno, yytext, "EQUAL"); }
"=="            { printf("%d: %s -> %s\n", yylineno, yytext, "EQEQUAL"); }
"!="            { printf("%d: %s -> %s\n", yylineno, yytext, "NOTEQUAL"); }
"<"             { printf("%d: %s -> %s\n", yylineno, yytext, "LESS"); }
"<="            { printf("%d: %s -> %s\n", yylineno, yytext, "LESSEQUAL"); }
">"             { printf("%d: %s -> %s\n", yylineno, yytext, "GREATER"); }
">="            { printf("%d: %s -> %s\n", yylineno, yytext, "GREATEREQUAL"); }




"("             { printf("%d: %s -> %s\n", yylineno, yytext, "LPAR"); }
")"             { printf("%d: %s -> %s\n", yylineno, yytext, "RPAR"); }
"["             { printf("%d: %s -> %s\n", yylineno, yytext, "LSQB"); }
"]"             { printf("%d: %s -> %s\n", yylineno, yytext, "RSQB"); }
"{"             { printf("%d: %s -> %s\n", yylineno, yytext, "LBRACE"); }
"}"             { printf("%d: %s -> %s\n", yylineno, yytext, "RBRACE"); }
":"             { printf("%d: %s -> %s\n", yylineno, yytext, "COLON"); }
","             { printf("%d: %s -> %s\n", yylineno, yytext, "COMMA"); }
";"             { printf("%d: %s -> %s\n", yylineno, yytext, "SEMI"); }


<<EOF>>         { printf("%d: %s -> %s\n", yylineno, yytext, "ENDMARKER"); }


%%