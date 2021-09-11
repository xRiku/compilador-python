package typing;

import static typing.Conv.B2I;
import static typing.Conv.B2F;
// import static typing.Conv.B2S;
import static typing.Conv.I2F;
// import static typing.Conv.I2S;
import static typing.Conv.NONE;
// import static typing.Conv.F2S;

import typing.Conv.Unif;

// Enumeração dos tipos primitivos que podem existir em EZLang.
public enum Type {
	INT_TYPE {
		public String toString() {
            return "int";
        }
	},
    FLOAT_TYPE {
		public String toString() {
			return "float";
		}
	},
    BOOL_TYPE {
		public String toString() {
            return "bool";
        }
	},
    STR_TYPE {
		public String toString() {
            return "string";
        }
	},
	LIST_TYPE {
		public String toString() {
			return "list";
		}
	},
	NO_TYPE { // Indica um erro de tipos.
		public String toString() {
            return "no_type";
        }
	};

	// Tabela de unificação de tipos primitivos para o operador '+'.
	private static Unif plus[][] ={
		{ new Unif(INT_TYPE, NONE, NONE), new Unif(FLOAT_TYPE, I2F, NONE),  new Unif(NO_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE) },
		{ new Unif(FLOAT_TYPE, NONE, I2F), new Unif(FLOAT_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE) },
		{ new Unif(INT_TYPE, B2I, NONE),  new Unif(FLOAT_TYPE, B2F, NONE),   new Unif(INT_TYPE, B2I, B2I), new Unif(NO_TYPE, NONE, NONE) },
		{ new Unif(NO_TYPE, NONE, NONE),  new Unif(NO_TYPE, NONE, NONE),   new Unif(NO_TYPE, NONE, NONE), new Unif(STR_TYPE, NONE, NONE) }
	};
	
	public Unif unifyPlus(Type that) {
		return plus[this.ordinal()][that.ordinal()];
	}
	
	// Tabela de unificação de tipos primitivos para os demais operadores aritméticos.
	private static Unif other[][] = {
		{ new Unif(INT_TYPE, NONE, NONE), new Unif(FLOAT_TYPE, I2F, NONE),  new Unif(NO_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE) },
		{ new Unif(FLOAT_TYPE, NONE, I2F), new Unif(FLOAT_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE) },
		{ new Unif(INT_TYPE, B2I, NONE),  new Unif(FLOAT_TYPE, B2F, NONE),   new Unif(INT_TYPE, B2I, B2I), new Unif(NO_TYPE, NONE, NONE) },
		{ new Unif(NO_TYPE, NONE, NONE),  new Unif(NO_TYPE, NONE, NONE),   new Unif(NO_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE) }
	};

	public Unif unifyOtherArith(Type that) {
	    return other[this.ordinal()][that.ordinal()];
	}

	// Tabela de unificação de tipos primitivos para os operadores de comparação.
	private static Unif comp[][] = {
		{ new Unif(BOOL_TYPE, NONE, NONE), new Unif(BOOL_TYPE, I2F, NONE),  new Unif(NO_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE)   },
		{ new Unif(BOOL_TYPE, NONE, I2F),  new Unif(BOOL_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE)   },
		{ new Unif(BOOL_TYPE, B2I, NONE),   new Unif(BOOL_TYPE, B2F, NONE),   new Unif(NO_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE)   },
		{ new Unif(NO_TYPE, NONE, NONE),   new Unif(NO_TYPE, NONE, NONE),   new Unif(NO_TYPE, NONE, NONE), new Unif(BOOL_TYPE, NONE, NONE) }
	};

	public Unif unifyComp(Type that) {
		return comp[this.ordinal()][that.ordinal()];
	}
}
