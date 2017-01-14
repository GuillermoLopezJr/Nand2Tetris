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
        else if (arrCmd.length == 1) { 
            return cmdType.C_ARITHMETIC;
        }
        else if (arrCmd[0].equals("function")) {
            return cmdType.C_FUNCTION;
        }
        else {
            //for program flow. just return anything for now.
            return cmdType.C_GOTO;
        }   
    }

    public String arg1()
    {
        String currCmd = arrCmds.get(currLineNum);
        String[] arrCmd = currCmd.split(" ");

        if (this.commandType() == cmdType.C_ARITHMETIC) {
            return arrCmd[0];
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
        for (int i = 0; i < arrCmds.size(); i++) {
            arrCmds.set(i, arrCmds.get(i).trim());
        }
    }
}
