all: scanner compile exec

scanner: scanner.l
	flex scanner.l

compile: lex.yy.c
	gcc -o scanner lex.yy.c -lfl

tests: scanner compile 

test1: tests
	./scanner < tests/input01.py

test2: tests
	./scanner < tests/input02.py	

test3: tests
	./scanner < tests/input03.py	