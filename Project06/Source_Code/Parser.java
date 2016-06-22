import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class Parser {
	
	private ArrayList<String> arrCmds;
	public static enum cmdType { A_COMMAND, C_COMMAND, L_COMMAND }
	private int currLineNum;

	private Code code = new Code();

	public Parser(String path) {	
		currLineNum = 0;
		arrCmds = new ArrayList<String>();

		try {
			FileInputStream fInStream = new FileInputStream(path);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fInStream));

			String line ="";
			while ((line = reader.readLine()) != null) {
				arrCmds.add(line);
			}

			reader.close();
			removeSpaces();
			deleteComments();
		}
		catch (Exception ex) {
			System.out.println("Error: " + ex.getMessage() );
			System.exit(0);
		}
	}

	public Boolean hasMoreCommands() {
		return (currLineNum != arrCmds.size());
	}

	public void advance() {
		currLineNum++;
	}

	public cmdType commandType() {
		String currLine = arrCmds.get(currLineNum);

		if (currLine.charAt(0) == '@') {
			return cmdType.A_COMMAND;
		}
		else if (currLine.charAt(0) == '(') {
			return cmdType.L_COMMAND;
		}
		else {
			return cmdType.C_COMMAND;
		}
	}

	public String symbol() {
		String currLine = arrCmds.get(currLineNum);
		String symb = ""; //string to be returned 

		if (commandType() == cmdType.A_COMMAND) {
			symb = currLine.substring(1);
		}
		if (commandType() == cmdType.L_COMMAND) {
			symb = currLine.substring(1, currLine.length()-1 );
		}

		return symb;
	}

	public String dest() {
		String line = arrCmds.get(currLineNum);
		int index = line.indexOf("=");

		if (index == -1) {
			line = "null";
		}
		else {
			line = line.substring(0, index);
		}

		return code.dest(line);
	}

	public String comp() {
		String line = arrCmds.get(currLineNum);

		int index1 = line.indexOf("=");
		int index2 = line.indexOf(";"); 

		if (index1 != -1) {
			line = line.substring(index1+1);
		}
		else {
			line = line.substring(0, index2);
		}

		char aBit = ' ';
		if (line.indexOf("M") == -1) {
			aBit = '0';
		}
		else {
			aBit = '1';
		}

		return (aBit + code.comp(line));
	}

	public String jump() {
		String line = arrCmds.get(currLineNum);
		int index = line.indexOf(";");

		if (index == -1) {
			line = "null";
		}
		else {
			line = line.substring(index+1);
		}
		
		return code.jump(line);
	}
	
	private void deleteComments() {
		for (int i = 0; i < arrCmds.size(); i++) {
			String line = arrCmds.get(i);
			int index = line.indexOf("//");

			if (index != -1) {
				String noCommentStr = line.substring(0, index);
				arrCmds.set(i, noCommentStr.trim());
			}
		}

		arrCmds.removeAll(Arrays.asList(""));
	}

	public void setCurrLineNum(int line) {
		currLineNum = line;
	}

	private void removeSpaces() {
		for(int i = 0; i < arrCmds.size(); i++)
			arrCmds.set(i, arrCmds.get(i).trim());
	}
}
