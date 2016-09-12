package test;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.lang.*;

public class Servernew {
	
	public final static int check= 7784;
	public final static int filesend= 3498;
	public final static int FILE_SIZE = 6022386;
	
	private static String getFileChecksum(MessageDigest digest, File file) throws IOException
	  {
	      
	      FileInputStream fis = new FileInputStream(file);
	      byte[] byteArray = new byte[1024];
	      int bytesCount = 0;
	      while ((bytesCount = fis.read(byteArray)) != -1) {
	          digest.update(byteArray, 0, bytesCount);
	      };
	      fis.close();
	      byte[] bytes = digest.digest();
	      StringBuilder sb = new StringBuilder();
	      for(int i=0; i< bytes.length ;i++)
	      {
	          sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
	      }
	       
	      return sb.toString();
	  }
	
	public static int checkinfold(String checksum) throws IOException{
	    File f = new File("C:/Users/user/Documents/receive/");
	    File[] Filelist = f.listFiles(); 
	     MessageDigest md5Digest = null;
	    for (int i = 0; i < Filelist.length; i++)
	    {
		     if (Filelist[i].isFile()) 
		     {
					try {
						md5Digest = MessageDigest.getInstance("MD5");
					} catch (NoSuchAlgorithmException e) {

						e.printStackTrace();
					}
				    String checksumfile = getFileChecksum(md5Digest, Filelist[i]);
				    if(checksumfile.equals(checksum))
				    	return 1;
		    	 
		     }
	    }
	    return 0;
		
		
	}
	
	  public static void main(String args[]) throws IOException {
		  
		    ServerSocket servSocket = new ServerSocket(check);
			System.out.println("<SERVER SIDE>");
		    System.out.println("Waiting for a client on port: " + check);
		    String checksum;
		    String filename;
		    //File f = new File("C:/Users/user/Documents/receive/");

		    Socket fromClientSocket = servSocket.accept();
		    
		    BufferedReader br = new BufferedReader(new InputStreamReader(fromClientSocket.getInputStream()));
		    PrintWriter pw = new PrintWriter(fromClientSocket.getOutputStream(), true);
		    
		    String nooffiles = br.readLine();
		    int flag;
		    System.out.println("The number of files on client side is: " +nooffiles);
		    System.out.println("");
		    int nofiles = Integer.parseInt(nooffiles);

		    
		    for (int i = 0; i < nofiles; i++)
		    {
		    	pw.println("send");
		    	
		    	checksum =br.readLine();
		    	System.out.println(checksum);
		    	flag= checkinfold(checksum);
		    	if(flag == 1){
		    		pw.println("Dont send");
		    		System.out.println("this file already exists");
		    		System.out.println("");
		    	}
		    	else{
		    		pw.println("Send file");
		    		filename = br.readLine();
		    		System.out.println("");
		    		System.out.println("File to be received is: " +filename);
		    		//--------------------------------------------------
			    	int bytesRead;
			        int current = 0;
			        FileOutputStream fos = null;
			        BufferedOutputStream bos = null;
			        Socket sock = null;
			        ServerSocket servSock1 = null;
			        
			    	try {
			    		servSock1 = new ServerSocket(filesend);
				    	
				    	sock = servSock1.accept();
			    	     
			    	      String newFile = "C:/Users/user/Documents/receive/" + filename;
			    	     

			    	      byte [] mybytearray  = new byte [FILE_SIZE];
			    	      InputStream is = sock.getInputStream();
			    	      fos = new FileOutputStream(newFile);
			    	      bos = new BufferedOutputStream(fos);
			    	      bytesRead = is.read(mybytearray,0,mybytearray.length);
			    	      current = bytesRead;

			    	      do {
			    	         bytesRead =
			    	            is.read(mybytearray, current, (mybytearray.length-current));
			    	         if(bytesRead >= 0) current += bytesRead;
			    	      } while(bytesRead > -1);

			    	      bos.write(mybytearray, 0 , current);
			    	      bos.flush();
			    	      System.out.println("File " + newFile + " downloaded of size " + current + " bytes");
			    	      System.out.println("");
			    	    }

			    		finally {
			    	      if (fos != null) fos.close();
			    	      if (bos != null) bos.close();
			    	      if (sock != null) sock.close();
			    	      if (servSock1 != null) servSock1.close();
			    	    }
		    	}
		    	
		    }
		    pw.close();
		    br.close();
		    fromClientSocket.close();
		    servSocket.close();
	  }

}
