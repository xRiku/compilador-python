package checker;

import java.io.IOException;

import java.util.Arrays;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.gui.TreeViewer;
import parser.Python3Lexer;
import parser.Python3Parser;

public class Main {

	/*
	 *  Programa principal para funcionamento do compilador.
	 *
	 *  Até o laboratório anterior, a gente usou o TestRig pronto
	 *  do ANTLR porque a gente só queria rodar o parser ou lexer.
	 *
	 *  A partir de agora, as demais funcionalidades do compilador
	 *  precisam ser desenvolvidas por nós, assim, é preciso uma
	 *  função principal customizada para definir o funcionamento
	 *  do programa.
	 *
	 *  Esta função espera um único argumento: o nome do
	 *  programa a ser compilado. Em um código real certamente
	 *  deveria haver alguma verificação de erro mas ela foi
	 *  omitida aqui para simplificar o código e facilitar a leitura.
	 */
	public static void main(String[] args) throws IOException {
		// Cria um CharStream que lê os caracteres de um arquivo.
		// O livro do ANTLR fala para criar um ANTLRInputStream,
		// mas a partir da versão 4.7 essa classe foi deprecada.
		// Esta é a forma atual para criação do stream.
		CharStream input = CharStreams.fromFileName(args[0]);

		// Cria um lexer que consome a entrada do CharStream.
		Python3Lexer lexer = new Python3Lexer(input);

		// Cria um buffer de tokens vindos do lexer.
		CommonTokenStream tokens = new CommonTokenStream(lexer);

		// Cria um parser que consome os tokens do buffer.
		Python3Parser parser = new Python3Parser(tokens);

		// Começa o processo de parsing na regra 'program'.
		// ParseTree tree = parser.program();
		ParseTree tree = parser.file_input();		
		// ParseTree tree = parser.lexpr();
		// System.out.println(tree.toStringTree(parser));
		// TestRig testRig = new TestRig();
		// TestRig.process(lexer,parser);

		// TreeViewer viewr = new TreeViewer(Arrays.asList(
        //          parser.getRuleNames()),tree);
        // viewr.open();

		if (parser.getNumberOfSyntaxErrors() != 0) {
			// Houve algum erro sintático. Termina a compilação aqui.
			return;
		}

		// Cria o analisador semântico e visita a ParseTree para
		// fazer a análise.
		
		SemanticChecker checker = new SemanticChecker();
		checker.visit(tree);

		// Saída final.
		
		if (checker.hasPassed()) {			
			System.out.println("PARSE SUCCESSFUL!");
			checker.printTables();
			// checker.printAST();
		}
	}

}


// public class Mylistener extends gramBaseListener {
// 	@Override public void enterEveryRule(ParserRuleContext ctx) {  //see gramBaseListener for allowed functions
// 		System.out.println("rule entered: " + ctx.getText());      //code that executes per rule
// 	}
// }
