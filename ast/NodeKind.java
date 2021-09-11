package ast;

// Enumeração dos tipos de nós de uma AST.
// Adaptado da versão original em C.
// Algumas pessoas podem preferir criar uma hierarquia de herança para os
// nós para deixar o código "mais OO". Particularmente eu não sou muito
// fã, acho que só complica mais as coisas. Imagine uma classe abstrata AST
// com mais de 20 classes herdando dela, uma classe para cada tipo de nó...
public enum NodeKind {
    FILE_INPUT_NODE {
		public String toString() {
            return "file_input";
        }
	},
	ASSIGN_NODE {
		public String toString() {
            return "=";
        }
	},
    BLOCK_NODE {
		public String toString() {
            return "block";
        }
	},
    DEF_NODE {
		public String toString() {
            return "def";
        }
	},
    BREAK_NODE {
		public String toString() {
            return "break";
        }
	},
    CONTINUE_NODE {
		public String toString() {
            return "continue";
        }
	},
    RETURN_NODE {
		public String toString() {
            return "return";
        }
	},
    PARAM_NODE {
		public String toString() {
            return "parameters";
        }
	},
    FUNC_CALL_NODE {
		public String toString() {
            return "function";
        }
	},
    PRINT_NODE {
		public String toString() {
            return "print";
        }
	},
    INPUT_NODE {
		public String toString() {
            return "input";
        }
	},
    COMPOUND_NODE {
		public String toString() {
            return "compound";
        }
	},
    WHILE_NODE {
		public String toString() {
            return "while";
        }
	},
    SIMPLE_STMT_NODE {
		public String toString() {
            return "simple_stmt";
        }
	},
    EXPR_STMT_NODE {
		public String toString() {
            return "expr_stmt";
        }
	},
    TEST_LIST_NODE {
		public String toString() {
            return "test_list";
        }
	},
    TEST_NODE {
		public String toString() {
            return "test";
        }
	},
    OR_TEST_NODE {
		public String toString() {
            return "or_test";
        }
	},
    AND_TEST_NODE {
		public String toString() {
            return "and_test";
        }
	},
    COMPARISON_NODE {
		public String toString() {
            return "comparison";
        }
	},
    EXPR_NODE {
		public String toString() {
            return "expr";
        }
	},
    XOR_NODE {
		public String toString() {
            return "xor";
        }
	},
    AND_NODE {
		public String toString() {
            return "and";
        }
	},
    SHIFT_NODE {
		public String toString() {
            return "shift";
        }
	},
    ARITH_EXPR_NODE {
		public String toString() {
            return "arith_expr";
        }
	},
    TERM_NODE {
		public String toString() {
            return "term";
        }
	},
    BOOL_VAL_NODE {
		public String toString() {
            return "";
        }
	},
    IF_NODE {
		public String toString() {
            return "if";
        }
	},
    ELIF_NODE {
		public String toString() {
            return "elif";
        }
	},
    ELSE_NODE {
		public String toString() {
            return "else";
        }
	},
    INT_VAL_NODE {
		public String toString() {
            return "";
        }
	},    
    MINUS_NODE {
		public String toString() {
            return "-";
        }
	},
    
    PLUS_NODE {
		public String toString() {
            return "+";
        }
	},
    FLOAT_VAL_NODE {
		public String toString() {
            return "";
        }
	},
    LIST_VAL_NODE {
		public String toString() {
            return "";
        }
	},
    STR_VAL_NODE {
		public String toString() {
            return "";
        }
	},
    STAR_NODE {
		public String toString() {
            return "*";
        }
	},
    DIV_NODE {
		public String toString() {
            return "/";
        }
	},
    MOD_NODE {
		public String toString() {
            return "%";
        }
	},
    LESS_THAN_NODE {
		public String toString() {
            return "<";
        }
	},
    GREATER_THAN_NODE {
		public String toString() {
            return ">";
        }
	},  
    EQUALS_NODE {
		public String toString() {
            return "==";
        }
	}, 
    GT_EQ_NODE {
		public String toString() {
            return ">=";
        }
	}, 
    LT_EQ_NODE {
		public String toString() {
            return "<=";
        }
	}, 
    NOT_EQ_2_NODE {
		public String toString() {
            return "!=";
        }
	},
    IN_NODE {
		public String toString() {
            return "in";
        }
	},
    IS_NODE {
		public String toString() {
            return "is";
        }
	},
    VAR_DECL_NODE {
		public String toString() {
            return "var_decl";
        }
	},
    VAR_LIST_NODE {
		public String toString() {
            return "var_list";
        }
	},
    VAR_USE_NODE {
		public String toString() {
            return "var_use";
        }
	},
    B2I_NODE { // Type conversion.
		public String toString() {
            return "B2I";
        }
	},
    B2F_NODE {
		public String toString() {
            return "B2F";
        }
	},
    B2S_NODE {
		public String toString() {
            return "B2S";
        }
	},
    I2F_NODE {
		public String toString() {
            return "I2F";
        }
	},
    I2S_NODE {
		public String toString() {
            return "I2S";
        }
	},
    F2S_NODE {
		public String toString() {
            return "F2S";
        }
	};
	
	public static boolean hasData(NodeKind kind) {
		switch(kind) {
	        case BOOL_VAL_NODE:
	        case INT_VAL_NODE:
	        case FLOAT_VAL_NODE:
	        case STR_VAL_NODE:
	        case VAR_DECL_NODE:
	        case VAR_USE_NODE:
	            return true;
	        default:
	            return false;
		}
	}
}
