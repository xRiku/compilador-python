package code;

import java.util.Formatter;

// Instruction quadruple.
public final class Instruction {

	// Público para não precisar de getter/setter.
	public final OpCode op;
	// Estes campos não podem ser final por causa do backpatching...
	public int o1;	// Operands, which can be int or float registers,
	public int o2;	// int addresses or offsets, or
	public int o3;	// integer or float constants (must be in an integer repr.)

	public Instruction(OpCode op, int o1, int o2, int o3) {
		this.op = op;
		this.o1 = o1;
		this.o2 = o2;
		this.o3 = o3;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Formatter f = new Formatter(sb);
		f.format("%s", this.op.toString());
		if (this.op.opCount == 1) {
			f.format(" %d", this.o1);
		} else if (this.op.opCount == 2) {
			f.format(" %d, %d", this.o1, this.o2);
		} else if (this.op.opCount == 3) {
			f.format(" %d, %d, %d", this.o1, this.o2, this.o3);
		}
		f.close();
		return sb.toString();
	}

	// public String toString() {
	// 	StringBuilder sb = new StringBuilder();
	// 	Formatter f = new Formatter(sb);
	// 	f.format("%s", this.op.toString());
	// 	if(this.op == OpCode.ADDf || this.op == OpCode.SUBf || this.op == OpCode.MULf || this.op == OpCode.DIVf || this.op == OpCode.LWf || this.op == OpCode.SWf )
	// 	{
	// 		if (this.op.opCount == 1) {
	// 			f.format(" f$%d", this.o1);
	// 		} else if (this.op.opCount == 2) {
	// 			f.format(" f$%d, %d", this.o1, this.o2);
	// 		} else if (this.op.opCount == 3) {
	// 			f.format(" f$%d, f$%d, f$%d", this.o1, this.o2, this.o3);
	// 		}
	// 	}
	// 	else if(this.op == OpCode.ADD || this.op == OpCode.SUB || this.op == OpCode.MUL || this.op == OpCode.DIV || this.op == OpCode.LI)
	// 	{
	// 		if (this.op.opCount == 1) {
	// 			f.format(" $%d", this.o1);
	// 		} else if (this.op.opCount == 2) {
	// 			f.format(" $%d, %d", this.o1, this.o2);
	// 		} else if (this.op.opCount == 3) {
	// 			f.format(" $%d, $%d, $%d", this.o1, this.o2, this.o3);
	// 		}
	// 	}
	// 	else if(this.op == OpCode.EQf || this.op == OpCode.NEQf || this.op == OpCode.GTf || this.op == OpCode.GEf || this.op == OpCode.LEQf || this.op == OpCode.LTf || this.op == OpCode.NEGf)
	// 	{
	// 		if (this.op.opCount == 1) {
	// 			f.format(" f$%d", this.o1);
	// 		} else if (this.op.opCount == 2) {
	// 			f.format(" f$%d, f$%d", this.o1, this.o2);
	// 		} else if (this.op.opCount == 3) {
	// 			f.format(" f$%d, f$%d, f$%d", this.o1, this.o2, this.o3);
	// 		}
	// 	}
	// 	else if(this.op == OpCode.EQ || this.op == OpCode.NEQ || this.op == OpCode.GT || this.op == OpCode.GE || this.op == OpCode.LEQ || this.op == OpCode.LT || this.op == OpCode.SW || this.op == OpCode.LW)
	// 	{
	// 		if (this.op.opCount == 1) {
	// 			f.format(" $%d", this.o1);
	// 		} else if (this.op.opCount == 2) {
	// 			f.format(" $%d, $%d", this.o1, this.o2);
	// 		} else if (this.op.opCount == 3) {
	// 			f.format(" $%d, $%d, $%d", this.o1, this.o2, this.o3);
	// 		}
	// 	}
	// 	else if(this.op == OpCode.BEQ || this.op == OpCode.BNE || this.op == OpCode.BGT || this.op == OpCode.BGE || this.op == OpCode.BLT || this.op == OpCode.BLE)
	// 	{
	// 		f.format(" $%d, $zero, %d", this.o1, this.o3);
	// 	}
	// 	else if(this.op == OpCode.SYSCALL){
	// 		if (this.o1 == 10){
	// 			f.format(" %d", this.o1);
	// 		}
	// 		else{
	// 			f.format(" %d, $%d", this.o1, this.o2);
	// 		}
	// 	}		
	// 	else{
	// 		if (this.op.opCount == 1) {
	// 			f.format(" %d", this.o1);
	// 		} else if (this.op.opCount == 2) {
	// 			f.format(" %d, %d", this.o1, this.o2);
	// 		} else if (this.op.opCount == 3) {
	// 			f.format(" %d, %d, %d", this.o1, this.o2, this.o3);
	// 		}
	// 	}
	// 	f.close();
	// 	return sb.toString();
	// }
	
	// Constantes
	
	// Basic arch: 32 int registers and 32 float registers.
	public static final int INT_REGS_COUNT   = 32;  // i0 to i31: int registers.
	public static final int FLOAT_REGS_COUNT = 32;	// f0 to f31: float registers.
	// The machine also has a dedicated program counter (PC) register.

	// Memory is split between data and instruction memory.
	// This is called the Harvard architecture, in contrast to the von Neumann
	// (stored program) architecture.
	public static final int INSTR_MEM_SIZE = 1024;	// instr_mem[]
	public static final int DATA_MEM_SIZE  = 1024;  // data_mem[]
	// The machine also has a string table str_tab[] for storing strings with
	// the command SSTR. Maximum size for each string is 128 chars.

}
