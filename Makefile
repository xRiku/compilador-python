all: bison flex gcc

bison: parser/parser.y
	bison -v -Wall --defines=parser.h -o parser.c parser/parser.y

flex: scanner/scanner.l
	flex scanner/scanner.l 

gcc: scanner.c parser.c
	gcc -Wall scanner.c parser.c -ly

test1: all
	./a.out < tests/input01.py

test2: all
	./a.out < tests/input02.py

test3: all
	./a.out < tests/input03.py

test4: all
	./a.out < tests/input04.py

test5: all
	./a.out < tests/input05.py

test6: all
	./a.out < tests/input06.py

test7: all
	./a.out < tests/input07.py

test8: all
	./a.out < tests/input08.py

testscanner: all
	./a.out < scanner/tests/input01.py

testflex: flex
	gcc scanner.c -lfl

clean:
	@rm -f *.o *.output scanner.c parser.h parser.c a.out