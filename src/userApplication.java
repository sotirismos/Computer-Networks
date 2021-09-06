



/*
* Computer Networks I
* Java serial communications programming
* Experimental Virtual Lab
* 
* @author Soririos Moschos
*/


import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
	    

	public class userApplication
{

		
	// **COMMUNICATION WITH THE VIRTUAL MODEM 
		public static void main(String[] param)
	{
		String code;
		System.out.println("Enter the request code");
		Scanner in=new Scanner(System.in); // fill in the request code
		code=in.nextLine();
		System.out.println("Your request code is:"+code);
		(new userApplication()).rx(code);
    }

	
		public void rx(String code) 
	   { // call the respective function depending on the letter
		   if (code.indexOf('E')!=-1){
			    echo(code);
		   } else if (code.indexOf('M')!=-1){
			    Image(code);
		   } else if( code.indexOf('G')!=-1){
			    ImageNoise(code);
		   } else if(code.indexOf('P')!=-1){
			    GPS(code);
		   } else if( code.indexOf('Q')!=-1){
			   System.out.println("You choose ARQ test so enter the second request code");
			   Scanner input=new Scanner(System.in);
		       String NACK=input.nextLine();
		       System.out.println("NACK result code:"+NACK);
			    echoNoise(code,NACK);
		   } else{
			   System.out.println("wrong request code,try again");
		   } 
	   }
	
	
	// **ECHO	
		public void echo(String code)
		{
			int k;
			File responseTime=new File("responseTime.txt");
			PrintWriter writer=null;
			
			try {
				writer = new PrintWriter(responseTime);
			}
			catch(Exception x){
				System.out.println(x);
               	}
			
			Modem modem;
			modem=new Modem();
			modem.setSpeed(1000);
			modem.setTimeout(2000);	
			modem.write("atd2310ithaki\r".getBytes());
			
			for(;;){ //check when intro message stops
				k=modem.read();
				if(k!=-1)  {
					System.out.print((char)k);
				}
				if (k==-1) break;
			}

			long finale=420000;
			long begin=System.currentTimeMillis();
			long check=System.currentTimeMillis();
			
			// store packets and save response time for 7 mins
			while (check-begin<finale){
			long S_time=System.currentTimeMillis();
			modem.write((code+"\r").getBytes());
			
			for (;;) {
				try {
					k=modem.read();
					if (k==-1) break;
					System.out.print((char)k);
				}catch (Exception x){
					System.out.println(x);
					break;
				}
			}
			System.out.println();
			long E_time=System.currentTimeMillis();
			long D_time=S_time-E_time;
			writer.print(D_time);
			check=System.currentTimeMillis();
                                }
			writer.close();
			modem.close();
		}

	
	// **IMAGE WITHOUT NOISE
    	public void Image(String code) 	
		{
			int a;
			Modem modem;
			modem=new Modem();
			modem.setSpeed(80000);
			modem.setTimeout(2000);
			
			// Virtual modem in command mode
			modem.write("atd2310ithaki\r".getBytes());
			modem.write((code+"\r").getBytes());
			// Virtual modem in data mode

			DataOutputStream image=null;
			try {
				image=new DataOutputStream(new FileOutputStream("E1.jpeg"));
			}
			catch(Exception x){
               System.out.println(x);
             }
			int a_previous=0;


			for(;;){
				try{
					a=modem.read();
					//System.out.print((char)k);
					if ((a==216)&& (a_previous==255)) //Searching for the first delimeter
					{
						image.writeByte((char)a_previous);
						image.writeByte((char)a);
						break;
					}
				}catch (Exception x) {
					break;
				}
				a_previous=a;
             	}
 
			for (;;) {
				try {
					a=modem.read();
	
					image.writeByte((char)a);
					if ((a==217) && (a_previous==255)){ //Searching for the stop delimeter
						image.close();
	                    break;
                                                     }
                	} catch (Exception x) {
                		break;
                                        }
					a_previous=a;
					System.out.print((char)a);
                 }
			modem.close();
				}
	    
    
    // **IMAGE WITH NOISE
		 public void ImageNoise(String code)
		 {
			 int a;
			 Modem modem;
			 modem=new Modem();
			 modem.setSpeed(80000);
			 modem.setTimeout(2000);
			 // Virtual modem in command mode
			 
			 modem.write("atd2310ithaki\r".getBytes());
			 modem.write((code+"\r").getBytes());
			 // Virtual modem in data mode

			 DataOutputStream image=null;
			 try {
				 image = new DataOutputStream(new FileOutputStream("E2.jpeg"));
			 }
			 catch(Exception x){
				 System.out.println(x);
                              }
			 
               int a_previous=0;


               for(;;){
            	   try{
            		   a=modem.read();
            		   System.out.print((char)a);
            		   if ((a==216)&& (a_previous==255))
            		   {
            			   image.writeByte((char)a_previous);
            			   image.writeByte((char)a);
            			   break;
            		   	}
            	  }catch (Exception x){
            	   		System.out.println(x);
            	   		break;
                                    }
                    	a_previous=a;
               }

               for (;;) {
            	   try {
	                a=modem.read();
                    image.writeByte((char)a);
                    if ((a==217) && (a_previous==255))
                    {
	                image.close();
	                break;
                       }
            	   } catch (Exception x){
            	   System.out.println(x);
            	   
                     break;
                           }
            	   a_previous=a;
            	   System.out.print((char)a);
               	}

        modem.close();
	}
		 
		 
   // **GPS	 
		public void GPS(String code)
		{
			 Modem modem;     
			 modem=new Modem();     
			 modem.setSpeed(80000);     
			 modem.setTimeout(2000);  
			 // Virtual modem in command mode  
			 modem.write("atd2310ithaki\r".getBytes()); 
			 // Virtual modem in data mode 
 
			 DataOutputStream image=null;
			 try {
				 image = new DataOutputStream(new FileOutputStream("M1.jpeg"));
			 }
			 catch(Exception x){
				 System.out.println(x);
			 }
 
			 int k=0; 
			 int k_previous=0;
			 int a=0;
			 int a_previous=0;
			 ArrayList <Character> matrixA=new ArrayList <Character>();
			 StringBuilder result = new StringBuilder(matrixA.size());
			 String matrixB;
			 String[] parts_B;
			 int B=0;
			 int C=0;
			 String str=null;
			 int i;
			 int j=0;
 
			 modem.write((code+"R=1000060\r").getBytes());
			 for (;;) {//read data from ithaki , store data in arraylist if we receive 'G' right after '$' 
				 try {
					 k=modem.read();
					 System.out.print((char)k);
                      	if (((char)k=='G') && ((char)k_previous=='$') )
                      	{	
                             matrixA.add((char)k_previous);
                             matrixA.add((char)k);
                             break;
                        }

                   }catch (Exception x){
                	   System.out.print(x);
                       break;
                   }
				 k_previous=k;
			 }
			 for(;;){ 
		           try { 
			           k=modem.read();
				
				       if (k==-1) break;
				       System.out.print((char)k);
				       matrixA.add((char)k);
					
				                   
				       }catch(Exception x){
		           		System.out.print(x);
			          
		                 break;  
			           }      	 
			  }	
			 
			 //convert arraylist matrixA into string matrixB
			for (Character c : matrixA) 
			{
		      result.append(c);
		    }
		      matrixB = result.toString();
		     
		      parts_B = matrixB.split("\\$");
				
		      int t;
		      for (t=1;t<=51;t=t+10)
				{
					System.out.print(parts_B[t]);
				} 
					
		  //for 6 echos(per 10 sec) store into String str the degrees of the coordinates
		  for (i=1;i<=51;i=i+10)
		  {
			  try{
				  //convert mins into mins and secs
		           B=Integer.parseInt(parts_B[i].substring(22,26));
				   B=B*60;
				   B=(B/10000);
				   C=Integer.parseInt(parts_B[i].substring(35,39));
				   C=C*60;
				   C=(C/10000);			   
	             }catch (NumberFormatException nfe)
	             {
		           System.out.println("NumberFormatException: " + nfe.getMessage());
	              }
				  
				  
			 if (j==0){
				  str=parts_B[i].substring(30,34);
				  
			  j=1;
			  }else {   str=str+parts_B[i].substring(30,34);   }
		    
			str=str+Integer.toString(C);
			str=str+parts_B[i].substring(17,21);
			str=str+Integer.toString(B);
			//System.out.print("str="+str); 
		  }
	
		  modem.write(((code+"T="+str.substring(0,12)+"T="+str.substring(12,24)+"T="+str.substring(24,36)+"T="+str.substring(36,48)+"T="+str.substring(48,60)+"T="+str.substring(60,72)+"\r")).getBytes());
		  //procedure of image making (searching for start and stop delimeter)
		  for(;;){
	           try{
	              a=modem.read();
		          
		           if ((a==216)&& (a_previous==255))
		            {
			          image.writeByte((char)a_previous);
			          image.writeByte((char)a);
			          break;
		             }
	             }catch (Exception x) {
	            	 System.out.print(x);
                     break;
                   }
                     a_previous=a;
                 }
 
       for (;;) {
                try {
	                 a=modem.read();
                     image.writeByte((char)a);
                     if ((a==217) && (a_previous==255))
                     {
	                  image.close();
	                  break;
                   }
                }catch (Exception x){
                	System.out.print(x);
                    break;
                  }
  a_previous=a;
                   
                }
  
        modem.close();
	}
		 
		
   // **ECHOS WITH ERRORS	
		public void echoNoise(String code,String NACK)
		 {
			int k;
			File responseTimeerrors=new File("responseTimeerrors.txt");
			PrintWriter writer=null;
			try {
				writer = new PrintWriter (responseTimeerrors);
				}
				catch(Exception x){
					System.out.println(x);
				}
			Modem modem;
			modem=new Modem();
			modem.setSpeed(1000);
			modem.setTimeout(2000);
		
			// Virtual modem in command mode
			modem.write("atd2310ithaki\r".getBytes());
			for(;;)
				{
					k=modem.read();
					if (k!=-1){
						System.out.print((char)k);
							   }
					if (k==-1) break;
							   }
							
			// Virtual modem in data mode
			long finale=420000;
			long begin=System.currentTimeMillis();
			long check=System.currentTimeMillis();
							
			char[] msg=new char[58];
			int result=0;
			String str;
			int fcs=0;
							
			int i;
			int j;
			char k_previous='a';
			char k1='P';
			char k2='S';
			char kk;
			long S_time=System.currentTimeMillis();
			long E_time=0;
			long D_time=0;
			modem.write((code+"\r").getBytes());
							
			while (check-begin<finale){
				i=0;
			    //read the data and store them into matrix msg.
			for (;;) {
				  try {
					   k=modem.read();
					   kk=(char)k;
					   System.out.print(kk);
					   if ((kk==k2) && (k_previous==k1))
					   {
						   	msg[0]=k1;
							msg[1]=k2;
							i=2;
							break;
					   }
					}catch(Exception x){
						break;
					}
					k_previous=kk;
			 }
							
			for (;;) {
		        try {
				      k=modem.read();
				      if (k==-1) break;
				      msg[i]=(char)k;
					  System.out.print(msg[i]);
					  i++;
					}catch(Exception x){
					System.out.println(x);
					break;
					}
			}
			System.out.println();
			//xor for successive chars of the packet         
			result=msg[31]^msg[32];
			for(j=33;j<=46;j++){
				result=result^msg[j];
				}
				//System.out.print(result);
				//System.out.print("\n");
							
				//convert the elements of msg into string 
				//convert the fcs char
				str=new String(msg);
				try{
					fcs=Integer.parseInt(str.substring(49,52));
					}catch (NumberFormatException nfe)
				{
					System.out.println("NumberFormatException: " + nfe.getMessage());
				}
				//compare result with fcs 
					 if (result==fcs){
						 E_time=System.currentTimeMillis();
						 D_time=S_time-E_time;
						 writer.print(D_time);
									
						 S_time=System.currentTimeMillis();
						 modem.write((code+"\r").getBytes());
						 check=System.currentTimeMillis();
						}else 
						{
						check=System.currentTimeMillis();
						modem.write((NACK+"\r").getBytes());
						}			
				}
				
				writer.close();
				modem.close();
		 }
}



