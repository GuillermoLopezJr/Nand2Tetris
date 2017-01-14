// This program runs an infinite loop that listens to the keyboard input. 
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel. When no key is pressed, the
// program clears the screen, i.e. writes "white" in every pixel.

@SCREEN				//A reg = 16384
D=A			    	//D reg =16384

@ptrToScreen 			// A Reg = 16
M=D				//address 16 =  16384

(INFINITE_LOOP)
	@KBD			//A reg = 25476
	D=M			//D reg = Memory[KBD]
	
	@KEY_PRESSED
	D;JNE			//if D ==1 goto KEY_PRESSED
	@NOT_PRESSED
	D;JEQ			//if D ==0 go to NOT_PRESSED
	
	@INFINITE_LOOP
	0;JMP			//go back to start of (INFINITE_LOOP)
	
	(KEY_PRESSED) 		//Turn Screen BLACK
		
		@24576		//A Reg = 24576 = 256 * 32 + 16384. ie Last pixel
		D=A		//D Reg = 24576
		@ptrToScreen	//A Reg = ptrToScreen
		D=D-M		//D Reg = 24576-M[ptrToScreen]
		
		@INCREM		
		D;JNE 		//if D != 0 increment

		@INFINITE_LOOP
		0;JMP
		
	(NOT_PRESSED)		//Turn Screen WHITE
	
		@SCREEN		//A Reg = @SCREEN
		D=A		//D Reg = Val(SCREEN)
		@ptrToScreen	//A Reg = ptrToScreen	
		D=M-D		//D = M[ptrToScreen] - val(Screen)
		
		@DECR				
		D;JGE		//if D > 0 decrement
		
		@INFINITE_LOOP
		0;JMP
		
	(INCREM)
		@ptrToScreen	//A Reg = ptrToScreen
		A=M		//A Reg = Mem[ptrToScreen]
		M=-1		//Mem[Mem[ptrToScreen]] = -1 (BLACK)
		@ptrToScreen	//A Reg = ptrToScreen
		M=M+1		//Mem[ptrToScreen] = Mem[ptrToScreen]+1
		
		@INFINITE_LOOP
		0;JMP
		
	(DECR)
		@ptrToScreen	//A Reg = ptrToScreen
		A=M		//A Reg = Mem[ptrToScreen]
		M=0		//Mem[Mem[PtrToScreen]] = 0
		@ptrToScreen	//A Reg = ptrToScreen
		M=M-1		//MeM[PtrToScreen] = Memory[ptrToScreen]-1 
		
		@INFINITE_LOOP
		0;JMP
