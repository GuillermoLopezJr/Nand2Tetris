import java.util.Hashtable;

public class SymbolTable {
	
	Hashtable<String, Integer> htable;

	public SymbolTable() {
		htable = new Hashtable<String, Integer>();
		addPredefinedSymbols();
	}

	public void addEntry(String symb, int add) {
		htable.put(symb, add);
	} 

	public boolean contains(String symb) {
		return htable.containsKey(symb);
	}

	public int getAddress(String symb) {
		return htable.get(symb);
	}

	private void addPredefinedSymbols() {
		htable.put("SP", 0);
		htable.put("LCL", 1);
		htable.put("ARG", 2);
		htable.put("THIS", 3);
		htable.put("THAT", 4);

		for (int i = 0; i <= 15; i++) {
			htable.put("R"+i, i);
		}

		htable.put("SCREEN", 16384);
		htable.put("KBD", 24576);
	}
}
