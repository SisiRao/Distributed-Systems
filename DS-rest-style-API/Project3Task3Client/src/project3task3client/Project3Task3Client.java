/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project3task3client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sisi
 */

// A simple class to wrap a result.
class Result {
    String value;

    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
}


public class Project3Task3Client {

    private static BigInteger n; // n is the modulus for both the private and public keys
    private static BigInteger e; // e is the exponent of the public key
    private static BigInteger d; // d is the exponent of the private key

    public Project3Task3Client(){
        e = new BigInteger("65537");
        d = new BigInteger("2240803607502707148730017397980550250867481678846273139684080974922339716789125989284711894142272127841164483917407336869506505649109902075534761057770733053328679417345368283990723110936991027777170984163662131514801591073330615273886883773");
        n = new BigInteger("4247448908890959317608652211466415670024646327882811359522071290050191699760201011128045217827970743089700557700509750876626560603783580929182683689452492570145824987928477257377769951051980034364980498172790647011947325123387814133108946633");
    
    }
    public static void main(String[] args) throws UnsupportedEncodingException{
        Project3Task3Client client = new Project3Task3Client();
        long start, end;
        
        loop:
        while (true) {
             System.out.println("1. Add a transaction to the blockchain.\n"
                    + "\n"
                    + "2. Verify the blockchain.\n"
                    + "\n"
                    + "3. View the blockchain.\n"
                    + "\n"
                    + "4. Exit.");
            Scanner sc = new Scanner(System.in);
            int option = Integer.parseInt(sc.nextLine());
            switch (option) {
                case 1: {
                    System.out.println("Enter difficulty");
                    int difficulty = Integer.parseInt(sc.nextLine());
                    System.out.println("Enter transaction");
                    String data = sc.nextLine();
                    byte[] hash = getHash(data);
                    String signature = rsaEncrypt(hash);
                    
//                    System.out.println("signature: "+signature);
                    String msg = "signature="+signature+"&data="+data+"&difficulty="+difficulty;
                    //Add transaction to the blockchain
                    start = System.currentTimeMillis();
                    if (doPost(msg) != 200) {
                        System.out.println("Key Verification failed.");
                        break;
                    }
                    end = System.currentTimeMillis();
                    System.out.printf("Total execution time to add this block %d milliseconds \n", (end - start));
                    break;
                }
                
                case 2: {
                    System.out.println("Verifying");
                    start = System.currentTimeMillis();
                    Result r = new Result();
                    int status = 0;
                    if ((status = doGet("verify",r)) == 200) {
                        System.out.println("Chain verification: true");
                    } else {
                        System.out.println("Chain verification: false");
                    }
                    end = System.currentTimeMillis();
                    System.out.printf("Total execution time to verify the chain is %d milliseconds \n", (end - start));
                    break;
                }
                
                case 3: {
                    System.out.println("View the Blockchain");
                    Result r = new Result();
                    int status = 0;
                    if ((status = doGet("view",r)) == 200) {
                        System.out.println(r.getValue());
                    } else {
                        System.out.println("Error: View the Blockchain.");
                    }
                    break;
                }
                case 4: {
                    break loop;
                }
 
            }
                
        }
        
    }
    public static int doGet(String name, Result r) {

         // Make an HTTP GET passing the name on the URL line
         
         r.setValue("");
         String response = "";
         HttpURLConnection conn;
         int status = 0;
         
         try {  
                
                // pass the name on the URL line
		URL url = new URL("http://localhost:8080/Project3Task3Server/BlockChainService" + "//"+name);
		conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
                // tell the server what format we want back
		conn.setRequestProperty("Accept", "text/plain");
 	
                // wait for response
                status = conn.getResponseCode();
                
                // If things went poorly, don't try to read any response, just return.
		if (status != 200) {
                    // not using msg
                    String msg = conn.getResponseMessage();
                    return conn.getResponseCode();
                }
                String output = "";
                // things went well so let's read the response
                BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));
 		
		while ((output = br.readLine()) != null) {
			response += output;
         
		}
 
		conn.disconnect();
 
	    } 
                catch (MalformedURLException e) {
		   e.printStackTrace();
	    }   catch (IOException e) {
		   e.printStackTrace();
	    }
            
         // return value from server 
         // set the response object
         r.setValue(response);
         // return HTTP status to caller
         return status;
    } 
    
    
    public static int doPost(String msg) {
          
        int status = 0;
        
        try {  
                // Make call to a particular URL
		URL url = new URL("http://localhost:8080/Project3Task3Server/BlockChainService/");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
                // set request method to POST and send name value pair
                conn.setRequestMethod("POST");
		conn.setDoOutput(true);
                // write to POST data area
                OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                out.write(msg);
                out.close();
                
                // get HTTP response code sent by server
                status = conn.getResponseCode();
                
                //close the connection
		conn.disconnect();
	    } 
            // handle exceptions
            catch (MalformedURLException e) {
		      e.printStackTrace();        
            } 
            catch (IOException e) {
		      e.printStackTrace();
	    }
       
            // return HTTP status
         
            return status;
    }
    
    private static byte[] getHash(String data)throws UnsupportedEncodingException{
        byte[] bytesOfMessage = data.getBytes("UTF-8");
         //SHA-256 hash
        MessageDigest sha;
        try {
            sha = MessageDigest.getInstance("SHA-256");
            bytesOfMessage = sha.digest(bytesOfMessage);

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Project3Task3Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bytesOfMessage;
    }

    private static String rsaEncrypt(byte[] hash) throws UnsupportedEncodingException{
        // since RSA doesn't support negative input    
        // add an additional 0 in the front to make the hash -> biginteger always positive
        byte[] newHash = hash;
        
        if (new BigInteger(hash).compareTo(BigInteger.ZERO) < 0) {
            newHash = new byte[33];
            newHash[0] = 0;
            for (int i = 1; i < newHash.length; i++) {
            newHash[i] = hash[i - 1];
            }
        }
        
        BigInteger m = new BigInteger(newHash);
        BigInteger c = m.modPow(d, n);
        return c.toString();
    }
}
