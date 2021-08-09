package checker;

import org.antlr.v4.runtime.Token;

import parser.Python3Parser;
// import parser.Python3Parser.Assign_stmtContext;
// import parser.Python3Parser.ExprIdContext;
// import parser.Python3Parser.ExprStrValContext;
// import parser.Python3Parser.Read_stmtContext;
import parser.Python3ParserBaseVisitor;
// import parser.Python3Parser.testlistStarExpr;
import parser.Python3Parser.Expr_stmtContext;
import parser.Python3Parser.AtomContext;
import org.antlr.v4.gui.TestRig;
import tables.StrTable;
import tables.VarTable;
import typing.Type;

/*
 * Analisador semântico de EZLang implementado como um visitor
 * da ParseTree do ANTLR. A classe Python3ParserBaseVisitor é gerada
 * automaticamente e já possui métodos padrão aonde o comportamento
 * é só visitar todos os filhos. Por conta disto, basta sobreescrever
 * os métodos que a gente quer alterar.
 * 
 * Por enquanto só há uma verificação simples de declaração de
 * variáveis usando uma tabela de símbolos. Funcionalidades adicionais
 * como análise de tipos serão incluídas no próximo laboratório.
 * 
 * O tipo Void indicado na super classe define o valor de retorno dos
 * métodos do visitador. Depois vamos alterar isso para poder construir
 * a AST.
 * 
 * Lembre que em um 'visitor' você é responsável por definir o
 * caminhamento nos filhos de um nó da ParseTree através da chamada
 * recursiva da função 'visit'. Ao contrário do 'listener' que
 * caminha automaticamente em profundidade pela ParseTree, se
 * você não chamar 'visit' nos métodos de visitação, o caminhamento
 * para no nó que você estiver, deixando toda a subárvore do nó atual
 * sem visitar. Tome cuidado neste ponto pois isto é uma fonte
 * muito comum de erros. Veja o método visitAssign_stmt abaixo para
 * ter um exemplo.
 */
public class SemanticChecker extends Python3ParserBaseVisitor<Void> {

	private StrTable st = new StrTable();   // Tabela de strings.
    private VarTable vt = new VarTable();   // Tabela de variáveis.
    
    Type lastDeclType;  // Variável "global" com o último tipo declarado.
    
    private boolean passed = true;

    // Testa se o dado token foi declarado antes.
    void checkVar(Token token) {
    	String text = token.getText();
    	int line = token.getLine();
   		int idx = vt.lookupVar(text);
    	if (idx == -1) {
    		System.err.printf(
    			"SEMANTIC ERROR (%d): variable '%s' was not declared.\n",
				line, text);
    		passed = false;
            return;
        }
    }
    
    // Cria uma nova variável a partir do dado token.
    void newVar(Token token) {
    	String text = token.getText();
    	int line = token.getLine();
   		int idx = vt.lookupVar(text);
        if (idx != -1) {
        	System.err.printf(
    			"SEMANTIC ERROR (%d): variable '%s' already declared at line %d.\n",
                line, text, vt.getLine(idx));
        	passed = false;
            return;
        }
        vt.addVar(text, line, lastDeclType);
    }
    
    // Retorna true se os testes passaram.
    boolean hasPassed() {
    	return passed;
    }
    
    // Exibe o conteúdo das tabelas em stdout.
    void printTables() {
        System.out.print("\n\n");
        System.out.print(st);
        System.out.print("\n\n");
    	System.out.print(vt);
    	System.out.print("\n\n");
    }
    
	/* Visita a regra expr_stmt: testlist_star_expr (annassign 
          | augassign (yield_expr|testlist)
          |('=' (yield_expr|testlist_star_expr))*) */
    // @Override
    // public Void visitExpr_stmt(Python3Parser.Expr_stmtContext ctx) {
    // 	// Visita a declaração de tipo para definir a variável lastDeclType.
    // 	// visit(ctx.testlist_star_expr());
    //     // System.out.println(ctx.NUMBER().getSymbol());
    // 	// Agora testa se a variável foi redeclarada.
    // 	return null; // Java says must return something even when Void
    // }

    // @Override
    // public Void visitAtom(Python3Parser.AtomContext ctx) {
    	// Visita a declaração de tipo para definir a variável lastDeclType.
		// this.lastDeclType = Type.INT_TYPE;
        // System.out.println(ctx.NUMBER().getSymbol());
		// checkVar(ctx.NAME().getSymbol());
		// checkVar(ctx.STRING().getSymbol());
    	// Agora testa se a variável foi redeclarada.
    	// return null; // Java says must return something even when Void
    // }

	
}
