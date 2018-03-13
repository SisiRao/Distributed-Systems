/**
 *
 * @author sisi
 */
import java.net.*;
import java.util.Random;
import java.util.Scanner;

import org.json.simple.JSONObject;

import java.io.*;
import java.math.BigInteger;
public class TCPInstallerUsingTEAandPasswordsAndRSA {
	//the pair P = (e,n) as the RSA public key.
	public static final BigInteger[] publicKey = new BigInteger[] {new BigInteger("65537"), new BigInteger("3888585980894685604170687731604431787109528416355770515871908987424323116066835390935627912657401064721591752077795903495658447798857636799247231532131170244985657346054623231354548516759613014110121059357395118273864852747612838400694349429")};
	
	/*1.Establish connection to server
	 *2. Get user input for personal information
	 *3. Encrypt user input
	 *4. Send to server to verify
	 *5. Get respond from server */
	@SuppressWarnings("unchecked")
	public static void main (String args[]) {
		// arguments supply message and hostname
		Socket s = null;
		try{
			int serverPort = 7896;
			s = new Socket(args[0], serverPort);
			
			//init
			DataInputStream in = new DataInputStream( s.getInputStream());
			DataOutputStream out =new DataOutputStream( s.getOutputStream());
 
			//Enter user information and encrypt for server to verify
			Scanner sc = new Scanner(System.in);
		    
			//generates a random 16-byte key for TEA
			Random rnd = new Random();
			BigInteger key = new BigInteger(16*8,rnd);
			TEA tea = new TEA(key.toByteArray());
			
			// Do the encryption, c is the cypher text
			// send key to server 
		    byte[] enc_key = key.modPow(publicKey[0], publicKey[1]).toByteArray();   
		    out.write(enc_key);
            out.flush();

		    JSONObject obj = new JSONObject();
			
			System.out.println("Enter your ID:");
			obj.put("ID",sc.nextLine());
			System.out.println("Enter your Password:");
			obj.put("passwd",sc.nextLine());
			System.out.println("Enter sensor ID:");
			obj.put("Sensor ID", sc.nextLine());
			System.out.println("Enter new sensor location:");
			String location[] = sc.nextLine().split(",");
			obj.put("Latitude", location[0]);
			obj.put("Longitude", location[1]);
			sc.close();
			
			StringWriter sw = new StringWriter();
		    try {
				obj.writeJSONString(sw);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		    String jsonText = sw.toString();
			byte[] encryM = tea.encrypt(jsonText.getBytes());
			
		
			
			//pass encrypted userInfo to server
			
			out.write(encryM);  
			out.flush();
			
			
			// read encrypted respond from server.
			byte[] data = new byte[1024];
			int count = in.read(data);
			if(count < 0) {
                return;
            }
			byte[] crypt = new byte[count];
			int i = 0;
			while(i< count)
			{
				crypt[i] = data[i];
				i++;
			}
			
			//decrypt server respond
			String respond  = new String(tea.decrypt(crypt));
            System.out.println(respond);
            out.close();
            in.close();
		}catch (UnknownHostException e){System.out.println("Socket:"+e.getMessage());
		}catch (EOFException e){System.out.println("EOF:"+e.getMessage());
		}catch (IOException e){System.out.println("readline:"+e.getMessage());
		}finally {if(s!=null) try {s.close();}catch (IOException e){System.out.println("close:"+e.getMessage());}}
     }
	
	
}
