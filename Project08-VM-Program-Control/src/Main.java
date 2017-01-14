import java.io.File;

public class Main {
	public static void main(String[] args) {
		try {
			boolean isDirectory = false;
			boolean initCode = true;

			if (args[0].indexOf(".vm") == -1) {
				isDirectory = true;	
			}

			if (args.length > 1) {
				if (args[1].equals("-n")) {
					initCode = false;
				}
				else {
					throw new RuntimeException("Not a valid Flag");
				}
			}	

			if (isDirectory) {
				File directory = new File(args[0]);
				File[] files = directory.listFiles();

				CodeWriter writer = new CodeWriter(directory+".asm"); 

				if (initCode) {
					writer.writeInit();
				}

				for (File file : files) {
					String file_name =  file.getName();
					if (file_name.indexOf(".vm") != -1) {
						Parser parser = new Parser(args[0]+"//"+file_name);
						writer.setFileName(file_name);
						parseFile(parser, writer);
					}	
				}

				writer.close();
			}
			else {
				String file_name = args[0];
				Parser parser = new Parser(file_name); //parses a .vm file

				file_name = file_name.substring(0, file_name.indexOf("."));
				file_name += ".asm";

				CodeWriter writer = new CodeWriter(file_name); 
				if (initCode) {
					writer.writeInit();
				}

				parseFile(parser, writer);
				writer.close();
			}

		}
		catch (Exception ex) {
			System.out.println("Error: " + ex.getMessage());
		}
	}

	public static void parseFile(Parser parser, CodeWriter writer) {
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
			else if (cmdType == Parser.cmdType.C_LABEL) {
				String label = parser.arg1();
				writer.writeLabel(label);
			}
			else if (cmdType == Parser.cmdType.C_IF) {
				String label = parser.arg1();
				writer.writeIf(label);
			}
			else if (cmdType == Parser.cmdType.C_GOTO) {
				String label = parser.arg1();
				writer.writeGoto(label);	
			}
			else if (cmdType == Parser.cmdType.C_FUNCTION) {
				String funName = parser.arg1();
				int numLocals = parser.arg2();
				writer.writeFunction(funName, numLocals);	
			}
			else if (cmdType == Parser.cmdType.C_RETURN) {
				writer.writeReturn();
			}
			else if (cmdType == Parser.cmdType.C_CALL) {
				String funName = parser.arg1();
				int argNums = parser.arg2();
				writer.writeCall(funName, argNums);
			}

			parser.advance();
		}
	}
}
