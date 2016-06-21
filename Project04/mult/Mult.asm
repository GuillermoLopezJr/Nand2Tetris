// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)

@R1
D=M 		//D = Mem[R1]

@counter
M=D 		// Mem[counter] = Mem[R1]

@sum
M=0		// Mem[sum] = 0

(LOOP)
	
	@counter
	D=M	//D= Mem[Counter]
	
	@END
	D;JEQ	// if counter == 0 goto END
	
	@R0
	D=M	//D=R0
	
	@sum
	M = M+D	//add R0 to sum
	
	@counter
	M=M-1   //decrement counter
	
	@LOOP
	0;JMP

(END)
	@sum
	D=M
	
	@R2
	M=D
	
	@END
	0;JMP
