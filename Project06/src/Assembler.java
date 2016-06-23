import java.util.ArrayList;
import java.io.PrintWriter;

public class Assembler {
	public static void main(String[] args) {	

		try {
			String fileName = args[0];

			if (fileName.indexOf(".asm") == -1) {
				throw new RuntimeException("Invalid File Name");
			}

			Parser parser = new Parser(fileName);
			SymbolTable symbTable = new SymbolTable();

			//stores binary commands. Array written to file.
			ArrayList<String> binCmds = new ArrayList<String>();

			//creates symbol table.
			firstPass(parser, symbTable);

			//second pass fills the arraylist with the binary commands.
			secondPass(parser, symbTable, binCmds); 

			//change file name to .hack and write to file.
			fileName = fileName.substring(0, fileName.indexOf("."));
			fileName += ".hack";
			writeToFile(binCmds, fileName);

		}
		catch (Exception ex){
			System.out.println("Error: " + ex.getMessage() );
		}
	}

	//first pass creates symbol Table (add lables only)
	public static void firstPass(Parser parser, SymbolTable symbTable) {
		int counter = 0; //counter recording ROM address.	
		while (parser.hasMoreCommands() ) {	
			if (parser.commandType() == Parser.cmdType.A_COMMAND || parser.commandType() == Parser.cmdType.C_COMMAND){
				counter++;
			}
			else if (parser.commandType() == Parser.cmdType.L_COMMAND){
				String symbol = parser.symbol();
				symbTable.addEntry(symbol, counter);
			}

			parser.advance();
		}
	}
	
	//second pass add variables to symbol table
	public static void secondPass(Parser parser, SymbolTable symbTable, ArrayList<String> binCmds) {
		int startingVarAdd = 16; // variables allocated starting at memory[16]
		int varCounter = 0;
		parser.setCurrLineNum(0);

		while (parser.hasMoreCommands()) {
			String word = ""; //16 bit binary rep of command

			if (parser.commandType() == Parser.cmdType.A_COMMAND) {
				String symb = parser.symbol();
				int dec =-1;
				
				if (isSymbol(symb)) {
					if (symbTable.contains(symb)) {
						int addr = symbTable.getAddress(symb);
						dec = addr;
					}
					else {
						int addr = startingVarAdd + varCounter;
						symbTable.addEntry(symb, addr); // add at next available address
						dec = addr;
						varCounter++;
					}
				}
				else {  
					dec = Integer.parseInt(symb);
				}

				String bin = Integer.toBinaryString(dec);
				word = appendZeroes(bin);
			}
			else if (parser.commandType() == Parser.cmdType.C_COMMAND) {
				String comp = parser.comp();
				String dest = parser.dest();
				String jump = parser.jump();

				word = "111" + comp + dest + jump;
			}

			parser.advance();

			//incase of L_command
			if (word != "") {
				binCmds.add(word);
			}
		}
	}

	public static void writeToFile(ArrayList<String> binCmds, String fileName) {
		PrintWriter writer;

		try {
			writer = new PrintWriter(fileName);

			for (int i = 0; i < binCmds.size(); i++) {
				writer.println(binCmds.get(i));	
			}

			writer.close();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static String appendZeroes(String bin) {
		String zeroes = "";

		//16 bit words
		for (int i = 0; i < 16-(bin.length()); i++) {
			zeroes += "0";
		}

		return (zeroes + bin);
	}

	public static boolean isSymbol(String s) {
		try {
			int num = Integer.parseInt(s);
		}
		catch (Exception ex) {
			return true;
		}
		return false;
	}
}
