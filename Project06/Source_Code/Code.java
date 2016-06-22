import java.util.Hashtable;

public class Code {
	
	Hashtable<String, String> destTable;
	Hashtable<String, String> compTable;
	Hashtable<String, String> jumpTable;

	public Code() {
		destTable = new Hashtable<String, String>();
		compTable = new Hashtable<String, String>();
		jumpTable = new Hashtable<String, String>();
		
		populateDestTable();
		populateCompTable();
		populateJumpTable();
	}

	public String dest(String mnemonic) {
		return destTable.get(mnemonic);
	}

	public String comp(String mnemonic) {
		return compTable.get(mnemonic);
	}

	public String jump(String mnemonic) {
		return jumpTable.get(mnemonic);
	}

	private void populateDestTable() {
		destTable.put("null", "000");
		destTable.put("M",    "001");
		destTable.put("D",    "010");
		destTable.put("MD",   "011");
		destTable.put("A",    "100");
		destTable.put("AM",   "101");
		destTable.put("AD",   "110");
		destTable.put("AMD",  "111");
	}

	private void populateCompTable() {
		//when a bit = 0
		compTable.put("0",   "101010");
		compTable.put("1",   "111111");
		compTable.put("-1",  "111010");
		compTable.put("D",   "001100");
		compTable.put("A",   "110000");
		compTable.put("!D",  "001101");
		compTable.put("!A",  "110001");
		compTable.put("-D",  "001111");
		compTable.put("-A",  "110011");
		compTable.put("D+1", "011111");
		compTable.put("A+1", "110111");
		compTable.put("D-1", "001110");
		compTable.put("A-1", "110010");	
		compTable.put("D+A", "000010");
		compTable.put("D-A", "010011");
		compTable.put("A-D", "000111");
		compTable.put("D&A", "000000");
		compTable.put("D|A", "010101");

		//when a bit = 1
		compTable.put("M",   "110000");
		compTable.put("!M",  "110001");
		compTable.put("-M",  "110011");
		compTable.put("M+1", "110111");
		compTable.put("M-1", "110010");
		compTable.put("D+M", "000010");
		compTable.put("D-M", "010011");
		compTable.put("M-D", "000111");
		compTable.put("D&M", "000000");
		compTable.put("D|M", "010101");
	}

	private void populateJumpTable() {
		jumpTable.put("null", "000");
		jumpTable.put("JGT",  "001");
		jumpTable.put("JEQ",  "010");
		jumpTable.put("JGE",  "011");
		jumpTable.put("JLT",  "100");
		jumpTable.put("JNE",  "101");
		jumpTable.put("JLE",  "110");
		jumpTable.put("JMP",  "111");
	}
}
