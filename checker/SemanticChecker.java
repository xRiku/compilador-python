package checker;

import org.antlr.v4.runtime.Token;

import static ast.NodeKind.ASSIGN_NODE;
import static ast.NodeKind.BOOL_VAL_NODE;
import static ast.NodeKind.EQ_NODE;
import static ast.NodeKind.IF_NODE;
import static ast.NodeKind.INT_VAL_NODE;
import static ast.NodeKind.MINUS_NODE;
import static ast.NodeKind.OVER_NODE;
import static ast.NodeKind.PLUS_NODE;
import static ast.NodeKind.REAL_VAL_NODE;
import static ast.NodeKind.STR_VAL_NODE;
import static ast.NodeKind.TIMES_NODE;
import static ast.NodeKind.VAR_DECL_NODE;
import static ast.NodeKind.VAR_LIST_NODE;
import static ast.NodeKind.VAR_USE_NODE;
import static typing.Conv.I2R;
import static typing.Type.BOOL_TYPE;
import static typing.Type.INT_TYPE;
import static typing.Type.NO_TYPE;
import static typing.Type.REAL_TYPE;
import static typing.Type.STR_TYPE;

import java.io.IOException;

import parser.Python3Parser;
// import parser.Python3Parser.Assign_stmtContext;
// import parser.Python3Parser.ExprIdContext;
// import parser.Python3Parser.ExprStrValContext;
// import parser.Python3Parser.Read_stmtContext;
import parser.Python3ParserBaseVisitor;
// import parser.Python3Parser.testlistStarExpr;
import parser.Python3Parser.Expr_stmtContext;
import parser.Python3Parser.Small_stmtContext;
import parser.Python3Parser.Assign_stmtContext;
import parser.Python3Parser.AtomContext;
import parser.Python3Parser.AtomNameContext;
import parser.Python3Parser.AtomNumberContext;
import parser.Python3Parser.AtomStringContext;
import parser.Python3Parser.AtomBoolContext;
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

    private boolean leftmostvar = true;

    private Token leftvar;

    private boolean assignment = false;
    
    private boolean passed = true;

    // Testa se o dado token foi declarado antes.
    void checkVar(Token token) {
    	String text = token.getText();
    	int line = token.getLine();
   		int idx = vt.lookupVar(text);
        // if (idx == -1) {
    	// 	System.err.printf(
    	// 		"SEMANTIC ERROR (%d): variable '%s' was not declared.\n",
		// 		line, text);
    	// 	passed = false;
        //     return;
        // }
        // if (idx == -1) {            
        //     vt.addVar(text, line, lastDeclType);
        // }
        // return new AST(VAR_USE_NODE, idx, vt.getType(idx));
    }
    
    // Cria uma nova variável a partir do dado token.
    void newVar(Type var_type) {
    	String text = leftvar.getText();
    	int line = leftvar.getLine();
   		int idx = vt.lookupVar(text);
        // if (idx != -1) {
        // 	System.err.printf(
    	// 		"SEMANTIC ERROR (%d): variable '%s' already declared at line %d.\n",
        //         line, text, vt.getLine(idx));
        // 	passed = false;
        //     return;
        // }
        idx = vt.addVar(text, line, var_type);
        // return new AST(VAR_DECL_NODE, idx, lastDeclType);
    }

    boolean varExists() {
        String text = leftvar.getText();
   		int idx = vt.lookupVar(text);
        return idx == -1 ? false : true;
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

    @Override
	public Void visitSmall_stmt(Python3Parser.Small_stmtContext ctx) {
    	// Visita a declaração de tipo para definir a variável lastDeclType.
        // System.out.println(ctx);
        visit(ctx.expr_stmt()); 
            // System.err.println(e);
        
        // checkVar(ctx.NAME().getSymbol());
    	// Agora testa se a variável foi redeclarada.
    	return null; // Java says must return something even when Void
    }

    @Override
	public Void visitAssign_stmt(Python3Parser.Assign_stmtContext ctx) {
    	assignment = true;
    	return null; // Java says must return something even when Void
    }
    
    // @Override
	// public Void visitExpr_stmt(Python3Parser.Expr_stmtContext ctx) {
    // 	// Visita a declaração de tipo para definir a variável lastDeclType.
    //     // System.out.println(ctx);
    // 	visit(ctx.testlist_star_expr(0)); 
        
    //     // checkVar(ctx.NAME().getSymbol());
    // 	// Agora testa se a variável foi redeclarada.
    // 	return null; // Java says must return something even when Void
    // }
    
    
    // /* Visita a regra expr_stmt: testlist_star_expr (annassign 
    //       | augassign (yield_expr|testlist)
    //       |('=' (yield_expr|testlist_star_expr))*) */
    // // @Override
    // public Void visitExpr_stmt(Python3Parser.Expr_stmtContext ctx) {
    // 	// Visita a declaração de tipo para definir a variável lastDeclType.
    // 	// visit(ctx.testlist_star_expr());
    //     // checkVar(ctx.NAME().getSymbol());
    //     // System.out.println(ctx.NUMBER().getSymbol());
    // 	// Agora testa se a variável foi redeclarada.
    // 	return null; // Java says must return something even when Void
    // }

    // @Override
    // public Void visitAtom(Python3Parser.AtomContext ctx) {
    // 	// Visita a declaração de tipo para definir a variável lastDeclType.
	// 	// this.lastDeclType = Type.INT_TYPE;
    //     System.out.println(ctx.NUMBER().getSymbol());
	// 	// checkVar(ctx.NAME().getSymbol());
	// 	// checkVar(ctx.STRING().getSymbol());
    // 	// Agora testa se a variável foi redeclarada.
    // 	return null; // Java says must return something even when Void
    // }

    @Override
    public Void visitAtomNumber(Python3Parser.AtomNumberContext ctx) {
    	// Visita a declaração de tipo para definir a variável lastDeclType.
		// this.lastDeclType = Type.INT_TYPE;
        // this.lastDeclType = Type.REAL_TYPE;
        // System.out.println("DEU");
        if (assignment) {
            if (!varExists()) {
                newVar(Type.REAL_TYPE);
            } 
            assignment = false;
        }

		// checkVar(ctx.NAME().getSymbol());
		// checkVar(ctx.STRING().getSymbol());
    	// Agora testa se a variável foi redeclarada.
    	return null; // Java says must return something even when Void
    }

    @Override
    public Void visitAtomName(Python3Parser.AtomNameContext ctx) {
    	// Visita a declaração de tipo para definir a variável lastDeclType.
		// this.lastDeclType = Type.INT_TYPE;
        this.lastDeclType = Type.STR_TYPE;
        System.out.println("Foi o x");
        if (leftmostvar) {
            leftvar = ctx.NAME().getSymbol();
            // leftmostvar = false;
            // newVar(ctx.NAME().getSymbol());
        }
        // System.out.println(ctx.NAME().getSymbol());
		// checkVar(ctx.NAME().getSymbol());
		// checkVar(ctx.STRING().getSymbol());
    	// Agora testa se a variável foi redeclarada.
    	return null; // Java says must return something even when Void
    }

    @Override
    public Void visitAtomString(Python3Parser.AtomStringContext ctx) {
    	// Visita a declaração de tipo para definir a variável lastDeclType.
		// this.lastDeclType = Type.INT_TYPE;
        // System.out.println(ctx.STRING());
        st.add(ctx.STRING().toString().substring(1,ctx.STRING().toString().length()-1));
        // this.lastDeclType = Type.STR_TYPE;
        if (assignment) {
            newVar(Type.STR_TYPE);
            assignment = false;
        }

		// checkVar(ctx.NAME().getSymbol());
		// checkVar(ctx.STRING().getSymbol());
    	// Agora testa se a variável foi redeclarada.
    	return null; // Java says must return something even when Void
    }

    @Override
    public Void visitAtomBool(Python3Parser.AtomBoolContext ctx) {
    	// Visita a declaração de tipo para definir a variável lastDeclType.
		// this.lastDeclType = Type.INT_TYPE;
        // System.out.println(ctx.STRING().getSymbol());
		// checkVar(ctx.NAME().getSymbol());
		// checkVar(ctx.STRING().getSymbol());
    	// Agora testa se a variável foi redeclarada.
    	return null; // Java says must return something even when Void
    }

	
}
