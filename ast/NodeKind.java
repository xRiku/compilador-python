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
    COMPOUND_NODE {
		public String toString() {
            return "compound";
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
    EQ_NODE {
		public String toString() {
            return "==";
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
    OVER_NODE {
		public String toString() {
            return "/";
        }
	},
    PLUS_NODE {
		public String toString() {
            return "+";
        }
	},
    REAL_VAL_NODE {
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
    TIMES_NODE {
		public String toString() {
            return "*";
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
    B2R_NODE {
		public String toString() {
            return "B2R";
        }
	},
    B2S_NODE {
		public String toString() {
            return "B2S";
        }
	},
    I2R_NODE {
		public String toString() {
            return "I2R";
        }
	},
    I2S_NODE {
		public String toString() {
            return "I2S";
        }
	},
    R2S_NODE {
		public String toString() {
            return "R2S";
        }
	};
	
	public static boolean hasData(NodeKind kind) {
		switch(kind) {
	        case BOOL_VAL_NODE:
	        case INT_VAL_NODE:
	        case REAL_VAL_NODE:
	        case STR_VAL_NODE:
	        case VAR_DECL_NODE:
	        case VAR_USE_NODE:
	            return true;
	        default:
	            return false;
		}
	}
}
