import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/*
 * This class use salt hash to protect user password.
 * Hashing algorithm is SHA-256
 */

/**
 * @author sisi 
 */
public class PasswordHash {
	static Map<String, Integer> dict = new HashMap<>();
	
	public PasswordHash() {}
	
	private void compute() {
		//get user info
		Scanner sc = new Scanner(System.in);
		System.out.println("#Enter user ID");
		String userid = sc.nextLine();
		System.out.println("#Enter password");
		String pwd = sc.nextLine();
		
		//generate salt for user
		SecureRandom srandom = new SecureRandom();
		int salt = srandom.nextInt();
		
		dict.put(userid, salt);
		
		//compute hash with SHA-256
		byte[] hash = getHash(salt, pwd.getBytes());
		String hex = javax.xml.bind.DatatypeConverter.printHexBinary(hash);

		System.out.println("#Generating a random number for salt using SecureRandom");
		System.out.println("#User ID = " + userid + "\n" +
							"#Salt = " + Integer.toHexString(salt) + "\n" +
							"#Hash of salt + password " + hex);	
		
		//authentication
		System.out.println("#Enter user ID for authentication testing");
		String authuserid = sc.nextLine();
		System.out.println("#Enter password for authentication testing");
		String authpwd = sc.nextLine();
		
		//compute hash for authentication
		salt = dict.get(userid);
		byte[] authhash = getHash(salt, authpwd.getBytes());
		
		//compare results
		if(Arrays.equals(hash, authhash))
			System.out.println("#Validated user id and password pair");
		else
			System.out.println("#Not able to validate this user id, password pair.");
		sc.close();
	}

	/*Compute hash for salt + password*/
	private byte[] getHash(Integer s, byte[] pwd) {
		//combine salt and user password
		
		byte[] salt = ByteBuffer.allocate(4).putInt(s).array();
		byte[] combined = new byte[salt.length+pwd.length];
		for (int i = 0; i < combined.length; ++i)
		{
		    combined[i] = i < salt.length ? salt[i] : pwd[i - salt.length];
		}
		//hash salt + password
		byte[] hash = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			hash = digest.digest(combined);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return hash;
	}


	public static void main(String[] args)
	{
		PasswordHash pwdhash = new PasswordHash();
		pwdhash.compute();
	}
    
}
