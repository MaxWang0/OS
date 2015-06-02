
import java.util.concurrent.Semaphore;
import java.util.ArrayList;
import java.util.Random;

/*
 * Project2.java
 *
 * Demonstrate use of semaphores for synchronization.
 *
 * YU WANG, 2015, University of Texas at Dallas 
 * adapted from example in C.
 *
 */



class customer implements Runnable
{
   
   private int num;            // customer id 
   private int service_needed; //- service desired.
   private static Semaphore max_capacity; //max capacity of postoffice
   private static Semaphore service_counter; // available service counter corresponding to each postal worker
   private static Semaphore cust_ready;   // status of customer
   private ArrayList<Semaphore> service_finished; // status of service finished
   private static Semaphore leave_service_counter; // status of leave_service_counter
   private static Semaphore mutex_inner; // for checking available postal worker
   private static Semaphore mutex_outer; // for creating critcal section for transfering customer id to postal worker thread 
   private static Semaphore mutex_greet;
   private static Semaphore scales; // the status of scales in use

   customer( int num, 
             Semaphore max_capacity,
             Semaphore service_counter,
             Semaphore cust_ready,
             ArrayList<Semaphore> service_finished,
             Semaphore leave_service_counter,
             Semaphore mutex_inner,
             Semaphore mutex_outer,
             Semaphore mutex_greet,
             Semaphore scales )
   {
      this.num = num;
      this.max_capacity = max_capacity;
      this.service_counter = service_counter;
      this.cust_ready = cust_ready;
      this.service_finished = service_finished;
      this.leave_service_counter = leave_service_counter;
      this.mutex_inner = mutex_inner;
      this.mutex_outer = mutex_outer;
      this.mutex_greet = mutex_greet;
      this.scales = scales;
      

      // randomly assigned service to each customer, 1 for buying stamp, 2 for mailing letter 3 for mailing package 
      Random generator = new Random();
      this.service_needed = generator.nextInt(3) + 1; 

      System.out.println( "Customer " + num + " created." );
   }

   public void run()
   {
      try
      {
         max_capacity.acquire();   // limit the number of customer entering postoffice to 10
      }catch (InterruptedException e){}
      
         System.out.println( "Customer " + num + " enters post office" );

      try
      {
         service_counter.acquire(); // limit the number of customer accepting service to 3
      }catch (InterruptedException e){}
      
      
      try
      {
         mutex_outer.acquire(); // start the critical section to transfer the customer id to postal worker thread
      }catch (InterruptedException e){}

      try
      {
         mutex_inner.acquire(); // checking the available postal workers
      }catch (InterruptedException e){}
 
      // both set the global cust_id and cust_service 
      Project2.cust_id = this.num; 
      Project2.cust_service = this.service_needed;

      cust_ready.release(); // customer is ready to take service

      mutex_inner.release(); 

      try
      {
         mutex_greet.acquire(); // wait till the custom id and cust_service transfer finished by the postal worker thread
      }catch (InterruptedException e){}

      mutex_outer.release(); // release the semaphore for the next customer id transfer

      try
      {
           service_finished.get(this.num).acquire(); // wait the signal for service_finish from postal worker thread
      }catch (InterruptedException e){}

      System.out.println( "Customer " + num + " " + Project2.serviceDisplay(2, this.service_needed) + "." ); // notify the finished service by printing out
      
      leave_service_counter.release(); //release the leave_service_counter signal

      System.out.println( "Customer " + num + " leaves post office." );
 
      max_capacity.release(); // increase the max_capacity semaphore for next customer entering
             
   }
}

class postal_worker implements Runnable
{
   private int num;
   private int customer_id;
   private int customer_service;
   private static Semaphore max_capacity;
   private static Semaphore service_counter;
   private static Semaphore cust_ready;
   private ArrayList<Semaphore> service_finished;
   private static Semaphore leave_service_counter;
   private static Semaphore mutex_inner;
   private static Semaphore mutex_greet;
   private static Semaphore scales;

   postal_worker( int num,
             Semaphore max_capacity,
             Semaphore service_counter,
             Semaphore cust_ready,
             ArrayList<Semaphore> service_finished,
             Semaphore leave_service_counter,
             Semaphore mutex_inner,
             Semaphore mutex_greet,
             Semaphore scales )
   {
      this.num = num;
      this.max_capacity = max_capacity;
      this.service_counter = service_counter;
      this.cust_ready = cust_ready;
      this.service_finished = service_finished;
      this.leave_service_counter = leave_service_counter;
      this.mutex_inner = mutex_inner;
      this.mutex_greet = mutex_greet;
      this.scales = scales;

      System.out.println( "Postal worker " + num + " created." );
   }

   public void run()
   {
      while(true)
      {
         try
         {
            cust_ready.acquire(); // start the thread once any customer is ready
         }catch (InterruptedException e){}
         
         try
         {
            mutex_inner.acquire(); // checking if there is any postal worker is available, if yes, continue, otherwise block the thread until semaphore release
         }catch (InterruptedException e){}

         //transfer the global cust_id and cust_service to this thread as the custom served 

         this.customer_id = Project2.cust_id;   
         this.customer_service = Project2.cust_service;

         //System.out.println( "Postal worker " + num + " greets customer "+ this.customer_id +" and begins working." );
         
         System.out.println( "Postal worker " + num + " serving customer "+ this.customer_id +"." );

         mutex_greet.release(); // notify the customer that the transfer is finished
  
         mutex_inner.release(); // release the semaphore to set the postal worker available

         System.out.println( "Customer " + this.customer_id + " asks postal worker " + num + " " + Project2.serviceDisplay(1, this.customer_service) + ".");

         if(Project2.serviceDisplay(1, this.customer_service).equals("to send a package") )    // if ther servicd ordered is mailing package then set the global scales_in_use to 1 and record the number of postal worker possessing the scales  
         {
             if(Project2.scales_in_use == 1)
             {
                 System.out.println("Scales in use by postal worker " + Project2.pws + ".");
             }

             try {
                     scales.acquire();
             }catch(InterruptedException e){}

             Project2.scales_in_use = 1;
             Project2.pws = num;

             try {
                    Thread.sleep(Project2.serviceSleep(this.customer_service));
             }catch(InterruptedException e){}

             System.out.println( "Postal worker " + num + " finished serving customer " + this.customer_id + ".");
             Project2.scales_in_use = 0; // when finished mailing package set the global scales back to 0 labled as unused
             scales.release(); //release the semaphore for the next package mail
             System.out.println( "Scales released by postal worker " + num);
         }else{

         try {
                Thread.sleep(Project2.serviceSleep(this.customer_service));
         }catch(InterruptedException e){}
         
         System.out.println( "Postal worker " + num + " finished serving customer " + this.customer_id + ".");
         }


               service_finished.get(this.customer_id).release(); //release the service_finished semaphore notifying customer thread 

         try
         {
            leave_service_counter.acquire(); // release the service_counter semaphore notifying the customer thread
         }catch (InterruptedException e){}

         service_counter.release(); // release the service_counter for next customer visit
        }
    }

} 
   
   

public class Project2
{
   private int num;
   public static int cust_id;  //global customer id for transfering from customer thread to postal worker thread
   public static int cust_service; //global service for transfering from customer thread to postal worker thread
   public static int scales_in_use = 0; //status of scales
   public static int pws; // postal worker id for possessing the scales 


   public static void main(String args[])
   {
      int i=0;
      final int NUMCUSTOMERS = 50; //number of customers
      final int NUMPOSTALWORKERS = 3; // number of postal workers
      final int NUMMAXCAPACITY = 10;
      
      System.out.println( "Simulating Post Office with 50 customers and 3 postal workers" );
     
      Semaphore max_capacity = new Semaphore( NUMMAXCAPACITY, true ); // initiation of max_capacity
      Semaphore service_counter = new Semaphore( NUMPOSTALWORKERS, true ); // initiation of service_counter
      Semaphore cust_ready = new Semaphore( 0, true); // initiation of cust_ready
      ArrayList<Semaphore> service_finished = new ArrayList<Semaphore>(); // set service_finish semphore customer specific
      for(int s=0; s<NUMCUSTOMERS; s++){
              service_finished.add(s, new Semaphore(0,true));
      }
      Semaphore leave_service_counter = new Semaphore( 0 , true); // initiation of leave_service_counter
      Semaphore mutex_inner = new Semaphore( 3 , true); // label for availability of postal worker 
      Semaphore mutex_outer = new Semaphore( 1 , true); // lable for critical section tranferring global cust_id to postal worker thread  
      Semaphore mutex_greet = new Semaphore( 0 , true); // setting to grantee global data tranfer without interferering by other thread
      Semaphore scales = new Semaphore( 1 , true); // set to create critical section for scales


      customer cThr[] = new customer[NUMCUSTOMERS];
      Thread customerThreads[] = new Thread[NUMCUSTOMERS];

      postal_worker pwThr[] = new postal_worker[NUMPOSTALWORKERS];
      Thread postalWorkerThreads[] = new Thread[NUMPOSTALWORKERS];

      // run postal worker thread

      for( i = 0; i < NUMPOSTALWORKERS; ++i )
      { 
         pwThr[i] = new postal_worker(i,
                                     max_capacity,
                                     service_counter,
                                     cust_ready,
                                     service_finished,
                                     leave_service_counter,
                                     mutex_inner,
                                     mutex_greet,
                                     scales );
         postalWorkerThreads[i] = new Thread( pwThr[i] );
         postalWorkerThreads[i].start();
      }

      // run customer thread

      for( i = 0; i < NUMCUSTOMERS; ++i )
      {
         cThr[i] = new customer(i,
                                max_capacity,
                                service_counter,
                                cust_ready,
                                service_finished,
                                leave_service_counter,
                                mutex_inner,
                                mutex_outer,
                                mutex_greet,
                                scales );
         customerThreads[i] = new Thread( cThr[i] );
         customerThreads[i].start();
      }
      

      // run thread join      

      for( i = 0; i < NUMCUSTOMERS; ++i ) 
      {
         try
         {
            customerThreads[i].join();
            System.out.println("Joined customer "+i+".");
         }catch (InterruptedException e){}
      }

      // when finish for loop of thread join then terminate the process and exit
      System.exit(0);	  

   }
   
   // set the function to print out the information according to different service orders

    public static String serviceDisplay(int u, int s)
   {
      if(u == 0){ // customer
                     switch(s)
                     {
                             case 1: return "to buy stamps";
         
                             case 2: return "to mail a letter";
 
                             case 3: return "to mail a package";
                     }
          }else if(u == 1){ // postal worker
          
           switch(s)
           {
                   case 1: return "to buy stamps";

                   case 2: return "to send a letter";

                   case 3: return "to send a package";
           }
       }else{ //customer finish action
           
        switch(s)
        {
                case 1: return "finished buying stamps";

                case 2: return "finished mailing a letter";

                case 3: return "finished mailing a package";
        }
       }   

          return "broken";
    }

    //define the function to set the sleep time based on different ordered service

    public static int serviceSleep(int s)
    {
       switch(s)
       {
          case 1: return 60000 + 60000%60000; // This is buying stamps.

          case 2: return 90000 + 90000%60000; // This is mailing a letter.

          case 3: return 120000 + 120000%60000; // This is mailing a package.
       }

          return 10000;
    } 
}





