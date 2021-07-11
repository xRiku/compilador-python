%option outfile="scanner/scanner.c" 
%option noinput
%{
#include "../parser/parser.h"
#include <stdlib.h>
#include <stdio.h>
#include <assert.h>
#define MAX_DEPTH 72


int lines = 0, chars = 0;

int open_parentheses = 0 ;
unsigned int indent_stack[MAX_DEPTH] ;
unsigned int level = 0 ;

unsigned int first = 1 ;

void process_indent(char* line) ;


/* Global vars */
// ^[ ]*\n       {/* Ignore blank lines. */} 
// [ ]{4}|\t       { printf("%d: %s -> %s\n", yylineno, "\\t", "INDENT"); chars++; }
%}

%option noyywrap
%option yylineno
name   [a-zA-Z0-9_]+
number [0-9]+\.[0-9]+|[0-9]|[1-9][0-9]*
string \"[^\"]*\"|\'[^\']*\'
%%
\n              {
                    if (chars != 0){
                        printf("%d: %s -> %s\n", yylineno, "\\n", "NEWLINE");
                    } else {
                        printf("%d: %s -> %s\n", yylineno, "\\n", "NL");
                    }
                    chars = 0;
                    return ENTER;
                }
^[ ]*[^ \n]+   {
                    int last = yyleng - 1;
                    process_indent(yytext) ;
                    while ((last >= 0) && (yytext[last] != ' ')) {
                        unput(yytext[last]);
                        last-- ;
                    }
                }
[ ]             {chars++;}
{number}        { printf("%d: %s -> %s\n", yylineno, yytext, "NUMBER"); chars++; return NUMBER;}
f?{string}      { printf("%d: %s -> %s\n", yylineno, yytext, "STRING"); chars++; }
"+"             { printf("%d: %s -> %s\n", yylineno, yytext, "PLUS"); chars++; return PLUS; }
"-"             { printf("%d: %s -> %s\n", yylineno, yytext, "MINUS"); chars++; }
"**"            { printf("%d: %s -> %s\n", yylineno, yytext, "DOUBLESTAR"); chars++; }
"*"             { printf("%d: %s -> %s\n", yylineno, yytext, "STAR"); chars++; }
"/"             { printf("%d: %s -> %s\n", yylineno, yytext, "SLASH"); chars++; }
"=="            { printf("%d: %s -> %s\n", yylineno, yytext, "EQEQUAL"); chars++; }
"="             { printf("%d: %s -> %s\n", yylineno, yytext, "EQUAL"); chars++; }
"!="            { printf("%d: %s -> %s\n", yylineno, yytext, "NOTEQUAL"); chars++; }
"<"             { printf("%d: %s -> %s\n", yylineno, yytext, "LESS"); chars++; }
"<="            { printf("%d: %s -> %s\n", yylineno, yytext, "LESSEQUAL"); chars++; }
">"             { printf("%d: %s -> %s\n", yylineno, yytext, "GREATER"); chars++; }
">="            { printf("%d: %s -> %s\n", yylineno, yytext, "GREATEREQUAL"); chars++; }
"("             { return LPAR; printf("%d: %s -> %s\n", yylineno, yytext, "LPAR"); chars++; open_parentheses++; }
")"             { return RPAR; printf("%d: %s -> %s\n", yylineno, yytext, "RPAR"); chars++; open_parentheses--; }
"["             { printf("%d: %s -> %s\n", yylineno, yytext, "LSQB"); chars++; }
"]"             { printf("%d: %s -> %s\n", yylineno, yytext, "RSQB"); chars++; }
"{"             { printf("%d: %s -> %s\n", yylineno, yytext, "LBRACE"); chars++; }
"}"             { printf("%d: %s -> %s\n", yylineno, yytext, "RBRACE"); chars++; }
":"             { printf("%d: %s -> %s\n", yylineno, yytext, "COLON"); chars++; }
";"             { printf("%d: %s -> %s\n", yylineno, yytext, "SEMI"); chars++; }
","             { printf("%d: %s -> %s\n", yylineno, yytext, "COMMA"); chars++; }
for             { printf("%d: %s -> %s\n", yylineno, yytext, "FOR"); chars++; }
in              { printf("%d: %s -> %s\n", yylineno, yytext, "IN"); chars++; }
while           { printf("%d: %s -> %s\n", yylineno, yytext, "WHILE"); chars++; }
break           { printf("%d: %s -> %s\n", yylineno, yytext, "BREAK"); chars++; }
continue        { printf("%d: %s -> %s\n", yylineno, yytext, "CONTINUE"); chars++; }
def             { printf("%d: %s -> %s\n", yylineno, yytext, "DEF"); chars++; }
pass            { printf("%d: %s -> %s\n", yylineno, yytext, "PASS"); chars++; }
return          { printf("%d: %s -> %s\n", yylineno, yytext, "RETURN"); chars++; }
if              { printf("%d: %s -> %s\n", yylineno, yytext, "IF"); chars++; }
elif            { printf("%d: %s -> %s\n", yylineno, yytext, "ELIF"); chars++; }
else            { printf("%d: %s -> %s\n", yylineno, yytext, "ELSE"); chars++; }
{name}          { printf("%d: %s -> %s\n", yylineno, yytext, "NAME"); chars++; }
<<EOF>>         { process_indent(""); printf("%d: %s -> %s\n", yylineno, "EOF", "ENDMARKER"); return EOF; }

%%

unsigned int white_count(char* line) {
  unsigned int count = 0 ;
  while (*line == ' ')
    count++, line++ ;
  return count ;
}

void process_indent(char* line) {
  if (open_parentheses)
    /* Ignore indents while nested. */
    return ;

  unsigned int indent = white_count(line) ;

  if (indent == indent_stack[level]) {
    if (!first);
    first = 0 ;
    return ;
  }

  if (indent > indent_stack[level]) {
    printf("%d: %s -> %s\n", yylineno, "\\t", "INDENT") ;
    assert(level+1 < MAX_DEPTH) ;
    indent_stack[++level] = indent ;
    return ;
  }

  while (indent < indent_stack[level]) {
    --level;
    printf("%d: %s -> %s\n", yylineno, "\"\"", "DEDENT") ;
  }

  assert(level >= 0) ;
}

/* int main(int argc, char* argv[]) {
  indent_stack[0] = 0 ;
  yylex() ;
  printf("\n") ;
} */