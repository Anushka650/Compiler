import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.io.FileReader;

public class Compiler {
    public static ArrayList<intSymbol> ints;
    public static ArrayList<stringSymbol> strings;

    public static void main(String[] args) {
        // INITIALIZE OBJECT ARRAYLISTS
        ints = new ArrayList<>(); //contains all ints defined
        strings = new ArrayList<>(); //contains all Strings defined
        ArrayList<String> code = new ArrayList<>(); //separates file into lines


        // READ FILE
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/Users/anushka/Downloads/CompileTest V2.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                code.add(line);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        compile(code);


    }
    public static void compile(ArrayList<String> code)
    {
        // read line by line
        for (int lineNumber = 0; lineNumber < code.size(); lineNumber++) {
            String line = code.get(lineNumber);
            // code ending with ;
            if (line.endsWith(";")) {

                // int
                if (line.startsWith("int ")) {
                    String varType = "int ";
                    String varName = null;
                    int assignedValue = 0;

                    // int var;
                    if (!line.contains("=")) {
                        varName = line.substring(varType.length(), line.length() - 1);
                        assignedValue = 0;
                    }
                    else {
                        int equalsIndex = line.indexOf("=");

                        // int var = expression
                        if (!line.contains("system.in")) {
                            varName = line.substring(varType.length(), equalsIndex);
                            assignedValue = evaluateIntegerExpression(line.substring(equalsIndex + 1, line.length() - 1), ints);
                        }

                        // int var = system.in
                        else {
                            Scanner input = new Scanner(System.in);
                            varName = line.substring(varType.length(), equalsIndex);
                            assignedValue = input.nextInt();
                            input.nextLine();
                        }
                    }

                    intSymbol placeholderInt = new intSymbol(varName);
                    placeholderInt.setNumber(assignedValue);
                    ints.add(placeholderInt);

                    if (checkForDuplicateVars(varName, strings, ints)) {
                        System.out.println("error: duplicate int variable " + varName + " declared on line: " + (lineNumber+1));
                        System.exit(0);
                    }
                }

                // String
                else if (line.startsWith("String ")) {
                    String varType = "String ";
                    String varName = null;
                    String assignedValue = null;

                    // String var;
                    if (!line.contains("=")) {
                        varName = line.substring(varType.length(), line.length() - 1);
                        assignedValue = null;
                    }
                    else {
                        int equalsIndex = line.indexOf("=");

                        // String var = expression
                        if (!line.contains("system.in")) {
                            varName = line.substring(varType.length(), equalsIndex);
                            assignedValue = line.substring(equalsIndex + 2, line.length() - 2);
                        }

                        // String var = system.in
                        else {
                            Scanner input = new Scanner(System.in);
                            varName = line.substring(varType.length(), equalsIndex);
                            assignedValue = input.nextLine();
                        }
                    }

                    stringSymbol placeholderString = new stringSymbol(varName);
                    placeholderString.setString(assignedValue);
                    strings.add(placeholderString);

                    if (checkForDuplicateVars(varName, strings, ints)) {
                        System.out.println("error: duplicate String variable " + varName + " declared on line: " + (lineNumber+1));
                        System.exit(0);
                    }
                }

                // print
                else if (line.startsWith("print ")) {
                    int printIndex = line.indexOf("print ");
                    // print "some string"
                    if(line.indexOf("\"") == 6) {
                        System.out.print(line.substring((printIndex) + 7, line.length() - 2));
                    }
                    else {
                        String name = line.substring((printIndex) + 6, line.length() - 1);
                        // print String
                        if (stringSymbolGivenVarNames(name, strings) != null) {
                            stringSymbol temp = stringSymbolGivenVarNames(name, strings);
                            System.out.print(temp.getString());
                        }
                        // print integer
                        else {
                            int val = evaluateIntegerExpression(name, ints);
                            System.out.print(val);
                        }
                    }
                }
                // printNewLine
                else if(line.startsWith("printNewLine")) {
                    System.out.println();
                }
                // var = expression
                else if(line.contains("=")) {
                    int equalIndex = line.indexOf("=");
                    String name = line.substring(0, equalIndex);
                    String expression = line.substring(equalIndex + 1, line.length() - 1);

                    if(checkIfNameExistsForInts(name, ints)){
                        int newVal = evaluateIntegerExpression(expression, ints);
                        intSymbol temp = intSymbolGivenVarNames(name, ints);
                        temp.setNumber(newVal);
                    }
                    else if(checkIfNameExistsForStrings(name, strings)){
                        stringSymbol temp = stringSymbolGivenVarNames(name, strings);
                        temp.setString(expression);
                    }
                    else {
                        System.out.println("error: unknown variable " + name + " on line: " + (lineNumber+1));
                        System.exit(0);
                    }
                }
                // unknown
                else {
                    System.out.println("error: unknown code on line: " + (lineNumber+1));
                    System.exit(0);
                }
            }
            // comment
            else if(line.startsWith("//")){
                // do nothing
            }
            // while
            else if(line.startsWith("while ")) {
                if (line.contains("(") && line.contains(")")) {
                    int parenOne = line.indexOf("(");
                    int parenTwo = line.indexOf(")");
                    int parenCounter = 1;
                    String condition = line.substring(parenOne + 1, parenTwo);
                    ArrayList<String> whileCodeBlock = new ArrayList<>();

                    lineNumber++;
                    while (lineNumber < code.size()) {
                        if (code.get(lineNumber).contains("}")) {
                            parenCounter--;
                        }
                        else if (code.get(lineNumber).contains("{")) {
                            parenCounter++;
                        }
                        if (parenCounter == 0) {
                            break;
                        }
                        whileCodeBlock.add(code.get(lineNumber));
                        lineNumber++;
                    }

                    while (checkCondition(condition)) {
                        compile(whileCodeBlock);
                    }
                }
            }
            // if
            else if(line.startsWith("if ")) {
                if (line.contains("(") && line.contains(")")) {
                    int parenOne = line.indexOf("(");
                    int parenTwo = line.indexOf(")");
                    int parenCounter = 1;
                    String condition = line.substring(parenOne + 1, parenTwo);
                    ArrayList<String> ifCodeBlock = new ArrayList<>();

                    lineNumber++;
                    while (lineNumber < code.size()) {
                        if (code.get(lineNumber).contains("}")) {
                            parenCounter--;
                        }
                        else if (code.get(lineNumber).contains("{")) {
                            parenCounter++;
                        }
                        if (parenCounter == 0) {
                            break;
                        }
                        ifCodeBlock.add(code.get(lineNumber));
                        lineNumber++;
                    }

                    if (checkCondition(condition)) {
                        compile(ifCodeBlock);
                    }
                }
            }
            // unknown
            else {
                System.out.println("error: unknown code on line: " + (lineNumber+1));
                System.exit(0);
            }
        }
    }


    public static boolean checkForDuplicateVars(String name, ArrayList<stringSymbol> strings, ArrayList<intSymbol> ints) {
        boolean check = false;
        int count = 0;
        for (intSymbol i : ints) {
            if (i.getName().equals(name)) {
                count++;
            }
        }


        for (stringSymbol s : strings) {
            if (s.getName().equals(name)) {
                count++;
            }
        }
        if (count >= 2) {
            check = true;
        }
        return check;
    }

    public static boolean checkCondition(String condition){
        boolean isTrue = false;
        if (condition.contains(">")) {
            int operation = condition.indexOf(">");
            String section1 = condition.substring(0, operation);
            String section2 = condition.substring(operation + 1, condition.length());
            if (isInteger(section1, section2)) {
                int a = Integer.parseInt(section1);
                int b = Integer.parseInt(section2);
                if (a > b){
                    isTrue = true;
                }
            }
            else if(isInteger(section1) && !isInteger(section2)){
                int a = Integer.parseInt(section1);
                int b = evaluateIntegerExpression(section2, ints);
                if (a > b){
                    isTrue = true;
                }
            }
            else if(!isInteger(section1) && isInteger(section2)){
                int a = evaluateIntegerExpression(section1, ints);
                int b = Integer.parseInt(section2);
                if (a > b){
                    isTrue = true;
                }
            }
            else {
                int a = evaluateIntegerExpression(section1, ints);
                int b = evaluateIntegerExpression(section2, ints);
                if (a > b){
                    isTrue = true;
                }
            }
        }

        if (condition.contains("<")) {
            int operation = condition.indexOf("<");
            String section1 = condition.substring(0, operation);
            String section2 = condition.substring(operation + 1, condition.length());
            if (isInteger(section1, section2)) {
                int a = Integer.parseInt(section1);
                int b = Integer.parseInt(section2);
                if (a < b){
                    isTrue = true;
                }
            }
            else if(isInteger(section1) && !isInteger(section2)){
                int a = Integer.parseInt(section1);
                int b = evaluateIntegerExpression(section2, ints);
                if (a < b){
                    isTrue = true;
                }
            }
            else if(!isInteger(section1) && isInteger(section2)){
                int a = evaluateIntegerExpression(section1, ints);
                int b = Integer.parseInt(section2);
                if (a < b){
                    isTrue = true;
                }
            }
            else {
                int a = evaluateIntegerExpression(section1, ints);
                int b = evaluateIntegerExpression(section2, ints);
                if (a < b){
                    isTrue = true;
                }
            }
        }

        if (condition.contains("==")) {
            int operation = condition.indexOf("==");
            String section1 = condition.substring(0, operation);
            String section2 = condition.substring(operation + 2, condition.length());
            if (isInteger(section1, section2)) {
                int a = Integer.parseInt(section1);
                int b = Integer.parseInt(section2);
                if (a == b){
                    isTrue = true;
                }
            }
            else if(isInteger(section1) && !isInteger(section2)){
                int a = Integer.parseInt(section1);
                int b = evaluateIntegerExpression(section2, ints);
                if (a == b){
                    isTrue = true;
                }
            }
            else if(!isInteger(section1) && isInteger(section2)){
                int a = evaluateIntegerExpression(section1, ints);
                int b = Integer.parseInt(section2);
                if (a == b){
                    isTrue = true;
                }
            }
            else {
                int a = evaluateIntegerExpression(section1, ints);
                int b = evaluateIntegerExpression(section2, ints);
                if (a == b){
                    isTrue = true;
                }
            }
        }
        if (condition.contains("!=")) {
            int operation = condition.indexOf("!=");
            String section1 = condition.substring(0, operation);
            String section2 = condition.substring(operation + 2, condition.length());
            if (isInteger(section1, section2)) {
                int a = Integer.parseInt(section1);
                int b = Integer.parseInt(section2);
                if (a != b){
                    isTrue = true;
                }
            }
            else if(isInteger(section1) && !isInteger(section2)){
                int a = Integer.parseInt(section1);
                int b = evaluateIntegerExpression(section2, ints);
                if (a != b){
                    isTrue = true;
                }            }
            else if(!isInteger(section1) && isInteger(section2)){
                int a = evaluateIntegerExpression(section1, ints);
                int b = Integer.parseInt(section2);
                if (a != b){
                    isTrue = true;
                }
            }
            else {
                int a = evaluateIntegerExpression(section1, ints);
                int b = evaluateIntegerExpression(section2, ints);
                if (a != b){
                    isTrue = true;
                }
            }
        }

        if (condition.contains(".equals.")) {
            int operation = condition.indexOf(".equals.");
            String section1 = condition.substring(0, operation);
            String section2 = condition.substring(operation + 8, condition.length());
            stringSymbol section1Symbol = stringSymbolGivenVarNames(section1, strings);
            stringSymbol section2Symbol = stringSymbolGivenVarNames(section2, strings);
            if (section1Symbol.getString().equals(section2Symbol.getString())){
                isTrue = true;
            }
        }
        return isTrue;
    }

    public static int evaluateIntegerExpression(String expression, ArrayList<intSymbol> ints ) {
        int integerValue = 0;

        if (expression.contains("+")) {
            int operation = expression.indexOf("+");
            String section1 = expression.substring(0, operation);
            String section2 = expression.substring(operation + 1, expression.length());
            if (isInteger(section1, section2)) {
                int a = Integer.parseInt(section1);
                int b = Integer.parseInt(section2);
                integerValue = a + b;
            }
            else if(isInteger(section1) && !isInteger(section2)){
                int a = Integer.parseInt(section1);
                int b = valueGivenVarNames(section2, ints);
                integerValue = a + b;
            }
            else if(!isInteger(section1) && isInteger(section2)){
                int a = valueGivenVarNames(section1, ints);
                int b = Integer.parseInt(section2);
                integerValue = a + b;
            }
            else {
                int a = valueGivenVarNames(section1, ints);
                int b = valueGivenVarNames(section2, ints);
                integerValue = a + b;
            }
        }

        else if (expression.contains("-")) {
            int operation = expression.indexOf("-");
            String section1 = expression.substring(0, operation);
            String section2 = expression.substring(operation + 1, expression.length());
            if (isInteger(section1, section2)) {
                int a = Integer.parseInt(section1);
                int b = Integer.parseInt(section2);
                integerValue = a - b;
            }
            else if(isInteger(section1) && !isInteger(section2)){
                int a = Integer.parseInt(section1);
                int b = valueGivenVarNames(section2, ints);
                integerValue = a - b;
            }
            else if(!isInteger(section1) && isInteger(section2)){
                int a = valueGivenVarNames(section1, ints);
                int b = Integer.parseInt(section2);
                integerValue = a - b;
            }
            else {
                int a = valueGivenVarNames(section1, ints);
                int b = valueGivenVarNames(section2, ints);
                integerValue = a - b;
            }
        }

        else if (expression.contains("*")) {
            int operation = expression.indexOf("*");
            String section1 = expression.substring(0, operation);
            String section2 = expression.substring(operation + 1, expression.length());
            if (isInteger(section1, section2)) {
                int a = Integer.parseInt(section1);
                int b = Integer.parseInt(section2);
                integerValue = a * b;
            }

            else if(isInteger(section1) && !isInteger(section2)){
                int a = Integer.parseInt(section1);
                int b = valueGivenVarNames(section2, ints);
                integerValue = a * b;
            }
            else if(!isInteger(section1) && isInteger(section2)){
                int a = valueGivenVarNames(section1, ints);
                int b = Integer.parseInt(section2);
                integerValue = a * b;
            }
            else {
                int a = valueGivenVarNames(section1, ints);
                int b = valueGivenVarNames(section2, ints);
                integerValue = a * b;
            }
        }

        else if (expression.contains("/")) {
            int operation = expression.indexOf("/");
            String section1 = expression.substring(0, operation);
            String section2 = expression.substring(operation + 1, expression.length());
            if (isInteger(section1, section2)) {
                int a = Integer.parseInt(section1);
                int b = Integer.parseInt(section2);
                integerValue = a / b;
            } else if(isInteger(section1) && !isInteger(section2)){
                int a = Integer.parseInt(section1);
                int b = valueGivenVarNames(section2, ints);
                integerValue = a / b;
            }
            else if(!isInteger(section1) && isInteger(section2)){
                int a = valueGivenVarNames(section1, ints);
                int b = Integer.parseInt(section2);
                integerValue = a / b;
            }
            else {
                int a = valueGivenVarNames(section1, ints);
                int b = valueGivenVarNames(section2, ints);
                integerValue = a / b;
            }
        }

        else if (expression.contains("%")) {
            int operation = expression.indexOf("%");
            String section1 = expression.substring(0, operation);
            String section2 = expression.substring(operation + 1, expression.length());
            if (isInteger(section1, section2)) {
                int a = Integer.parseInt(section1);
                int b = Integer.parseInt(section2);
                integerValue = a % b;
            }
            else if(isInteger(section1) && !isInteger(section2)){
                int a = Integer.parseInt(section1);
                int b = valueGivenVarNames(section2, ints);
                integerValue = a % b;
            }
            else if(!isInteger(section1) && isInteger(section2)){
                int a = valueGivenVarNames(section1, ints);
                int b = Integer.parseInt(section2);
                integerValue = a % b;
            }
            else {
                int a = valueGivenVarNames(section1, ints);
                int b = valueGivenVarNames(section2, ints);
                integerValue = a % b;
            }
        }

        else if (isInteger(expression)){
            integerValue = Integer.parseInt(expression);
        }

        else {
            integerValue = valueGivenVarNames(expression, ints);
        }

        return integerValue;
    }

    public static boolean isInteger(String section1, String section2) {
        boolean isInteger = true;
        for (int i = 0; i < section1.length(); i++) {
            char a = section1.charAt(i);
            if (!Character.isDigit(a)) {
                isInteger = false;
            }

            for (int j = 0; j < section2.length(); j++) {
                if (!Character.isDigit(a)) {
                    isInteger = false;
                }

            }
        }
        return isInteger;
    }

    public static boolean isInteger(String num) {
        boolean isInteger = true;
        for (int i = 0; i < num.length(); i++) {
            char a = num.charAt(i);
            if (!Character.isDigit(a)) {
                isInteger = false;
            }
        }
        return isInteger;
    }

    public static int valueGivenVarNames(String name, ArrayList<intSymbol> ints) {
        int value = 0;
        boolean found = false;
        for (intSymbol i : ints) {
            if (i.getName().equals(name)) {
                value = i.getNumber();
                found = true;
            }
        }
        if (!found) {
            System.out.println("error: unknown int variable " + name);
            System.exit(0);
        }
        return value;
    }

    public static boolean checkIfNameExistsForStrings(String name, ArrayList<stringSymbol> strings) {
        boolean result = false;
        for (stringSymbol s : strings) {
            if (s.getName().equals(name)) {
                result = true;
                break;
            }
        }
        return result;
    }

    public static boolean checkIfNameExistsForInts(String name, ArrayList<intSymbol> ints) {
        boolean result = false;
        for (intSymbol s : ints) {
            if (s.getName().equals(name)) {
                result = true;
                break;
            }
        }
        return result;
    }

    public static intSymbol intSymbolGivenVarNames(String name, ArrayList<intSymbol> ints) {
        intSymbol value = null;
        for (intSymbol i : ints) {
            if (i.getName().equals(name)) {
                value = i;
            }
        }
        return value;
    }

    public static stringSymbol stringSymbolGivenVarNames(String name, ArrayList<stringSymbol> strings) {
        stringSymbol value = null;
        for (stringSymbol s : strings) {
            if (s.getName().equals(name)) {
                value = s;
            }
        }
        return value;
    }


}
