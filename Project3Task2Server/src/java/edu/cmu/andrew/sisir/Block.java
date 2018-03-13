
package edu.cmu.andrew.sisir;
/**
 * This class represents a simple Block.
 * For detailed implementation: http://www.andrew.cmu.edu/course/95-702/examples/javadoc/index.html
 * 
 * Created Date: 1 March 2018
 * @author sisi rao
 */

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Block {
    private int difficulty;
    private int index;
    private BigInteger nouce = new BigInteger("0");
    private Timestamp timestamp;
    private String data;
    private String previousHash;
    
	public Block(int index, Timestamp timestamp, String data, int difficulty) {
	    this.difficulty = difficulty;
	    this.data = data;
	    this.timestamp = timestamp;
	    this.index = index;
		
	}

	/**
        * This method computes a hash of the concatenation of the index, 
        * timestamp, data, previousHash, nonce, and difficulty.
        * 
        * @return a String holding Hexadecimal characters
        */
        public String calculateHash() {  
	    String combined = this.index + this.timestamp.toString() + this.data+this.previousHash+this.nouce.toString()+this.difficulty;
	    byte[] bytesOfMessage = null;
	    byte[] hash = null;
        MessageDigest sha;
        try {
            bytesOfMessage = combined.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            sha = MessageDigest.getInstance("SHA-256");
            hash = sha.digest(bytesOfMessage);
            
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Block.class.getName()).log(Level.SEVERE, null, ex);
        }
        //contert to hexadecimal text and base 64 notation
        String hex = javax.xml.bind.DatatypeConverter.printHexBinary(hash);
       
	    return hex;
	}
	
        /**
        * This method finds a good hash.
        * 
        * @param difficulty number of leading zeros
        * @return a hex String with a hash that has the appropriate number of 
        * leading hex zeroes.
        */
	public String proofOfWork(int difficulty){
	
	    int count = 0;
	    String hash = calculateHash();
	    
        while(hash.charAt(count)=='0'&& count<hash.length()-1) {
            count++;
        }
	    while(count != difficulty) {	        
	        nouce = nouce.add(BigInteger.valueOf(1));
	        hash = calculateHash();
	       count = 0;
	        while(hash.charAt(count)=='0'&&count<hash.length()-1) {
	            count++;
	        }
	    }
	    
	    return hash;
	}
	
        
    /**
     * Getters and Setters 
     */    
    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public void setData(String data) {
        this.data = data;
    }
    
    public String getData() {
        return data;
    }
    
    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }
    
    public String getPreviousHash() {
        return previousHash;
    }
    
    @Override
    public String toString() {
        return "{\"index\":" + index + ", \"timestamp\":\"" + timestamp + "\", \"Tx\":\"" + data
                        + "\", \"previousHash\":\"" + previousHash + "\", \"nouce\":" + nouce + ", \"difficulty\":"
                        + difficulty + "}";
    }
}

