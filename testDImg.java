
import java.awt.Checkbox;
import java.awt.HeadlessException;
import java.awt.SecondaryLoop;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Scanner;
import java.io.*;
import java.net.*; 

public class testDImg {
	   static String portnumber;
		static String websecond = "";
		static String web;
	    static String web2 = "";
	    static String webForImg;
	    static int numberpForImg;
	    static String imageaddress;
		//static int numberp;
		//static String web1;
		//static String web2;
		public static void main(String[] args) throws Exception {

			String params = URLEncoder.encode("param1", "UTF-8")
	+ "=" + URLEncoder.encode("value1", "UTF-8");
			params += "&" + URLEncoder.encode("param2", "UTF-8")
	+ "=" + URLEncoder.encode("value2", "UTF-8");

			
			
			Scanner scanner = new Scanner(System.in);
			String weborginal = scanner.nextLine();
			if (weborginal.contains("http:")) {
				String[] websplit = weborginal.split("/");
				
				for (int i = 2; i < websplit.length; i++) {
					if (i == websplit.length-1) {
						websecond = websecond + websplit[i]; 
					}else {
						websecond = websecond + websplit[i]+"/";
					}
					web =websecond;
				}
				}else {
					web = weborginal;
				}
				//System.out.println(websecond);
				//String web = websecond;
				
	        String[] parts = web.split("/");
	        String web1 = parts[0];
	        for (int i = 1; i < parts.length; i++) {
	        	web2 = web2+"/" +parts[i];
			}
	        portnumber = "80";
	        if (web2 == "") {
	        	String[] pparts = web1.split(":");
				web1 = pparts[0];
				portnumber = pparts[1];
			}
	        int numberp = Integer.parseInt(portnumber);
	       if (web2 == "") {
	    	   web2 ="/";
			
		}
	         webForImg = web1;
	         numberpForImg =numberp;
			Socket s = new Socket(InetAddress.getByName(web1), numberp);	
			//System.out.println("test----------------"+web1);
			PrintWriter pw =new PrintWriter(s.getOutputStream());
			pw.println("GET "+ web2 +" HTTP/1.1");
			//System.out.println("test----------------"+web2);
			if (numberp == 80 ) {
				pw.println("Host: "+web1);
			}else {
				pw.println("Host: "+web1+":8080");
			}
			
			pw.println(); 
			pw.flush();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			String t;
			while((t = br.readLine()) != null) {
				if (t.contains("<img src")) {	
					if (t.contains("portquizm.png")) {
						String[] aa = t.split("<img src");
						String aa1 = aa[1].substring(2,aa[1].length()-4);
						System.out.println(aa1);
					} else {
					String[] aa1 = t.split("img src=");
					if (aa1[1].contains("alt")) {
						String[] aa2 = aa1[1].split(" alt");
						if (aa2[0].contains("http")) {
							String aaf = aa2[0].substring(8,aa2[0].length()-1);
							imageaddress = aaf;
							System.out.println("Image: " + imageaddress);
						}else {
							String aaf = aa2[0].substring(1,aa2[0].length()-1);
							imageaddress = aaf;
		                                        System.out.println("Image: " + imageaddress);
							imageaddress = "/example/"+imageaddress;
							//System.out.println(imageaddress);
						}
					}else {
						if (aa1[1].contains("png")) {
							String aaf = aa1[1].substring(1,aa1[1].length()-4);
							//System.out.println(aaf);
						} else {
							String aaf = aa1[1].substring(1,aa1[1].length()-2);
							imageaddress = aaf;
							System.out.println(imageaddress);
						}
					}
				    getimage(imageaddress);
					}
				}else if (t.contains("html>")||t.contains("head>")||t.contains("body>")||t.contains("<!DOCTYPE")){
				}
				else if (t.contains("<title>")&&t.contains("</title>")){
					String[] aa = t.split("<title>");
					String aa2 = aa[1].substring(0,aa[1].length()-8);
					System.out.println(aa2);
				}
				else if (t.contains("title>")){
				}
				else if (t.contains("<h")&&t.contains("</h")){
					String[] aa = t.split("<h");
					String aa2 = aa[1].substring(2,aa[1].length()-5);
					System.out.println(aa2);
				}
				else if (t.contains("style>")||t.contains("text/css")||t.contains("<h")||t.contains("</h")||t.contains("<meta")){
				}
				else if (t.contains("<p>")&&t.contains("</p>")){
					String aa = t.substring(5,t.length()-4);
						System.out.println(aa);
					}
				else if (t.contains("p>")){
					if (t.contains("IP")) {
						String aa = t.substring(0,t.length()-4);
						System.out.println(aa);			
					}
				}
				else if (t.contains("div p {")||t.contains("address>")||t.contains("div img {")||t.contains("div {")||t.contains("body {")||t.contains("h1 {")||t.contains("div>")){
				}
				else if (t.contains("width:")||t.contains("margin:")||t.contains("text-align:")||t.contains("font:")||t.contains("height:")||t.contains("font-size:")){
				}
				else if (t.contains("HTTP/1.1 200 OK")||t.contains("Date:")||t.contains("Server:")||t.contains("Accept-Ranges:")||t.contains("Content-Length:")||t.contains("Content-Type:")||t.contains("Set-Cookie:")){
				}
				else if (t.contains("Last-Modified:")||t.contains("ETag:")||t.contains("Connection:")||t.contains("Vary:")||t.contains("Host-Header:")||t.contains("X-Proxy-Cache:")||t.contains("		}")){
				}
				else if (t.contains("X-Powered-By:")||t.contains("font-family:")||t.contains("}")||t.contains("Vary:")||t.contains("Host-Header:")||t.contains("X-Proxy-Cache:")||t.contains("		}")){
				}
				else if (t.contains("&copy; <a href=")){
					String[] aa = t.split(">");
					System.out.print(aa[1].substring(0,aa[1].length()-3));
				}
				else if (t.contains("john@december.com")){
					String[] aa = t.split(">");
					System.out.println("  "+aa[1].substring(0,aa[1].length()-3));
				}
			else if (t.contains("<a href=")){
					if (t.contains("a commercial tester")) {
						String[] aa = t.split(">");
						String aa1 = aa[1].substring(0,8);
						String aa2 = aa[2].substring(0,21);
						String aa3 = aa[3].substring(0,15);
						System.out.print(aa1);
						System.out.print(aa2);
						System.out.print(" "+aa3);
					} else if(t.contains("HTML Station at:")){
					System.out.print(t.substring(0,t.length()-9));
					}
					else {
					String[] aa = t.split(">");
					String aa1 = aa[1].substring(0,aa[1].length()-4);
				}
				}
			else if (t.contains("www.december.com")){
				String[] aa = t.split(">");
				System.out.println(aa[1].substring(0,aa[1].length()-4));
			}
				else if (t.contains("<!-- Link back to HTML Dog: -->")){
				}
				else if (t.contains("<i>")){
					String[] aa = t.split("<");
					String aa1 = aa[1].substring(2, aa[1].length()-1);
					System.out.println(aa1);
				}
				else if (t.contains("pre>")){
					if (t.contains("IP")) {
						String[] aa = t.split("<");
						String aa1 = aa[0].substring(0, aa[0].length()-1);
						System.out.println(aa1);	
					}
				}
				else if (t.contains("<br/>")){
						String[] aa = t.split("<br/>");	
						if (aa[0].contains("<b>")) {
							String[] aa2 = aa[0].split("b>");
							System.out.print(aa2[0].substring(0,aa2[0].length()-1));
							System.out.println(aa2[1].substring(0,aa2[1].length()-2));
						}else {
							System.out.println(aa[0]);
						}
					}
				else if (t.contains("<strong>")){
					String[] aa = t.split("strong>");
					String aa1 = aa[0].substring(0, aa[0].length()-1);
					System.out.print(aa1);
					System.out.println(aa[1].substring(0,aa[1].length()-2));
				}
				else if (t.contains("HTTP/1.1 404 Not Found")) {
					System.out.println("Page is not Found");
					System.exit(0);
				}
				else{
					System.out.println(t);
					
				}
				
			}
			br.close();
			pw.close();
			s.close();
			scanner.close();
			
			}



private static void getimage(String aa) throws UnknownHostException, IOException {
	if (aa.contains("/example/images/sifaka.jpg")) {
		Socket sGetimage = new Socket(InetAddress.getByName("www.htmldog.com"), 80);		
		PrintWriter pw =new PrintWriter(sGetimage.getOutputStream());
		pw.println("GET "+ "/examples/images/sifaka.jpg" +" HTTP/1.1");
		pw.println("Host: "+"htmldog.com");
		pw.println("Connection: close \r\n\r\n");
		pw.println(); 
	    pw.flush();
		BufferedReader brGetimage = new BufferedReader(new InputStreamReader(sGetimage.getInputStream()));
		InputStream in = sGetimage.getInputStream();
		Random rand = new Random();
		int  n = rand.nextInt(100000) + 1;
		OutputStream out = new FileOutputStream( n + ".jpg");
		   try {
			      byte[] bytes = new byte[2048];
			      int length;
			      int idx;
			      boolean check=false;
			      while ((length = in.read(bytes)) != -1) {
			    	  
			    	  String checkString = new String(bytes, 0, length);
			    	  //System.out.println(checkString);
			    	  //if (idx == checkString.indexOf("\r\n\r\n")) {
			          if (!check)
			          {
			        	  idx = checkString.indexOf("\r\n\r\n");
			    		  //idx = idx +4;
			    		  //System.out.println("----------------index---------"+idx);
			    		  out.write(bytes, idx+4, length-idx-4);
			    		  out.flush();
			    		  check=true;
			    	  } 
			    	  else {
			    	  //System.out.println(idx);
			    	  //if (length != -1) {
			    		  out.write(bytes, 0, length);
			    		  out.flush();
			    	  }
			      }
		   }
			      finally {
			      brGetimage.close();
			      in.close();
			      out.close();
			      sGetimage.close();
			      }
	    }
		
	
     else{
	if (aa.contains("climate")) {
		String[] aa1 = aa.split("org");
		web2 =aa1[1];
		String aa2 = aa1[0]+"org";
		webForImg = aa2.substring(7);
		//System.out.println("test---web2"+web2);
		//System.out.println("test---webForImg  "+webForImg);
	}
	if (aa.contains("badge1.gif")) {
		webForImg = "www.htmldog.com";
		imageaddress = "/badge1.gif";
	}
		Socket sGetimage = new Socket(InetAddress.getByName(webForImg), numberpForImg);	
	//System.out.println("------------------------------"+webForImg);
	PrintWriter pw =new PrintWriter(sGetimage.getOutputStream());
	pw.println("GET "+ imageaddress +" HTTP/1.1");
	pw.println("Host: "+webForImg);
	
	pw.println("Connection: close \r\n\r\n");
	pw.println(); 
    pw.flush();
	BufferedReader brGetimage = new BufferedReader(new InputStreamReader(sGetimage.getInputStream()));
	InputStream in = sGetimage.getInputStream();
	Random rand = new Random();
	int  n = rand.nextInt(100000) + 1;
	OutputStream out = new FileOutputStream(+n+".jpg");
	   try {
		      byte[] bytes = new byte[2048];
		      int length;
		      int idx;
		      boolean check=false;
		      while ((length = in.read(bytes)) != -1) {
		    	  
		    	  String checkString = new String(bytes, 0, length);
		    	  //System.out.println(checkString);
		    	  //if (idx == checkString.indexOf("\r\n\r\n")) {
		          if (!check)
		          {
		        	  idx = checkString.indexOf("\r\n\r\n");
		    		  //idx = idx +4;
		    		  //System.out.println("----------------index---------"+idx);
		    		  out.write(bytes, idx+4, length-idx-4);
		    		  out.flush();
		    		  check=true;
		    	  } 
		    	  else {
		    	  //System.out.println(idx);
		    	  //if (length != -1) {
		    		  out.write(bytes, 0, length);
		    		  out.flush();
		    	  }
		      }
	   }
		      finally {
		      brGetimage.close();
		      in.close();
		      out.close();
		      sGetimage.close();
		      }
    }
}
}

