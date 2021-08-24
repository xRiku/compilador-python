package checker;

import org.antlr.v4.runtime.Token;

import ast.AST;
import static ast.NodeKind.*;
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
import parser.Python3ParserBaseVisitor;
import parser.Python3Parser.Expr_stmtContext;
import parser.Python3Parser.Comp_opContext;
import parser.Python3Parser.StmtContext;
import parser.Python3Parser.Small_stmtContext;
import parser.Python3Parser.Assign_stmtContext;
import parser.Python3Parser.AtomContext;
import parser.Python3Parser.AtomNameContext;
import parser.Python3Parser.AtomNumberContext;
import parser.Python3Parser.AtomStringContext;
// import parser.Python3Parser.AtomBoolContext;
import parser.Python3Parser.AtomBoolTrueContext;
import parser.Python3Parser.AtomBoolFalseContext;
import parser.Python3Parser.AtomListContext;
import parser.Python3Parser.Atom_exprContext;
import parser.Python3Parser.Simple_stmtContext;
import parser.Python3Parser.Expr_stmtContext;
import parser.Python3Parser.Small_stmtContext;
import parser.Python3Parser.Testlist_star_exprContext;
import parser.Python3Parser.TestContext;
import parser.Python3Parser.Or_testContext;
import parser.Python3Parser.And_testContext;
import parser.Python3Parser.Not_testContext;
import parser.Python3Parser.ComparisonContext;
import parser.Python3Parser.ExprContext;
import parser.Python3Parser.Xor_exprContext;
import parser.Python3Parser.And_exprContext;
import parser.Python3Parser.Shift_exprContext;
import parser.Python3Parser.Arith_exprContext;
import parser.Python3Parser.TermContext;
import parser.Python3Parser.FactorContext;
import parser.Python3Parser.PowerContext;
import parser.Python3Parser.Atom_exprContext;
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
public class SemanticChecker extends Python3ParserBaseVisitor<AST> {

	private StrTable st = new StrTable();   // Tabela de strings.
    private VarTable vt = new VarTable();   // Tabela de variáveis.
    
    Type lastDeclType;  // Variável "global" com o último tipo declarado.
    
    AST root;

    private boolean leftmostvar = true;

    private Token leftvar;

    private boolean assignment = false;

    private boolean comparison = false;
    
    private boolean passed = true;

     

    // Testa se o dado token foi declarado antes.
    AST checkVar(Token token) {
    	String text = token.getText();
    	int line = token.getLine();
   		int idx = vt.lookupVar(text);
        if (idx == -1) {
    		System.err.printf(
    			"SEMANTIC ERROR (%d): variable '%s' was not declared.\n",
				line, text);
    		passed = false;
            return null;
        }        
        return new AST(VAR_USE_NODE, idx, vt.getType(idx));        
    }
    
    // Cria uma nova variável a partir do dado token.
    AST newVar(Type var_type) {
    	String text = leftvar.getText();
    	int line = leftvar.getLine();
   		int idx = vt.lookupVar(text);
        if(idx == -1)
        {
           idx = vt.addVar(text, line, var_type);
        }
        return new AST(VAR_DECL_NODE, idx, NO_TYPE);        
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

    void printAST() {
    	AST.printDot(root, vt);
    }

    @Override
	public AST visitFile_input(Python3Parser.File_inputContext ctx) {
        this.root = AST.newSubtree(FILE_INPUT_NODE, NO_TYPE);
        for (int i = 0; i < ctx.stmt().size(); i++) {
    		AST child = visit(ctx.stmt(i));
    		this.root.addChild(child);
    	}        
		return this.root;
    }

    @Override
	public AST visitStmt(Python3Parser.StmtContext ctx) {
    	leftmostvar = true;
        assignment = false;
        try {
            return visit(ctx.simple_stmt());
            // return AST.newSubtree(BLOCK_NODE, NO_TYPE, simple_stmt);
        } catch (Exception e) {
            AST compound_stmt = visit(ctx.compound_stmt());
            return AST.newSubtree(COMPOUND_NODE, NO_TYPE, compound_stmt);
        }
    }

    @Override
	public AST visitSimple_stmt(Python3Parser.Simple_stmtContext ctx) 
    {
        if(ctx.small_stmt().size() != 1)
        {
            AST node = AST.newSubtree(SIMPLE_STMT_NODE, NO_TYPE);
            for (int i = 0; i < ctx.small_stmt().size(); i++) 
            {
                AST child = visit(ctx.small_stmt(i));
                node.addChild(child);
            }
            return node; 
        } else{
            return visit(ctx.small_stmt(0));
        }
    }

    @Override
	public AST visitSmall_stmt(Python3Parser.Small_stmtContext ctx) {
        return visit(ctx.expr_stmt());
    }
    
    @Override
	public AST visitExpr_stmt(Python3Parser.Expr_stmtContext ctx) {
        if(ctx.testlist_star_expr().size() != 1)
        {
            AST node;
            try {
                node = visit(ctx.assign_stmt(0));   
            } catch (Exception e) {
                node = AST.newSubtree(EXPR_STMT_NODE, NO_TYPE);
            }
            for (int i = 0; i < ctx.testlist_star_expr().size(); i++) 
            {
                AST child = visit(ctx.testlist_star_expr(i));
                node.addChild(child);
            }
            return node; 
        } else{
            return visit(ctx.testlist_star_expr(0));
        }
    }

    @Override
	public AST visitAssign_stmt(Python3Parser.Assign_stmtContext ctx) {
    	assignment = true;
    	return AST.newSubtree(ASSIGN_NODE, NO_TYPE);
    }

    @Override
	public AST visitTestlist_star_expr(Python3Parser.Testlist_star_exprContext ctx) {
        if(ctx.test().size() != 1)
        {
            AST node = AST.newSubtree(TEST_LIST_NODE, NO_TYPE);
            for (int i = 0; i < ctx.test().size(); i++) 
            {
                AST child = visit(ctx.test(i));
                node.addChild(child);
            }
            return node; 
        } else{
            return visit(ctx.test(0));
        }        
    }

    @Override
	public AST visitTest(Python3Parser.TestContext ctx) {
        if(ctx.or_test().size() != 1)
        {
            AST node = AST.newSubtree(TEST_NODE, NO_TYPE);
            for (int i = 0; i < ctx.or_test().size(); i++) 
            {
                AST child = visit(ctx.or_test(i));
                node.addChild(child);
            }
            return node; 
        } else{
            return visit(ctx.or_test(0));
        }
    }

    @Override
	public AST visitOr_test(Python3Parser.Or_testContext ctx) {
        if(ctx.and_test().size() != 1)
        {
            AST node = AST.newSubtree(OR_TEST_NODE, NO_TYPE);
            for (int i = 0; i < ctx.and_test().size(); i++) 
            {
                AST child = visit(ctx.and_test(i));
                node.addChild(child);
            }
            return node; 
        }
        else
        {
            return visit(ctx.and_test(0));
        }
    }

    @Override
	public AST visitAnd_test(Python3Parser.And_testContext ctx) {
        if(ctx.not_test().size() != 1)
        {
            AST node = AST.newSubtree(AND_TEST_NODE, NO_TYPE);
            for (int i = 0; i < ctx.not_test().size(); i++) 
            {
                AST child = visit(ctx.not_test(i));
                node.addChild(child);
            }
            return node; 
        }
        else
        {
            return visit(ctx.not_test(0));
        }        
    }
    @Override
	public AST visitNot_test(Python3Parser.Not_testContext ctx) {
        return visit(ctx.comparison());
    }
    @Override
	public AST visitComparison(Python3Parser.ComparisonContext ctx) {
        if(ctx.expr().size() != 1)
        {
            AST node = AST.newSubtree(COMPARISON_NODE, NO_TYPE);
            for (int i = 0; i < ctx.expr().size(); i++) 
            {
                AST child = visit(ctx.expr(i));
                node.addChild(child);
            }
            return node; 
        }
        else
        {
            return visit(ctx.expr(0));
        }        
    }
    @Override
	public AST visitExpr(Python3Parser.ExprContext ctx) {
        if(ctx.xor_expr().size() != 1)
        {
            AST node = AST.newSubtree(EXPR_NODE, NO_TYPE);
            for (int i = 0; i < ctx.xor_expr().size(); i++) 
            {
                AST child = visit(ctx.xor_expr(i));
                node.addChild(child);
            }
            return node; 
        }
        else
        {
            return visit(ctx.xor_expr(0));
        }        
    }
    @Override
	public AST visitXor_expr(Python3Parser.Xor_exprContext ctx) {
        if(ctx.and_expr().size() != 1)
        {
            AST node = AST.newSubtree(XOR_NODE, NO_TYPE);
            for (int i = 0; i < ctx.and_expr().size(); i++) 
            {
                AST child = visit(ctx.and_expr(i));
                node.addChild(child);
            }
            return node; 
        }
        else
        {
            return visit(ctx.and_expr(0));
        }
    }
    @Override
	public AST visitAnd_expr(Python3Parser.And_exprContext ctx) {
        if(ctx.shift_expr().size() != 1)
        {
            AST node = AST.newSubtree(AND_NODE, NO_TYPE);
            for (int i = 0; i < ctx.shift_expr().size(); i++) 
            {
                AST child = visit(ctx.shift_expr(i));
                node.addChild(child);
            }
            return node; 
        }
        else
        {
            return visit(ctx.shift_expr(0));
        }
    }
    @Override
	public AST visitShift_expr(Python3Parser.Shift_exprContext ctx) {
        if(ctx.arith_expr().size() != 1)
        {
            AST node = AST.newSubtree(SHIFT_NODE, NO_TYPE);
            for (int i = 0; i < ctx.arith_expr().size(); i++) 
            {
                AST child = visit(ctx.arith_expr(i));
                node.addChild(child);
            }
            return node; 
        }
        else
        {
            return visit(ctx.arith_expr(0));
        }        
    }

    @Override // fazer depois para cada sinal
	public AST visitArith_expr(Python3Parser.Arith_exprContext ctx) {
        if(ctx.term().size() != 1)
        {
            AST node = AST.newSubtree(ARITH_EXPR_NODE, NO_TYPE);
            for (int i = 0; i < ctx.term().size(); i++) 
            {
                AST child = visit(ctx.term(i));
                node.addChild(child);
            }
            return node; 
        }
        else
        {
            return visit(ctx.term(0));
        }        
    }
    @Override
	public AST visitTerm(Python3Parser.TermContext ctx) {
        if(ctx.factor().size() != 1)
        {
            AST node = AST.newSubtree(TERM_NODE, NO_TYPE);
            for (int i = 0; i < ctx.factor().size(); i++) 
            {
                AST child = visit(ctx.factor(i));
                node.addChild(child);
            }
            return node; 
        }
        else
        {
            return visit(ctx.factor(0));
        }        
    }
    @Override
	public AST visitFactor(Python3Parser.FactorContext ctx) {
        return visit(ctx.power());
    }
    @Override
	public AST visitPower(Python3Parser.PowerContext ctx) {
        return visit(ctx.atom_expr());
    }
    @Override
	public AST visitAtom_expr(Python3Parser.Atom_exprContext ctx) {
        return visit(ctx.atom());
    }

    @Override
    public AST visitComp_op(Python3Parser.Comp_opContext ctx) {

        if(assignment){
            String text = leftvar.getText();
            int idx = vt.lookupVar(text);
            vt.setType(idx, Type.BOOL_TYPE);
        }
        return null;
    }    
        
    @Override
    public AST visitAtomNumber(Python3Parser.AtomNumberContext ctx) {
        Type localtype = Type.REAL_TYPE;
        float floatNumber = 0;
        int integerNumber = 0;
        try {
            integerNumber = Integer.parseInt(ctx.NUMBER().getSymbol().getText());
            localtype = Type.INT_TYPE;
        } catch (NumberFormatException nfe) {
            floatNumber = Float.parseFloat(ctx.NUMBER().getSymbol().getText());
            localtype = Type.REAL_TYPE;
        }
    	if (assignment) {
            String text = leftvar.getText();
   		    int idx = vt.lookupVar(text);
            if (vt.getType(idx) != Type.REAL_TYPE) vt.setType(idx, localtype);
        }
        if(localtype == Type.REAL_TYPE) return new AST(REAL_VAL_NODE, floatNumber, REAL_TYPE);
        else return new AST(INT_VAL_NODE, integerNumber, INT_TYPE);
		
    }

    @Override
    public AST visitAtomName(Python3Parser.AtomNameContext ctx) {
    	// Visita a declaração de tipo para definir a variável lastDeclType.
		if (leftmostvar) {
            leftvar = ctx.NAME().getSymbol();
            leftmostvar = false;
            return newVar(NO_TYPE);            
        } else {
            return checkVar(ctx.NAME().getSymbol());
        }        
    }

    @Override
    public AST visitAtomString(Python3Parser.AtomStringContext ctx) {
    	// Visita a declaração de tipo para definir a variável lastDeclType.		
        st.add(ctx.STRING().toString().substring(1,ctx.STRING().toString().length()-1));
        Type localtype = Type.STR_TYPE;
        int idx = -1;
        if (assignment) {
            String text = leftvar.getText();
   		    idx = vt.lookupVar(text);
            vt.setType(idx, localtype);            
        }
    	return new AST(STR_VAL_NODE, idx, STR_TYPE);
    }

    @Override
    public AST visitAtomBoolTrue(Python3Parser.AtomBoolTrueContext ctx) {
        Type localtype = Type.BOOL_TYPE;
        if (assignment) {
            String text = leftvar.getText();
   		    int idx = vt.lookupVar(text);
            vt.setType(idx, localtype);
            
        }    	
    	return new AST(BOOL_VAL_NODE, 1, BOOL_TYPE);
    }

    @Override
    public AST visitAtomBoolFalse(Python3Parser.AtomBoolFalseContext ctx) {
        Type localtype = Type.BOOL_TYPE;
        if (assignment) {
            String text = leftvar.getText();
   		    int idx = vt.lookupVar(text);            
            vt.setType(idx, localtype);
                        
        }    	
    	return new AST(BOOL_VAL_NODE, 0, BOOL_TYPE);
    }


    public AST visitAtomList(Python3Parser.AtomListContext ctx) {
        Type localtype = Type.LIST_TYPE;
        int idx = -1;
        if (assignment) {
            String text = leftvar.getText();
   		    idx = vt.lookupVar(text);
            vt.setType(idx, localtype);            
        }
        return new AST(LIST_VAL_NODE, idx, Type.LIST_TYPE);
    }

	
}
