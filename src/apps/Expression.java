package apps;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	String expr;                
	ArrayList<ScalarSymbol> scalars;   
	ArrayList<ArraySymbol> arrays;
	
	Stack ss = new Stack(); //stackStore
	Stack temp = new Stack();
	
	ArrayList<String> sep = new ArrayList<String>();
	int index = 0;
	
    
    public static final String delims = " \t*+-/()[]";
    
    public Expression(String expr) {
        this.expr = expr;
    }
    
    public void buildSymbols(){
    	StringTokenizer str= new StringTokenizer(expr);
    	String token;
    	int index;
    	arrays = new ArrayList<ArraySymbol>();
    	scalars = new ArrayList<ScalarSymbol>();
    	boolean repeatArray;
    	boolean repeatScalar;

    	while(str.hasMoreTokens()){
    		repeatArray = false;
    		repeatScalar = false;
    		token = str.nextToken(delims);
    		char isNum = token.charAt(0);
    		if (isNum == '1' || isNum == '2' || isNum == '3' || isNum == '4' || isNum == '5' || isNum == '6' || isNum == '7' || isNum == '8' || isNum == '9'
    				|| isNum == '9' || isNum == '0'){
    		}
    		else{
	    		if(expr.indexOf(token) + token.length() == expr.length())
	    			index = expr.indexOf(token) + token.length() - 1;
	    		else
	    			index = expr.indexOf(token) + token.length();
	    		
	    		// checking if there is repeat & adding to arrayList
	    		if (expr.charAt(index) == '['){
		    		for (int cnt = 0; cnt < arrays.size(); cnt++){
		    			if (token.equals(arrays.get(cnt).name))
		    				repeatArray = true;
		    		}
		    		if (!repeatArray)
		    			arrays.add(new ArraySymbol(token));
	    		}
	    		else{
		    		for (int cnt = 0; cnt < scalars.size(); cnt++){
		    			if (token.equals(scalars.get(cnt).name))
		    				repeatScalar = true;
		    		}
		    		if (!repeatScalar)
		    			scalars.add(new ScalarSymbol(token));
	    		}
    		}
    	}
    }
    
    public void loadSymbolValues(Scanner sc) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String sym = st.nextToken();
            ScalarSymbol ssymbol = new ScalarSymbol(sym);
            ArraySymbol asymbol = new ArraySymbol(sym);
            int ssi = scalars.indexOf(ssymbol);
            int asi = arrays.indexOf(asymbol);
            if (ssi == -1 && asi == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                scalars.get(ssi).value = num;
            } else { // array symbol
            	asymbol = arrays.get(asi);
            	asymbol.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    String tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    asymbol.values[index] = val;              
                }
            }
        }
    }
    
    
    
    // Convert string array to number for arrays
    private float strToNumArr(String str, int pos){
    	for (int cnt = 0; cnt < arrays.size(); cnt++){
    		if (arrays.get(cnt).name.equals(str))
    			return arrays.get(cnt).values[pos];
    	}
    	return 0;
    }
    
    // Convert string variable to number for scalars
    private float strToNumSc(String str){
    	char isNum = str.charAt(0);
    	if (isNum == '1' || isNum == '2' || isNum == '3' || isNum == '4' || isNum == '5' || isNum == '6' || isNum == '7' || isNum == '8' || isNum == '9')
    		return Integer.parseInt(str);
    	for (int cnt = 0; cnt < scalars.size(); cnt++){
    		if (scalars.get(cnt).name.equals(str))
    			return scalars.get(cnt).value;
    	}
    	return 0;
    }
    
    private float eval(){
    	float var1;
    	float var2;
    	String oper;
    	if (ss.size() == 1){
    		return (float)ss.pop();
    	}
    	while (ss.peek() != "("){
    		if (index + 1 == sep.size() && ss.size() == 1){
    			temp.push((float)ss.pop());
    			break;
    		}
    		var1 = (float)ss.pop();
    		oper = (String)ss.pop();
    		if (oper.equals("[")){
    			temp.push(var1);
    			//if (temp.size() == 0)
    			//	temp.push(var1);
    			break;
    		}
    		if (oper.equals("(")){
    			temp.push(var1);
    			break;
    		}
    		if (oper.equals("*")){
    			ss.push(var1 * (float)ss.pop());
    		}
    		else if (oper.equals("/")){
    			var2 = (float)ss.pop();
    			ss.push(var2 / var1);
    		}
    		else{
    			temp.push(var1);
    			temp.push(oper);
    		}
    		if (ss.size() == 1){
    			temp.push((float)ss.pop());
    			break;
    		}
    	}
    	while (temp.size() > 1){
    		var1 = (float)temp.pop();
    		oper = (String)temp.pop();
    		var2 = (float)temp.pop();
    		if (oper.equals("+")){
    			temp.push(var1 + var2);
    		}
    		else{
    			temp.push(var1 - var2);
    		}
    	}
    	return (float)temp.pop();
    }
    
    private void seperation(){
    	String token = "";
		String token2 = "";
		expr = expr.replaceAll(" ", "");
		StringTokenizer st = new StringTokenizer(expr);
		StringTokenizer first = new StringTokenizer(expr);
		StringTokenizer second = new StringTokenizer(expr);
		boolean stillSpace = true;
		int counter = 0;

		if (first.nextToken(" \t*+-/()[]").length() == expr.length()){
			sep.add(expr);
		}
		else{
			while (stillSpace){
				if (expr.charAt(counter) == ' '){
					counter++;
				}
				else{
					stillSpace = false;
					if (!(expr.charAt(counter) == '(')){
						sep.add(st.nextToken(" \t*+-/()[]"));
					}
				}
			}
			while (st.hasMoreTokens()){
				token = st.nextToken("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
				token2 = st.nextToken(" \t*+-/()[]");
				char num = token2.charAt(0);
				if (num == '1' || num == '2' || num == '3' || num == '4' || num == '5' || num == '6' || num == '7' || num == '8' || num == '9'){
					for (int cnt = 0; cnt < token.length(); cnt++){
						if (token.charAt(cnt) != ' '){
							sep.add(token.charAt(cnt) + "");
						}
					}
					sep.add(token2);
				}
				else if (!(Character.isLetter(token.charAt(0))) && token.length() > 1){
					for (int cnt = 0; cnt < token.length(); cnt++){
						if (token.charAt(cnt) != ' '){
							sep.add(token.charAt(cnt) + "");
						}
					}
					sep.add(token2);
				}
				else{
					sep.add(token);
					sep.add(token2);
				}
			}
			if (!((expr.lastIndexOf(token2) + token2.length()) == expr.length())){
				token2 = st.nextToken("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
				for (int cnt = 0; cnt < token2.length(); cnt++){
					if (token2.charAt(cnt) != ' '){
						sep.add(token2.charAt(cnt) + "");
					}
				}
			}
		}
		sep.add("");
    }
    
    
    
    /**
     * Evaluates the expression, using RECURSION to evaluate subexpressions and to evaluate array 
     * subscript expressions.
     * 
     * @return Result of evaluation
     */
    float answer = 0;
    String arrayName = "";
    public float evaluate() {
    	if (index == 0){
    		seperation();
    	}
    	while (sep.get(index) != ""){
    		if(sep.get(index).equals("(")){
    			ss.push(sep.get(index));
    			index++;
    			ss.push(evaluate());
    		}
    		else if(sep.get(index).equals(")")){
    			index++;
    			return eval();
    		}
    		else if(sep.get(index).equals("[")){
    			ss.pop();
    			ss.push(sep.get(index));
    			index++;
    			ss.push(strToNumArr(arrayName,(int)evaluate()));
    		}
    		else if(sep.get(index).equals("]")){
    			index++;
    			return eval();
    		}
    		else if(sep.get(index).equals("+") || sep.get(index).equals("-") || sep.get(index).equals("*") || sep.get(index).equals("/")){
    			ss.push(sep.get(index));
    			index++;
    		}
    		else{
    			if (!sep.get(index + 1).equals("[")){
    				ss.push(strToNumSc(sep.get(index)));
    			}
    			else{
    				ss.push(sep.get(index));
    				arrayName = sep.get(index);
    			}
	    		index++;
    		}
    	}
    	return eval();
    }

    public void printScalars() {
        for (ScalarSymbol ss: scalars) {
            System.out.println(ss);
        }
    }
    
    public void printArrays() {
    		for (ArraySymbol as: arrays) {
    			System.out.println(as);
    		}
    }

}
