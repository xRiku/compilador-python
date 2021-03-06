package ast;

/*
 * Classe abstrata que define a interface do visitor para a AST.
 * Implementa o despacho do método 'visit' conforme o 'kind' do nó.
 * Com isso, basta herdar desta classe para criar um interpretador
 * ou gerador de código.
 */
public abstract class ASTBaseVisitor<T> {

	// Único método público. Começa a visita a partir do nó raiz
	// passado. Precisa ter outro nome porque tem a mesma assinatura
	// que o método "genérico" 'visit'.
	public void execute(AST root) {
		visit(root);
	}
	
	// Método "genérico" que despacha a visitação para os métodos
	// especializados conforme o 'kind' do nó atual. Igual ao código
	// em C. Novamente fica o argumento sobre usar OO ou não aqui.
	// Se tivéssemos trocentas classes especializando o nó da AST
	// esse despacho seria feito pela JVM. Aqui precisa fazer na mão.
	// Por outro lado, assim não precisa de trocentas classes com o
	// código todo espalhado entre elas...
	protected T visit(AST node) {
		switch(node.kind) {
	        case ASSIGN_NODE:   		return visitAssign(node);
			case PRINT_NODE:			return visitPrint(node);
			case INPUT_NODE:			return visitRead(node);
			case FILE_INPUT_NODE: 		return visitFile_input(node);
	        case EQUALS_NODE:       	return visitEq(node);
	        case NOT_EQ_2_NODE:       	return visitNeq(node);
	        case BLOCK_NODE:    		return visitBlock(node);
	        case BOOL_VAL_NODE: 		return visitBoolVal(node);
	        case IF_NODE:       		return visitIf(node);			
	        case INT_VAL_NODE:  		return visitIntVal(node);
	        case MINUS_NODE:    		return visitMinus(node);
	        case PLUS_NODE:     		return visitPlus(node);
			case STAR_NODE:     		return visitTimes(node);
			case DIV_NODE:     			return visitDiv(node);			
			case MOD_NODE:     			return visitMod(node);			
	        case FLOAT_VAL_NODE: 		return visitFloatVal(node);
	        case STR_VAL_NODE:  		return visitStrVal(node);
	        case VAR_DECL_NODE: 		return visitVarDecl(node);
	        case VAR_LIST_NODE: 		return visitVarList(node);
	        case VAR_USE_NODE:  		return visitVarUse(node);
			case FUNC_CALL_NODE:  		return visitFuncCall(node);
			case DEF_NODE:  			return visitDefNode(node);
			case PARAM_NODE:  			return visitParamNode(node);
			case WHILE_NODE:			return visitWhile(node);
			case RETURN_NODE:			return visitReturn(node);
			case LESS_THAN_NODE:		return visitLt(node);	
			case GREATER_THAN_NODE:		return visitGt(node);	
			case GT_EQ_NODE: 			return visitGtEq(node);
			case LT_EQ_NODE: 			return visitLtEq(node);			
			case B2I_NODE:      		return visitB2I(node);
	        case B2F_NODE:      		return visitB2R(node);
	        case B2S_NODE:      		return visitB2S(node);
	        case I2F_NODE:      		return visitI2R(node);
	        case I2S_NODE:      		return visitI2S(node);
	        case F2S_NODE:      		return visitR2S(node);			
	        default:
	            System.err.printf("Invalid kind: %s!\n", node.kind.toString());
	            System.exit(1);
	            return null;
		}
	}
	
	// Métodos especializados para visitar um nó com um certo 'kind'.

	protected abstract T visitAssign(AST node);

	protected abstract T visitEq(AST node);
	
	protected abstract T visitNeq(AST node);

	protected abstract T visitGtEq(AST node);

	protected abstract T visitLtEq(AST node);

	protected abstract T visitBlock(AST node);

	protected abstract T visitBoolVal(AST node);

	protected abstract T visitIf(AST node);	

	protected abstract T visitIntVal(AST node);

	protected abstract T visitLt(AST node);

	protected abstract T visitGt(AST node);

	protected abstract T visitMinus(AST node);

	protected abstract T visitDiv(AST node);

	protected abstract T visitMod(AST node);

	protected abstract T visitPlus(AST node);
	
	protected abstract T visitTimes(AST node);

	protected abstract T visitFile_input(AST node);

	protected abstract T visitRead(AST node);

	protected abstract T visitFloatVal(AST node);

	protected abstract T visitWhile(AST node);
	
	protected abstract T visitReturn(AST node);

	protected abstract T visitStrVal(AST node);

	protected abstract T visitVarDecl(AST node);

	protected abstract T visitVarList(AST node);

	protected abstract T visitVarUse(AST node);

	protected abstract T visitFuncCall(AST node);

	protected abstract T visitDefNode(AST node);

	protected abstract T visitParamNode(AST node);

	protected abstract T visitPrint(AST node);

	protected abstract T visitB2I(AST node);

	protected abstract T visitB2R(AST node);

	protected abstract T visitB2S(AST node);

	protected abstract T visitI2R(AST node);

	protected abstract T visitI2S(AST node);

	protected abstract T visitR2S(AST node);
	
}