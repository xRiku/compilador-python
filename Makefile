all: bison flex gcc

bison: parser/parser.y
	bison -Wall --defines=parser/parser.h -o parser/parser.c parser/parser.y

flex: scanner/scanner.l
	flex scanner/scanner.l 

gcc: scanner/scanner.c parser/parser.c
	gcc -Wall scanner/scanner.c parser/parser.c -ly

test1: all
	./a.out < tests/input01.py

clean:
	@rm -f parser/*.o scanner/*.o parser/*.output scanner/*.output scanner/scanner.c parser/parser.h parser/parser.c a.out
