import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;

public class Parser {

	private ArrayList<String> arrCmds;
	private int currLineNum;

	public static enum cmdType { 
		C_ARITHMETIC, 
	  	C_PUSH,
	  	C_POP, 
	  	C_LABEL,
	  	C_GOTO,
	  	C_IF,
	  	C_FUNCTION,
	  	C_RETURN,
	  	C_CALL
	}

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
			System.out.println("Error: " + ex.getMessage());
		}
	} 

	public boolean hasMoreCommands() {
		return (currLineNum != arrCmds.size());
	}

	public void advance() {
		currLineNum++;
	}

	public cmdType commandType() {
		String currCmd = arrCmds.get(currLineNum);
		String[] arrCmd = currCmd.split(" ");

		if (arrCmd[0].equals("push") ) {
			return cmdType.C_PUSH;
		}	
		else if (arrCmd[0].equals("pop")) {
			return cmdType.C_POP;
		}
		else if (arrCmd[0].equals("function")) {
			return cmdType.C_FUNCTION;
		}
		else if (arrCmd[0].equals("label")) {
			return cmdType.C_LABEL;
		}
		else if (arrCmd[0].equals("goto")) {
			return cmdType.C_GOTO;
		}
		else if (arrCmd[0].equals("if-goto")) {
			return cmdType.C_IF;
		}
		else if (arrCmd[0].equals("return")) {
			return cmdType.C_RETURN;
		}
		else if (arrCmd[0].equals("call")) {
			return cmdType.C_CALL;
		}
		else {
			return cmdType.C_ARITHMETIC;
		}	
	}

	public String arg1() {
		String currCmd = arrCmds.get(currLineNum);
		String[] arrCmd = currCmd.split(" ");

		if (this.commandType() == cmdType.C_ARITHMETIC || this.commandType() == cmdType.C_RETURN) {
			return arrCmd[0];
		}
		else if (this.commandType() == cmdType.C_LABEL    || 
				 this.commandType() == cmdType.C_IF	      ||
				 this.commandType() == cmdType.C_GOTO     ||
				 this.commandType() == cmdType.C_FUNCTION ||
				 this.commandType() == cmdType.C_CALL)
		{
			return arrCmd[1];
		}
		else {
			return arrCmd[1];
		}
	}

	public int arg2() {
		String currCmd = arrCmds.get(currLineNum);
		String[] arrCmd = currCmd.split(" ");
		return Integer.parseInt(arrCmd[2]);
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
		for(int i = 0; i < arrCmds.size(); i++) {
			arrCmds.set(i, arrCmds.get(i).trim());
		}
	}
}
