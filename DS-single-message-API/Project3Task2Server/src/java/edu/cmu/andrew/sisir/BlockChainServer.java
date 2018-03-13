/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cmu.andrew.sisir;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/** This is a single JAX-WS web service. The service will examine the message 
 * from client and will check the signature and if the signature is valid, 
 * will store the transaction on the BlockChain. If the message is a query 
 * message then no signature needs to be checked.
 * 
 * @author sisi
 */
@WebService(serviceName = "BlockChain")
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
     * Web service: receive client option and perform corresponding BlockChain operation.
     */
    @WebMethod(operationName = "getMenu")
    public String getMenu(@WebParam(name = "request") String request) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        XMLHelper helper = new XMLHelper();
        BlockChainRequest req = helper.process(request);
        switch(req.getOption()){
            case 1:{
                // RSA decrypt msg
//                System.out.println("sig:"+req.getSignature());
                BigInteger clear = new BigInteger(req.getSignature()).modPow(e, n);
//                System.out.println("clear:"+clear);
                byte[] byteArray = clear.toByteArray();
                byte[] hash = getHashValue(req.getData());
                if(!compareBytes(hash, byteArray)){
                    System.out.println("Key Verification failed.");
                    return "false";
                }
                addBlock(req.getSignature(),req.getData(),req.getDifficulty());
                return "true";
            }
            
            case 2:{
                if(isChainVaild())
                    return "true";
                else return "false";
            }
            
            case 3:{
                return blockChainToString();
            }
            
            default: break;

        }

        return null;
    }
    
    public void addBlock(String sig, String data,int difficulty){

        // add signature to transaction
        data = data+"#"+sig;
        Block b = new Block(index++, new Timestamp(System.currentTimeMillis()), data, difficulty);
        BlockChain.addBlock(b);
    }
    
    public Boolean isChainVaild() {
        return blockchain.isChainVaild();
    }
    
    public String blockChainToString() {
        return blockchain.toString();
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
    
      
}


