

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This is a REST web service. The service will verify user's signature and the
 * BlockChain will be used to store the transaction and the signature.
 *
 * @author sisi
 */

@WebServlet(name = "BlockChainService", urlPatterns = {"/BlockChainService/*"})
public class BlockChainService extends HttpServlet {
    BigInteger n; // n is the modulus for both the private and public keys
    BigInteger e; // e is the exponent of the public key
    int index = 1;
    BlockChain blockchain;
    
    public BlockChainService() {
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

    /** Get method to view BlockChain or verify BlockChain
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException 
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("Console: doGET visited");
        // The request content is in the path /<info> so skip over the '/'
        String content = (request.getPathInfo()).substring(1);
        
        String feedback="";
        switch (content) {
            case "verify":
                if(blockchain.isChainVaild())
                    response.setStatus(200);
                else
                    response.setStatus(401);
                return;
            case "view":
                response.setStatus(200);
                // tell the client the type of the response
                response.setContentType("text/plain;charset=UTF-8");
                feedback = blockchain.toString();
                PrintWriter out = response.getWriter();
                out.println(feedback);
                break;
            default:
                response.setStatus(401);
                break;
        }
        
    }

    /** Post Method to add a block to the BlockChain
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException 
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            System.out.println("Console: doPost visited");
            
            // Read what the client has placed in the POST data area
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String msg = br.readLine();
            
            //retrieve information from data
            String[] infos = msg.split("&");
            //sig,data,diff
            String sig = infos[0].replace("signature=","");
            String data = infos[1].replace("data=","");
            int  difficulty = Integer.parseInt(infos[2].replace("difficulty=",""));
            
            // RSA decrypt msg
            BigInteger clear = new BigInteger(sig).modPow(e, n);
            System.out.println("clear:"+clear);
            byte[] byteArray = clear.toByteArray();
            byte[] hash = getHashValue(data);
            if(!compareBytes(hash, byteArray)){
                response.setStatus(401);
                return;
            }
            
            // add signature to transaction
            data = data+"#"+sig;
            Block b = new Block(index++, new Timestamp(System.currentTimeMillis()), data, difficulty);
            BlockChain.addBlock(b);
            response.setStatus(200);
            return;

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(BlockChainService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(BlockChainService.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    

}
