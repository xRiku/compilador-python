# Modifique as variaveis conforme o seu setup.

JAVA=java
JAVAC=javac

# Eu uso ROOT como o diretório raiz para os meus labs.

ROOT=/home/philipe/compiladores

ANTLR_PATH=/usr/local/lib/antlr-4.9.2-complete.jar

# Diretório para aonde vão os arquivos gerados pelo ANTLR.
GEN_PATH=parser

# Diretório para os arquivos .class
BIN_PATH=bin

CLASS_PATH_OPTION=-cp .:$(ANTLR_PATH):$(BIN_PATH)

# Comandos como descritos na página do ANTLR.
ANTLR4=$(JAVA) -jar $(ANTLR_PATH)
GRUN=$(JAVA) $(CLASS_PATH_OPTION) org.antlr.v4.gui.TestRig

# Diretório para os casos de teste
DATA=$(ROOT)/tests
IN=$(DATA)/in

MAIN_PATH=checker

all: antlr javac
	@echo "Done."

antlr: Python3Lexer.g4 Python3Parser.g4
	$(ANTLR4) -visitor -o $(GEN_PATH) Python3Lexer.g4 Python3Parser.g4
	cp Python3LexerBase.java $(GEN_PATH)

# Compila todos os subdiretórios e joga todos os .class em BIN_PATH pra organizar.
javac:
	rm -rf $(BIN_PATH)
	mkdir $(BIN_PATH)
	$(JAVAC) $(CLASS_PATH_OPTION) -d $(BIN_PATH) */*.java Main.java

# 'Python3' é o prefixo comum das duas gramáticas (Python3Lexer e Python3Parser).
# 'file_input' é a regra inicial de Python3Parser.
run:
	$(JAVA) $(CLASS_PATH_OPTION):$(BIN_PATH) Main $(FILE)

tm: tm.c tables.c types.c
	gcc -Wall -Wconversion -o tmsim tm.c tables.c types.c


clean:
	@rm -rf $(GEN_PATH) $(BIN_PATH)
