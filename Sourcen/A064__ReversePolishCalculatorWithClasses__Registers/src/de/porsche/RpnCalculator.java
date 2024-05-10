package de.porsche;

import java.util.*;


interface Operator {

    void apply( Stack<Integer> stack );
}

interface Comparator {

    void apply( Stack<Integer> stack );

}

interface Command {

    int run( String[] tokens, int tokenPtr, Stack<Integer> stack );
    /*
    The int returned by the run method is the new tokenPtr.
    The reason for that is, that loops (While, For, Do) require
    processing of the same tokens multiple times. Once the loop
    is done, the tokenPtr needs to be set to the next token after
    the end of the loop.

    There is also special processing required for If statements
    (Then and Else part).
     */
}

class StackHandling {
    
    static boolean tosIsFalse(Stack<Integer> stack){
        return stack.pop() == 0;
    }

    static boolean tosIsTrue(Stack<Integer> stack){
        return ! tosIsFalse( stack );
    }

}


class  CMD {
    static final String IF = "IF";
    final static String ELSE = "ELSE";
    final static String THEN = "THEN";
    final static String PRINT = ".";
    final static String DUP_PRINT = "D.";
    final static String DUP = "DUP";
    final static String DROP = "DROP";
    final static String PRINT_TEXT = ".\"";
    final static String CR = "CR";
    final static String SPACE = "SP";
    final static String TAB = "TB";
    final static String DO = "DO";
    final static String LOOP = "LOOP";
    final static String RUNINDEX_ACTUAL_LOOP = "I";
    final static String RUNINDEX_EARLIER_LOOP = "I-";
    final static String STORE_REGISTER = "STO-";
    final static String RECALL_REGISTER = "RCL-";
    final static String ADD_TO_REGISTER = "ADDTO-";
    final static String SUBSTRACT_FROM_REGISTER = "SUBSTRACTFROM-";
    final static String CLEAR_AND_DELETE__REGISTER = "CLEAR-";

}
public class RpnCalculator {

    private final Stack<Integer> stack;
    
    private SettingsAndAttributes saa = SettingsAndAttributes.getInstance();

    public RpnCalculator() {
        stack = new Stack<>();
    }

    public int evaluate(String expression) throws Exception {
        String[] tokens = expression.split("\\s+");
        int tokenPtr = 0;
        while (tokenPtr < tokens.length) {
            String token = tokens[ tokenPtr ].toUpperCase();
            if (isOperator(token)) {
                Operator operator = getOperator(token);
                operator.apply( stack );
                tokenPtr++;
            } else if (isComparator(token)) {
                Comparator comp = getComparator(token);
                comp.apply( stack );
                tokenPtr++;
            } else if( isCmd( token ) ) {
                tokenPtr = processCmdToken(token, tokenPtr, tokens);
            } else {
                int number = Integer.parseInt(token);
                stack.push(number);
                tokenPtr++;
            }
        }


        // if (stack.size() != 1) {
        if (stack.isEmpty()) {
            throw new Exception("Invalid RPN expression");
        }


        return stack.pop();
    }

    private int processCmdToken(String token, int tokenPtr, String[] tokens) throws Exception {
        // Arraysize does not matter. As long as there are array elements we are in an IF statement
        if( ! saa.ifNesting.isEmpty() ) {
            // THEN ends the if statement
            if (token.equals("THEN")) {
                // Just continue the program
                tokenPtr++;
                saa.ifNesting.remove(saa.ifNesting.size() - 1 );
            } else if (token.equals("ELSE")) {
                // ELSE part can only show up when we need to process the IF part.
                // If the IF part would have been not processed, then the IFCMD had
                // already skipped to the ELSE part

                // Therefore read until THEN
                // as this part needs to be stopped.
                while (!token.equalsIgnoreCase("THEN")) {
                    tokenPtr++;
                    token = tokens[tokenPtr];
                }
            } else {
                tokenPtr = runCommand(tokens, tokenPtr, stack);
            }
        } else {
            tokenPtr = runCommand(tokens, tokenPtr, stack);
        }
        return tokenPtr;
    }

    private int runCommand(String[] tokens, int tokenPtr, Stack<Integer> stack) throws Exception {

        String token = tokens[tokenPtr];
        List<Command> cmds = getCommand(token);
        for (Command cmd : cmds) {
            tokenPtr = cmd.run(tokens, tokenPtr, stack);
        }

        return ++tokenPtr;
    }

    private boolean isCmd(String inpToken) {
        String token = inpToken.trim().toUpperCase();
        return token.equals(CMD.IF)
                || token.equals(CMD.ELSE)
                || token.equals(CMD.THEN)
                || token.equals(CMD.PRINT)
                || token.equals(CMD.DUP_PRINT)
                || token.equals(CMD.DUP)
                || token.equals(CMD.DROP)
                || token.equals(CMD.PRINT_TEXT)
                || token.equals(CMD.CR)
                || token.equals(CMD.SPACE)
                || token.equals(CMD.TAB)
                || token.equals(CMD.DO)
                || token.equals(CMD.LOOP)
                || token.equals(CMD.RUNINDEX_ACTUAL_LOOP)
                || token.startsWith(CMD.RUNINDEX_EARLIER_LOOP)
                || token.startsWith(CMD.STORE_REGISTER)
                || token.startsWith(CMD.RECALL_REGISTER)
                || token.startsWith(CMD.ADD_TO_REGISTER)
                || token.startsWith(CMD.SUBSTRACT_FROM_REGISTER)
                || token.startsWith(CMD.CLEAR_AND_DELETE__REGISTER)
                ;
    }

    private boolean isOperator(String inpToken) {
        String token = inpToken.toUpperCase();
        return  token.equals("+")
                || token.equals("-")
                || token.equals("*")
                || token.equals("/")
                || token.equals("SQR")
                ;
    }

    private boolean isComparator(String inpToken) {
        String token = inpToken.toUpperCase();
        return  token.equals(">")
                || token.equals("<")
                || token.equals(">=")
                || token.equals("<=")
                || token.equals("=")
                || token.equals("==")
                ;
    }

    private Operator getOperator(String token) throws Exception {
        return switch (token.toUpperCase()) {
            case "+" -> new AdditionOperator();
            case "-" -> new SubtractionOperator();
            case "*" -> new MultiplicationOperator();
            case "/" -> new DivisionOperator();
            case "SQR" -> new SqrOperator();
            default -> throw new Exception("Invalid operator: " + token);
        };

        /*  switch (token) {
                case "+":   return new AdditionOperator();
                case "-":   return new SubtractionOperator();
                case "*":   return new MultiplicationOperator();
                case "/":   return new DivisionOperator();
                default:    throw new Exception("Invalid operator: " + token);
            }
         */
    }

    private List<Command> getCommand(String inpToken)  throws Exception {
        List<Command> cmds = new ArrayList<>();

        String token = inpToken.trim().toUpperCase();
        String token4switch = token;

        if( token.startsWith(CMD.RUNINDEX_EARLIER_LOOP))        { token4switch = CMD.RUNINDEX_EARLIER_LOOP; }
        if( token.startsWith(CMD.STORE_REGISTER))               { token4switch = CMD.STORE_REGISTER; }
        if( token.startsWith(CMD.RECALL_REGISTER))              { token4switch = CMD.RECALL_REGISTER; }
        if( token.startsWith(CMD.ADD_TO_REGISTER))              { token4switch = CMD.ADD_TO_REGISTER; }
        if( token.startsWith(CMD.SUBSTRACT_FROM_REGISTER))      { token4switch = CMD.SUBSTRACT_FROM_REGISTER; }
        if( token.startsWith(CMD.CLEAR_AND_DELETE__REGISTER))   { token4switch = CMD.CLEAR_AND_DELETE__REGISTER; }

        switch (token4switch) {
            case CMD.IF->                           {   cmds.add( new CmdIF()           ); }
            case CMD.PRINT ->                       {   cmds.add( new CmdPrint()        ); }
            case CMD.DUP_PRINT ->                   {   cmds.add( new CmdDup()          );
                                                        cmds.add( new CmdPrint()        ); }
            case CMD.DUP->                          {   cmds.add( new CmdDup()          ); }
            case CMD.DROP->                         {   cmds.add( new CmdDrop()         ); }
            case CMD.PRINT_TEXT ->                  {   cmds.add( new CmdPrtTxt()       ); }
            case CMD.CR ->                          {   cmds.add( new CmdNewLine()      ); }
            case CMD.SPACE ->                       {   cmds.add( new CmdSpace()      ); }
            case CMD.TAB ->                         {   cmds.add( new CmdTab()      ); }
            case CMD.DO ->                          {   cmds.add( new CmdDo()           ); }
            case CMD.LOOP ->                        {   cmds.add( new CmdLoop()         ); }
            case CMD.RUNINDEX_ACTUAL_LOOP ->        {   cmds.add( new CmdLoopIndex()    ); }
            case CMD.RUNINDEX_EARLIER_LOOP ->       {   cmds.add( new CmdLoopIndex(Integer.parseInt(token.substring(2)))); }
            case CMD.STORE_REGISTER ->              {   cmds.add( new CmdRegisterHandling(CMD.STORE_REGISTER,   token.substring(4).toUpperCase() ) ); }
            case CMD.RECALL_REGISTER ->             {   cmds.add( new CmdRegisterHandling(CMD.RECALL_REGISTER,  token.substring(4).toUpperCase() ) ); }
            case CMD.ADD_TO_REGISTER ->             {   cmds.add( new CmdRegisterHandling(CMD.ADD_TO_REGISTER,   token.substring(6).toUpperCase() ) ); }
            case CMD.SUBSTRACT_FROM_REGISTER ->     {   cmds.add( new CmdRegisterHandling(CMD.SUBSTRACT_FROM_REGISTER, token.substring(14).toUpperCase() ) ); }
            case CMD.CLEAR_AND_DELETE__REGISTER ->  {   cmds.add( new CmdRegisterHandling(CMD.CLEAR_AND_DELETE__REGISTER,   token.substring(6).toUpperCase() ) ); }
            default -> throw new Exception("Invalid command: " + token);
        };
        return cmds;
    }

    private Comparator getComparator(String token)  throws Exception {
        return switch (token.toUpperCase()) {
            case ">" -> new CompGT();
            case "<" -> new CompLT();
            case ">=" -> new CompGTE();
            case "<=" -> new CompLTE();
            case "=" -> new CompEQ();
            case "==" -> new CompEQ();
            default -> throw new Exception("Invalid command: " + token);
        };
    }

}

class AdditionOperator implements Operator {

    @Override
    public void apply( Stack<Integer> stack ) {

        int operand2 = stack.pop();
        int operand1 = stack.pop();

        int result = operand1 + operand2;
        stack.push(result);
    }
}

class SubtractionOperator implements Operator {

    @Override
    public void apply( Stack<Integer> stack ) {

        int operand2 = stack.pop();
        int operand1 = stack.pop();

        int result = operand1 - operand2;
        stack.push(result);
    }
}

class MultiplicationOperator implements Operator {

    @Override
    public void apply( Stack<Integer> stack ) {

        int operand2 = stack.pop();
        int operand1 = stack.pop();

        int result = operand1 * operand2;
        stack.push(result);
    }
}

class DivisionOperator implements Operator {

    @Override
    public void apply( Stack<Integer> stack ) {

        int operand2 = stack.pop();
        int operand1 = stack.pop();

        int result = operand1 / operand2;
        stack.push(result);
    }
}

class SqrOperator implements Operator {

    @Override
    public void apply( Stack<Integer> stack ) {

        int operand1 = stack.pop();

        int result = operand1 * operand1;
        stack.push(result);
    }
}

class CompGT implements Comparator {

    @Override
    public void apply( Stack<Integer> stack ) {

        int operand2 = stack.pop();
        int operand1 = stack.pop();

        boolean result = operand1 > operand2;
        stack.push(result?1:0);
    }
}

class CompLT implements Comparator {

    @Override
    public void apply( Stack<Integer> stack ) {

        int operand2 = stack.pop();
        int operand1 = stack.pop();

        boolean result = operand1 < operand2;
        stack.push(result?1:0);
    }
}

class CompGTE implements Comparator {

    @Override
    public void apply( Stack<Integer> stack ) {

        int operand2 = stack.pop();
        int operand1 = stack.pop();

        boolean result = operand1 >= operand2;
        stack.push(result?1:0);
    }
}

class CompLTE implements Comparator {

    @Override
    public void apply( Stack<Integer> stack ) {

        int operand2 = stack.pop();
        int operand1 = stack.pop();

        boolean result = operand1 <= operand2;
        stack.push(result?1:0);
    }
}

class CompEQ implements Comparator {

    @Override
    public void apply( Stack<Integer> stack ) {

        int operand2 = stack.pop();
        int operand1 = stack.pop();

        boolean result = operand1 == operand2;
        stack.push(result?1:0);
    }
}

class CmdIF extends StackHandling implements Command {
    @Override
    public int run(String[] tokens, int tokenPtr, Stack<Integer> stack) {

        SettingsAndAttributes saa = SettingsAndAttributes.getInstance();

        int retVal = -1;

        saa.ifNesting.add(true);

        if( tosIsTrue( stack ) ) {
            // just continue
            // ++tokenPtr;
        } else {
            /* Must be FALSE
               Got to skip until Else or Then part starts
               Since Then ends the if statement we can continue after that.
             */
            while( ! tokens[tokenPtr].equalsIgnoreCase("ELSE")
                    &&   ! tokens[tokenPtr].equalsIgnoreCase("THEN") ) {
                ++tokenPtr;
            }

            // Easy when we reached the then part-> if statement is finished,
            // nothing else to do
            if( tokens[tokenPtr].equalsIgnoreCase("ELSE") ) {
                // Nothing to do      ++tokenPtr;
            }
        }

        retVal = tokenPtr;
        return retVal;
    }
}

class CmdDo extends StackHandling implements Command {
    @Override
    public int run(String[] tokens, int tokenPtr, Stack<Integer> stack) {
        SettingsAndAttributes saa = SettingsAndAttributes.getInstance();

        int index = stack.pop();
        int limit = stack.pop();
        DoLoopData dld = new DoLoopData(tokenPtr, index, limit);
        saa.doLoopNesting.add(dld);
        return tokenPtr;
    }
}

class CmdLoop extends StackHandling implements Command {
    @Override
    public int run(String[] tokens, int tokenPtr, Stack<Integer> stack) {
        SettingsAndAttributes saa = SettingsAndAttributes.getInstance();

        DoLoopData dld = saa.doLoopNesting.get(saa.doLoopNesting.size() - 1);
        dld.index++;
        if ( dld.index < dld.limit ) {
            tokenPtr = dld.doTokenPtr;
        } else {
            // end of loop
            saa.doLoopNesting.remove(saa.doLoopNesting.size() - 1);
        }
        return tokenPtr;
    }
}

class CmdLoopIndex extends StackHandling implements Command {
    int whichLoop = 0;
    public CmdLoopIndex() {
        whichLoop = 0;
    }

    public CmdLoopIndex(int whichLoop) {
        this.whichLoop = whichLoop;
    }

    @Override
    public int run(String[] tokens, int tokenPtr, Stack<Integer> stack) {
        SettingsAndAttributes saa = SettingsAndAttributes.getInstance();

        DoLoopData dld = saa.doLoopNesting.get(saa.doLoopNesting.size() - whichLoop - 1);
        stack.push(dld.index);
        return tokenPtr;
    }
}

class CmdPrint extends StackHandling implements Command {
    @Override
    public int run(String[] tokens, int tokenPtr, Stack<Integer> stack) {
        Integer val = stack.pop();
        System.out.printf( "%d", val );
        return tokenPtr;
    }
}

class CmdPrtTxt extends StackHandling implements Command {
    @Override
    public int run(String[] tokens, int tokenPtr, Stack<Integer> stack) {
        boolean firstToken = true;
        String token = "";

        ++tokenPtr;
        token = tokens[tokenPtr];
        while( ! token.equalsIgnoreCase("\"") ) {
            if( !firstToken ) { System.out.printf(" %s", token ); }
            else              { System.out.printf("%s", token ); }
            ++tokenPtr;
            token = tokens[tokenPtr];
        }
        return tokenPtr;
    }
}

class CmdNewLine extends StackHandling implements Command {
    @Override
    public int run(String[] tokens, int tokenPtr, Stack<Integer> stack) {
        System.out.println();
        return tokenPtr;
    }
}

class CmdSpace extends StackHandling implements Command {
    @Override
    public int run(String[] tokens, int tokenPtr, Stack<Integer> stack) {
        System.out.printf(" ");
        return tokenPtr;
    }
}

class CmdTab extends StackHandling implements Command {
    @Override
    public int run(String[] tokens, int tokenPtr, Stack<Integer> stack) {
        System.out.printf("\t");
        return tokenPtr;
    }
}

class CmdDup extends StackHandling implements Command {
    @Override
    public int run(String[] tokens, int tokenPtr, Stack<Integer> stack) {
        Integer val = stack.pop();
        stack.push( val );
        stack.push( val );
        return tokenPtr;
    }
}

class CmdDrop extends StackHandling implements Command {
    @Override
    public int run(String[] tokens, int tokenPtr, Stack<Integer> stack) {
        stack.pop();
        return tokenPtr;
    }
}

class CmdRegisterHandling extends StackHandling implements Command {

    String cmd;
    String rName;
    public CmdRegisterHandling(String cmd, String rName) {
        this.cmd = cmd;
        this.rName = rName;
    }

    @Override
    public int run(String[] tokens, int tokenPtr, Stack<Integer> stack) {
        SettingsAndAttributes saa = SettingsAndAttributes.getInstance();
        HashMap<String, Integer> regs = saa.Registers;

        switch( cmd ) {
            case CMD.STORE_REGISTER ->              { regs.put(rName, stack.pop());  }
            case CMD.RECALL_REGISTER ->             { stack.push(regs.getOrDefault(rName, 0));  }
            case CMD.ADD_TO_REGISTER ->             { regs.put(rName, regs.getOrDefault(rName, 0) + stack.pop()); }
            case CMD.SUBSTRACT_FROM_REGISTER ->     { regs.put(rName, regs.getOrDefault(rName, 0) - stack.pop()); }
            case CMD.CLEAR_AND_DELETE__REGISTER ->  { regs.remove(rName); }
            default -> cmd = cmd; //"should never ever happen!"
        }
        return tokenPtr;
    }
}



class SettingsAndAttributes{
    private static SettingsAndAttributes saa = new SettingsAndAttributes();

    private SettingsAndAttributes(){}

    public static SettingsAndAttributes getInstance() {
        return saa;
    }

    //boolean ifStm_active = false;

    ArrayList<Boolean> ifNesting = new ArrayList<Boolean>();

    ArrayList<DoLoopData> doLoopNesting = new ArrayList<>();

    HashMap<String, Integer> Registers = new HashMap<>();


}

class DoLoopData{
    public DoLoopData() {
    }

    int doTokenPtr = -1;

    public DoLoopData(int doTokenPtr, int index, int limit) {
        this.doTokenPtr = doTokenPtr;
        this.index = index;
        this.limit = limit;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    int index = -1;
    int limit = -1;

    public int getDoTokenPtr() {
        return doTokenPtr;
    }

    public void setDoTokenPtr(int doTokenPtr) {
        this.doTokenPtr = doTokenPtr;
    }
}

