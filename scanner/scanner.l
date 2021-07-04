%%

"+"             { printf("%d: %s -> %s\n", yylineno, yytext, "PLUS"); }
"-"             { printf("%d: %s -> %s\n", yylineno, yytext, "MINUS"); }
"*"             { printf("%d: %s -> %s\n", yylineno, yytext, "STAR"); }
"/"             { printf("%d: %s -> %s\n", yylineno, yytext, "SLASH"); }
";"             { printf("%d: %s -> %s\n", yylineno, yytext, "SEMI"); }
"="             { printf("%d: %s -> %s\n", yylineno, yytext, "EQUAL"); }
"=="             { printf("%d: %s -> %s\n", yylineno, yytext, "EQEQUAL"); }
"<"             { printf("%d: %s -> %s\n", yylineno, yytext, "LESS"); }
">"             { printf("%d: %s -> %s\n", yylineno, yytext, "GREATER"); }




"("             { printf("%d: %s -> %s\n", yylineno, yytext, "LPAR"); }
")"             { printf("%d: %s -> %s\n", yylineno, yytext, "RPAR"); }
"["             { printf("%d: %s -> %s\n", yylineno, yytext, "LSQB"); }
"]"             { printf("%d: %s -> %s\n", yylineno, yytext, "RSQB"); }
":"             { printf("%d: %s -> %s\n", yylineno, yytext, "COLON"); }
","             { printf("%d: %s -> %s\n", yylineno, yytext, "COMMA"); }
";"             { printf("%d: %s -> %s\n", yylineno, yytext, "SEMI"); }


%%