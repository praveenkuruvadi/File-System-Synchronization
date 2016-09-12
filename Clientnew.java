package test;

import java.io.IOException;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

public class Clientnew {
	public final static int check= 7784;
	public final static int filesend= 3498;
	
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
	
	public static void main (String [] args ) throws IOException{
		String message = null;
		String reply =null;
		FileInputStream fis = null;
	    BufferedInputStream bis = null;
	    OutputStream os = null;
		File files = new File("C:/Users/user/Documents/send/");
		Socket s1;
		System.out.println("<CLIENT SIDE>");
		System.out.println("Sending file's checksum");
						
		s1 = new Socket ("127.0.0.1",check);
	    File[] Filelist = files.listFiles(); 
		MessageDigest md5Digest = null;
		
	    PrintWriter pw = new PrintWriter(s1.getOutputStream(), true);
        BufferedReader br = new BufferedReader(new InputStreamReader(s1.getInputStream()));

		 
	    pw.println(Filelist.length);
	    System.out.println(Filelist.length);
	    for (int i = 0; i < Filelist.length; i++)
	    {
	    	File  file = Filelist[i];
			try {
				md5Digest = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}

			String checksum1 = getFileChecksum(md5Digest, Filelist[i]);
			System.out.println("");
			System.out.println("checksum of " +Filelist[i].getName() +" is: "+ checksum1);
		    message = br.readLine();
			if(message.equals("send")){
				pw.println(checksum1);
			}
			reply = br.readLine();
			System.out.println("Message from server for file " +Filelist[i].getName() +" is: " +reply);
			if(reply.equals("Send file")){
				pw.println(Filelist[i].getName());
				//--------------------------------------------------------------
	        	Socket s2 = new Socket("127.0.0.1", filesend);
	        	
	        try{
	        		
	            fis = new FileInputStream(file);
	            bis = new BufferedInputStream(fis);
	            
	            System.out.println("File sent is:  " + file + " of size:" + file.length() + " bytes)");
	            
	            byte [] mybytearray  = new byte [(int)file.length()];
	              bis.read(mybytearray,0,mybytearray.length);
		          os = s2.getOutputStream();
		          os.write(mybytearray,0,mybytearray.length);
		          os.flush();
		          System.out.println("Transfered.");
		        }

	        
	        	catch (IOException ex){
	        		System.out.println(ex.getMessage()+": connection problem");
	        	}
	        	//-------------------------------------------------------------
	        	
	        	
	        	finally {
	                if (bis != null) bis.close();
	                if (os != null) os.close();
	                if (s2!=null) s2.close();
	              }
	      }
			}
	      br.close();
	      pw.close();
	      s1.close();
	}
}
