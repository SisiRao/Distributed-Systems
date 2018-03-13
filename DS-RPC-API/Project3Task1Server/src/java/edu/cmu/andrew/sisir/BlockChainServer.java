
package edu.cmu.andrew.sisir;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 * BlockChainServer.java Class is a JAX-WS RPC-Style web service. It implements 
 * methods as a SOAP based API.
 *
 * @author sisi
 */
@WebService(serviceName = "BlockChainServer")
public class BlockChainServer {

    BigInteger n; // n is the modulus for both the private and public keys
    BigInteger e; // e is the exponent of the public key
    int index = 1;
    BlockChain blockchain;

    public BlockChainServer() {
        e = new BigInteger("65537");
        n = new BigInteger("4247448908890959317608652211466415670024646327882811359522071290050191699760201011128045217827970743089700557700509750876626560603783580929182683689452492570145824987928477257377769951051980034364980498172790647011947325123387814133108946633");
        blockchain = new BlockChain();
    }
    
    
    /**
    * This method calculates the hash value of a given string with SHA-256.
    * 
    * @param data the string to be hashed.
    * 
    * @return a byte[] contains the hash result.
    */
    public byte[] getHashValue(String data) throws UnsupportedEncodingException, NoSuchAlgorithmException {

        // caculate hash
        byte[] bytesOfMessage = data.getBytes("UTF-8");
        MessageDigest sha;
        // md is a MessageDigest object that implements the specified digest algorithm.
        sha = MessageDigest.getInstance("SHA-256");
        bytesOfMessage = sha.digest(bytesOfMessage);
        // since RSA doesn't support negative input    
        // add an additional 0 in the front to make the hash -> biginteger always positive
        byte[] result = bytesOfMessage;
        if (new BigInteger(bytesOfMessage).compareTo(BigInteger.ZERO) < 0) {
            // add 0 to the front of the array
            result = new byte[bytesOfMessage.length + 1];
            result[0] = 0;
            for (int i = 1; i < result.length; i++) {
                result[i] = bytesOfMessage[i - 1];
            }
        }        
        return result;
    }
    
    /**
    * This method compares if two byte[] are the same.
    * 
    * @param hash the hash value of original data with which the user generates
    *             signature.
    * @param decrypt the hash value of RSA decrypted signature
    * 
    * @return true if two byte[] is the same.
    */
    public boolean compareBytes(byte[] hash, byte[] decrypt) {
        
        if (hash.length != decrypt.length) {
            return false;
        }
        for (int i = 0; i < hash.length; i++) {
            if (hash[i] != decrypt[i]) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Web service operation: Add a new Block to BlockChain.
     */
    @WebMethod(operationName = "addBlock")
    public boolean addBlock(@WebParam(name = "signature") String sig,@WebParam(name = "data") String data, @WebParam(name = "difficulty") int difficulty) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        // RSA decrypt msg
        BigInteger clear = new BigInteger(sig).modPow(e, n);
        System.out.println("clear:"+clear);

        byte[] byteArray = clear.toByteArray();
        byte[] hash = getHashValue(data);
        if(!compareBytes(hash, byteArray)){
            System.out.println("Key Verification failed.");
            return false;
        }

        // add signature to transaction
        data = data+"#"+sig;
        Block b = new Block(index++, new Timestamp(System.currentTimeMillis()), data, difficulty);
        BlockChain.addBlock(b);
        return true;
    }

    /**
     * Web service operation: Validate BlockChain.
     */
    @WebMethod(operationName = "isChainVaild")
    public Boolean isChainVaild() {
        return blockchain.isChainVaild();
    }
    
     /**
     * Web service operation: Return BlockChain content in a string.
     */
    @WebMethod(operationName = "blockChainToString")
    public String blockChainToString() {
        return blockchain.toString();
    }

}
