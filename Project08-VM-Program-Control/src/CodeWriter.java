import java.io.PrintWriter;

public class CodeWriter {

    private PrintWriter writer;
    private String file_name;
    private int labelCount = 0;
    private int labelReturnCount = 0;

    public CodeWriter(String file_name) {
        try {
            this.file_name = file_name;
            writer = new PrintWriter(file_name);
        }
        catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    public void setFileName(String file_name) {
        this.file_name = file_name;
    }

    public void openFile() {
        try {
            writer = new PrintWriter(file_name);
        }
        catch(Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    public void close() {
        writer.close();
    }

    public void writeInit() {
        writer.println("@256");
        writer.println("D=A");
        writer.println("@SP");
        writer.println("M=D");
        writeCall("Sys.init", 0);
    }

    public void writeLabel(String label) {
        writer.println("("+label+")");
    }

    public void writeGoto(String label) {
        writer.println("@"+label);
        writer.println("0; JMP");
    }

    public void writeIf(String label) {
        decStackPtr();
        popOperand();
        writer.println("@"+label);
        writer.println("D;JNE");
    }

    public void writeCall(String funName, int numArgs) {
        //push return address 
        writer.println("@RETURN_ADDR"+labelReturnCount);
        writer.println("D=A");
        storeResult();
        incStackPtr();

        //push LCL
        writer.println("@LCL");
        writer.println("D=M");
        storeResult();
        incStackPtr();

        //PUSH ARG
        writer.println("@ARG");
        writer.println("D=M");
        storeResult();
        incStackPtr();

        //PUSH THIS
        writer.println("@THIS");
        writer.println("D=M");
        storeResult();
        incStackPtr();

        //PUSH tHAT
        writer.println("@THAT");
        writer.println("D=M");
        storeResult();
        incStackPtr();

        //ARG = SP - N - 5
        writer.println("@SP");
        writer.println("D=M");
        writer.println("@"+numArgs);
        writer.println("D=D-A");
        writer.println("@5");
        writer.println("D=D-A");
        writer.println("@ARG");
        writer.println("M=D");

        //LCL = SP;
        writer.println("@SP");
        writer.println("D=M");
        writer.println("@LCL");
        writer.println("M=D");

        //goto f 
        writeGoto(funName);
        writer.println("(RETURN_ADDR"+labelReturnCount+")");
        labelReturnCount++;
    }

    public void writeReturn() {
        //Frame = LCL (frame temp var)
        writer.println("@LCL");
        writer.println("D=M");
        writer.println("@R13");
        writer.println("M=D");

        //RET = *(FRAME - 5)
        writer.println("@5");
        writer.println("D=A");
        writer.println("@R13");
        writer.println("A=M-D");    //A-REG => MEM(LCL-5) => 312
        writer.println("D=M");      //D-REG => 9
        writer.println("@R14");
        writer.println("M=D");      //R14 = 9

        //*ARG = POP();
        decStackPtr();
        writer.println("A=M");
        writer.println("D=M");  //store result in d reg.

        writer.println("@ARG"); //@2
        writer.println("A=M");  //A-REG = RAM[2]
        writer.println("M=D");  //ram[A-reg] = D
        
        //SP = ARG+1
        writer.println("@ARG");
        writer.println("D=M");
        writer.println("@SP");
        writer.println("M=D+1");

        //That = *(FRAME-1)
        pushSymbol(1, "THAT");

        //THIS = *(FRAME-2)
        pushSymbol(2, "THIS");

        //ARG
        pushSymbol(3, "ARG");

        //LCL
        pushSymbol(4, "LCL");

        //goto ret
        writer.println("@R14");     //This was the problem!! had R13 instead of 14!
        writer.println("A=M");
        writer.println("0;JMP");
    
    }

    public void pushSymbol(int val, String memSeg) {
        writer.println("@"+val);
        writer.println("D=A");
        writer.println("@R13");
        writer.println("A=M-D");    //A-REG => MEM(LCL-2) => 312
        writer.println("D=M");
        writer.println("@"+memSeg);
        writer.println("M=D");      //THAT =    
    }

    public void writeFunction(String funName, int numLocals) {
        writeLabel(funName);
        for (int i = 0; i < numLocals; i++) {
            writePushPop(Parser.cmdType.C_PUSH, "constant", 0); 
        }

    }

    public void writeArithmetic(String cmd) {
        if (cmd.equals("add")) {   
            decStackPtr();
            popOperand();
            decStackPtr();
            doArithemetic(cmd);
            storeResult();
            incStackPtr();
        }
        else if (cmd.equals("sub")) {
            decStackPtr();
            popOperand();
            decStackPtr();
            doArithemetic(cmd);
            storeResult();
            incStackPtr();
        }
        else if (cmd.equals("and")) {
            decStackPtr();
            popOperand();
            decStackPtr();
            doArithemetic(cmd);
            storeResult();
            incStackPtr();  
        }
        else if (cmd.equals("or")) {
            decStackPtr();
            popOperand();
            decStackPtr();
            doArithemetic(cmd);
            storeResult();
            incStackPtr();
        }
        else if (cmd.equals("eq")) {
            decStackPtr();
            popOperand();
            decStackPtr();
            doArithemetic("sub");
            jump(cmd);
            incStackPtr();
        }
        else if (cmd.equals("gt")) {
            decStackPtr();
            popOperand();
            decStackPtr();
            doArithemetic("sub");
            jump(cmd);
            incStackPtr();
        }
        else if (cmd.equals("lt")) {
            decStackPtr();
            popOperand();
            decStackPtr();
            doArithemetic("sub");
            jump(cmd);
            incStackPtr();
        }
        else if (cmd.equals("neg")) {
            decStackPtr();
            popOperand();
            writer.println("D=!D");
            writer.println("D=D+1");
            storeResult();
            incStackPtr();
        }
        else if (cmd.equals("not")) {
            decStackPtr();
            popOperand();
            writer.println("D=!D");
            storeResult();
            incStackPtr();
        }
    }

    private void popOperand() {
        writer.println("@SP");
        writer.println("A=M");
        writer.println("D=M");
    }

    private void doArithemetic(String cmd) {
        writer.println("A=M");

        if (cmd.equals("add")) {
            writer.println("D=D+M");
        }
        else if (cmd.equals("sub")) {
            writer.println("D=M-D");
        }
        else if (cmd.equals("and")) {
            writer.println("D=D&M");
        }
        else if (cmd.equals("or")) {
            writer.println("D=D|M");
        }
    }

    private void doMemSeg(String memSeg, int val) {       
        if (memSeg.equals("argument")) {
            writer.println("@ARG");
        }
        else if (memSeg.equals("local")) {
            writer.println("@LCL");
        }
        else if (memSeg.equals("this")) {
            writer.println("@THIS");
        }
        else if (memSeg.equals("that")) {
            writer.println("@THAT");
        }
        
        writer.println("D=M");      
        writer.println("@"+val);    //A-REG = VALUE
        writer.println("A=D+A");    //A-REG = BASE+ index 
        writer.println("D=M");      //D-REG = Val (Base + index)
    }

    private void incStackPtr() {
        writer.println("@SP");
        writer.println("M=M+1");
    }

    private void decStackPtr() {
        writer.println("@SP");
        writer.println("M=M-1");
    }

    private void storeResult() {
        writer.println("@SP");
        writer.println("A=M"); 
        writer.println("M=D");      
    }

    private void doPop(String memSeg, int val) {
        if (memSeg.equals("argument")) {
            writer.println("@ARG");
        }
        else if (memSeg.equals("local")) {
            writer.println("@LCL");
        }
        else if (memSeg.equals("this")) {
            writer.println("@THIS");
        }
        else if (memSeg.equals("that")) {
            writer.println("@THAT");
        }

        writer.println("D=M");      //D-REg = Ram[A-REG]
        writer.println("@"+val);    //A-REG = val
        writer.println("D=D+A");    //D-Reg holds value of ARG_bASE + val(index)
        writer.println("@R13");     //general purpose reg. used to
        writer.println("M=D");      //used to store the address of local MEM.
        decStackPtr();
        writer.println("@SP");
        writer.println("A=M");      //A reg = Top of stack
        writer.println("D=M");      //D-REG = val at RAM[top of stack]
        writer.println("@R13");
        writer.println("A=M");      //A-REG now holds address of local MEM.
        writer.println("M=D");      //push val from stack onto local mem.
    }

    private void jump(String cmd) {
        writer.println("@IF_LABEL"+labelCount);
        
        if(cmd.equals("eq")) {
            writer.println("D;JEQ");    
        }
        else if(cmd.equals("lt")) {
            writer.println("D;JLT");
        }
        else if(cmd.equals("gt")) {
            writer.println("D;JGT");
        }

        writer.println("@SP");      //if not equal then store 0. ie false.
        writer.println("A=M");  
        writer.println("M=0");
        writer.println("@END"+labelCount); 
        writer.println("0;JMP");
        writer.println("(IF_LABEL"+labelCount+")"); //L_COMMAND equal.
        writer.println("@SP");
        writer.println("A=M");      
        writer.println("M=-1");     //store -1 Mem[A-REG] in  ie True.
        writer.println("(END"+labelCount+")");labelCount++;
        labelCount++;
    }

    public void writePushPop(Parser.cmdType cmd, String memSeg, int val) {
        if(cmd == Parser.cmdType.C_PUSH) {
            if(memSeg.equals("constant")) {
                writer.println("@"+val);
                writer.println("D=A");      
                storeResult();
                incStackPtr();
            }
            else if (memSeg.equals("argument")) {
                doMemSeg(memSeg, val);
                storeResult();
                incStackPtr();  
            }
            else if (memSeg.equals("local")) {
                doMemSeg(memSeg, val);
                storeResult();
                incStackPtr();
            }
            else if (memSeg.equals("this")) {
                doMemSeg(memSeg, val);
                storeResult();
                incStackPtr();
            }
            else if (memSeg.equals("that")) {
                doMemSeg(memSeg, val);
                storeResult();
                incStackPtr();
            }
            else if (memSeg.equals("temp")) {
                writer.println("@R"+(5+val));   //A-Reg = RAM[5+val]
                writer.println("D=M");          //D-REG = val.
                storeResult();
                incStackPtr();
            }
            else if (memSeg.equals("pointer")) {
                writer.println("@"+(3+val));
                writer.println("D=M");          //D-REG = val.
                storeResult();
                incStackPtr();
            }
            else if (memSeg.equals("static")) {
                String f_name = file_name.substring(0, file_name.indexOf("."));
                String var_name = f_name + "." + val; //of the format "xxx.val"
                
                writer.println("@"+var_name); 
                writer.println("D=M");
                storeResult();
                incStackPtr();
            }
        }
        else if (cmd == Parser.cmdType.C_POP) { 
            if (memSeg.equals("local")) {
                doPop(memSeg, val);
            }
            else if (memSeg.equals("argument")) {
                doPop(memSeg, val);
            }
            else if (memSeg.equals("this")) {
                doPop(memSeg, val);
            }
            else if (memSeg.equals("that")) {
                doPop(memSeg, val);
            }
            else if (memSeg.equals("temp")) {
                decStackPtr();
                writer.println("@SP");      
                writer.println("A=M");      //A reg = Top of stack
                writer.println("D=M");      //D-REG = val at RAM[top of stack
                writer.println("@R"+(5+val)); 
                writer.println("M=D");
            }
            else if (memSeg.equals("pointer")) {
                decStackPtr();
                storeResult();  
                writer.println("@R13");
                writer.println("M=D");
                writer.println("@"+(3+val));
                writer.println("M=D");
            }
            else if (memSeg.equals("static")) {
                String f_name = file_name.substring(0, file_name.indexOf("."));
                String var_name = f_name + "." + val; //of the format "xxx.val"
                
                decStackPtr();
                writer.println("@SP");
                writer.println("A=M");          //a reg val at top of stack
                writer.println("D=M");          //a reg val at address.
                writer.println("@"+var_name);
                writer.println("M=D");          //STORE Dreg val into @xxx.val
            }
        }
    }
}
