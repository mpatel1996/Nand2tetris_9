/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mpjv1
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Assembler {
	public static  int counter=0;
	public static int nextRam = 16;
	public static String compT,destT,jumpT; // temp's
	public static FileWriter fw;
	public static BufferedWriter  bw;
    
        public static void main(String[] args) {
	
		String name = args[0].substring(0, args[0].indexOf('.'));		
		String outFileName = name+".hack";  //out file name
		
		SymbolTable st = new SymbolTable(); //init's symbol table		
		Code ct = new Code();  //init's code tables		
		Parser myParser = new Parser(args[0]);  //new parser object
	
		File out = new File(outFileName);  //output name.hack file
		
		try {
                    fw = new FileWriter(out.getAbsoluteFile());
		} catch (IOException e) {
                    e.printStackTrace();
		}
                
		bw = new BufferedWriter(fw); // Ready to write on file
	
		//first pass
		while(myParser.hasMoreCommand()) {  
		if(myParser.commandType()== Parser.commandType.L_COMMAND) { 
			
                    st.addEntry(myParser.symbol(),Integer.toString(counter)) ; //adds new symbol to symbol table
		}
		else {
                    counter++; //next line
                }
		
		myParser.advance();  // next command
		
		}
		myParser.lineCount =0;   // resets counter for starts from first line
		
		//second pass
		while(myParser.hasMoreCommand())
		{
                    if(myParser.commandType()== Parser.commandType.A_COMMAND) //@xxx
                    {
                        if(myParser.strFileArr[myParser.lineCount].startsWith("@"))
                        {
                            String tmp  = myParser.symbol(); //returns xxx
                            if(myParser.isNum(tmp))  //checks if xxx is number
                            {
                                int xxx = Integer.parseInt(tmp);
                                tmp = Parser.dexToBin(xxx);	// return bin value of xxx
                                tmp = myParser.addZero(tmp);
                                try {
                                        bw.write(tmp + '\n');//write to hack
                                } catch (IOException e) {
                                        e.printStackTrace();
                                }

                            }
                            else  //if not number
                            {
                                if(!st.containKey(tmp))  // not exists in Symbol Table
                                {
                                    st.addEntry(tmp,Integer.toString(nextRam));  //Adds to Symbol Table
                                    nextRam++;
                                }
                                if(st.containKey(tmp)) // already exists in Symbol Table
                                {
                                    String tmp2 = st.getValue(tmp);
                                    int xxx = Integer.parseInt(tmp2);
                                    tmp2 = Parser.dexToBin(xxx);
                                    tmp2 = myParser.addZero(tmp2);
                                    try {
                                            bw.write(tmp2+'\n');  //write to hack
                                    } catch (IOException e) {
                                            e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }//if command type A_COMMAND 
			if(myParser.commandType()== Parser.commandType.C_COMMAND)
			{
                            if(myParser.strFileArr[myParser.lineCount].contains("="))//dest=comp
                            {

                                destT = ct.getDest(myParser.dest());
                                compT = ct.getComp(myParser.comp());
                                jumpT = ct.getJump("NULL");  //no need jump
                                try {
                                        bw.write("111" + compT + destT + jumpT +'\n');
                                } catch (IOException e) {
                                        e.printStackTrace();
                                }
                            }
                            else if(myParser.strFileArr[myParser.lineCount].contains(";")) //jump
                            {
                                destT = ct.getDest("NULL"); // no need dest
                                compT = ct.getComp(myParser.comp());
                                jumpT = ct.getJump(myParser.jump());

                                try {
                                        bw.write("111" + compT + destT + jumpT +'\n');
                                } catch (IOException e) {
                                        e.printStackTrace();
                                }
                            }
			}//if command type C_COMMAND 
			myParser.advance();		
		}//end while
		
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }//main

}//end class
