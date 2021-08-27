package checker;

import org.antlr.v4.runtime.Token;

import ast.AST;
import static ast.NodeKind.*;
import static typing.Conv.I2F;
import static typing.Type.BOOL_TYPE;
import static typing.Type.INT_TYPE;
import static typing.Type.NO_TYPE;
import static typing.Type.FLOAT_TYPE;
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
import parser.Python3Parser.Plus_minusContext;
import parser.Python3Parser.Basic_termsContext;
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
import parser.Python3Parser.Compound_stmtContext;
import parser.Python3Parser.While_stmtContext;
import parser.Python3Parser.If_stmtContext;
import parser.Python3Parser.SuiteContext;
import parser.Python3Parser.FuncdefContext;
import parser.Python3Parser.ParametersContext;
import parser.Python3Parser.TypedargslistContext;
import parser.Python3Parser.TfpdefContext;
import parser.Python3Parser.TrailerContext;
import parser.Python3Parser.ArglistContext;
import parser.Python3Parser.ArgumentContext;
import parser.Python3Parser.Flow_stmtContext;
import parser.Python3Parser.Break_stmtContext;
import parser.Python3Parser.Continue_stmtContext;
import parser.Python3Parser.Return_stmtContext;
import org.antlr.v4.gui.TestRig;
import tables.StrTable;
import tables.VarTable;
import typing.Type;
import typing.Conv;
import typing.Conv.Unif;


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

// ----------------------------------------------------------------------------
    // Type checking and inference.

    private static void typeError(int lineNo, String op, Type t1, Type t2) {
    	System.out.printf("SEMANTIC ERROR (%d): incompatible types for operator '%s', LHS is '%s' and RHS is '%s'.\n",
    			lineNo, op, t1.toString(), t2.toString());
    	System.exit(1);
    }

    private static void checkBoolExpr(int lineNo, String cmd, Type t) {
        if (t != BOOL_TYPE) {
            System.out.printf("SEMANTIC ERROR (%d): conditional expression in '%s' is '%s' instead of '%s'.\n",
               lineNo, cmd, t.toString(), BOOL_TYPE.toString());
            System.exit(1);
        }
    }

// ----------------------------------------------------------------------------

// Visita a regra file_input: (NEWLINE | stmt)* EOF
    @Override
	public AST visitFile_input(Python3Parser.File_inputContext ctx) {
        this.root = AST.newSubtree(FILE_INPUT_NODE, NO_TYPE);
        for (int i = 0; i < ctx.stmt().size(); i++) {
    		AST child = visit(ctx.stmt(i));
    		this.root.addChild(child);
    	}        
		return this.root;
    }

// Visita a regra stmt: simple_stmt | compound_stmt;
    @Override
	public AST visitStmt(Python3Parser.StmtContext ctx) {
    	leftmostvar = true;
        assignment = false;
        try {
            return visit(ctx.simple_stmt());            
        } catch (Exception e) {
            return visit(ctx.compound_stmt());            
        }
    }

// Visita a regra compound_stmt: if_stmt | while_stmt | for_stmt | try_stmt | with_stmt | funcdef | classdef | decorated | async_stmt
    @Override
    public AST visitCompound_stmt(Python3Parser.Compound_stmtContext ctx) {
        try {
            return visit(ctx.while_stmt());
        } catch (Exception e) {
            try {
                return visit(ctx.if_stmt());
            } catch (Exception e2) {
                return visit(ctx.funcdef());
            }            
        }
    }

// Visita a regra simple_stmt: small_stmt (';' small_stmt)* (';')? NEWLINE
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

// Visita a regra small_stmt: (expr_stmt | del_stmt | pass_stmt | flow_stmt | import_stmt | global_stmt | nonlocal_stmt | assert_stmt)
    @Override
	public AST visitSmall_stmt(Python3Parser.Small_stmtContext ctx) {
        try {
            return visit(ctx.expr_stmt());
        } catch (Exception e) {
            return visit(ctx.flow_stmt());
        }
    }

    @Override
	public AST visitFlow_stmt(Python3Parser.Flow_stmtContext ctx) {
        if(ctx.break_stmt() != null){
            return visit(ctx.break_stmt());
        } else if (ctx.continue_stmt() != null){
            return visit(ctx.continue_stmt());
        } else {
            return visit(ctx.return_stmt());
        }
    }

    @Override
	public AST visitBreak_stmt(Python3Parser.Break_stmtContext ctx) {
        return AST.newSubtree(BREAK_NODE, NO_TYPE);
    }

    @Override
	public AST visitContinue_stmt(Python3Parser.Continue_stmtContext ctx) {
        return AST.newSubtree(CONTINUE_NODE, NO_TYPE);
    }

    @Override
	public AST visitReturn_stmt(Python3Parser.Return_stmtContext ctx) {
        return AST.newSubtree(RETURN_NODE, NO_TYPE);
    }
    
// Visita a regra expr_stmt: testlist_star_expr (annassign | augassign (yield_expr|testlist) |(assign_stmt (yield_expr|testlist_star_expr))*);
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

// Visita a regra assign_stmt: ASSIGN
    @Override
	public AST visitAssign_stmt(Python3Parser.Assign_stmtContext ctx) {
    	assignment = true;
    	return AST.newSubtree(ASSIGN_NODE, NO_TYPE);
    }

// Visita a regra testlist_star_expr:(test |star_expr) (',' (test|star_expr))* (',')? 
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

// Visita a regra test: or_test ('if' or_test 'else' test)? | lambdef;
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

// Visita a regra or_test: and_test ('or' and_test)*
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

// Visita a regra and_test: not_test ('and' not_test)*
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

// Visita a regra not_test: 'not' not_test | comparison
    @Override
	public AST visitNot_test(Python3Parser.Not_testContext ctx) {
        return visit(ctx.comparison());
    }

// Visita a regra comparison: expr (comp_op expr)*
    @Override
	public AST visitComparison(Python3Parser.ComparisonContext ctx) {
        if(ctx.expr().size() != 1)
        {
            AST node_l = visit(ctx.expr(0));
            AST node_r;
            AST node = AST.newSubtree(COMPARISON_NODE, BOOL_TYPE);
            for(int i = 0; i < ctx.comp_op().size(); i++){
                if(i != 0){
                    node_l = node;
                }
                
                node = visit(ctx.comp_op(i));
                node_r = visit(ctx.expr(i+1));
                
                
                Type lt = node_l.type;
		        Type rt = node_r.type;
		        Unif unif;                
                unif = lt.unifyComp(rt);
                if (unif.type == NO_TYPE) {
                    typeError(ctx.comp_op(i).op.getLine(), ctx.comp_op(i).op.getText(), lt, rt);
                }
                
                node_l = Conv.createConvNode(unif.lc, node_l);
		        node_r = Conv.createConvNode(unif.rc, node_r);
                node.type = unif.type;
                node.addChild(node_l);
                node.addChild(node_r);
            }            
            return node; 
        }
        else
        {
            return visit(ctx.expr(0));
        }
    }

// Visita a regra comp_op: LESS_THAN | GREATER_THAN  | EQUALS | GT_EQ |LT_EQ | NOT_EQ_1 | NOT_EQ_2 | IN |'not' 'in' | IS |'is' 'not'
    @Override
    public AST visitComp_op(Python3Parser.Comp_opContext ctx) {
        AST node;        
        switch (ctx.getText()) {
            case "==":
                node = AST.newSubtree(EQUALS_NODE, BOOL_TYPE);
                break;
            case "<":
                node = AST.newSubtree(LESS_THAN_NODE, BOOL_TYPE);
                break;
            case ">":
                node = AST.newSubtree(GREATER_THAN_NODE, BOOL_TYPE);
                break;
            case "<=":
                node = AST.newSubtree(LT_EQ_NODE, BOOL_TYPE);
                break;
            case ">=":
                node = AST.newSubtree(GT_EQ_NODE, BOOL_TYPE);
                break;
            case "!=":
                node = AST.newSubtree(NOT_EQ_2_NODE, BOOL_TYPE);
                break;                
            case "is":
                node = AST.newSubtree(IS_NODE, BOOL_TYPE);
                break;
            case "in":
                node = AST.newSubtree(IN_NODE, BOOL_TYPE);
                break;
            default:
                node = AST.newSubtree(COMPARISON_NODE, BOOL_TYPE);
                break;
        }
        if(assignment){
            String text = leftvar.getText();
            int idx = vt.lookupVar(text);
            vt.setType(idx, Type.BOOL_TYPE);
        }
        return node;
    }

// Visita a regra expr: xor_expr ('|' xor_expr)*
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

// Visita a regra xor_expr: and_expr ('^' and_expr)*
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

// Visita a regra and_expr: shift_expr ('&' shift_expr)*
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

// Visita a regra shift_expr: arith_expr (('<<'|'>>') arith_expr)*
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

// Visita a regra arith_expr: term ((plus_minus) term)*
    @Override // fazer depois para cada sinal
	public AST visitArith_expr(Python3Parser.Arith_exprContext ctx) {
        if(ctx.term().size() != 1)
        {   
            AST node_l = visit(ctx.term(0));
            AST node_r;
            AST node = AST.newSubtree(ARITH_EXPR_NODE, NO_TYPE);
            for(int i = 0; i < ctx.plus_minus().size(); i++){
                if(i != 0){
                    node_l = node;
                }
                node = visit(ctx.plus_minus(i));                
                node_r = visit(ctx.term(i+1));
                Type lt = node_l.type;
		        Type rt = node_r.type;
		        Unif unif;
                if(node.kind == PLUS_NODE){
                    unif = lt.unifyPlus(rt);
                }else{
                    unif = lt.unifyOtherArith(rt);
                }

                if (unif.type == NO_TYPE) {
                    typeError(ctx.plus_minus(i).op.getLine(), ctx.plus_minus(i).op.getText(), lt, rt);
                }
                node_l = Conv.createConvNode(unif.lc, node_l);
		        node_r = Conv.createConvNode(unif.rc, node_r);
                node.type = unif.type;
                node.addChild(node_l);
                node.addChild(node_r);
            }
            return node; 
        }
        else
        {
            return visit(ctx.term(0));
        }        
    }

// Visita a regra plus_minus: ADD | MINUS
    @Override
    public AST visitPlus_minus(Python3Parser.Plus_minusContext ctx) {
        AST node;        
        if(ctx.getText().equals("+")) {
            node = AST.newSubtree(PLUS_NODE, NO_TYPE);
        }else{
            node = AST.newSubtree(MINUS_NODE, NO_TYPE);
        }
        return node;
    }

// Visita a regra term: factor ((basic_terms |'@'|'//') factor)*
    @Override
	public AST visitTerm(Python3Parser.TermContext ctx) {
        if(ctx.factor().size() != 1)
        {
            AST node_l = visit(ctx.factor(0));
            AST node_r;
            AST node = AST.newSubtree(TERM_NODE, NO_TYPE);
            for(int i = 0; i < ctx.basic_terms().size(); i++){
                if(i != 0){
                    node_l = node;
                }
                node = visit(ctx.basic_terms(i));                
                node_r = visit(ctx.factor(i+1));
                Type lt = node_l.type;
		        Type rt = node_r.type;
		        Unif unif;
                unif = lt.unifyOtherArith(rt);                

                if (unif.type == NO_TYPE) {
                    typeError(ctx.basic_terms(i).op.getLine(), ctx.basic_terms(i).op.getText(), lt, rt);
                }
                node_l = Conv.createConvNode(unif.lc, node_l);
		        node_r = Conv.createConvNode(unif.rc, node_r);
                node.type = unif.type;
                node.addChild(node_l);
                node.addChild(node_r);
            }
            return node; 
        }
        else
        {
            return visit(ctx.factor(0));
        }        
    }

// Visita a regra basic_terms: STAR | DIV | MOD
    @Override
    public AST visitBasic_terms(Python3Parser.Basic_termsContext ctx) {
        AST node;
        switch(ctx.getText()) {
            case "*":
                node = AST.newSubtree(STAR_NODE, NO_TYPE);  
                break;
            case "/":
                node = AST.newSubtree(DIV_NODE, NO_TYPE);  
                break;
            case "%":
                node = AST.newSubtree(MOD_NODE, NO_TYPE);  
                break;
            default:
                node = AST.newSubtree(TERM_NODE, NO_TYPE); 
        }
        return node;
    }

// Visita a regra factor: ('+'|'-'|'~') factor | power
    @Override
	public AST visitFactor(Python3Parser.FactorContext ctx) {
        return visit(ctx.power());
    }

// Visita a regra power: atom_expr ('**' factor)?
    @Override
	public AST visitPower(Python3Parser.PowerContext ctx) {
        return visit(ctx.atom_expr());
    }

// Visita a regra atom_expr: (AWAIT)? atom trailer*
    @Override
	public AST visitAtom_expr(Python3Parser.Atom_exprContext ctx) {
        if(ctx.trailer().size() > 0){
            // AST trailerNode = visit(ctx.trailer(0));
            return visit(ctx.trailer(0));            
        }
        return visit(ctx.atom());
    }

// Visita a regra trailer: '(' (arglist)? ')' | '[' subscriptlist ']' | '.' NAME
    @Override
	public AST visitTrailer(Python3Parser.TrailerContext ctx) {
        if(ctx.arglist() == null) return AST.newSubtree(FUNC_CALL_NODE, NO_TYPE);
        return visit(ctx.arglist());
    }

// Visita a regra arglist: argument (',' argument)*  (',')?
    @Override
    public AST visitArglist(Python3Parser.ArglistContext ctx) {
        AST node = AST.newSubtree(FUNC_CALL_NODE, NO_TYPE);
        for (int i = 0; i < ctx.argument().size(); i++) {
            AST child = visit(ctx.argument(i));
            node.addChild(child);
        }
        return node;
    }

// Visita a regra argument: ( test (comp_for)? | test '=' test | '**' test | '*' test );
    @Override
    public AST visitArgument(Python3Parser.ArgumentContext ctx) {
        return visit(ctx.test(0));
    }

// Visita a regra atom: NUMBER      
    @Override
    public AST visitAtomNumber(Python3Parser.AtomNumberContext ctx) {
        Type localtype = Type.FLOAT_TYPE;
        float floatNumber = 0;
        int integerNumber = 0;
        try {
            integerNumber = Integer.parseInt(ctx.NUMBER().getSymbol().getText());
            localtype = Type.INT_TYPE;
        } catch (NumberFormatException nfe) {
            floatNumber = Float.parseFloat(ctx.NUMBER().getSymbol().getText());
            localtype = Type.FLOAT_TYPE;
        }
    	if (assignment) {
            String text = leftvar.getText();
   		    int idx = vt.lookupVar(text);
            if (vt.getType(idx) != Type.FLOAT_TYPE) vt.setType(idx, localtype);
        }
        if(localtype == Type.FLOAT_TYPE) return new AST(REAL_VAL_NODE, floatNumber, FLOAT_TYPE);
        else return new AST(INT_VAL_NODE, integerNumber, INT_TYPE);
		
    }
    
// Visita a regra atom: NAME
    @Override
    public AST visitAtomName(Python3Parser.AtomNameContext ctx) {
    	// Visita a declaração de tipo para definir a variável lastDeclType.
		if (leftmostvar && assignment) {
            leftvar = ctx.NAME().getSymbol();
            leftmostvar = false;
            return newVar(NO_TYPE);
        } else {
            return checkVar(ctx.NAME().getSymbol());
        }        
    }
    
// Visita a regra atom: STRING
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

// Visita a regra atom: 'True'
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

// Visita a regra atom: 'False'
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

// Visita a regra atom: 
    @Override
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

// Visita a regra if_stmt: 'if' test ':' suite ('elif' test ':' suite)* ('else' ':' suite)?
    @Override
    public AST visitIf_stmt(Python3Parser.If_stmtContext ctx) {
        
        AST exprNode = visit(ctx.test(0));
		checkBoolExpr(ctx.IF().getSymbol().getLine(), "if", exprNode.type);

		// Constrói o bloco de código do loop.
        AST suiteNode = visit(ctx.suite(0));
    	AST ifNode = AST.newSubtree(IF_NODE, NO_TYPE, exprNode, suiteNode);
        if(ctx.test().size() > 1){
            AST elifNode;
            for(int i = 1; i < ctx.test().size(); i++){
                AST exprNodeElif = visit(ctx.test(i));
                AST suiteNodeElif = visit(ctx.suite(i));
                elifNode = AST.newSubtree(ELIF_NODE, NO_TYPE, exprNodeElif, suiteNodeElif);
                ifNode.addChild(elifNode);
            }
        }
        if(ctx.suite().size() > 1 && ctx.suite().size() > ctx.test().size()){
            AST suiteElseNode = visit(ctx.suite(ctx.suite().size()-1));
            AST elseNode = AST.newSubtree(ELSE_NODE, NO_TYPE, suiteElseNode);
            ifNode.addChild(elseNode);            
        }
        return ifNode;
    }

// Visita a regra while_stmt: 'while' test ':' suite ('else' ':' suite)?
    @Override
	public AST visitWhile_stmt(Python3Parser.While_stmtContext ctx) {
		// Analisa a expressão booleana.
		AST exprNode = visit(ctx.test());
		checkBoolExpr(ctx.WHILE().getSymbol().getLine(), "while", exprNode.type);

		// Constrói o bloco de código do loop.
        AST suiteNode = visit(ctx.suite(0));
    	return AST.newSubtree(WHILE_NODE, NO_TYPE, suiteNode, exprNode);
	}

// Visita a regra suite: simple_stmt | NEWLINE INDENT stmt+ DEDENT
    @Override
    public AST visitSuite(Python3Parser.SuiteContext ctx) {
        AST blockNode = AST.newSubtree(BLOCK_NODE, NO_TYPE);
        if(ctx.stmt().size() > 0){
            for (int i = 0; i < ctx.stmt().size(); i++) {
                AST child = visit(ctx.stmt(i));
                blockNode.addChild(child);
            }
            return blockNode;
        }
        else{
            return visit(ctx.simple_stmt());
        }
    }

// Visita a regra funcdef: 'def' NAME parameters ('->' test)? ':' suite
    @Override
    public AST visitFuncdef(Python3Parser.FuncdefContext ctx)
    {        
        AST paramNode = visit(ctx.parameters());
        
        AST blockNode = visit(ctx.suite());       
        
        return AST.newSubtree(DEF_NODE, NO_TYPE, paramNode, blockNode);
    }

// Visita a regra parameters: '(' (typedargslist)? ')'
    @Override
    public AST visitParameters(Python3Parser.ParametersContext ctx)
    {
        if(ctx.typedargslist() == null) return AST.newSubtree(PARAM_NODE, NO_TYPE);
        return visit(ctx.typedargslist());
    }

// Visita a regra typedargslist: (tfpdef ('=' test)? (',' tfpdef ('=' test)?)* (',' (
//        '*' (tfpdef)? (',' tfpdef ('=' test)?)* (',' ('**' tfpdef (',')?)?)?
//      | '**' tfpdef (',')?)?)?
//  | '*' (tfpdef)? (',' tfpdef ('=' test)?)* (',' ('**' tfpdef (',')?)?)?
//  | '**' tfpdef (',')?);
    @Override
    public AST visitTypedargslist(Python3Parser.TypedargslistContext ctx)
    {
        AST node = AST.newSubtree(PARAM_NODE, NO_TYPE);
        for (int i = 0; i < ctx.tfpdef().size(); i++) {
            AST child = visit(ctx.tfpdef(i));
            node.addChild(child);
        }
        return node;
    }

// Visita a regra tfpdef: NAME (':' test)?
    @Override
    public AST visitTfpdef(Python3Parser.TfpdefContext ctx)
    {
        leftvar = ctx.NAME().getSymbol();        
        newVar(NO_TYPE);
        int idx = vt.lookupVar(leftvar.getText());
        return checkVar(ctx.NAME().getSymbol());
    }    
}
