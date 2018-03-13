package project3task1client;

import edu.cmu.andrew.sisir.NoSuchAlgorithmException_Exception;
import edu.cmu.andrew.sisir.UnsupportedEncodingException_Exception;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * BlockChainClient.java Class will make SOAP requests to the BlockChain API.
 * All transaction data that will modify the BlockChain will be signed with 
 * user's private key (d,n) generated with RSA.
 */

public class BlockChainClient {

    private static BigInteger n; // n is the modulus for both the private and public keys
    private static BigInteger e; // e is the exponent of the public key
    private static BigInteger d; // d is the exponent of the private key

    /**
    * This method computes a hash of a given String with SHA-256.
    * @param data a string to be hashed.
    * 
    * @return a byte[] that contains the hash.
    */
    private static byte[] getHash(String data)throws UnsupportedEncodingException{
        byte[] bytesOfMessage = data.getBytes("UTF-8");
         //SHA-256 hash
        MessageDigest sha;
        try {
            sha = MessageDigest.getInstance("SHA-256");
            bytesOfMessage = sha.digest(bytesOfMessage);

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(BlockChainClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bytesOfMessage;
    }

    /**
    * This method encrypt the byte[] hash with user's RSA private key.
    * @param byte[] hash.
    * 
    * @return a string of encrypted message.
    */
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

    public BlockChainClient() {
        e = new BigInteger("65537");
        d = new BigInteger("2240803607502707148730017397980550250867481678846273139684080974922339716789125989284711894142272127841164483917407336869506505649109902075534761057770733053328679417345368283990723110936991027777170984163662131514801591073330615273886883773");
        n = new BigInteger("4247448908890959317608652211466415670024646327882811359522071290050191699760201011128045217827970743089700557700509750876626560603783580929182683689452492570145824987928477257377769951051980034364980498172790647011947325123387814133108946633");
    }

    /**
    * This method provides the BlockChain menu to the user and make request calls
    * to the BlockChain Server.
    * 
    * @param args
    */
    public static void main(String[] args) throws UnsupportedEncodingException, NoSuchAlgorithmException_Exception, UnsupportedEncodingException_Exception {
        BlockChainClient client = new BlockChainClient();
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
                    //Add transaction to the blockchain
                    start = System.currentTimeMillis();
                    if (!addBlock(signature,data,difficulty)) {
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
                    if (isChainVaild()) {
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
                    System.out.println(blockChainToString());
                    break;
                }
                case 4: {
                    break loop;
                }

            }

        }
    }

    

    private static Boolean isChainVaild() {
        edu.cmu.andrew.sisir.BlockChainServer_Service service = new edu.cmu.andrew.sisir.BlockChainServer_Service();
        edu.cmu.andrew.sisir.BlockChainServer port = service.getBlockChainServerPort();
        return port.isChainVaild();
    }

    private static String blockChainToString() {
        edu.cmu.andrew.sisir.BlockChainServer_Service service = new edu.cmu.andrew.sisir.BlockChainServer_Service();
        edu.cmu.andrew.sisir.BlockChainServer port = service.getBlockChainServerPort();
        return port.blockChainToString();
    }

    private static boolean addBlock(java.lang.String signature, java.lang.String data, int difficulty) throws NoSuchAlgorithmException_Exception, UnsupportedEncodingException_Exception {
        edu.cmu.andrew.sisir.BlockChainServer_Service service = new edu.cmu.andrew.sisir.BlockChainServer_Service();
        edu.cmu.andrew.sisir.BlockChainServer port = service.getBlockChainServerPort();
        return port.addBlock(signature, data, difficulty);
    }
}
