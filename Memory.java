import java.io.*;

import java.util.Scanner;
import java.lang.*;

/**
 * @author Yu Wang
 *
 */

public class Memory	
{
	
	private float[] memory = new float[2000];

	String filename = null;
	
	private void initial(String args){

		File inFile = null;
		//if (0 < args.length) {
			inFile = new File(args);
			filename = args;
		//} 
		//else {
		//	System.err.println("Invalid arguments count:" + args.length);
		//}

		BufferedReader br = null;

		try{

			int index =-1;
			
			String addr[];

			String temp;


			String sCurrentLine;

			br = new BufferedReader(new FileReader(inFile));


			while ((sCurrentLine = br.readLine()) != null) {

				
				String [] tokens = sCurrentLine.split("   ");

				//System.out.println(tokens[0]);
				
				
				if(sCurrentLine.length() != 0 && tokens[0].length() != 0 ){
					float instruction = Float.parseFloat(tokens[0]);

					if (sCurrentLine.startsWith(".")){
						//System.out.println(instruction);
						index = (int)(instruction * 10000);
						index --;
						//System.out.println(index);
					}else{

					//System.out.println(instruction);
					index++;
					write(index, instruction);
					}
				}



			}
		}

		catch (IOException e) {
			e.printStackTrace();
		}

	}

	private float read(int addr){

		return memory[addr];
	}
	

	private void write(int addr,float data){

		memory[addr] = data;

	}
	
	
	public static void main(String args[])throws Exception
	{

		Scanner sc = new Scanner(System.in);

		String temp = null;
		String name = null;
		if (sc.hasNext())
			name = sc.nextLine();
		String arrs[];
		int addr;
		float data;

		Memory m = new Memory();
		
		m.initial(name);


		while(true){
			if (sc.hasNext()){
				temp=sc.nextLine();
				arrs=temp.split(" ");  //One spcae
				if(arrs[0].equals("1")){      //read
					addr=Integer.valueOf(arrs[1]);
					data=m.read(addr);
					System.out.println(data);
					}
					else if(arrs[0].equals("2")){ //write
						addr=Integer.valueOf(arrs[1]);
						data=Float.valueOf(arrs[2]);
						m.write(addr, data);
						}
						}

		}


	}
}
