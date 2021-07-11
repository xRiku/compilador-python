all: scanner compile exec

scanner: scanner.l
	flex scanner.l

compile: scanner.c
	gcc -o scanner scanner.c -lfl

tests: scanner compile 

test1: tests
	./scanner < tests/input01.py

test2: tests
	./scanner < tests/input02.py	

test3: tests
	./scanner < tests/input03.py	