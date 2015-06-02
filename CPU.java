
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.Runtime;
import java.lang.*;
/**
 *
 * @author Yu Wang
 */
public class CPU {
    
    public int IR=0,SP=1000,PC=0,AC=0,X=0,Y=0;
	public int[] sp_returnaddr = new int[2000]; //saved PC in user mode stack
    private int Timer=0,timeout;
    private int Cycle=0;   //Control the instruction process stage. When cycle=0,it means ready for execute instrucion, otherwise it's in a processing cycle. 
	private int TimerCycle = 0;
    public int Cache=-1;   //temporary store for addr & data, when it's ready for use, it should be -1, can't preserve
    private int Save_Load=0; //indicate do what to memo, 0 is do nothing, 1 is load, 2 is store. 
    private boolean kernel=false;
    private boolean Terminate=false;
    private int interrupt_type=0;   //0 is no interrupt, 1 is int, 2 is time out
    Random rand= new Random(100);
	static String filename = null;    
    
    
    
    private void initial(String args[]){
		File inFile = null;
		if (0 < args.length) {

			filename = args[0];

			timeout = Integer.parseInt(args[1]);


			inFile = new File(args[0]);
		}else{
			System.err.println("Invalid arguments count:" + args.length);
		}



	}
	
	private void Instruction_Controller(){
         switch(IR){
             //Load Value
            case 1 : 
                if(Cycle==0)
                {    PC++;
                     Cycle++;
                     Cache= PC;    ///Set PC's addr as data's addr
                     Save_Load=1;  ///Load Data from PC+1
                     break;}
                else{
                     PC++;
                     AC=Cache;  //Save data
					 //System.out.println(AC);
                     Cycle=0;  //reset the cycle
                     Save_Load=0;
                     Timer++;
                     break;  //Meanwhile the value is already in cache
                }
///////////////////////////////////////////////////////////////////////////////   
             //Load Value from addr
            case 2:
                if(Cycle==0){
                    PC++;
                    Cycle++;
                    Cache=PC;  ///Set PC's addr as data's addr
                    Save_Load=1;
                    break;}
                else if(Cycle==1){
                    if(Cache > 999 && kernel == false){
						System.out.println("Memory violation: " + Cache);
						Cycle = 0;
						Save_Load = 0;
						PC++;
						Timer++;
						break;
					}
					else{
						Cycle++; //Data in the cache as the address, load data from that addr and go to next cycle
						break;
                    }
                }
                else{   //cycle 2
                    AC=Cache;   //
                    Cycle=0;
                    Save_Load=0;
                    PC++;
                    Timer++;
                    break;
                }
///////////////////////////////////////////////////////// ///////////////////
            //Load the value from the address found in the address into the AC (JESUS! HARD TO READ!)
            case 3:
                if(Cycle==0){
                    PC++;        //Point to instr data
                    Cycle++;
                    Cache=PC;
                    Save_Load=1;
                    break;
                }
                else if(Cycle==3){
                    AC=Cache;
                    Cycle=0;
                    Save_Load=0;
                    PC++;
                    Timer++;
                    break;
                }
                else {      //Cycle=1,2
                    Cycle++;
                    break;
                }
///////////////////////////////////////////////////////////////////////////////   
             //LoadIdxX addr   
            case 4:
                if(Cycle==0){
                    PC++;        //Point to instr data
                    Cycle++;
                    Cache=PC;
                    Save_Load=1;
                    break;
                }
                else if(Cycle==1){
                    Cache=ALU(1,Cache,X);      //Addr+X
                    Cycle++;
                    break;
                }
                else{
                    AC=Cache;
                    Cycle=0;
                    Save_Load=0;
                    PC++;
                    Timer++;
                    break;
                }
////////////////////////////////////////////////////////////////////////////
            //LoadIdxY addr       
            case 5:
                if(Cycle==0){
                    PC++;        //Point to instr data
                    Cycle++;
                    Cache=PC;
                    Save_Load=1;
                    break;
                }
                else if(Cycle==1){
                    Cache=ALU(1,Cache,Y);      //Addr+Y
                    Cycle++;
                    break;
                }
                else{
                    AC=Cache;
                    Cycle=0;
                    Save_Load=0;
                    PC++;
                    Timer++;
                    break;
                }
////////////////////////////////////////////////////////////////////////////
           //LoadSpX
            case 6:
			    if(Cycle==0){
                Cache=ALU(1,SP,X);     //Sp+X=address
				Save_Load=1;
                Cycle++;
                break;
				}
				else if(Cycle==1){
					AC = Cache;
					Cycle=0;
					Save_Load=0;
					PC++;
					Timer++;
					break;
				}

////////////////////////////////////////////////////////////////////////////                
            case 7:
                if(Cycle==0){
                    PC++;        //Point to instr data
                    Cycle++;
                    Cache=PC;
                    Save_Load=1;
                    break;
                }
                else if(Cycle==1){
                    Save_Load=2;  //Change to Store
                    Cycle++;    //////Cache has the addr
					break;
                }
                else{           //Save complete
                    Cycle=0;
                    Save_Load=0;
                    PC++;
                    Timer++;
                    break;
                }
//////////////////////////////////////////////////////////////////////////// 
            //Get
            case 8: 
                AC=rand.nextInt(101);
                while(AC==0){
                    AC=rand.nextInt(101); 
                }
                PC++;
                Timer++;
                break;
/////////////////////////////////////////////////////////////////////////////
            case 9:
                if(Cycle==0){
                    PC++;        //Point to instr data
                    Cycle++;
                    Cache=PC;
                    Save_Load=1;
                    break;
                }
                else{
                    IOScreen(Cache);
                    Cycle=0;
                    Save_Load=0;
                    PC++;
                    Timer++;
                    break;
                }
////////////////////////////////////////////////////////////////////////
                
             //Add X
            case 10: 
                AC=ALU(1,AC,X);
                PC++;
                Timer++;
                break;
                
             //Add Y   
            case 11: 
                AC=ALU(1,AC,Y);
                PC++;
                Timer++;
                break;
             //Sub X
            case 12: 
                AC=ALU(2,AC,X);
                PC++;
                Timer++;
                break;
             //Sub Y
            case 13: 
                AC=ALU(2,AC,Y);
                PC++;
                Timer++;
                break;
             //Copy to X   
            case 14: 
                X=AC;
                PC++;
                Timer++;
                break;
            //copy from X
            case 15: 
                AC=X;
                PC++;
                Timer++;
                break;
            //Copy to Y
            case 16: 
                Y=AC;
                PC++;
                Timer++;
                break;
            //Copy from Y
            case 17: 
                AC=Y;
                PC++;
                Timer++;
                break;
            //copy to sp
            case 18: 
                SP=AC;
                PC++;
                Timer++;
                break;
            //Copy from sp
            case 19: 
                AC=SP;
                PC++;
                Timer++;
                break;
////////////////////////////////////////////////////////////////////////                
             //Jump to address.
            case 20: 
                if(Cycle==0){
                    PC++;
                    Cycle++;
                    Cache=PC;  ///Set PC's addr as data's addr
                    Save_Load=1;
                    break;}
                else {
                    PC=Cache;   //Jump to the addr fetched as data
                    Cycle=0;
                    Save_Load=0;
                    Timer++;
                    break;
                }
////////////////////////////////////////////////////////////////////////  
             //JumpIfEqual addr   
            case 21:
                if(Cycle==0){   
                    if(AC!=0){     //AC=/=0 do next instruc.
                        PC+=2;     //jump to next instrcution
                        break;
                    }
                    else{
                        PC++;
                        Cycle++;
                        Cache=PC;  ///Set PC's addr as data's addr
                        Save_Load=1;
                        break;
                    }
                }
                else{
                    PC=Cache;  //
                    Cycle=0;
                    Save_Load=0;
                    Timer++;
                    break;
                }
////////////////////////////////////////////////////////////////////////  
             //JumpIfNotEqual addr   
            case 22:
                if(Cycle==0){   
                    if(AC==0){     //AC=0 do next instruc.
                        PC=ALU(1,PC,2);     //jump to next instrcution
                        break;
                    }
                    else{
                        PC++;
                        Cycle++;
                        Cache=PC;  ///Set PC's addr as data's addr
                        Save_Load=1;
                        break;
                    }
                }
                else{
                    PC=Cache;  //
                    Cycle=0;
                    Save_Load=0;
                    Timer++;
                    break;
                }
 ////////////////////////////////////////////////////////////////////////
            //Call addr
            case 23:
                if(Cycle==0){
                    PC++;        //Point to instr data
                    Cycle++;
                    Cache=PC;
                    Save_Load=1;
                    break;
                }
                else if(Cycle==1){
                    SP--;
					sp_returnaddr[SP] = PC;
					PC=AC;    //Since PC will be discard, it can be a temporary cache.
                    AC=Cache; //The data need to be stored
					Cache=SP;  //Where the data stored
                    Save_Load=2; //Set to store
                    Cycle++;
                    break;
                }
                else{ 
                    Cache=AC;  //Swap AC & PC by cache (Since data in cache is useless)
                    AC=PC;     //AC restored
                    PC=Cache;  //PC has the addr to jump
                    Cycle=0;
                    Save_Load=0;
                    Timer++;
                    break;
                }
/////////////////////////////////////////////////////////////////////////
            //Ret           
            case 24:
                if(Cycle==0){
					PC = sp_returnaddr[SP];
					sp_returnaddr[SP] = 0;
					PC++;
                    SP++;     //SP point to the last element in stack
					Timer++;
                    break;
                }
/////////////////////////////////////////////////////////////////////////
                //IncX
            case 25:
                X=ALU(1,X,1);
                PC++;
                Timer++;
                break;
/////////////////////////////////////////////////////////////////////////
                //DecX
            case 26:
                X=ALU(2,X,1);
                PC++;
                Timer++;
                break;
 ///////////////////////////////////////////////////////////////////////////
                //Push
            case 27:
                if(Cycle==0){
                    SP--;
					Cache=SP;        //Point to instr data
                    Cycle++;
                    Save_Load=2;
					
					break;
                }
                else{
                    //SP--;
                    Cycle=0;
                    Save_Load=0;
					sp_returnaddr[SP] = PC;
                    PC++;
                    Timer++;
                    break;
                }
///////////////////////////////////////////////////////////////////////////// 
                //Pop
            case 28:
                if(Cycle==0){
                    Cache=SP;      //point to the top of stack
                    Save_Load=1;
                    Cycle++;
                    break;
                }
                else{
					sp_returnaddr[SP] = 0;
					SP++;
                    AC=Cache;  //Save the data to AC
                    Cycle=0;
                    Save_Load=0;
                    PC++;
                    Timer++;
                    break;                 
                }
//////////////////////////////////////////////////////////////////////////
                //int
            case 29://Save sequence: PC SP
                if(Cycle==0){
                    if(!kernel){    //Need save PC first
                        kernel=true;
                        AC=PC;
                        Cache=1999;  //Save PC to bottom of sys stack
                        Save_Load=2;
                        Cycle++;
                    }
                    else{
                        //if in kernel mode, ignore syscall;
                        PC++;
                    }
                    break;
                }
                else if(Cycle==1){
					SP--;
					//sp_returnaddr[SP] = AC;
                    AC=SP;//Need save SP then
                    Cache=1998;
                    Cycle++;
					break;
                }
                else if(Cycle==2){
                    PC=1500; //Jump to handler
                    SP=1998;  //Stack point to sys stack
                    Cycle=0;
                    Save_Load=0;
                    interrupt_type=1;  //Set a flag
                    break;      //The handler will deal the rest register
                }
//////////////////////////////////////////////////////////////////////////
                //Iret
                //this command should be use when X Y AC are already restored
            case 30:
                if(interrupt_type==0){
                    //no interrupt ignore;
                    PC++;
                }
                //if it's int
                else if(interrupt_type==1){
                      if(Cycle==0){   
                          Cache=1998;   //where sp is
                          Save_Load=1;
                          Cycle++;
					  break;
                  }
                  else if(Cycle==1){
                      SP=Cache;   //restore SP
                      Cache=1999; //where PC is
                      Cycle++;
                      break;
                  }
                  else if(Cycle==2){
                      PC=Cache;   //restore PC
                      Cycle=0;
                      Save_Load=0;
                      kernel=false;
                      interrupt_type=0;
                      PC++;
					  break;
                  }
                }
                
                //if it's time out
                else if(interrupt_type==2){
					if(Cycle==0){   
                          Cache=1998;   //where sp is
                          Save_Load=1;
                          Cycle++;
                          break;
                  }
                  else if(Cycle==1){
                      SP=Cache;   //restore SP
                      Cache=1999; //where PC is
                      Cycle++;
                      break;
                  }
                  else if(Cycle==2){
                      PC=Cache;  //Restore PC
                      Cache=1997; //Where AC is
                      Cycle++;
                      break;  
                }
                else if(Cycle==3){
                    AC=Cache;   //restore AC
                    Cache=1996; //Where X is
                    Cycle++;
                    break;
                }
                else if(Cycle==4){
                    X=Cache;  //restore X
                    Cache=1995;  //Where Y is
                    Cycle++;
                    break;
                }
                else{
                    Y=Cache; //restore Y
                    Cycle=0;
                    Save_Load=0;
                    kernel=false;
                    interrupt_type=0;
					Timer = 0;
					break;
                }
				}
                              
                
//////////////////////////////////////////////////////////////////////////
                //End
            case 50:
                Terminate=true;
                Cycle=0;
                PC=0;
                Save_Load=0;
                AC=0;X=0;Y=0;IR=0;
                Cache=0;
                SP=999;
                kernel=false;
                Timer=0;
                interrupt_type=0;
                break;
               
           }
                
    }
    
     private void CheckTime() {//Save sequence PC SP AC X Y
		if(Timer>=timeout){
            if(TimerCycle==0){
				IR = 0;
                if(kernel){
                    Timer=0;    //If during kernel mode, ignore timer interrupt
                }
                else{    //Save PC
                    kernel=true;
                    Cache=AC;//Swap PC and AC
                    AC=PC;
                    PC=Cache;//PC has AC, AC has PC, next store current AC's valve to stack
                    Cache=1999;  //Save PC to bottom of sys stack
                    Save_Load=2;
                    Cycle = 1;
					TimerCycle++;
                }   
            }
            else if (TimerCycle==1){//Save SP
                AC=SP;//Need save SP then
                Cache=1998;
                Cycle = 2;
				Save_Load=2;
				TimerCycle++;
            }
            else if(TimerCycle==2){ //Save AC
                    AC=PC; //restore AC
                    Cache=1997;
                    Cycle = 3;
					Save_Load=2;
					TimerCycle++;
                }
            else if(TimerCycle==3){
                    AC=X;
                    Cache=1996;
                    Cycle = 4;
					Save_Load=2;
					TimerCycle++;
            }
            else if(TimerCycle==4){
                   AC=Y;
                   Cache=1995;
                   Cycle = 5;
				   Save_Load=2;
				   TimerCycle++;
            }
            else{
                SP=1995;
                PC=1000;
                Cycle=0;
				TimerCycle = 0;
                Save_Load=0;
                interrupt_type=2;
                Timer=0;
            }
            
            
            
        }
    }
    
    private int ALU(int CaseNumber,int a, int b){
        switch(CaseNumber){
            case 1: return a+b;
            case 2: return a-b;
            default: return 00000;    
        }
    }
    
    
    private void IOScreen(int i){
        if(i==1)
		{
			System.out.print(AC);
        }else{

			System.out.print((char)AC);
        }
            
        
    
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
                
        CPU demo = new CPU();
        String arrs[],temp=null;
        String file=null;

		demo.initial(args);

        
        Runtime rt = Runtime.getRuntime();
        try {
            
            Process proc = rt.exec("java Memory");
            
            //outStream & input Stream to send/recive data from memory
            InputStream is = proc.getInputStream();
            OutputStream os =proc.getOutputStream();
            //Write to memory
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(os));
			//Read from memory
			
			pw.println(filename);   //Initial the memory
			 pw.flush();

			Scanner sc = new Scanner(is);

			
			
            
            //Memory Access Controller
            while(!demo.Terminate){
                int Save_Load=demo.Save_Load;
                int Cache=demo.Cache;
                int AC=demo.AC;
				int PC = demo.PC;
                String result;
                int Cycle=demo.Cycle;

                
                //if it's begin to fetch instruction from memory
                
                 demo.CheckTime();   //check if it's timeout      
                if(demo.Cycle==0){        
  ///instruction fetch part.        
                //See if it's in user mode
                    if(!demo.kernel && demo.PC>999){   //if so, than PC can't exceed 999
                    System.out.println("Memory Error occured: Access Violation n");
                    }
                    else{        
                        pw.println(1+" "+demo.PC);
                        pw.flush();
                    
                        result= sc.nextLine();
						
						if(result.endsWith(".0")){
							float intermediate = Float.parseFloat(result);
							demo.IR= (int)intermediate;

						}
                       }
                }
  /////data fetch part              
                //if it's access data from memory
                else {
             //Don't access memory
                    if(demo.Save_Load==0){
                    //Do nothing to the memory
                    }
            //Load from the memory
                    else if(demo.Save_Load==1){
                        pw.println(1+" "+Cache);
                        pw.flush();
                        
                        result=sc.nextLine();
						if(result.endsWith(".0")){
							float intermediate = Float.parseFloat(result);
							demo.Cache= (int)intermediate;
						}
                    }

            //Write to the memory
                    else if(demo.Save_Load==2){
						pw.println(2+" "+demo.Cache+" "+demo.AC);
                        pw.flush();

                    }
                    else{
                        System.out.println("Memory Error Occured");
                    }
                }
                
                demo.Instruction_Controller();   //run instruction
            }
            
            proc.destroy();
            proc.waitFor();
            int exitVal = proc.exitValue();
            System.out.println("No more instruction \n" + "Process exited: " + exitVal);
            System.exit(0);
                     
        } catch (IOException ex) {
            Logger.getLogger(CPU.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(CPU.class.getName()).log(Level.SEVERE, null, ex);
        }
  
    }

   
}
