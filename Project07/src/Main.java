public class Main {
	public static void main(String[] args) {
		try {

			String file_name = args[0];

			if(file_name.indexOf(".vm") == -1) {
				throw new RuntimeException("File Not Found");  
			}
			
			Parser parser = new Parser(file_name); //parses a .vm file

			file_name = file_name.substring(0, file_name.indexOf(".")); 
			file_name += ".asm";    //change extension to .asm

			CodeWriter writer = new CodeWriter(file_name); //writes to a .asm file.

			while (parser.hasMoreCommands()) {
				Parser.cmdType cmdType = parser.commandType();
				
				if (cmdType == Parser.cmdType.C_PUSH || cmdType == Parser.cmdType.C_POP) {
					String memSeg = parser.arg1();
					int val = parser.arg2();
					writer.writePushPop(cmdType, memSeg, val);
				}		
				else if (cmdType == Parser.cmdType.C_ARITHMETIC) {
					String op = parser.arg1();
					writer.writeArithmetic(op);
				}

				parser.advance();
			}

			writer.close();
		}
		catch (Exception ex) {
			System.out.println("Error: " + ex.getMessage());
		}
	}
}
